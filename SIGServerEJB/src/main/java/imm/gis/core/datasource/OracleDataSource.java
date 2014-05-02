package imm.gis.core.datasource;

import imm.gis.core.layer.definition.DataSourceDefinition;

import java.security.Principal; 
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import oracle.jdbc.OracleConnection;
import org.apache.log4j.Logger;
import org.jboss.resource.adapter.jdbc.WrappedConnection;
import org.jboss.security.SecurityAssociation;

public class OracleDataSource extends GenericDataSource implements
		IProxyDataSource {
//	private static final String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
	public static final String WEB_REQUEST_KEY = "javax.servlet.http.HttpServletRequest";
	private static Logger log = Logger
			.getLogger(OracleDataSource.class.getName());
	private boolean withProxy = false;
	
	public OracleDataSource(DataSourceDefinition od) {
		super(od);
		withProxy = od.isWithProxy();
	}

	public Connection getConnection() throws SQLException {
		if (withProxy){
			Principal callerP = SecurityAssociation.getCallerPrincipal();
			if (callerP == null){
				throw new SQLException("No existe contexto de seguridad, por favor se necesita autenticacion del usuario!!!");
			}
			//TODO SE PUSO ESTO HARDCODEO PARA PROBAR DESDE EL CLIENTE SWING DESDE EL ECLIPSE
			String proxyUsr = callerP.getName().equals("anonymous") ? "im777771": callerP.getName();
			return getConnection(proxyUsr, null);			
		} else {
			return super.getConnection();
		}
	}

	public Connection getConnection(String prxUsr, String prxPwd)
			throws SQLException {
		WrappedConnection wc = (WrappedConnection) super.getConnection();
			OracleConnection conn = (OracleConnection) wc.getUnderlyingConnection();
			log.info("proxyUser = " + prxUsr);
			if (!conn.isProxySession()) {
				Properties props = new Properties();
				props.put(OracleConnection.PROXY_USER_NAME, prxUsr);
				((OracleConnection) conn).openProxySession(
						OracleConnection.PROXYTYPE_USER_NAME, props);
			}
		
		return wc;
	}

	public boolean isWithProxy() {
		return withProxy;
	}
}
