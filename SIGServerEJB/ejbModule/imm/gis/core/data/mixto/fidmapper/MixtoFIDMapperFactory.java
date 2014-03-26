package imm.gis.core.data.mixto.fidmapper;

import imm.gis.IAppManager;
import imm.gis.core.datasource.IDataSource;
import imm.gis.core.layer.definition.DataOriginDefinition;
import imm.gis.core.layer.definition.DataSourceDefinition;
import imm.gis.core.layer.definition.LayerDefinition;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.jdbc.fidmapper.BasicFIDMapper;
import org.geotools.data.jdbc.fidmapper.DefaultFIDMapperFactory;
import org.geotools.data.jdbc.fidmapper.FIDMapper;
import org.geotools.data.jdbc.fidmapper.FIDMapperFactory;
import org.geotools.feature.FeatureType;



public class MixtoFIDMapperFactory implements FIDMapperFactory {
	
	private IAppManager currentApp = null;
	private OracleFIDMapperFactory orafmf = new OracleFIDMapperFactory();
	private UnWrappedFIDMapperFactory pgfmf = new PostGISExtendedFIDMapperFactory();
	private DefaultFIDMapperFactory dfmf = new DefaultFIDMapperFactory();
	
	private Map<String, FIDMapper> mappersCache = new HashMap<String, FIDMapper>();
	
	
	public MixtoFIDMapperFactory(IAppManager app) {
		currentApp = app;
	}
	
	
    public FIDMapper getMapper(String catalog, String schema, String typeName,
            Connection connection) throws IOException{
    	
    	FIDMapper newMapper;
    	
    	if (mappersCache.containsKey(typeName))
    		return mappersCache.get(typeName);
    	
		LayerDefinition dc;
		DataOriginDefinition dod;
		String tableName = null;
		String _schema = null;
		
		
		dc = currentApp.getLayerDefinition(typeName);
		dod = dc.getDataOriginDefinition();
		
		tableName = dod.getTableDefinition().getName();
		_schema = dod.getTableDefinition().getSchema();
		if (tableName == null)
			throw new IOException("Couldn't find main table for feature type "+typeName);
		
		String fid = dod.getTableDefinition().getFid();
		if (fid != null){
			newMapper = new BasicFIDMapper(fid, 10);
		} else if (dod.getDataSourceDefinition().getType() == DataSourceDefinition.TYPE_POSTGIS){
			newMapper = pgfmf.getMapper(catalog, _schema, tableName, connection);
		} else if (dod.getDataSourceDefinition().getType() == DataSourceDefinition.TYPE_ORACLE){
			String sequence = dod.getTableDefinition().getSequence();
			if (sequence != null && sequence.trim().length() > 0){
				orafmf.setSequence(sequence);
			}
			newMapper = orafmf.getMapper(catalog, _schema, tableName.toUpperCase(), connection);
			orafmf.setSequence(null);
		}else{
			newMapper = dfmf.getMapper(catalog, _schema, tableName, connection);
		}

		mappersCache.put(typeName, newMapper);
		
		return newMapper;
    }

    
    
    public FIDMapper getMapper(FeatureType featureType) {
    	FIDMapper res = null;
    	
    	// Primero me fijo que no este ya en el cache
    	if (mappersCache.containsKey(featureType.getTypeName()))
    		return  mappersCache.get(featureType.getTypeName());
    	
    	// Busco una conexion para pasarle al otro getMapper
		IDataSource ifd = this.currentApp.getDataSource(featureType.getTypeName());
		Connection c = null;
		
    	try {
    		c = ifd.getConnection();
    		res = getMapper(null,null,featureType.getTypeName(), c);
    	} catch (Exception sqle) {
    		sqle.printStackTrace();
    	} finally{
    		if (c != null){
    			try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}    		
    		}
    	}
    	
    	return res;
    }    
}
