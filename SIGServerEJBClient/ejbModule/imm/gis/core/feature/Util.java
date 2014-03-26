package imm.gis.core.feature;

import imm.gis.core.model.ILinePoints;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geotools.feature.Feature;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;



/**
 * Clase con operaciones varias sobre features.
 * 
 * @author agrassi
 *
 */
public class Util {

	public static final int SRID = 32721;

	private static Logger logger = Logger.getLogger(Util.class);
	
	private static String UTM_WKT = "PROJCS[\"WGS 84 / UTM zone 21S\","
		+ "GEOGCS[\"UTM zone 21S\","
		+ "DATUM[\"World Geodetic System 1984\","
		+ "SPHEROID[\"WGS 84\",6378137,298.257223563]],"
		+ "PRIMEM[\"Greenwich\",0],"
		+ "UNIT[\"DMSH\",0.0174532925199433]],"
		+ "PROJECTION[\"Transverse_Mercator\"],"
		+ "PARAMETER[\"latitude_of_origin\",0],"
		+ "PARAMETER[\"central_meridian\",-57],"
		+ "PARAMETER[\"scale_factor\",0.9996],"
		+ "PARAMETER[\"false_easting\",500000],"
		+ "PARAMETER[\"false_northing\",10000000]," + "UNIT[\"metre\",1]]";
	
	
	public static CoordinateReferenceSystem  crs  = null;
	static{
		CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
		
		try {
			crs =  crsFactory.createFromWKT(UTM_WKT);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private Util() {
	}
	

	/*
	public static Object createDefaultValue(Class cl) {
		
		if (Float.class.isAssignableFrom(cl))
			return new Float(0);
		else if (Number.class.isAssignableFrom(cl))
			return new Integer(0);
		else if (String.class.isAssignableFrom(cl))
			return "";
		else if (java.util.Date.class.isAssignableFrom(cl))
			return new java.util.Date();
		else if (ExternalAttribute.class.isAssignableFrom(cl))
			return new ExternalAttribute(new Integer(0),"");
		else
			return null;
	}
	*/
	
	
	public static void calcularExtremosLinea(Feature f) {
		
		Point p1,p2;
		double deltaX, deltaY, anguloRadianes, res; 
		LineString l;
		Geometry g;

		g = f.getDefaultGeometry();
		
		if (MultiLineString.class.isAssignableFrom(g.getClass()))
			l = (LineString) ((MultiLineString) g).getGeometryN(0);
		else
			l = (LineString) f.getDefaultGeometry();
		
		try {
			
			// Primero para el punto inicial
			
			p1 = l.getPointN(0);
			p2 = l.getPointN(1);
			
			deltaX = p1.getX() - p2.getX();
			deltaY = p1.getY() - p2.getY();
			
			f.setAttribute(ILinePoints.INIT_POINT_ATTR,p1);
			
			if (deltaX==0) {
				if (deltaY>0)
					f.setAttribute(ILinePoints.INIT_ROTATION_ATTR,new Double(270));
				else
					f.setAttribute(ILinePoints.INIT_ROTATION_ATTR,new Double(90));
			}
			else {
				
                anguloRadianes = Math.asin( deltaY / Math.sqrt ((deltaX*deltaX)+(deltaY*deltaY)));

                res = ((anguloRadianes*360)/(2*3.1416));
                
                if (deltaX<0)
                	res -= 180;
                else
                	res = -res;
                
                f.setAttribute(ILinePoints.INIT_ROTATION_ATTR,new Double(res));
			}
			
			// Ahora punto final
			
			p1 = l.getPointN(l.getNumPoints()-2);
			p2 = l.getPointN(l.getNumPoints()-1);
			
			deltaX = p1.getX() - p2.getX();
			deltaY = p1.getY() - p2.getY();
			
			f.setAttribute(ILinePoints.END_POINT_ATTR,p2);
			
			if (deltaX==0) {
				if (deltaY>0)
					f.setAttribute(ILinePoints.END_ROTATION_ATTR,new Double(90));
				else
					f.setAttribute(ILinePoints.END_ROTATION_ATTR,new Double(270));
			}
			else {
				
                anguloRadianes = Math.asin( deltaY / Math.sqrt ((deltaX*deltaX)+(deltaY*deltaY)));

                res = ((anguloRadianes*360)/(2*3.1416));
    			
                if (deltaX>0)
                	res = -(res + 180);
                
                f.setAttribute(ILinePoints.END_ROTATION_ATTR,new Double(res));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
//	private static Object calcularAtributo(Feature f, String funcion, Vector<DefinicionParametro> parametros) {
//		
//		if (funcion.equalsIgnoreCase("CONCATENACION"))
//			return calcularAtributoConcatenacion(f,parametros);
//		else
//			return null;
//	}
	
//	private static String calcularAtributoConcatenacion(Feature f, Vector<DefinicionParametro> parametros) {
//		
//		DefinicionParametro dp;
//		Iterator<DefinicionParametro> i = parametros.iterator();
//		String s = new String();
//		
//		while (i.hasNext()) {
//			dp = i.next();
//			s = s.concat(valorParametro(f,dp).toString());
//		}
//		
//		return s;
//	}
	
//	private static Object valorParametro(Feature f, DefinicionParametro dp) {
//		
//		if (dp.getTipo().equalsIgnoreCase("literal"))
//			return dp.getValor();
//		else if (dp.getTipo().equalsIgnoreCase("atributo"))
//			return f.getAttribute(dp.getValor());
//		else
//			return null;
//	}
	
	public static Map<?,?> cloneFormData(Map<?,?> data) throws IllegalAttributeException {

		Map<Object,Object> itemNuevo;

		Object currentKey = null;
		Object currentValue = null;
		Feature currentFeature = null;
		Map<?,?> currentMap = null;
		Object element2Put = null;

		Iterator<?> i = data.keySet().iterator();
		itemNuevo = new HashMap<Object,Object>();
		while (i.hasNext()) {
			currentKey = i.next();
			currentValue = data.get(currentKey);

			// Si el elemento es un feature lo copio
			if (currentValue instanceof Feature) {
				currentFeature = ((Feature) currentValue).getFeatureType()
						.duplicate(((Feature) currentValue));
				element2Put = currentFeature;
			} else {
				// Si no llamo recursivamente para cargar los map's.
				if (currentValue instanceof Map) {

					currentMap = cloneFormData((Map<?,?>) currentValue);
					element2Put = currentMap;

				} else {
					throw new IllegalAttributeException(
							"Error en la estrucutura a clonar se espera un "
									+ Map.class.toString() + "pero se obtuvo "
									+ currentValue.getClass());
				}
			}

			itemNuevo.put(currentKey, element2Put);
		}
		return itemNuevo;
	}

}
