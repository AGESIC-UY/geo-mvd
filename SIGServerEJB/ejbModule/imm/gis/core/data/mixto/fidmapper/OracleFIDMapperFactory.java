package imm.gis.core.data.mixto.fidmapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.geotools.data.jdbc.fidmapper.FIDMapper;

public class OracleFIDMapperFactory extends UnWrappedFIDMapperFactory {
	private static Logger log = Logger.getLogger(OracleFIDMapperFactory.class);
	private String sequence = null;
	
    protected FIDMapper buildNoPKMapper(String schema, String tableName,
            Connection connection) {
        return super.buildNoPKMapper(schema, tableName, connection);
    }

    protected FIDMapper buildLastResortFidMapper(String schema,
            String tableName, Connection connection, ColumnInfo[] colInfos) {
    	return null;
    }
    
    protected FIDMapper buildSingleColumnFidMapper(String schema, String tableName, Connection connection, ColumnInfo ci) {
    	boolean find = false;
    	ResultSet rs = null;
    	String sql = null;
    	String _sequenceName = (sequence == null) ? 
    							tableName.toUpperCase() + "_" + ci.getColName().toUpperCase() + "_SEQ" : 
    							sequence;
    	
    	// Lo que hago primero es determinar si tiene una secuencia o no.
    	// Para ello asumo que si existe una sequencia de la forma
		// nombreTabla_nombreColumna_seq, entonces el valor del proximo
		// id se saca de dicha columna.
    	
    	try {
    		sql = "SELECT SEQUENCE_NAME FROM ALL_SEQUENCES WHERE SEQUENCE_NAME = '" + _sequenceName;
    		if (schema != null) sql = sql + "' AND SEQUENCE_OWNER = '" + schema.toUpperCase();
    		sql = sql + "'";
    		
       	Statement statement = connection.createStatement();
        	
    		rs = statement.executeQuery(sql);
    		
			find = rs.next();
		}
    	catch (SQLException e) {
			e.printStackTrace();
		}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	finally {
    		if (rs != null) {
    			try {
					rs.close();
				}
    			catch (SQLException e) {
					e.printStackTrace();
				}
    		}
    	}

    	if (find){
			_sequenceName = (schema != null) ? schema+"."+ _sequenceName:_sequenceName;
    		log.debug("tabla " + tableName+ ", FIDMapper con la secuencia " + _sequenceName);
    		return new OracleFIDMapper(tableName, ci.getColName(), ci.getDataType(), _sequenceName);
    	} else {
    		log.debug("No se encontro secuencia " + _sequenceName + " para tabla "+ tableName+", se crea fid mapper por columna " + ci.getColName());
    		return super.buildSingleColumnFidMapper(schema, schema+"."+tableName, connection, ci);
    	}
    }

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
}
