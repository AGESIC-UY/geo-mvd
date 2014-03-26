package imm.gis.core.data.mixto;

import imm.gis.core.data.mixto.attributeio.LayerIO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

import org.geotools.data.DataSourceException;
import org.geotools.data.Transaction;
import org.geotools.data.jdbc.JDBCUtils;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;

public class FilasResultado {
	private FeatureType featureType = null;
	private Statement statement;
	private Connection connection;
	private ResultSet resultSet;
    private boolean hasNextCalled = false;
    private boolean lastNext;
    private MixtoDataStore dataStore;
	private String[] properties;
	private Transaction transaction;
	
	private LayerIO mainLayerIO;
	

	public FilasResultado(FeatureType featureType, ResultSet resultSet, Statement statement, String properties[], LayerIO mainLayerIO,
						MixtoDataStore dataStore, Connection connection, Transaction transaction) {
		this.featureType = featureType;
		this.resultSet = resultSet;
		this.statement = statement;
		this.properties = properties;
		this.mainLayerIO = mainLayerIO;
		this.dataStore = dataStore;
		this.connection = connection;
		this.transaction = transaction;
	}
	
	public Transaction getTransaction() {
		return transaction;
	}
	
	public String[] getProperties() {
		return properties;
	}
	
	public FeatureType getFeatureType(){
		return featureType;
	}
	
	public Object read(String attributeName) throws IOException {
		Object o = mainLayerIO.read(resultSet, attributeName); 
		return o;
	}
	
	public void write(String attributeName, Object value) throws IOException {
		mainLayerIO.write(resultSet, attributeName, value);
	}
	
	
	public String readFidColumn() {
		return mainLayerIO.getActualFID();
	}
	
	
    public void next() throws IOException {
        if ((!hasNextCalled && !hasNext()) || !lastNext) {
            throw new NoSuchElementException("No feature to read, hasNext did return false");
        }

        hasNextCalled = false;
    }

    public boolean hasNext() throws IOException {
        try {
            if (!hasNextCalled) {
                hasNextCalled = true;
                lastNext = resultSet.next();
                
                if (lastNext)
                	mainLayerIO.next(resultSet);
            }
        } catch (Exception e) {
            throw new DataSourceException("Problem moving on to the next attribute", e);
        }
        return lastNext;
    }

    public void write(Feature ft) throws IOException, SQLException {
            dataStore.executeQueryUpdate(ft, connection);
    }
    
    public void remove(Feature ft) throws SQLException {
            dataStore.executeQueryRemove(ft, connection);
    }
    
    public void insert(Feature ft) throws IOException, SQLException {
            dataStore.executeQueryInsert(ft, connection);
    }
    
    public void close() {
        JDBCUtils.close(resultSet);
        JDBCUtils.close(statement);
        JDBCUtils.close(connection, this.transaction, null);
    	
        mainLayerIO.close();
        
        resultSet = null;
        statement = null;
        connection = null;
	}
}
