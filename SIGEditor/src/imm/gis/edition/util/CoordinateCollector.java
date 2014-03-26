package imm.gis.edition.util;

import imm.gis.core.interfaces.IMap;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class CoordinateCollector {

	private ArrayList<Coordinate> coordinates;
	
	private GeometryFactory geometryFactory;
	private IMap map;
	
	public CoordinateCollector(GeometryFactory geometryFactory, IMap map) {
		this.geometryFactory = geometryFactory;
		this.map = map;
		
		this.coordinates = new ArrayList<Coordinate>();
	}
	
	public void addCoordinate(Coordinate c) {
		coordinates.add(c);
	}
	
	public void clearCoordinates() {
		coordinates.clear();
	}
	
	public int numCoordinates() {
		return coordinates.size();
	}
	
	public boolean isEmpty() {
		return coordinates.isEmpty();
	}
	
	public Coordinate[] getCoordinates() {
		return (Coordinate[]) coordinates.toArray(new Coordinate[coordinates.size()]);
	}
	
	public Coordinate getLastCoordinate() {
		if (coordinates.isEmpty())
			return null;
		else
			return (Coordinate) coordinates.get(coordinates.size() - 1);
	}
	
	public void removeLastCoordinate() {
		coordinates.remove(coordinates.size() - 1);
	}
	
	public Polygon createPolygon() {
		Coordinate polygonCoordinates[] = new Coordinate[coordinates.size() + 1];
		Coordinate actualCoordinates[] = getCoordinates();
		
		System.arraycopy(actualCoordinates, 0, polygonCoordinates, 0, actualCoordinates.length);
		polygonCoordinates[actualCoordinates.length] = polygonCoordinates[0];
		
		return geometryFactory.createPolygon(geometryFactory.createLinearRing(polygonCoordinates), null);
	}
	
	public LineString createLinestring() {
		return geometryFactory.createLineString(getCoordinates());
	}
	
	public Point createPoint() {
		return geometryFactory.createPoint(getCoordinates()[0]);
	}
	
	public Point2D createPointShape() {
		return map.worldToPixel(getCoordinates()[0]);
	}
	
	public GeneralPath createLinestringShape() {
		
		Iterator it = coordinates.iterator();
		
		Point2D begin = map.worldToPixel((Coordinate) it.next());
		Point2D end = map.worldToPixel((Coordinate) it.next());
		
		GeneralPath resultPath = new GeneralPath(new Line2D.Double(begin, end));
		
		begin = end;

		while (it.hasNext()) {
			end = map.worldToPixel((Coordinate) it.next());
			resultPath.append(new Line2D.Double(begin, end), true);
			begin = end;
		}
		
		return resultPath;
	}
}
