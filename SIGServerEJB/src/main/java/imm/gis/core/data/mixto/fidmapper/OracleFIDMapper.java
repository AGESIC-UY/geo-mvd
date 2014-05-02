package imm.gis.core.data.mixto.fidmapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import org.geotools.data.jdbc.fidmapper.AutoIncrementFIDMapper;
import org.geotools.data.jdbc.fidmapper.FIDMapper;
import org.geotools.feature.Feature;

public class OracleFIDMapper extends AutoIncrementFIDMapper implements FIDMapper {

    /**
	 * 
	 */
	private static final long serialVersionUID = -905128019104185136L;

    /** The actual name of the sequence, if we have found it */
    String sequenceName = null;
    
    public OracleFIDMapper(String tableName, String colName, int dataType, String sequenceName) {
            super(tableName, colName, dataType);
            
            this.sequenceName = sequenceName;
    }

    /**
     * Attempts to determine the FID after it was inserted, using three techniques:
     * 1. SELECT currval(pg_get_serial_sequence(...))
     * 2. SELECT currval(sequence name) <-- using other methods to get name
     * 3. SELECT fid ... ORDER BY fid DESC LIMIT 1
     */
    public String createID( Connection conn, Feature feature, Statement statement )
        throws IOException {
    	
    	String sql = null;
    	ResultSet rs = null;

        sql = "SELECT " + sequenceName + ".NEXTVAL FROM "+"dual";//tableName;
        
        try {
            rs = statement.executeQuery(sql); 
            if (rs.next() && rs.getBigDecimal(1) != null) {
                return rs.getBigDecimal(1).toString();
            }
            else {
                sequenceName = null;
            }
        }
        catch (Exception e) {
            sequenceName = null;
        } finally {
            if (rs != null) {
                try {
                        rs.close();
                }
                catch (Exception e) {
                	e.printStackTrace();
                }
            }
        }
    	
   		return findInsertedFID(conn, feature, statement);
    }

    /**
     * Our last resort method for getting the FID. 
     */
    private String findInsertedFID( Connection conn, Feature feature, Statement statement )
        throws IOException {
        String sql = "SELECT " + getColumnName() + " FROM ";
        String schema = getTableSchemaName();
        if (schema != null && !schema.equals("")) {
            sql = sql + schema + "."; 
        }
        sql = sql + getTableName() + " ORDER BY " + getColumnName()
            + " DESC LIMIT 1;"; 
        ResultSet rs = null;
        try {
            statement.execute(sql); 
            rs = statement.getResultSet();
            rs.next();
            return new Integer(Integer.parseInt(rs.getString(getColumnName()))+1).toString();
        } catch (Exception e) { //i surrender
            return super.createID(conn, feature, statement);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            	e.printStackTrace();
                //oh well
            }
        }
    }
    
    
    /**
     * Para tener en cuenta el caso de las columnas con tipo Decimal.
     */
    public Object[] getPKAttributes(String FID) throws IOException {
        if (getColumnType(0) == Types.DECIMAL)
        	return new Object[] { new Long(Long.parseLong(FID)) };
        else
        	return super.getPKAttributes(FID);
    }
}
