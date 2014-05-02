package imm.gis.comm;

import imm.gis.GisException;
import imm.gis.comm.interfaces.IClientFacade;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

public class ClientFacade implements IClientFacade {

	private static ClientFacade instance = null;

	static Logger logger = Logger.getLogger(ClientFacade.class);

	private ClientFacade() {

	}

	public static IClientFacade getInstance() {
		if (instance == null) {
			instance = new ClientFacade();
		}
		return instance;

	}

	public Serializable execute(String serviceInterface, String serviceName,
			Object[] parameters, Class[] parameterTypes) throws GisException {
		
	/*	logger.debug("execute("+serviceInterface+"," +serviceName+","+
			parameters+","+parameterTypes+")");*/
		return executeAs("anonymous", serviceInterface, serviceName,
				parameters, parameterTypes);
	}

	public Serializable executeAs(String user, String serviceInterface,
			String serviceName, Object[] parameters, Class[] parameterTypes)
			throws GisException {
		Serializable toReturn = null;
		try {
			Object ejb = EJBLocator.locateEJB(serviceInterface);
			Class ejbClazz = ejb.getClass();
			Class[] parametersClazz = parameterTypes;// extractParametersClazz(parameters);

			Method ejbMethod = ejbClazz.getMethod(serviceName, parametersClazz);
			toReturn = (Serializable) ejbMethod.invoke(ejb, parameters);
		} catch (InvocationTargetException e) {
			logger.error("Error de invocacion",e);
			throw new GisException(e.getTargetException());
		} catch (Exception e) {
			logger.error("Error antes de invocacion",e);
			throw new GisException(e);
		}
		logger.debug("Sali executeAs retornando " + toReturn);
		return toReturn;
	}
}
