package imm.gis;

import imm.gis.comm.IServerServices;
import imm.gis.comm.ServerServicesFactory;
import imm.gis.core.controller.ContPpal;
import imm.gis.core.gui.SplashWindow;
import imm.gis.core.interfaces.ICoreAccess;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Observer;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;

import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;
import com.vividsolutions.jts.geom.Envelope;

public class SigEditor {
	static private Logger logger = Logger.getLogger(SigEditor.class); 
	private IServerServices services;
	
	public SigEditor(String app){
		this(app, null, null, null);
	}
	
	public SigEditor(String app, String frameTitle) {
		this(app, null, frameTitle, null);
	}

	public SigEditor(String app, Observer obs, String frameTitle){
		this(app, obs, frameTitle, null);
	}
	
	public SigEditor(String app, String frameTitle, double minx, double maxx, double miny, double maxy){
		this(app, null, frameTitle, new Envelope(minx, maxx, miny, maxy));
	}
	
	public SigEditor(String app, Observer obs, String frameTitle, Envelope init) {
		SplashWindow splash = SplashWindow.getInstance();
		ICoreAccess controlador;
		
		try {
			logger.info("Configurando UI...");
			splash.setText("Configurando UI...");
			configureUI();

			logger.info("Cargando definicion desde el servidor...");
			splash.setText("Cargando definicion desde el servidor...");
			services = ServerServicesFactory.getServerServices();
			services.initApp(app);
			logger.info("Creando controlador principal...");
			splash.setText("Creando controlador principal...");
			
			controlador = (init == null) ? // Centrado en imm
					new ContPpal(new Envelope(574082, 574435, 6137011, 6137337)) : 
					new ContPpal(init);
			
			GraphicsEnvironment env = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice device = env.getDefaultScreenDevice();

			if (device.getDisplayMode().getWidth() <= 800)
				logger.info("Resolucion de pantalla inadecuada...");
			else
				logger.info("La resolucion de pantalla es buena: "
						+ device.getDisplayMode().getWidth() + " x "
						+ device.getDisplayMode().getHeight());

			controlador.getIApplication().getMainGUI().setTitle(frameTitle);
/*			
			logger.info("Confirmando si se puede editar...");
			try{
				if (!isInRole("RA_CS_EDIT")){
					controlador.setEditable(false);					
				}
			} catch (GisException e){
				logger.info("Deshabilitando la edicion...", e);
				controlador.setEditable(false);					
			}
*/			
			logger.info("Listo");
		} catch (Exception e) {
			logger.info("Iniciando aplicacion ", e);
			e.printStackTrace();
			
		} finally{
			splash.stopRunIndicator();			
		}
	}

	private void configureUI() {
		try {
			logger.info("Intentando asignar look & feel Nimbus...");
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		    
			String lafName = LookUtils.IS_OS_WINDOWS_XP ? Options
					.getCrossPlatformLookAndFeelClassName() : Options
					.getSystemLookAndFeelClassName();
					
			logger.info("No disponible, intentando con " + lafName);
			UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
			UIManager.setLookAndFeel(lafName);
		} catch (Exception e) {
			logger.info("Queda look & feel por defecto, " + e.getMessage());
		}
	}

	public ICoreAccess getControlador() {
		return AppContext.getInstance().getCoreAccess();
	}
	
	public String getRemoteUser() throws GisException{
		return services.getRemoteUser();
	}

	public Boolean isInRole(String rol) throws GisException{
		return services.isInRol(rol);
	}
}
