package imm.gis.core.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public interface IProxyDataSource extends IDataSource {
	public Connection getConnection(String prxUsr,String prxPwd) throws SQLException;
}
