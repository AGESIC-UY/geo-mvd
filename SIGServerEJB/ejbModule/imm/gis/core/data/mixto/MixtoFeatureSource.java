package imm.gis.core.data.mixto;

import imm.gis.core.layer.Layer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import org.apache.log4j.Logger;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultFeatureResults;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.MaxFeatureReader;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.Transaction;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.data.jdbc.JDBCUtils;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.filter.IllegalFilterException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

public class MixtoFeatureSource implements FeatureSource {
	private MixtoDataStore dataStore = null;
	private Layer capa = null;
	
	private static Logger logger = Logger.getLogger(MixtoFeatureSource.class);
	
	public MixtoFeatureSource(MixtoDataStore ds, Layer c){
		dataStore = ds;
		capa = c;
	}
	
	public DataStore getDataStore() {
		return dataStore;
	}

	public void addFeatureListener(FeatureListener listener) {
		dataStore.listenerManager.addFeatureListener(this, listener);
	}

	public void removeFeatureListener(FeatureListener listener) {
		dataStore.listenerManager.removeFeatureListener(this, listener);
	}

	public FeatureCollection getFeatures(Query request) throws IOException {
        String typeName = capa.getNombre();

        if ((request.getTypeName() != null)
                && !typeName.equals(request.getTypeName())) {
            throw new IOException("Cannot query " + typeName + " with:"
                + request);
        }

        if (request.getTypeName() == null) {
            request = new DefaultQuery(capa.getNombre(),
                    request.getFilter(), request.getMaxFeatures(),
                    request.getPropertyNames(), request.getHandle());
        }

        final Query query = request;

        return new DefaultFeatureResults(this, query) {
                /**
                 * JDBCDataStore has a more direct query method
                 *
                 * @return DOCUMENT ME!
                 *
                 * @throws IOException DOCUMENT ME!
                 */
                public FeatureReader reader() throws IOException {
                    int maxFeatures = query.getMaxFeatures();
                    FeatureReader reader = dataStore.getFeatureReader(query,
                            getTransaction());

                    if (maxFeatures == Integer.MAX_VALUE)
                        return reader;
                    else
                        return new MaxFeatureReader(reader, maxFeatures);
                }

                /**
                 * Performs optimizated count if possible.
                 *
                 * @return
                 *
                 * @throws IOException
                 *
                 * @see org.geotools.data.DefaultFeatureResults#getCount()
                 */
                public int getCount() throws IOException {
                    int count = count(query, getTransaction());

                    if (count != -1) {
                        int maxFeatures = query.getMaxFeatures();

                        return (count < maxFeatures) ? count : maxFeatures;
                    }

                    return super.getCount();
                }
            };
	}

	public FeatureCollection getFeatures(Filter filter) throws IOException {
        return getFeatures(new DefaultQuery(capa.getNombre(), filter));
	}

	public FeatureCollection getFeatures() throws IOException {
        return getFeatures(Filter.EXCLUDE);
	}

	public FeatureType getSchema() {
		return capa.getFt();
	}

	public Envelope getBounds() throws IOException {
        return getBounds(Query.ALL);
	}

	public Envelope getBounds(Query query) throws IOException {
        if (query.getFilter() == Filter.INCLUDE) {
            if(capa.getFt()!=null)
                return new ReferencedEnvelope(new Envelope(),capa.getFt().getDefaultGeometry().getCoordinateSystem());

            return new Envelope();
        }

        return null; // to expensive right now :-)
	}

	public int getCount(Query query) throws IOException {
        try {
            return count(query, Transaction.AUTO_COMMIT);
        } catch (IOException e) {
            // could not acomplish optimization
            return -1;
        }
	}

    public int count(Query query, Transaction transaction)
    throws IOException {
        Filter filter = query.getFilter();

        if (filter == Filter.INCLUDE) {
            return 0;
        }

        MixtoSQLBuilder sqlBuilder = dataStore.getSQLBuilder();

        if (sqlBuilder.getPostQueryFilter(query.getFilter()) != null) {
            // this would require postprocessing the filter
            // so we cannot optimize
            return -1;
        }

        Connection conn = null;

        try {
            String typeName = getSchema().getTypeName();
            conn = dataStore.getConnection(typeName);

            String wantedAttributes[] = dataStore.createNames(getSchema());

            String sql = "SELECT COUNT(*) as cnt FROM (SELECT ";
            
            SQLBuilderResult builderResult = sqlBuilder.sqlColumns(true, wantedAttributes, typeName, null, 1);
            String from = sqlBuilder.sqlFrom(typeName, wantedAttributes);
            String joins = sqlBuilder.sqlJoin(typeName, wantedAttributes);
            String where = sqlBuilder.sqlWhere(typeName, Filter.EXCLUDE, builderResult.getLayerIO());
            
            sql += builderResult.getSQL() + " FROM " + from;
            
            if (where.length() == 0)
    			where = joins;
    		else if (joins.length() > 0)
    			where = where + " AND " + joins;
            
            String dbProductName = conn.getMetaData().getDatabaseProductName();
            String subQuerySuffix = dbProductName=="PostgreSQL"  ? "AS FOO":"";
            sql += where.length()>0 ? " WHERE " + where + ") " +subQuerySuffix:") "+ subQuerySuffix;
            
            if (logger.isDebugEnabled())
            	logger.debug("Query : " + sql);
            
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);
            results.next();

            int count = results.getInt("cnt");
            results.close();
            statement.close();
            builderResult.getLayerIO().close();
            if (logger.isDebugEnabled())
            	logger.debug("count - Resultado: " + count);
            
            return count;
        }
        catch (SQLException sqlException) {
            JDBCUtils.close(conn, transaction, sqlException);
            conn = null;
            throw new DataSourceException("Could not count " + query.getHandle(), sqlException);
        } catch (FilterToSQLException e) {
        	logger.error(e);
            return -1;
        } catch (IllegalFilterException f){
        	logger.error(f);
        	return -1;
        } finally {
            JDBCUtils.close(conn, transaction, null);
        }
    }

	public QueryCapabilities getQueryCapabilities() {
		return null;
	}

	public Set<?> getSupportedHints() {
		return null;
	}

}
