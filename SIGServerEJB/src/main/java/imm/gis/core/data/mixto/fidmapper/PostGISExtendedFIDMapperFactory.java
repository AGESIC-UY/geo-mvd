package imm.gis.core.data.mixto.fidmapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.geotools.data.jdbc.fidmapper.FIDMapper;
import org.geotools.data.postgis.fidmapper.OIDFidMapper;

public class PostGISExtendedFIDMapperFactory extends UnWrappedFIDMapperFactory {
	private static Logger log = Logger.getLogger(PostGISExtendedFIDMapperFactory.class);

    protected FIDMapper buildNoPKMapper(String schema, String tableName,
            Connection connection) {
        if (getDatabaseMajorVersion(connection) <= 7)
            return new OIDFidMapper();
        return super.buildNoPKMapper(schema, tableName, connection);
    }

    /**
     * Retrieves Postgresql database major version number. This is used to see
     * if OID are there or not.
     * 
     * @param connection
     * @return
     */
    private int getDatabaseMajorVersion(Connection connection) {
        int major;
        try {
            major = connection.getMetaData().getDatabaseMajorVersion();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve Postgres "
                    + "database version number, assuming 7. Error is: "
                    + e.getMessage(), e);
            major = 7;
        }
        return major;
    }

    protected FIDMapper buildLastResortFidMapper(String schema,
            String tableName, Connection connection, ColumnInfo[] colInfos) {

        if (getDatabaseMajorVersion(connection) > 7)
            throw new IllegalArgumentException(
                    "Tables for postgis 8+ must have a primary key defined");

        return new OIDFidMapper();
    }
    
    protected FIDMapper buildSingleColumnFidMapper(String schema, String tableName, Connection connection, ColumnInfo ci) {
    	if (ci.isAutoIncrement()) {
    		log.debug(tableName + " se detecto como auto incremental por la columna " + ci.getColName());
            return new PostGISExtendedFIDMapper(tableName, ci.getColName(), ci.getDataType());
    	}
    	
		log.debug(tableName + " con clave por la columna " + ci.getColName());
    	return super.buildSingleColumnFidMapper(schema, schema + "." + tableName, connection, ci);
    }
    /**
     *  see@DefaultFIDMapperFactory in main module (jdbc)
     *   This version pre-double quotes the column name and table name and passes it to the superclass's version.
     */
    protected boolean isAutoIncrement(String catalog, String schema,
            String tableName, Connection conn, ResultSet tableInfo,
            String columnName, int dataType) throws SQLException 
	{
        String schemaName = null;
        if (schema != null) {
            schemaName = "\"" + schema + "\"";
        }
    	return super.isAutoIncrement( catalog, schemaName, "\""+tableName+"\"",conn, tableInfo,
    			"\""+columnName+"\"",dataType);
    }
}
