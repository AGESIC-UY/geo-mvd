package imm.gis.core.controller;

import imm.gis.core.feature.Util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.text.NumberFormat;

import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureReader;
import org.geotools.data.Transaction;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ContUtils {
	static private FilterFactory2 filterFactory;
	static private NumberFormat numberFormat = null;
	static private Toolkit toolkit = null;
	static private double pixelSize; // En unidades del mapa (en este caso metros)
	static Logger logger = Logger.getLogger(ContUtils.class);
	public static double dpi ;
	
	static{
		filterFactory = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
		numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(1);
		toolkit = Toolkit.getDefaultToolkit();

		try{
			dpi = toolkit.getScreenResolution();
			pixelSize = 0.0254 / dpi;
		
		} catch (Exception e){
			dpi =25.4 / 0.28;
			e.printStackTrace();
			pixelSize = 0.00028;
		}finally{
			logger.info("Resolution = "+ toolkit.getScreenResolution()+" PixelSize ="+pixelSize);
		}
	}
	
	static public FeatureReader elementsAt(DataStore ds, String schema, Geometry point) {
		FeatureReader fr = null;
		try {
			FeatureType ft = ds.getSchema(schema);

			Filter gf = filterFactory.intersects(
					filterFactory.property(ft.getDefaultGeometry().getLocalName()),
					filterFactory.literal(point)
			);

			DefaultQuery q = new DefaultQuery(schema, gf);
			fr = ds.getFeatureReader(q, Transaction.AUTO_COMMIT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fr;
	}
	
	static public Coordinate pixelToWorld(double x, double y, Envelope mapExtent, Dimension screenSize){
		try {
	        double scaleX = screenSize.getWidth() / mapExtent.getWidth();
	        double scaleY = screenSize.getHeight() / mapExtent.getHeight();

	        double tx = -mapExtent.getMinX() * scaleX;
	        double ty = (mapExtent.getMinY() * scaleY) + screenSize.getHeight();

	        AffineTransform at = new AffineTransform(scaleX, 0.0d, 0.0d, -scaleY,
	                tx, ty);
			java.awt.geom.Point2D point = at.inverseTransform(
					new java.awt.geom.Point2D.Double(x, y), null);
			
			return new Coordinate(point.getX(), point.getY());
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return null;		
	}

	static public Envelope centerAt(Coordinate center, Dimension screenSize, long scale) {
		
		double widthInMapUnits = screenSize.width * pixelSize * scale;
		double heightInMapUnits = screenSize.height * pixelSize * scale;
		
		Coordinate upperLeft = new Coordinate(center.x - widthInMapUnits/2, center.y - heightInMapUnits/2);
		Coordinate lowerRight = new Coordinate(center.x + widthInMapUnits/2, center.y + heightInMapUnits/2);
		
		return new Envelope(upperLeft, lowerRight);
		
	/*	RendererUtilities.wo
		RendererUtilities.createMapEnvelope(new Rectangle(screenSize), AffineTransform, CoordinateReferenceSystem)
		*/
	}
	
	static public long getScale(Envelope mapExtent, Dimension screenSize) {
		return Math.round(mapExtent.getWidth() / (screenSize.getWidth() * pixelSize));
	}
	
	
	static public double scaleToWorld(double distance, Envelope mapExtent, Dimension screenSize) {
		return distance * pixelSize * getScale(mapExtent, screenSize);
	}
	
	static public java.awt.geom.Point2D worldToPixel(Coordinate c, Envelope mapExtent, Dimension screenSize) {
		
        double scaleX = screenSize.getWidth() / mapExtent.getWidth();
        double scaleY = screenSize.getHeight() / mapExtent.getHeight();

        double tx = -mapExtent.getMinX() * scaleX;
        double ty = (mapExtent.getMinY() * scaleY) + screenSize.getHeight();

        AffineTransform at = new AffineTransform(scaleX, 0.0d, 0.0d, -scaleY,
                tx, ty);
		
		return at.transform(new java.awt.geom.Point2D.Double(c.x, c.y), null);			
	}
	
	
	
	static public Envelope calcBbox(Envelope bbox, Coordinate begin, Coordinate end, double tolerance) {
		Coordinate newBegin;
		Coordinate newEnd;
		
		if (begin.distance(end) > tolerance){
			Envelope userEnvelope = new Envelope(begin, end);
			double bboxWidth = bbox.getWidth();
			double bboxHeight = bbox.getHeight();
			double relationBbox = bboxWidth / bboxHeight;
			
			Coordinate tmp[] = getCoordinatesForEnvelope(begin, end);
			begin = tmp[0];
			end = tmp[1];
			double userWidth = userEnvelope.getWidth();
			double userHeight = userEnvelope.getHeight();
			Coordinate userCenter = new Coordinate(begin.x + userWidth/2, begin.y + userHeight/2);
			
			double newHeight = userWidth / relationBbox; //nueva altura si ajusto por el ancho
			double newWidth = userHeight * relationBbox; //nuevo ancho si ajusto por la altura
		
			//Determino para cual de las 2 dimensiones mantengo la del usuario  
			if (newHeight > userHeight || newWidth < userWidth)
				newWidth =  userWidth;
			else
				newHeight = userHeight;
			
			newBegin = new Coordinate(userCenter.x - newWidth/2, userCenter.y - newHeight/2);
			newEnd = new Coordinate(userCenter.x + newWidth/2, userCenter.y + newHeight/2);	
		} else {
			newBegin = new Coordinate(begin.x - bbox.getWidth()/4, begin.y - bbox.getHeight()/4);
			newEnd = new Coordinate(end.x + bbox.getWidth()/4, end.y + bbox.getHeight()/4);					
		}

		return new Envelope(newBegin, newEnd);
	}
	
	static public Coordinate[] getCoordinatesForEnvelope(Coordinate begin, Coordinate end){
		Coordinate p1, p2;

		if (end.x > begin.x){
			if (end.y > begin.y){
				p1 = begin;
				p2 = end;
			}
			else {
				p1 = new Coordinate(begin.x, end.y);
				p2 = new Coordinate(end.x, begin.y);
			}
		}
		else {
			if (end.y > begin.y){
				p1 = new Coordinate(end.x, begin.y);
				p2 = new Coordinate(begin.x, end.y);
			}
			else {
				p1 = end;
				p2 = begin;
			}			
		}
		
		return new Coordinate[]{p1, p2};
	}	
	
	public static Envelope enfocar(Envelope bbox, Envelope nuevo) {
		return enfocar(
				bbox,
				new Coordinate(nuevo.getMinX(),nuevo.getMinY()),
				new Coordinate(nuevo.getMaxX(),nuevo.getMaxY()));
	}
	

	private static Envelope enfocar(Envelope bbox, Coordinate c1, Coordinate c2) {
		
		// Si me mandan enfocar un punto lo hago con un zoom fijo
		if (c1.equals(c2)) {
			c1.x -= 200;
			c1.y -= 200;
			c2.x += 200;
			c2.y += 200;
		}
		
		Envelope nuevo = calcBbox(bbox,c1,c2,0);
		
		
		double margenx = (nuevo.getMaxX()-nuevo.getMinX())/30;
		double margeny = (nuevo.getMaxY()-nuevo.getMinY())/30;

		return new ReferencedEnvelope(nuevo.getMinX()-margenx, nuevo.getMaxX()+margenx, nuevo.getMinY()-margeny, nuevo.getMaxY()+margeny,Util.crs);
	}

	
	static public Envelope centrar(Envelope e, Coordinate coord){
		double x, y;
		Coordinate min, max;
		
		x = coord.x - e.getWidth() / 2;
		y = coord.y - e.getHeight() / 2;
		min = new Coordinate(x, y);
		x = x + e.getWidth();
		y = y + e.getHeight();
		max = new Coordinate(x, y);
		
		return new Envelope(min, max);
	}
	
	static public String formatNumber(double n){
		return numberFormat.format(n);
	}
}
