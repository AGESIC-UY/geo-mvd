package imm.gis.core.data.mixto;

import imm.gis.core.feature.Util;

import java.io.IOException;

import org.geotools.data.jdbc.FilterToSQL;
import org.geotools.filter.FilterCapabilities;
import org.geotools.filter.SQLEncoderOracle;
import org.opengis.filter.Id;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class GeomFilterToOracleSQL extends FilterToSQL {
	private GeometryFactory geometryFactory = new GeometryFactory();
	private Envelope envelope = new Envelope();
	private Geometry geom;
		
	//BBOX, Beyond, Contains, Crosses, Disjoint, DWithin, Equals, Intersects, Overlaps,
	//Touches, Within		

	@Override
    public Object visit(BBOX filter, Object extraData) {
		try {
			
			envelope.init(
					filter.getMinX(),
					filter.getMaxX(),
					filter.getMinY(),
					filter.getMaxY()
			);
			
			geom = geometryFactory.toGeometry(envelope);
			
			out.write("SDO_RELATE(");
			out.write(filter.getPropertyName());
            out.write(",");
            
			out.write(SQLEncoderOracle.toSDOGeom(geom, Util.SRID));

//            out.write("SDO_GEOMETRY('POLYGON ((");
//            out.write(filter.getMinX() + " " + filter.getMinY() + ",");
//            out.write(filter.getMinX() + " " + filter.getMaxY() + ",");
//            out.write(filter.getMaxX() + " " + filter.getMaxY() + ",");
//            out.write(filter.getMaxX() + " " + filter.getMinY() + ",");
//            out.write(filter.getMinX() + " " + filter.getMinY() + "))'");
//            out.write(", " + Util.SRID + ")");
            
            out.write(",'mask=anyinteract querytype=WINDOW') = 'TRUE' ");
		} catch (IOException e) {
            throw new RuntimeException(IO_ERROR, e);
		}
		
		return extraData;
    }

	@Override
    protected Object visitBinarySpatialOperator(BinarySpatialOperator geomFilter, Object extraData) {
    	String mask;
    	
    	if (Contains.class.isAssignableFrom(geomFilter.getClass())){
    		mask = "contains";
    	} else if (Crosses.class.isAssignableFrom(geomFilter.getClass())){
    		mask = "overlapbydisjoint";
    	} else if (Equals.class.isAssignableFrom(geomFilter.getClass())){
    		mask = "equal";
    	} else if (Overlaps.class.isAssignableFrom(geomFilter.getClass())){
    		mask = "overlapbyintersect";
    	} else if (Touches.class.isAssignableFrom(geomFilter.getClass())){
    		mask = "touch";
    	} else if (Within.class.isAssignableFrom(geomFilter.getClass())){
    		mask = "inside";
    	} else if (Disjoint.class.isAssignableFrom(geomFilter.getClass())){
    		mask = "anyinteract";
    	} else if (Intersects.class.isAssignableFrom(geomFilter.getClass())){
    		mask = "anyinteract";
    	} else{
            throw new RuntimeException("Filtro espacial " + geomFilter + " no implementado...");    		
    	}
      	
        PropertyName attExpr;
        Literal geomExpr;
	
        Expression left = geomFilter.getExpression1();
        Expression right = geomFilter.getExpression2();
        
        if (PropertyName.class.isAssignableFrom(left.getClass()) &&
        	Literal.class.isAssignableFrom(right.getClass())){
        	attExpr = (PropertyName)left;
        	geomExpr = (Literal)right;
        } else if (Literal.class.isAssignableFrom(left.getClass()) &&
        			PropertyName.class.isAssignableFrom(right.getClass())){
        	geomExpr = (Literal)left;
        	attExpr = (PropertyName)right;        	
        } else {
        	throw new RuntimeException("Oracle currently supports one geometry and one " +
                    "attribute expr.  You gave: " + left + ", " + right);
        }

        if ((attExpr != null) && (geomExpr != null) && (mask != null)) {
        	throw new RuntimeException("Invalid Filter, geometry with only one expression");
        }
        
        try {
			out.write("SDO_RELATE(");
            attExpr.accept(this, null);
            out.write(",");
            geomExpr.accept(this, null);
            // for disjoint we ask for no interaction, anyinteract == false
            if(Disjoint.class.isAssignableFrom(geomFilter.getClass())) {
                out.write(",'mask=" + mask + " querytype=WINDOW') <> 'TRUE' ");
            } else {
                out.write(",'mask=" + mask + " querytype=WINDOW') = 'TRUE' ");
            }
		} catch (IOException e) {
            throw new RuntimeException(IO_ERROR, e);
		}
        
        return extraData;
    }

	@Override
    protected FilterCapabilities createFilterCapabilities() {
		FilterCapabilities fc = super.createFilterCapabilities();
		fc.addType(BBOX.class);
		fc.addType(Contains.class);
		fc.addType(Crosses.class);
		fc.addType(Equals.class);
		fc.addType(Overlaps.class);
		fc.addType(Touches.class);
		fc.addType(Within.class);
		fc.addType(Disjoint.class);
		fc.addType(Intersects.class);
		
		fc.addType(PropertyIsLike.class);
		return fc;
    }
	
	//TODO: Este metodo esta corregido en la version 2.5 de geotools, si se usa 2.5 eliminar este override
	@SuppressWarnings("unchecked")
	@Override
    public Object visit(Id filter, Object extraData) {
        if (mapper == null) {
            throw new RuntimeException(
                "Must set a fid mapper before trying to encode FIDFilters");
        }

        FeatureId[] fids = (FeatureId[]) filter.getIdentifiers().toArray(new FeatureId[0]);

        // prepare column name array
        String[] colNames = new String[mapper.getColumnCount()];

        for (int i = 0; i < colNames.length; i++) {
            colNames[i] = mapper.getColumnName(i);
        }

        for (int i = 0; i < fids.length; i++) {
            try {
                Object[] attValues = mapper.getPKAttributes(fids[i].getID());

                out.write("(");

                for (int j = 0; j < attValues.length; j++) {
                    out.write( escapeName(colNames[j]) );
                    out.write(" = '");
                    out.write(attValues[j].toString()); //DJB: changed this to attValues[j] from attValues[i].
                    out.write("'");

                    if (j < (attValues.length - 1)) {
                        out.write(" AND ");
                    }
                }

                out.write(")");

                if (i < (fids.length - 1)) {
                    out.write(" OR ");
                }
            } catch (java.io.IOException e) {
                throw new RuntimeException(IO_ERROR, e);
            }
        }
        
        return extraData;
    }

}
