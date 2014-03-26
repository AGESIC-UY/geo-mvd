package imm.gis.core.interfaces;

import java.awt.Shape;
import java.util.Collection;

import org.geotools.feature.Feature;
import org.geotools.map.MapContext;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public interface IMap {

	public java.awt.geom.Point2D worldToPixel(Coordinate c);
	public Coordinate pixelToWorld(double x, double y);
	public double scaleToWorld(double distance);
	public Shape worldToPixel(Geometry g);
	
	public Feature getFeatureOn(Coordinate c) throws Exception;
	public Feature getFeatureOn(String type, Coordinate c) throws Exception;
	public Collection<Feature> getAllFeaturesOn(String type, Coordinate c) throws Exception;
	public Collection<Feature> getAllFeaturesOn(Coordinate c) throws Exception;
	public double getAccuracyTolerance();
	
	public void setBoundingBox(Envelope e);
	public Envelope getBoundingBox();
	
	public void center(Coordinate coordinate);
	public void center(Geometry g);
	public void addCoordinateListener(CoordinateListener listener);
	public void removeCoordinateListener(CoordinateListener listener);

	// se llama a la del model, supongo habria que pasarla al contmapa
	public MapContext getContext();
	public long getScale();
	public void setScale(long scale);
	public void refresh();
}
