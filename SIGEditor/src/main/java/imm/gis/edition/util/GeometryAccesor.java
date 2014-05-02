package imm.gis.edition.util;

import java.util.ArrayList;

import imm.gis.GisException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;

public class GeometryAccesor {

	private Geometry geometry;
	
	public GeometryAccesor(Geometry geometry) {
		this.geometry = geometry;
	}
	
	public boolean isPolygon() {
		return geometry.getClass().isAssignableFrom(com.vividsolutions.jts.geom.Polygon.class);
	}
	
	public boolean isPoint() {
		return geometry.getClass().isAssignableFrom(com.vividsolutions.jts.geom.Point.class);
	}
	
	public boolean isLineString() {
		return geometry.getClass().isAssignableFrom(com.vividsolutions.jts.geom.LineString.class);
	}
	
	public int getNearestVertex(Coordinate c) {
		
		Coordinate vertexs[] = geometry.getCoordinates();
		
		int nearestVertexIndex = 0;
		double minimumDistance = c.distance(vertexs[0]);
		
		for (int i = 1; i < vertexs.length; i++) {
			if (c.distance(vertexs[i]) < minimumDistance) {
				minimumDistance = c.distance(vertexs[i]);
				nearestVertexIndex = i;
			}
		}
		
		return nearestVertexIndex;
	}
	
	public int getNearestVertex(Coordinate c, double accuracy) {
		
		Coordinate vertexs[] = geometry.getCoordinates();
		
		int nearestVertexIndex = -1;
		double minimumDistance = -1;
		
		for (int i = 0; i < vertexs.length; i++) {
			if (c.distance(vertexs[i]) <= accuracy) {
				if ((nearestVertexIndex == -1) || (c.distance(vertexs[i]) < minimumDistance)) {
					nearestVertexIndex = i;
					minimumDistance = c.distance(vertexs[i]);
				}
			}
		}
		
		return nearestVertexIndex;
	}
	
	public LineSegment getNearestLineSegment(Coordinate c) throws GisException {
		
		if (isPoint())
			throw new GisException("Geometry can't be of type Point");
		
		Coordinate vertexs[] = geometry.getCoordinates();
		
		LineSegment nearestLineSegment = new LineSegment(vertexs[0], vertexs[1]);
		double minimumDistance = nearestLineSegment.distance(c);
		
		double iteratingDistance;
		LineSegment iteratingLineSegment;
		
		for (int i = 1; i < vertexs.length - 1; i++) {
			iteratingLineSegment = new LineSegment(vertexs[i], vertexs[i + 1]);
			iteratingDistance = iteratingLineSegment.distance(c);
			
			if (iteratingDistance < minimumDistance) {
				nearestLineSegment = iteratingLineSegment;
				minimumDistance = iteratingDistance;
			}
		}
		
		return nearestLineSegment;
	}
	
	public LineSegment getNearestLineSegment(Coordinate c, double accuracy) throws GisException {
		
		if (isPoint())
			throw new GisException("Geometry can't be of type Point");
		
		Coordinate vertexs[] = geometry.getCoordinates();
		
		LineSegment nearestLineSegment = null;
		double minimumDistance = -1;
		
		double iteratingDistance;
		LineSegment iteratingLineSegment;
		
		for (int i = 0; i < vertexs.length - 1; i++) {
			iteratingLineSegment = new LineSegment(vertexs[i], vertexs[i + 1]);
			iteratingDistance = iteratingLineSegment.distance(c);
			
			if (iteratingDistance <= accuracy) {
				if ((nearestLineSegment == null) || (iteratingDistance < minimumDistance)) {
					nearestLineSegment = iteratingLineSegment;
					minimumDistance = iteratingDistance;					
				}
			}
		}
		
		return nearestLineSegment;		
	}
	
	
	public Geometry deleteVertex(int index) throws GisException {
		if ((isPolygon() && geometry.getNumPoints() <= 3) ||
			(isLineString() && geometry.getNumPoints() <= 2) ||
			isPoint())
			throw new GisException("No se pueden eliminar mas vertices a esta geometria");
		
		Coordinate geometryCoordinates[] = geometry.getCoordinates();
		Coordinate newCoordinates[] = new Coordinate[geometryCoordinates.length -1];
		
		for (int i = 0; i < geometryCoordinates.length; i++) {
			if (i < index)
				newCoordinates[i] = geometryCoordinates[i];
			else if (i > index)
				newCoordinates[i - 1] = geometryCoordinates[i];
		}
		
		// Aca abarco el caso en que haya borrado una de las dos coordenadas que inician y cierran el poligono
		
		if (isPolygon() && (index == 0 || index == geometryCoordinates.length - 1)) {
			if (index == 0)
				newCoordinates[newCoordinates.length - 1] = newCoordinates[0];
			else
				newCoordinates[0] = newCoordinates[newCoordinates.length - 1];
		}
		
		Geometry newGeometry;
		
		if (isPolygon())
			newGeometry = geometry.getFactory().createPolygon(geometry.getFactory().createLinearRing(newCoordinates), null);
		else
			newGeometry = geometry.getFactory().createLineString(newCoordinates);
		
		return newGeometry;
	}
	
	public Geometry addVertex(int index, Coordinate c) throws GisException {

		if (isPoint())
			throw new GisException("Geometry can't be of type Point");
		
		Coordinate geometryCoordinates[] = geometry.getCoordinates();
		Coordinate newCoordinates[] = new Coordinate[geometryCoordinates.length + 1];
			
		for (int i = 0; i < geometryCoordinates.length; i++) {
			if (i <= index)
				newCoordinates[i] = geometryCoordinates[i];
			else if (i > index)
				newCoordinates[i + 1] = geometryCoordinates[i];
		}
		
		newCoordinates[index + 1] = c;
			
		
		Geometry newGeometry;
		
		if (isPolygon())
			newGeometry = geometry.getFactory().createPolygon(geometry.getFactory().createLinearRing(newCoordinates), null);
		else
			newGeometry = geometry.getFactory().createLineString(newCoordinates);
		
		return newGeometry;
	}

	
	
	public Geometry[] split(LineString splitLine) throws GisException {
		
		if (isPoint())
			throw new GisException("Geometry can't be of type Point");
		
		Geometry newGeometries[];
		
		if (isLineString()) {
			
			// Particion de linea
			Geometry diff = geometry.difference(splitLine);
			
			newGeometries = new Geometry[diff.getNumGeometries()];
			
			for (int i = 0; i < diff.getNumGeometries(); i++)
				newGeometries[i] = diff.getGeometryN(i);
		}
		else {
			// Particion de poligono
			MultiLineString mls = geometry.getFactory().createMultiLineString(new LineString[]{splitLine});
			
			Geometry nodification = mls.union(geometry.getBoundary());
			Polygonizer polygonizer = new Polygonizer();
			polygonizer.add(nodification);

			Geometry toFilterGeometries[] =  (Polygon[]) polygonizer.getPolygons().toArray(new Polygon[]{});
			ArrayList<Geometry> filteredGeometries = new ArrayList<Geometry>();
			
			for (int i = 0; i < toFilterGeometries.length; i++)
				if (geometry.contains(toFilterGeometries[i].getCentroid()))
					filteredGeometries.add(toFilterGeometries[i]);
			
			newGeometries = (Geometry[]) filteredGeometries.toArray(new Geometry[filteredGeometries.size()]);			
		}

		return newGeometries;
	}
}
