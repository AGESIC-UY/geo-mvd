package imm.gis.comm;

public class ServerServicesFactory {
	public static final String SERVICES_X_RMI = "services.rmi";
	public static final String SERVICES_CODEBASE = "services.codebase";
	
	
	static public IServerServices getServerServices(){
		String services = System.getProperty(SERVICES_X_RMI);
		
		return (services == null) ? getHttpServerServices() : getRemoteServerServices();
	}
	
	@SuppressWarnings("unused")
	static private IServerServices getLocalServerServices(){
		return null;//new LocalServerServices();		
	}

	static private IServerServices getRemoteServerServices(){
		return RemoteServerServices.getInstance();		
	}
	
	static private IServerServices getHttpServerServices(){
		HttpServerServices services = (HttpServerServices)HttpServerServices.getInstance();
		String codebase = System.getProperty(SERVICES_CODEBASE);
		
		if (codebase != null && !(codebase.length() == 0)){
			services.setCodebase(codebase);
		}
		
		return services;
	}
}
