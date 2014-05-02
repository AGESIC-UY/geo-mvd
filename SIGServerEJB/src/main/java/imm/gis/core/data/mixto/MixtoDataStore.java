package imm.gis.core.data.mixto;

import imm.gis.IAppManager;
import imm.gis.core.datasource.IDataSource;
import imm.gis.core.layer.Layer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureListenerManager;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureWriter;
import org.geotools.data.InProcessLockingManager;
import org.geotools.data.LockingManager;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.data.jdbc.JDBCTransactionState;
import org.geotools.data.view.DefaultView;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.SchemaException;
import org.opengis.filter.Filter;
import org.geotools.filter.IllegalFilterException;

public class MixtoDataStore implements DataStore {
	
	private LockingManager lockingManager = new InProcessLockingManager();
	public FeatureListenerManager listenerManager = new FeatureListenerManager();
	private MixtoSQLBuilder sqlBuilder;
	
	private IAppManager currentApp;
	
	private static Logger log = Logger.getLogger(MixtoDataStore.class.getName());

	public MixtoDataStore(IAppManager app){
		this(app, false, false);
	}
	
	public MixtoDataStore(IAppManager app, boolean WKB, boolean b){
		sqlBuilder = new MixtoSQLBuilder(app);
		sqlBuilder.setWKBEnabled(WKB);
		sqlBuilder.setByteEnabled(b);	
		this.currentApp = app;
	}

	public String[] getTypeNames() {
		return this.currentApp.getTypeNames();
	}

	public FeatureType getSchema(String typeName) throws IOException {
		return this.currentApp.getLayer(typeName).getFt();
	}

	public FeatureSource getView(Query query) throws IOException,
			SchemaException {
		return new DefaultView(this.getFeatureSource(query.getTypeName()),
				query);
	}

	public FeatureSource getFeatureSource(String typeName) throws IOException {
		return new MixtoFeatureSource(this, this.currentApp.getLayer(typeName));
	}

	
	public FeatureReader getFeatureReader(Query query, Transaction transaction)
			throws IOException {
		String capa = query.getTypeName();
		
		log.debug("Ejecutando consulta para capa " + capa);
		
		FilasResultado fr = null;
		
		try {
			fr = executeQuery(query, transaction);
			log.debug("Devuelvo feature reader...");
		} catch (Exception e) {
			if (fr != null){
				fr.close();
			}
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}

		return new MixtoFeatureReader(fr);
	}

	
	private FeatureWriter getFeatureWriter(Query query, Transaction transaction)
			throws IOException {

		FilasResultado fr = null;
		
		try {
			fr = executeQuery(query, transaction);
		} catch (Exception e) {
			if (fr != null) fr.close();
			throw new IOException(e.getMessage());
		}

		return new MixtoFeatureWriter(fr);
	}
	

	public FeatureWriter getFeatureWriter(String type, Transaction t) throws IOException {
		return getFeatureWriter(type, Filter.EXCLUDE, t);
	}

	
	public FeatureWriter getFeatureWriter(String type, Filter f, Transaction t) throws IOException {
		return getFeatureWriter(new DefaultQuery(type, f), t);
	}

	
    public FeatureWriter getFeatureWriterAppend(String typeName, Transaction transaction) throws IOException {
        
    	FeatureWriter writer = getFeatureWriter(typeName, Filter.INCLUDE, transaction);

        while (writer.hasNext())
            writer.next(); 

        return writer;
    }
    
    
    public FeatureWriter getFeatureWriterAppend(String typeName, Filter f, Transaction transaction) throws IOException {
        
    	FeatureWriter writer = getFeatureWriter(typeName, f, transaction);

        while (writer.hasNext())
            writer.next(); 

        return writer;
    }

    
	public String[] createNames(FeatureType ft) {
		AttributeType todos[] = ft.getAttributeTypes();

		String[] names = new String[todos.length];

		for (int i = 0; i < todos.length; i++)
			names[i] = todos[i].getLocalName();

		return names;
	}

    private FilasResultado executeQuery(Query q, Transaction t)
			throws FilterToSQLException, SQLException, DataSourceException, IOException, IllegalFilterException {
    	
    	String wantedAttributes[];
    	
    	String typeName = q.getTypeName();
    	
    	Layer layer = this.currentApp.getLayer(typeName);
    	
		if (q.getPropertyNames() == null) {
			wantedAttributes = createNames(layer.getFt());
			
			DefaultQuery dq = new DefaultQuery();
			
			dq.setTypeName(q.getTypeName());
			dq.setFilter(q.getFilter());
			dq.setPropertyNames(wantedAttributes);
			dq.setHandle(q.getHandle());
			dq.setMaxFeatures(q.getMaxFeatures());
			dq.setCoordinateSystem(q.getCoordinateSystem());
			dq.setCoordinateSystemReproject(q.getCoordinateSystemReproject());

			q = dq;
		}
		
		
		SQLBuilderResult builderResult = sqlBuilder.buildSQLQuery(q);
		log.debug("Consulta: " + builderResult.getSQL());

		log.debug("Obteneniendo conexion...");
		Connection conn = getConnection(t, typeName);
		
		
		Statement stmt = conn.createStatement();
		log.debug("ejecutando...");
		ResultSet rs = stmt.executeQuery(builderResult.getSQL());
		log.debug("consulta ejecutada...");
		FilasResultado fr = new FilasResultado(layer.getFt(), rs, stmt, q.getPropertyNames(), builderResult.getLayerIO(), this, conn, t);

		return fr;
	}


