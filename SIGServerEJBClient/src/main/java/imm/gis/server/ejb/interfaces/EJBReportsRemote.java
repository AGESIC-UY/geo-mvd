package imm.gis.server.ejb.interfaces;


import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.ejb.Remote;

@Remote
public interface EJBReportsRemote  {
	public URL runReport(String idApp, String idReport, HashMap<String, Object> parameters) throws RemoteException, Exception;
	
}
