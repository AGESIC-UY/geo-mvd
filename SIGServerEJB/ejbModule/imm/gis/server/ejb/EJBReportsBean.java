package imm.gis.server.ejb;

import imm.gis.NoReportDataException;
import imm.gis.ServerAppManager;
import imm.gis.report.GenericDS;
import imm.gis.report.ReportDataLoader;
import imm.gis.report.ReportDataType;
import imm.gis.report.conf.ReportConfManager;
import imm.gis.server.ejb.interfaces.EJBReportsLocal;
import imm.gis.server.ejb.interfaces.EJBReportsRemote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Properties;

import javax.ejb.Stateless;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;

@Stateless
public class EJBReportsBean implements EJBReportsLocal, EJBReportsRemote {
	private HashMap<String, Properties> reportProperties = new HashMap<String, Properties>();
	public static final String F_SEPARATOR = System.getProperty("file.separator");
	private static final String HTML_BASE_PATH = ".." + F_SEPARATOR + "server" + F_SEPARATOR + "default" +
									F_SEPARATOR + "deploy" + F_SEPARATOR + "jbossweb-tomcat55.sar" + 
									F_SEPARATOR + "ROOT.war";
	private static final File tmpDirectory = new File(HTML_BASE_PATH);
	private static final String BIN_BASE_PATH = "SIG" +  F_SEPARATOR + "applications";
	private static Logger log = Logger.getLogger(EJBReportsBean.class);
	
	public URL runReport(String idApp, String idReport, HashMap<String, Object> parameters) throws RemoteException,NoReportDataException {
		log.info("Ejecutando reporte " + idReport + " de la aplicacion " + idApp);
		Properties p = reportProperties.get(idApp);
		if (p == null){
			String props = BIN_BASE_PATH + F_SEPARATOR + idApp + F_SEPARATOR + "reports" + F_SEPARATOR + idApp + ".properties";
			
			log.info("Cargando " + props);
			p = new Properties();
			try {
				p.load(new FileInputStream(props));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RemoteException("No se encontro el archivo properties " + props, e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RemoteException("Error al leer el archivo properties " + props, e);
			}
			reportProperties.put(idApp, p);
		}

		log.info("Properties cargadas");
		String clazz = p.getProperty(idReport);
		if (clazz == null){
			throw new RemoteException("No se encontro la clave " +  idReport + " en el archivo properties para " + idApp);			
		}
		ReportDataLoader dl;
		log.info("Intanciando clase " + clazz);
		try {
			dl = (ReportDataLoader)Thread.currentThread().getContextClassLoader().loadClass(clazz).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("Error al instanciar clase " + clazz, e);
		}
		
		ReportDataType rdt = null;
		log.info("Cargando datos");
		try{
			rdt = dl.loadData(ServerAppManager.getInstance().getAppManager(idApp) ,parameters);			
		} catch (IOException e){
			e.printStackTrace();
			throw new RemoteException("Error al cargar datos en " + clazz, e);			
		}
				
		if (rdt == null || rdt.length() == 0){
			log.info("No hay datos para el reporte " + idReport + " de la aplicacion " + idApp);
			throw new NoReportDataException(idApp, idReport);
		}
		
		JasperPrint prn;
		String report = BIN_BASE_PATH + F_SEPARATOR + idApp + F_SEPARATOR + "reports" + F_SEPARATOR + idReport + ".jasper";

		String fileName = null;
		try {
			fileName = getTmpFileName(".html");
			String fullFileName = getFullPathFileName(idApp, fileName);
			log.info("Ejecutando reporte " + report + " al archivo " + fullFileName);
			prn = JasperFillManager.fillReport(report, dl.reportParameters(), new GenericDS(rdt));
			JasperExportManager.exportReportToHtmlFile(prn, fullFileName);
		} catch (IOException io){
			io.printStackTrace();
			throw new RemoteException("Error al crear archivo temporal para el reporte " + report, io);			
		} catch (JRException e) {
			e.printStackTrace();
			throw new RemoteException("Error al ejecutar reporte " + report, e);
		}

		log.info("Ok");
		String strUrl = null;
		try {
			
			strUrl = ReportConfManager.getURLReportPreffix(idApp) + idApp + "/" + fileName;
			return new URL(strUrl);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("Error al general URL con " + strUrl, e);
		}
	}
	
	public static String getFullPathFileName(String idApp, String name){
		return HTML_BASE_PATH + F_SEPARATOR + idApp + F_SEPARATOR + name;		
	}
	
	public static String getTmpFileName(String suffix) throws IOException {
		return File.createTempFile("tmp", suffix, tmpDirectory).getName();
	}
	
	
}
