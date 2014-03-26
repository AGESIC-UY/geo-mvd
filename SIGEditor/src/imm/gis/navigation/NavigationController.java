package imm.gis.navigation;

import static imm.gis.core.gui.PanelDibujo.DRAG_IMAGE_BUFFER_FACTOR;

import java.util.Stack;

import org.apache.log4j.Logger;

import imm.gis.core.controller.ContUtils;
import imm.gis.core.interfaces.AbstractNonUILogic;
import imm.gis.core.interfaces.IDrawPanel;
import imm.gis.core.interfaces.IMap;
import imm.gis.core.interfaces.IUserInterface;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class NavigationController {
	private Stack<Envelope> previousBoundingBox = new Stack<Envelope>();
	private Stack<Envelope> nextBoundingBox = new Stack<Envelope>();
	private IMap map;
	private javax.swing.JFrame frame;
	private IUserInterface userInterface;
	private IDrawPanel drawPanel;
	private boolean useDragCache = false;
	private Envelope homeEnvelope = null;
	public Envelope tmpEnvelope = new Envelope();
	private final static int MOVER_ARRIBA = 0;
	private final static int MOVER_ABAJO = 1;
	private final static int MOVER_IZQUIERDA = 2;
	private final static int MOVER_DERECHA = 4;
	private static Logger log = Logger.getLogger(NavigationController.class.getName());

	public void moveNorth() {
    	mover(MOVER_ARRIBA);			
	}
	
	public void moveSouth() {
       	mover(MOVER_ABAJO);			
	}
	
	public void moveWest() {
       	mover(MOVER_IZQUIERDA);			
	}

	public void moveEast() {
      	mover(MOVER_DERECHA);			
	}

	private void mover(final int direccion) {
		Envelope tmp = new Envelope();
		tmp.init(map.getBoundingBox());
		nextBoundingBox.clear();
		previousBoundingBox.push(tmp);
		
		Envelope bbox = map.getBoundingBox();
		final Envelope newBoundingBox;
		
		double dx = (bbox.getMaxX() - bbox.getMinX()) / 2;
		double dy = (bbox.getMaxY() - bbox.getMinY()) / 2;

		switch (direccion) {
			case MOVER_ARRIBA:
				newBoundingBox = new Envelope(bbox.getMinX(), bbox.getMaxX(), bbox.getMinY() + dy, bbox.getMaxY() + dy);
				break;
			case MOVER_ABAJO:
				newBoundingBox = new Envelope(bbox.getMinX(), bbox.getMaxX(), bbox.getMinY() - dy, bbox.getMaxY() - dy);
				break;
			case MOVER_IZQUIERDA:
				newBoundingBox = new Envelope(bbox.getMinX() - dx, bbox.getMaxX() - dx, bbox.getMinY(), bbox.getMaxY());
				break;
			case MOVER_DERECHA:
				newBoundingBox = new Envelope(bbox.getMinX() + dx, bbox.getMaxX() + dx, bbox.getMinY(), bbox.getMaxY());
				break;
			default:
				return;
		}
		
		AbstractNonUILogic l = new ChangeBBox(newBoundingBox);
		userInterface.doNonUILogic(l);	
	}



	public void previousZoom() {		
		
		if (previousBoundingBox.isEmpty()){
			log.info("No existe zoom previo, se ignora la petición...");			
		} else {
			final Envelope tmpBoundingBox = new Envelope(previousBoundingBox.pop());
			AbstractNonUILogic l = new ChangeBBox(tmpBoundingBox);
			userInterface.doNonUILogic(l);	
			
			Envelope tmp = new Envelope();
			tmp.init(map.getBoundingBox());
			nextBoundingBox.push(tmp);			
		}
	}
	
	public void nextZoom() {		
		
		if (nextBoundingBox.isEmpty()){
			log.info("No existe zoom posterior, se ignora la petición...");			
		} else {
			final Envelope tmpBoundingBox = new Envelope(nextBoundingBox.pop());
			AbstractNonUILogic l = new ChangeBBox(tmpBoundingBox);
			userInterface.doNonUILogic(l);	
			
			Envelope tmp = new Envelope();
			tmp.init(map.getBoundingBox());
			previousBoundingBox.push(tmp);			
		}
	}
	
	public boolean pan(Coordinate initialCoordinate, Coordinate finalCoordinate) {
		if (initialCoordinate.distance(finalCoordinate) > map.getAccuracyTolerance()) {
			final double deltax = initialCoordinate.x - finalCoordinate.x;
			final double deltay = initialCoordinate.y - finalCoordinate.y;
			final Envelope actualBBox = map.getBoundingBox();

			Envelope tmp = new Envelope();
			tmp.init(actualBBox);
			nextBoundingBox.clear();
			previousBoundingBox.push(tmp);
			Envelope env = new Envelope(
					actualBBox.getMinX() + deltax, 
					actualBBox.getMaxX() + deltax, 
					actualBBox.getMinY() + deltay, 
					actualBBox.getMaxY() + deltay
			);

			if (isUseDragCache() && navigateOutOfImage(tmpEnvelope, env)){
					actualBBox.init(env);
					return false;
			} else {
				AbstractNonUILogic l = new ChangeBBox(env);
				userInterface.doNonUILogic(l);
			}
		}
		
		return true;
	}
	
	private boolean navigateOutOfImage(Envelope initial, Envelope temporal){
		double deltaX = initial.getWidth() / (DRAG_IMAGE_BUFFER_FACTOR);
		double deltaY = initial.getHeight() / (DRAG_IMAGE_BUFFER_FACTOR);
		
		Coordinate center1 = initial.centre();
		Coordinate center2 = temporal.centre();
		
		double distX = Math.abs(center1.x - center2.x);
		double distY = Math.abs(center1.y - center2.y);
		
		return distX < deltaX && distY < deltaY;
	}
	
	public void refreshMap() {
		AbstractNonUILogic l = new AbstractNonUILogic() {
			public void logic() {
				map.refresh();
			}
		};
		
		userInterface.doNonUILogic(l);
	}
	

	public void zoomIn(final Coordinate initialCoordinate, final Coordinate finalCoordinate) {
		Envelope tmp = new Envelope();
		tmp.init(map.getBoundingBox());
		nextBoundingBox.clear();
		previousBoundingBox.push(tmp);
		
		Envelope bbox = map.getBoundingBox();
		bbox = ContUtils.calcBbox(bbox, initialCoordinate,
					finalCoordinate, map.getAccuracyTolerance());
		
		AbstractNonUILogic l = new ChangeBBox(bbox);
		userInterface.doNonUILogic(l);
	}

	public void zoomOut(final Coordinate c) {
		Envelope tmp = new Envelope();
		tmp.init(map.getBoundingBox());
		nextBoundingBox.clear();
		previousBoundingBox.push(tmp);

		Envelope bbox = map.getBoundingBox();
		Coordinate newBegin = new Coordinate(c.x - bbox.getWidth(),
				c.y - bbox.getHeight());
		Coordinate newEnd = new Coordinate(c.x + bbox.getWidth(),
				c.y + bbox.getHeight());
		bbox = new Envelope(newBegin, newEnd);

		AbstractNonUILogic l = new ChangeBBox(bbox);
		userInterface.doNonUILogic(l);
	}

	public IMap getMap() {
		return map;
	}

	public void setMap(IMap map) {
		this.map = map;
	}

	public javax.swing.JFrame getFrame() {
		return frame;
	}

	public void setFrame(javax.swing.JFrame frame) {
		this.frame = frame;
	}

	public IUserInterface getUserInterface() {
		return userInterface;
	}

	public void setUserInterface(IUserInterface userInterface) {
		this.userInterface = userInterface;
	}

	public IDrawPanel getDrawPanel() {
		return drawPanel;
	}

	public void setDrawPanel(IDrawPanel drawPanel) {
		this.drawPanel = drawPanel;
	}

	public boolean isUseDragCache() {
		return useDragCache;
	}

	public void setUseDragCache(boolean useDragCache) {
		this.useDragCache = useDragCache;
	}

	public Envelope getHomeEnvelope() {
		return homeEnvelope;
	}

	public void setHomeEnvelope(Envelope homeEnvelope) {
		this.homeEnvelope = homeEnvelope;
		tmpEnvelope.init(homeEnvelope);
	}

	public void goToHome() {
		Envelope tmp = new Envelope();
		tmp.init(map.getBoundingBox());
		nextBoundingBox.clear();
		previousBoundingBox.push(tmp);
		AbstractNonUILogic l = new ChangeBBox(homeEnvelope);
		
		userInterface.doNonUILogic(l);
	}
	
	public void setPreviousBoundingBox(){
		Envelope tmp = new Envelope();
		tmp.init(map.getBoundingBox());
		nextBoundingBox.clear();
		previousBoundingBox.push(tmp);
	}
	
	private class ChangeBBox extends AbstractNonUILogic{
		private Envelope envelope;
		
		public ChangeBBox(Envelope env){
			envelope = env;
		}
		
		public void logic() {
			tmpEnvelope.init(envelope);
			map.setBoundingBox(envelope);
		}		
	}
}
