package imm.gis.comm.interfaces;

import imm.gis.GisException;

import java.io.Serializable;



public interface IClientFacade {

	/*
	 * Método para comunicarse con la logica remota
	 */
	public Serializable execute(String serviceInterface,String serviceName,Object[] parameters,Class[] clasess) throws GisException;
	public Serializable executeAs(String user,String serviceInterface,String serviceName,Object[] parameters,Class[] clasess) throws GisException;	
}
