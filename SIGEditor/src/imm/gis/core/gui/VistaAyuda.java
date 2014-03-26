package imm.gis.core.gui;


import java.awt.Desktop;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;

import javax.swing.*;


public class VistaAyuda extends JDialog{

	private String ubicacionManual = "";
	
	public VistaAyuda(String ubicacionManual) {
				
		this.ubicacionManual = ubicacionManual;
		
		init();
	}
	
	private void init(){	
		
		try {
			setTitle("Guía de Usuario");
			
	     /* 
	      * Utiliza la libreria IcePdf
	      * 
	      *   SwingController controller = new SwingController();
	        SwingViewBuilder factory = new SwingViewBuilder(controller);
	        JPanel viewerComponentPanel = factory.buildViewerPanel();
	        
	        ComponentKeyBinding.install(controller, viewerComponentPanel);
	
	        controller.getDocumentViewController().setAnnotationCallback(
	                new org.icepdf.ri.common.MyAnnotationCallback(
	                        controller.getDocumentViewController()));
	
	       
	        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	        getContentPane().add(viewerComponentPanel);
	
	        //Abro el pdf
	        URL url = new URL(ubicacionManual);
	        controller.openDocument(url);
	   */     
			
			
	        pack();
	        setVisible(true);        
		}
		catch(Exception e){
			e.printStackTrace();
		}		
	} 	
}
