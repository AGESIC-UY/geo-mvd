package imm.gis.core.controller;

import imm.gis.core.interfaces.CoordinateListener;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.FeatureReader;
import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ContSnap implements CoordinateListener {
	private ContPpal contPpal;
	private ContMapa contMapa;
	private GeometryFactory geometryFactory;
	private double bufferSize = 8.0;
	private Feature snapFeature = null;
	private Coordinate snapCoordinate = null;
	private Coordinate actualCoordinate;
	
	public ContSnap(ContPpal p, ContMapa c){
		contPpal = p;
		contMapa = c;
		geometryFactory = new GeometryFactory();
	}
	
	public void makeSnap(boolean snapAllLayers) {
		if (!contPpal.getContMousePanelDibujo().isMouseDown()) {
			snapCoordinate = null;
			return;
		}
		
		Geometry point = geometryFactory.createPoint(actualCoordinate); 
		Geometry buffer = point.buffer(bufferSize);
		
		try{
			java.awt.Graphics2D g = (java.awt.Graphics2D)contPpal.getPanelDibujo().getGraphics();
			g.setColor(Color.GRAY);
			g.draw(worldToPixel(buffer.getEnvelopeInternal()));
			
			String layers[] = (snapAllLayers) ? contPpal.getModel().getNotEmptyTypes() : contPpal.getModel().getNotEmptyEditableTypes();
			
			List snapFeatures = getClosestFeaturesFromLayers(point, buffer, layers);
			
			getClosestVertexFromFeatures(point, buffer, snapFeatures);
			
			if (snapCoordinate != null){ 
				flashCoordinate(g, snapCoordinate);				
			}
		} catch (Exception ex){
			snapFeature = null;
			snapCoordinate = null;
			ex.printStackTrace();
		}
	}

	private List getClosestFeaturesFromLayers(Geometry point, Geometry buffer, String layers[]) {
		FeatureReader fr = null;
		ArrayList<Feature> list = new ArrayList<Feature>();
		
		for (int i = 0; i < layers.length; i++){
			try{
				fr = ContUtils.elementsAt(contPpal.getModel().getDataStore(), layers[i], buffer);
				
				while (fr.hasNext()) {
					list.add(fr.next());
				}
				
				fr.close();				
			}
			catch (Exception e) {
				e.printStackTrace();
				if (fr != null) {
					try {
						fr.close();
					}
					catch(Exception e1) {
					}
				}
			}
		}
		
		return list;
	}
	
	private void getClosestVertexFromFeatures(Geometry point, Geometry buffer, List features){
		Coordinate points[];
		Geometry g = null;
		double distance = Double.MAX_VALUE, tmp;
		Coordinate closestCoord = null;
		Feature closestFeature = null;
		Feature f;
		
		for (Iterator it = features.iterator(); it.hasNext();){
			f = (Feature)it.next();
			points = f.getDefaultGeometry().getCoordinates();
			for (int i = 0; i < points.length; i++){
				g = geometryFactory.createPoint(points[i]);
				tmp = point.distance(g);
				if (buffer.contains(g) && (tmp < distance)){
					distance = tmp;
					closestCoord = points[i];
					closestFeature = f;
				}
			}
		}
		
		snapFeature = closestFeature;
		snapCoordinate = closestCoord;
	}

	public void flashCoordinate(java.awt.Graphics2D g, Coordinate c){
		Point2D point = contMapa.worldToPixel(c);

		g.setColor(Color.RED);
		g.fillOval(Math.round((int)point.getX()) - 4, (int)Math.round(point.getY()) - 4, 8, 8);
	}

	public Coordinate getSnapCoordinate() {
		return snapCoordinate;
	}

	public Feature getSnapFeature() {
		return snapFeature;
	}	
	
	private Ellipse2D worldToPixel(Envelope r){
		Point2D pointMin = contMapa.worldToPixel(new Coordinate(r.getMinX(), r.getMinY()));
		Point2D pointMax = contMapa.worldToPixel(new Coordinate(r.getMaxX(), r.getMaxY()));

		Envelope e = new Envelope(pointMin.getX(), pointMax.getX(), pointMin.getY(), pointMax.getY());

		return new Ellipse2D.Double(e.getMinX(), e.getMinY(), e.getWidth() , e.getHeight());
	}

	public double getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(double bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void resetSnap() {
		snapCoordinate = null;
	}

	public void coordinateChanged(Coordinate c) {
		actualCoordinate = c;
	}
}