    public void executeQueryUpdate(Feature ft, Connection conn) throws IOException, SQLException {
		
		String consulta = sqlBuilder.buildSQLQueryUpdate(ft);
		
		log.debug("Consulta: "+consulta);

		Statement stmt = conn.createStatement();
		
		try {
			stmt.execute(consulta);
		} catch (SQLException e) {
			if (!e.getSQLState().equals("02000")) {
				e.printStackTrace();
				throw e;
			}
		}
	}

	public void executeQueryInsert(Feature ft, Connection conn) throws IOException, SQLException {
		
	   	//Capa capa = this.currentApp.getCapa(ft.getFeatureType().getTypeName());
	   	
		String consulta = sqlBuilder.buildSQLQueryInsert(ft);

		log.debug(consulta);
		
		Statement stmt = conn.createStatement();
		
		try {
			stmt.execute(consulta);
		} catch (SQLException e) {
			
			if (!e.getSQLState().equals("02000"))
				throw e;
		}
	}

	public void executeQueryRemove(Feature ft, Connection conn) throws SQLException {
	   	
		Statement stmt = null;
		
		try {
			String consulta = sqlBuilder.buildSQLQueryRemove(ft);
			
			log.debug(consulta);

			stmt = conn.createStatement();
			
			stmt.executeQuery(consulta);
		}
		catch (IOException ioe) {
			throw new SQLException(ioe.getLocalizedMessage());
		}
		catch (SQLException e) {
			if (!e.getSQLState().equals("02000"))
				throw e;
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	public Connection getConnection(Transaction t, String type) throws IOException {
		
		if (t != Transaction.AUTO_COMMIT) {
			log.debug("transaccion especificada...");
			JDBCTransactionState state = (JDBCTransactionState) t.getState(this);

			if (state == null) {
				try {
                    Connection conn = createConnection(type);
                    conn.setAutoCommit(false);

					state = new JDBCTransactionState(conn);
					t.putState(this, state);
				} catch (SQLException eep) {
					throw new DataSourceException("Connection failed:" + eep, eep);
				}
			}
			
			return state.getConnection();
		}

		try {
			return createConnection(type);
		} catch (SQLException sqle) {
			throw new DataSourceException("Connection failed:" + sqle, sqle);
		}
	}
	
	
	
	private Connection createConnection(String typeName) throws SQLException {
		IDataSource ids = currentApp.getDataSource(typeName);
		log.debug("Pido conexion al pool...");
		Connection conn = ids.getConnection();
		log.debug("Conexion obtenida...");
		
		return conn;
	}
	
	
	public LockingManager getLockingManager() {
		return this.lockingManager;
	}

	
	public MixtoSQLBuilder getSQLBuilder() {
		return sqlBuilder;
	}

	
	public Connection getConnection(String nombreCapa) throws SQLException {
		return this.currentApp.getDataSource(nombreCapa).getConnection();
	}
	
	public int getSourceType(String layer){
		return currentApp.getLayerDefinition(layer).getDataOriginDefinition().getDataSourceDefinition().getType();
	}
	
	public boolean isWKBEnabled() {
		return sqlBuilder.isWKBEnabled();
	}

	
	public boolean isByteEnabled() {
		return sqlBuilder.isByteEnabled();
	}

	
	public void setByteEnabled(boolean byteEnabled) {
		sqlBuilder.setByteEnabled(byteEnabled);
	}

	
	public void setWKBEnabled(boolean enabled) {
		sqlBuilder.setWKBEnabled(enabled);
	}

	
	public void createSchema(FeatureType featureType) throws IOException {
		throw new UnsupportedOperationException("Operacion no implementada");
	}

	
	public void updateSchema(String typeName, FeatureType featureType)
			throws IOException {
		throw new UnsupportedOperationException("Operacion no implementada");
	}

	
	private void closeAllConnections() {
		String layers[] = null;
		
		layers = getTypeNames();
		
		IDataSource ds;
		
		for (int i = 0; i < layers.length; i++){
			ds = this.currentApp.getDataSource(layers[i]);

			try {
				if (!ds.isClosed())
					ds.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void dispose() {
		closeAllConnections();
	}
}
