package imm.gis.core.controller;

import imm.gis.core.interfaces.CoordinateListener;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.interfaces.IMap;
import imm.gis.edition.EditionContext;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.vividsolutions.jts.geom.Coordinate;

public class ContMousePanelDibujo implements MouseMotionListener, MouseListener {

	private EditionContext editionController = null;
	
	private boolean mouseDown = false;
	
	
	private Coordinate mouseDownCoordinate;

	private Collection<CoordinateListener> listeners;
	private IMap map;
	private ICoreAccess coreAccess;
	
	public ContMousePanelDibujo(ContPpal cont) {
		this.coreAccess = cont;
		this.editionController = cont.getEditionContext();
		this.map = coreAccess.getIMap();

		listeners = new ArrayList<CoordinateListener>();
	}
	
	public void addCoordinateListener(CoordinateListener listener) {
		listeners.add(listener);
	}
	
	public void removeCoordinateListener(CoordinateListener listener) {
		listeners.remove(listener);
	}

	public void mouseDragged(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1 || arg0.getButton() == MouseEvent.NOBUTTON){
			
			Coordinate eventCoordinate = map.pixelToWorld(arg0.getX(), arg0.getY());

			if (mouseDownCoordinate.distance(eventCoordinate) > map.getAccuracyTolerance()) {
				Iterator iterator = listeners.iterator();
				
				while (iterator.hasNext())
					((CoordinateListener)iterator.next()).coordinateChanged(eventCoordinate);
				
				if (editionController.isWithSnapping())
					editionController.snap(arg0.isControlDown());
	
				coreAccess.getActiveTool().mouseDragged(arg0.getX(), arg0.getY(), eventCoordinate);
			}
		}
	}

	
	
	public void mouseMoved(MouseEvent arg0) {
		Coordinate eventCoordinate = map.pixelToWorld(arg0.getX(), arg0.getY());
		
		coreAccess.getActiveTool().mouseMoved(eventCoordinate);

		Iterator iterator = listeners.iterator();
		
		while (iterator.hasNext())
			((CoordinateListener)iterator.next()).coordinateChanged(eventCoordinate);
	}

	public void mouseClicked(final MouseEvent arg0) {
		if (arg0.isPopupTrigger()){
			return;
		}
		
		final Coordinate eventCoordinate = map.pixelToWorld(arg0.getX(), arg0.getY());
		
		if (arg0.getClickCount() == 1)
			coreAccess.getActiveTool().mouseClicked(eventCoordinate);
		else
			coreAccess.getActiveTool().mouseDoubleClicked(eventCoordinate);
	}

	public void mousePressed(MouseEvent arg0) {
		mouseDown = true;
		
		Coordinate eventCoordinate = map.pixelToWorld(arg0.getX(), arg0.getY());
		
		mouseDownCoordinate = eventCoordinate;
		
		coreAccess.getActiveTool().mousePressed(arg0.getX(), arg0.getY(), eventCoordinate);
	}

	public void mouseReleased(final MouseEvent arg0) {
		mouseDown = false;
		
		Coordinate eventCoordinate;
		
		if (editionController.isWithSnapping()) {
			eventCoordinate = editionController.getSnapCoordinate();
			editionController.resetSnap();
			
			if (eventCoordinate == null || mouseDownCoordinate.distance(eventCoordinate) < map.getAccuracyTolerance())
				eventCoordinate = map.pixelToWorld(arg0.getX(), arg0.getY());
		}
		else
			eventCoordinate = map.pixelToWorld(arg0.getX(), arg0.getY()); 
				
		coreAccess.getActiveTool().mouseReleased(arg0.getX(), arg0.getY(), eventCoordinate);
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}
	
	public boolean isMouseDown() {
		return this.mouseDown;
	}
}
