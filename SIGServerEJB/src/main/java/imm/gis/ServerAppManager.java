package imm.gis;

import imm.gis.core.data.mixto.MixtoDataStore;
import imm.gis.core.mixto.xml.DefinicionAplicacion;
import imm.gis.core.mixto.xml.ParserApp;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class ServerAppManager {
	private HashMap<String, AppManager> appManagers = new HashMap<String, AppManager>();

	private static ServerAppManager instance = null;
	private final static String F_SEPARATOR = System
			.getProperty("file.separator");
	private static Logger logger = Logger.getLogger(ServerAppManager.class
			.getName());

	private ServerAppManager() {
	}

	public static ServerAppManager getInstance() {
		if (instance == null)
			instance = new ServerAppManager();

		return instance;
	}

	public void loadAppManager(String appID) {
		java.net.URI uri;

		try {
			String filePathStr = getXMLPath(appID);
			File fileTmp2 = new File(filePathStr);
			String uriStr = fileTmp2.toURI().toString();

			logger.info("-> Cargando la applicacion " + appID + " desde " + uriStr);
			uri = new java.net.URI(uriStr);
			ParserApp pa = new ParserApp(uri.toString() + F_SEPARATOR);
			// TODO sacar este Hardcodeo .app
			String app = appID + ".app";
			pa.load(app);
			DefinicionAplicacion da = pa.getDef();
			AppManager tmpApp = new AppManager(appID);
			tmpApp.init(da, filePathStr + F_SEPARATOR);
			MixtoDataStore mixto = new MixtoDataStore(tmpApp);
			tmpApp.setDataStore(mixto);
			appManagers.put(appID, tmpApp);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public AppManager getAppManager(String appID) {
		AppManager toReturn = null;

		if (!appManagers.containsKey(appID)) {
			loadAppManager(appID);
		}

		toReturn = appManagers.get(appID);

		return toReturn;

	}

	public static String getXMLPath(String idApp) {
		return getCurrentPath()+ F_SEPARATOR + "apps" + F_SEPARATOR + 
		idApp + F_SEPARATOR + "xml";
	}

	private static String getCurrentPath() {
		URI uri = null;
		String value;
		
		try {
			// Valor en JBoss5
			value = System.getProperty("jboss.server.config.url");
			if (value == null){
				// Valor en JBoss7+
				value = System.getProperty("jboss.server.config.dir");
			}
			
			uri = new URI(value);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return uri.getPath();
	}

	/**
	 * Elimina los recursos asociados a una aplicacion geografica.
	 * 
	 * @param appID
	 *            Id de la aplicacion
	 * @throws GisException
	 *             Lanza esta excepcion si la aplicacion no existe o <br>
	 *             no se encuentra cargada actualmente.
	 */
	public void cleanApplication(String appID) throws GisException {
		if (!this.appManagers.containsKey(appID))
			throw new GisException("Error la aplicacion " + appID
					+ "\n no se encuentra cargada actualmente");
		IAppManager aM =  appManagers.remove(appID);
		aM.dispose();

	}

}
