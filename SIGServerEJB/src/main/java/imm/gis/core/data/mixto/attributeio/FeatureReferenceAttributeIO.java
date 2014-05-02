package imm.gis.core.data.mixto.attributeio;

import imm.gis.core.feature.FeatureReferenceAttribute;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.geotools.data.DataSourceException;
import org.geotools.data.jdbc.fidmapper.FIDMapper;


/**
 * AttributeIO para convertir de tipos JDBC a FeatureReferenceAttribute y viceversa
 * 
 * @author agrassi
 *
 */
public class FeatureReferenceAttributeIO extends ReferencingAttributeIO {

	private FIDMapper mapper;
	
	public FeatureReferenceAttributeIO(String referencedLayer, FIDMapper mapper) {
		super(referencedLayer);
	
		this.mapper = mapper;
	}
	
	public Object read(ResultSet rs, int position) throws IOException {
		
		Object foreignPKValues[] = new Object[mapper.getColumnCount()];
		
		try {
			for (int i = 0; i < mapper.getColumnCount(); i++)
				foreignPKValues[i] = rs.getObject(position + i);
		}
		catch (SQLException sqle) {
			throw new IOException(sqle.getLocalizedMessage());
		}
		
		return new FeatureReferenceAttribute(mapper.getID(foreignPKValues), getReferencedLayer());
	}

	public void write(ResultSet rs, int position, Object value)
			throws IOException {
        try {
        	FeatureReferenceAttribute fra = (FeatureReferenceAttribute) value;
        	
            if (value == null) {
                rs.updateNull(position);
            }
            else {
            	rs.updateObject(position,fra.getReferencedFeatureID());
            }
        }
        catch (Exception e) {
            throw new DataSourceException("Sql problem.", e);
        }
	}

	public void write(PreparedStatement ps, int position, Object value)
			throws IOException {
        try {
        	FeatureReferenceAttribute lv = (FeatureReferenceAttribute) value;
        	
            if (value == null) {
                ps.setNull(position, ps.getMetaData().getColumnType(position));
            }
            else {
                ps.setObject(position, lv.getReferencedFeatureID());
            }
        }
        catch (Exception e) {
            throw new DataSourceException("Sql problem.", e);
        }   
	}
}
