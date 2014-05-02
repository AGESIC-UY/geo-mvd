package imm.gis.server.servlet;

import imm.gis.comm.GisSerialization.FeatureTransporter;
import imm.gis.comm.GisSerialization.FilterTransporter;
import imm.gis.comm.datatypes.datainput.ISaveDataType;
import imm.gis.server.ejb.interfaces.EJBGISServicesLocal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

 public class ServletGISServices extends javax.servlet.http.HttpServlet {
   static final long serialVersionUID = 1L;
   private EJBGISServicesLocal service;
   private Logger log = Logger.getLogger(ServletGISServices.class);
   
	public ServletGISServices() {
		super();
		
	}   	
	
	@Override
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		try {
			InitialContext ic = new InitialContext();
			service = (EJBGISServicesLocal)ic.lookup("SIGServerEAR/EJBGISServicesBean/local");
		} catch (NamingException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}  	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getRemoteUser() == null){
			throw new ServletException("Se necesita la autenticacion del usuario!!!");
		}
		
		Object res = null;
		Map<?,?> params = null;
		ObjectInputStream ois = new ObjectInputStream(request.getInputStream());
		ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
		
		try{
			params = (Map<?,?>)ois.readObject();
			String servicio = (String)params.get("SERVICIO");
			
			if (servicio.equals("initApp")){
				res = service.initApp((String)params.get("appID"));
			} else if (servicio.equals("loadLayer")){
				res = service.loadLayer(
						(String)params.get("appID"), 
						(String)params.get("layerName"), 
						(FilterTransporter)params.get("filter"));			
			} else if (servicio.equals("loadLov")){
				res = service.loadLov(
						(String)params.get("appID"), 
						(String)params.get("layerName"), 
						(FilterTransporter)params.get("filter"), 
						(String[])params.get("properties"),
						(String)params.get("sortBy"));
			} else if (servicio.equals("createFeatureID")){
				res = service.createFeatureID(
						(String)params.get("appID"), 
						(FeatureTransporter)params.get("feature"));
			} else if (servicio.equals("saveData")){
				service.saveData(
						(String)params.get("appID"), 
						(ISaveDataType)params.get("data"));
			} else if (servicio.equals("remoteUser")){
				res = request.getRemoteUser();
			} else if (servicio.equals("isInRol")){
				res = request.isUserInRole((String)params.get("rol"));
			} else if (servicio.equals("closeApp")){
				log.info("Invalidando sesion del usuario " + request.getRemoteUser());
				request.getSession().invalidate();
			} else {
				throw new IllegalArgumentException("El servicio " + servicio + " no esta implementado");
			}
			
			oos.writeObject(res);
			oos.flush();
		} catch (Exception e){
			e.printStackTrace();
			throw new ServletException(e);
		} finally{
			oos.close();
			oos = null;
		}
	}   	  	    
}