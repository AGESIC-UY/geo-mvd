package imm.gis.core.feature;


import org.geotools.feature.AttributeType;
import org.geotools.feature.type.GeometricAttributeType;
import org.geotools.feature.type.NumericAttributeType;
import org.geotools.feature.type.TemporalAttributeType;
import org.geotools.feature.type.TextualAttributeType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.expression.Expression;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;



/**
 * Implementaci�n de AttributeTypeFactory para tener en cuenta
 * los feature de tipe Lov
 * @author agrassi
 *
 */
public class MixtoAttributeTypeFactory {

    
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
	private static CoordinateReferenceSystem  crs  = null;
	private static FilterFactory filterFactory = new FilterFactoryImpl();

	static{
		CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
		
		try {
			crs =  crsFactory.createFromWKT(UTM_WKT);
		} catch (FactoryException e) {
			e.printStackTrace();
		}

	}
	
	public static AttributeType newAttributeType(String name, Class<?> clazz,
            boolean isNillable,int fieldLength,Object defaultValue) {
            return createAttributeType(name, clazz, isNillable,fieldLength, defaultValue);
        }
    
    protected static AttributeType createAttributeType(String name, Class<?> clazz, 
            boolean isNillable, int fieldLength, Object defaultValue) {

            PropertyIsLessThanOrEqualTo ple = null;
        	
        		ple = filterFactory.lessOrEqual(
        				filterFactory.function("LengthFunction", Expression.NIL), 
        				filterFactory.literal(fieldLength)
        		);
                    	
            if (Number.class.isAssignableFrom(clazz))
                return new NumericAttributeType(
                    name, clazz, isNillable,1,1,defaultValue,ple);
            else if (CharSequence.class.isAssignableFrom(clazz))
                return new TextualAttributeType(name,isNillable,1,1,defaultValue,ple);
            else if (java.util.Date.class.isAssignableFrom(clazz))
                return new TemporalAttributeType(name,isNillable,1,1,defaultValue,ple);
            else if (Geometry.class.isAssignableFrom( clazz ))
                return new GeometricAttributeType(name,clazz,isNillable,1,1, defaultValue,crs,ple);
            else if (ExternalAttribute.class.isAssignableFrom( clazz ))
            	return new ExternalAttributeType(name, clazz, isNillable, 1, 1, defaultValue, ple);
            else if (FeatureReferenceAttribute.class.isAssignableFrom(clazz))
            	return new FeatureReferenceAttributeType(name, clazz, isNillable, 1, 1, defaultValue, ple);
            return new MixtoDefaultAttributeType(name, clazz, isNillable,1,1,defaultValue,ple);
        }
}
