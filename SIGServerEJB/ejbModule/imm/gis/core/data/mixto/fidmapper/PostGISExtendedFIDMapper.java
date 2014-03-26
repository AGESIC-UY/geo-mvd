package imm.gis.core.data.mixto.fidmapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.geotools.data.jdbc.fidmapper.AutoIncrementFIDMapper;
import org.geotools.data.jdbc.fidmapper.FIDMapper;
import org.geotools.feature.Feature;

public class PostGISExtendedFIDMapper extends AutoIncrementFIDMapper implements FIDMapper {

    /**
	 * 
	 */
	private static final long serialVersionUID = -905128019104185136L;

	/** Indicates that the pg_get_serial_sequence function exists, and works for this table */ 
    boolean can_usepg_get_serial_sequence = true;

    /** Flag to indicate when we can't find the table's sequence */
    boolean hasSerialSequence = true;
    
    /** The actual name of the sequence, if we have found it */
    String sequenceName = null;
    
    boolean hasLookedForSequence = false;
    
    public PostGISExtendedFIDMapper(String tableName, String colName, int dataType) {
            super(tableName, colName, dataType);
    }

    /**
     * Attempts to determine the FID after it was inserted, using three techniques:
     * 1. SELECT currval(pg_get_serial_sequence(...))
     * 2. SELECT currval(sequence name) <-- using other methods to get name
     * 3. SELECT fid ... ORDER BY fid DESC LIMIT 1
     */
    public String createID( Connection conn, Feature feature, Statement statement )
        throws IOException {
    	
    	String columnDefault = null;
    	ResultSet rs = null;
    	
    	if (!hasLookedForSequence) {
        	// Lo que hago primero es determinar si tiene una secuencia o no,
        	// usando para ello el default. 
        	
	    	try {
				rs = conn.getMetaData().getColumns("", "", getTableName(), getColumnName());
				
				if (rs.next()) {
					columnDefault = rs.getString("COLUMN_DEF");
					
					if (columnDefault != null) {
						
						Scanner scanner = new Scanner(columnDefault);
						
						if (scanner.findInLine("nextval\\('(\\w+)'::regclass\\)") != null) {
							MatchResult result = scanner.match();
							
							if (result.groupCount() == 1)
								sequenceName = result.group(1);
						}
						scanner.close();
					}
				}
			}
	    	catch (SQLException e) {
				e.printStackTrace();
			}
	    	catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    	finally {
	    		hasLookedForSequence = true;
	    		if (rs != null) {
	    			try {
						rs.close();
					}
	    			catch (SQLException e) {
						e.printStackTrace();
					}
	    		}
	    	}
    	}
    	
    	if (sequenceName != null) {
            String sql = "SELECT nextval('\"" + sequenceName + "\"')";
            
            try {
                rs = statement.executeQuery(sql); 
                if (rs.next() && rs.getString("nextval") != null)
                    return rs.getString("nextval");
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
    	}
    	
   		return findInsertedFID(conn, feature, statement);
    	/*
        ResultSet rs = null;
        if (can_usepg_get_serial_sequence) {
            try {
                //use pg_get_serial_sequence('"table name"','"column name"')
                String sql = "SELECT currval(pg_get_serial_sequence('\"";
                String schema = getTableSchemaName();
                if (schema != null && !schema.equals("")) {
                    sql = sql + schema + "."; 
                }
                sql = sql + getTableName() + "\"','" + getColumnName() + "'))";
                rs = statement.executeQuery(sql); 
                if (rs.next() && rs.getString("currval") != null)
                    return rs.getString("currval");
                else {
                    can_usepg_get_serial_sequence = false;
                }
            } catch (Exception e) {
                can_usepg_get_serial_sequence = false;
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                } catch (Exception e) {
                    //oh well
                }
            }
        }
        //TODO: add logging
        if (hasSerialSequence) {
            if (sequenceName == null) {
                // try to find the sequence (this makes the assumption that
                // the sequence name contains "tableName_columnName")
                String sql = "SELECT relname FROM pg_catalog.pg_class WHERE relkind = 'S' AND relname LIKE '"
                    + getTableName() + "_" + getColumnName() + "_seq'";
                try {
                    rs = statement.executeQuery(sql);
                    if (rs.next() && rs.getString(1) != null) {
                        sequenceName = rs.getString(1);
                    } else {
                        hasSerialSequence = false;
                    }
                } catch (Exception e) {
                    hasSerialSequence = false;
                } finally {
                    try {
                        if (rs != null)
                            rs.close();
                    } catch (Exception e) {
                        //oh well
                    }
                }
            }
            
            if (sequenceName != null) {
                //get the sequence value
                String sql = "SELECT currval('\"" + sequenceName + "\"')";
                try {
                    rs = statement.executeQuery(sql); 
                    if (rs.next() && rs.getString("currval") != null)
                        return rs.getString("currval");
                    else {
                        hasSerialSequence = false;
                    }
                } catch (Exception e) {
                    hasSerialSequence = false;
                } finally {
                    try {
                        if (rs != null)
                            rs.close();
                    } catch (Exception e) {
                        //oh well
                    }
                }
            }
        }
        return findInsertedFID(conn, feature, statement);
        */
    }

    /**
     * Our last resort method for getting the FID. 
     */
    private String findInsertedFID( Connection conn, Feature feature, Statement statement )
        throws IOException {
        String sql = "SELECT \"" + getColumnName() + "\" FROM \"";
        String schema = getTableSchemaName();
        if (schema != null && !schema.equals("")) {
            sql = sql + schema + "\".\""; 
        }
        sql = sql + getTableName() + "\" ORDER BY \"" + getColumnName()
            + "\" DESC LIMIT 1;"; 
        ResultSet rs = null;
        try {
            statement.execute(sql); 
            rs = statement.getResultSet();
            rs.next();
            return rs.getString(getColumnName());
        } catch (Exception e) { //i surrender
            return super.createID(conn, feature, statement);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                //oh well
            }
        }
    }

}
