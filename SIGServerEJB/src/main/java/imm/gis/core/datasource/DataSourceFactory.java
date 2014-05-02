package imm.gis.core.datasource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import imm.gis.core.layer.definition.DataSourceDefinition;

/**
 * Se encargaria de obtener implementaciones para cada tipo
 * de fuente de datos
 * 
 * @author sig
 *
 */
public class DataSourceFactory {

	private static Logger logger = Logger.getLogger(DataSourceFactory.class.getName());

	private Map<String, IDataSource> dataSources = new HashMap<String, IDataSource>();
	
	public IDataSource createDataSource(DataSourceDefinition def) throws IOException, SQLException{
		IDataSource dataSource = null;

		if (!dataSources.containsKey(def.getJNDIName())) {
				logger.info("Cargando origen datos " + def.getJNDIName());
				dataSource = DataSourceFactory.getFuenteDatos(def);
				dataSources.put(def.getJNDIName(), dataSource);
		}
		else {
			logger.debug("Origen de datos " + def.getJNDIName() + " ya cargado...");
				
			dataSource = (IDataSource) dataSources.get(def.getJNDIName());
		}			
		
		return dataSource;
	}
	

	private static IDataSource getFuenteDatos(DataSourceDefinition def) throws IOException, SQLException {
		return def.getType()== DataSourceDefinition.TYPE_POSTGIS ? new GenericDataSource(def):new OracleDataSource(def);
	}
}
