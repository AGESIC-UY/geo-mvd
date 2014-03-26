package imm.gis.comm;

import imm.gis.comm.interfaces.IClientFacade;

public class FactoryClientFacade {

	public static IClientFacade getIClientFacade(){
		
		return ClientFacade.getInstance();
	}
}
