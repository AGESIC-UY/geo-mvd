package imm.gis.core.datasource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Ahora es cualquiera, es un ejemplo nomas. Tendria las operaciones
 * que usaria el FeatureReader/Writer mixto para realizar los
 * cambios en ambas bases.
 * 
 * @author sig
 *
 */
public interface IDataSource {

	public Connection getConnection() throws SQLException;
	public void close();
	public boolean isClosed();
}
