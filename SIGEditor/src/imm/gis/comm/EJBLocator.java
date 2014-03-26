package imm.gis.comm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;

public class EJBLocator {
	private static Logger logger = Logger.getLogger(EJBLocator.class);
	private static Context context = null;
	private static Map<String, Object> ejbs = new HashMap<String, Object>();

	public static Object locateEJB(String ejbFullName) throws Exception {
		if (ejbs.containsKey(ejbFullName)){
			return ejbs.get(ejbFullName);
		}
		
		Object ejb = locateEJB3(ejbFullName);
		ejbs.put(ejbFullName, ejb);
		
		return ejb;
	}

	private static Object locateEJB3(String ejbFullName) throws Exception {
		if (context == null) {
			if (!loadContextFrom("/jeditor.jndi.properties")){
				createDefaultContext();				
			}
		}
		
		return context.lookup(ejbFullName);
	}

	public static boolean loadContextFrom(String fileName) {
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		Properties jndiProps = new Properties();

		try{
			logger.info("Cargando conf JNDI: " + fileName);
			InputStream is = EJBLocator.class.getResourceAsStream(fileName);
			jndiProps.load(is);
			is.close();
			for (Iterator iter = jndiProps.keySet().iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				Object value = jndiProps.getProperty(element);
				logger.debug(element + " = " + value);
				env.put(element, value);
			}
			if (!env.containsKey(Context.SECURITY_PRINCIPAL)){
				logger.debug(Context.SECURITY_PRINCIPAL + " = anonymous");
				env.put(Context.SECURITY_PRINCIPAL, "anonymous");
			}
			
			context = new InitialContext(env);
			return true;
		} catch (Exception e){
			logger.info("No se pudo cargar " + fileName, e);
			return false;
		}		
	}

	private static void createDefaultContext() throws NamingException {
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		logger.info("No se encontro conf JNDI, tomando jboss en localhost...");
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jboss.security.jndi.JndiLoginInitialContextFactory");
		env.put(Context.PROVIDER_URL, "localhost:1099");
		env.put("java.naming.factory.url.pkgs",
				"org.jboss.naming:org.jnp.interfaces");
		env.put(Context.SECURITY_PRINCIPAL, "anonymous");

		context = new InitialContext(env);
	}
}
