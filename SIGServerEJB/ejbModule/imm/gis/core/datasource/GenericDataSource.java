package imm.gis.core.datasource;

import imm.gis.core.layer.definition.DataSourceDefinition;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class GenericDataSource implements IDataSource {

	protected DataSource ds;

	private boolean closed = false;

	public GenericDataSource(DataSourceDefinition od) {
		Context ctx;
		try {
			ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:" + od.getJNDIName());
			
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void close() {
		closed = true;

	}

	public Connection getConnection() throws SQLException {
		closed = false;
		return ds.getConnection();
	}

	public boolean isClosed() {
		return closed;
	}

}
