package imm.gis.core.data.mixto.attributeio;

import imm.gis.core.feature.ExternalAttribute;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.geotools.data.DataSourceException;

/**
 * AttributeIO para convertir de tipos JDBC a ExternalAttribute y viceversa
 * 
 * @author agrassi
 *
 */
public class ExternalAttributeIO extends ReferencingAttributeIO {

	private String referencedFID;
	
	public ExternalAttributeIO(String originLayer) {
		super(originLayer);
	}
	
	protected String getReferencedFID() {
		return referencedFID;
	}
	
	public void setReferencedFID(String referencedFID) {
		this.referencedFID = referencedFID;
	}

	public Object read(ResultSet rs, int position) throws IOException {
        try {
        	return new ExternalAttribute(getReferencedFID(), rs.getObject(position), getReferencedLayer());
        }
        catch (SQLException e) {
            throw new DataSourceException("Sql problem.", e);
        }
	}

	public void write(ResultSet rs, int position, Object value)
			throws IOException {
        try {
        	ExternalAttribute lv = (ExternalAttribute) value;
        	
            if (value == null) {
                rs.updateNull(position);
                rs.updateNull(position+1);
            }
            else {
            	rs.updateObject(position,lv.getExternalFID());
            	rs.updateObject(position+1,lv.getValue());
            }
        }
        catch (Exception e) {
            throw new DataSourceException("Sql problem.", e);
        }
	}

	public void write(PreparedStatement ps, int position, Object value)
			throws IOException {
        try {
        	ExternalAttribute lv = (ExternalAttribute) value;
        	
            if (value == null) {
                ps.setNull(position, ps.getMetaData().getColumnType(position));
                ps.setNull(position+1, ps.getMetaData().getColumnType(position + 1));
            }
            else {
                ps.setObject(position, lv.getExternalFID());
                ps.setObject(position+1,lv.getValue());
            }
        }
        catch (Exception e) {
            throw new DataSourceException("Sql problem.", e);
        }   
	}
}
