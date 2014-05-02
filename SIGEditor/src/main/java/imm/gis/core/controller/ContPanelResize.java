package imm.gis.core.controller;

import imm.gis.core.gui.worker.BlockingSwingWorker;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Timer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class ContPanelResize extends ComponentAdapter implements ActionListener {
	private ContPpal contPpal;
	private Dimension previousSize = null;
	private Dimension newSize;
	private Timer timer;
	
	public ContPanelResize(ContPpal c, Dimension d) {
		contPpal = c;
		previousSize = d;
		timer = new Timer(300, this);
	}

	public void componentResized(ComponentEvent e) {
		newSize = e.getComponent().getSize();
		
		if (!timer.isRunning())
			timer.start();
		else
			timer.restart();
	}

	public void actionPerformed(ActionEvent e) {
		timer.stop();
		
		if (newSize.width > 0 && newSize.height > 0) {
			try {
				// Si utilizo contMapa.getScale(), la escala va a cambiar, dado que se utiliza el boundingbox anterior
				// pero la nueva dimension del panel de dibujo del mapa, por lo tanto calculo la escala por la mia
				long actualScale = ContUtils.getScale(contPpal.getContMapa().getBoundingBox(), previousSize);
				
				//long actualScale = contPpal.getContMapa().getScale(); 
				Coordinate center = contPpal.getContMapa().getBoundingBox().centre();
				
				final Envelope newBBox = ContUtils.centerAt(center, newSize, actualScale);
				
				contPpal.getModel().setMapDimension(newSize);
				
				new BlockingSwingWorker(contPpal.getVistaPrincipal()) {
		            protected void doNonUILogic() throws RuntimeException {
						
		            	try {
		            		if (contPpal.getNavigationController().getHomeEnvelope() == null){
		            			contPpal.getNavigationController().setHomeEnvelope(newBBox);
		            		}
		            		contPpal.getModel().setBbox(newBBox);
		            	}
		            	catch (Exception e) {
		            		e.printStackTrace();
		            		contPpal.showError(e);
		            	}
		            }
	        	}.start();

	        	previousSize = newSize;
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		previousSize = newSize;
	}
}
