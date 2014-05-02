package imm.gis.edition;

import imm.gis.core.controller.ContPpal;
import imm.gis.core.controller.ContSnap;
import imm.gis.core.controller.IGeometryType;
import imm.gis.core.gui.worker.BlockingSwingWorker;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.interfaces.ISelection;
import imm.gis.core.model.ModelData;
import imm.gis.edition.util.GeomUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureType;

import com.vividsolutions.jts.geom.Coordinate;


public class EditionContext implements IGeometryType {

	private String editedType = null;
	private ContPpal mainController;
	private ContSnap snapController;
	
	private ModelData model;
	private ISelection selectionModel;
	
	private int editedTypeGeometry;

	
	private boolean withSnapping = false;

	private Timer timer;
	private SnappingAction snapAction;
	private ICoreAccess coreAccess;
	
	private Collection<EditionContextListener> contextListeners;


	private static Logger logger = Logger.getLogger(EditionContext.class.getName());

	
	public EditionContext(ContPpal mainController) {
		this.mainController = mainController;
		this.coreAccess = mainController;
		
		model = mainController.getModel();
		selectionModel = mainController.getSelectionModel();
		
		contextListeners = new ArrayList<EditionContextListener>();
		
		snapController = new ContSnap(mainController, mainController.getContMapa());
		
		snapAction = new SnappingAction();
		timer = new Timer(500, snapAction);
		timer.setRepeats(false);
	}
	
	public void setUpListeners() {
		coreAccess.getIMap().addCoordinateListener(snapController);		
	}
	
	public void addContextListener(EditionContextListener listener) {
		contextListeners.add(listener);
	}
	
	public void removeContextListener(EditionContextListener listener) {
		contextListeners.remove(listener);
	}
	
	public void notifyListeners(boolean entered, String type) {

		EditionContextListener listener;
		
		Iterator i = contextListeners.iterator();
		
		while (i.hasNext()) {
			listener = (EditionContextListener) i.next();
			
			if (entered)
				listener.editionEntered(type);
			else
				listener.editionExited(type);
		}
	}
	
	public String[] getEditableTypes() throws Exception {
		return model.getNotEmptyTypesAndEditableTypes();
	}
	
	public void setEditableType(String layer) throws IOException {
		
		if (editedType != null && !editedType.equals(layer)) {
			
			if (layer == null)
				editedType = null;
			
			selectionModel.cleanSelection(editedType);
			notifyListeners(false, editedType);
		}

		if (layer == null) {
			editedType = null;
		}
		else {
			editedType = layer;
			FeatureType ft = coreAccess.getIModel().getSchema(editedType);
			
			editedTypeGeometry = GeomUtil.isPointGeometry(ft) ? POINT_GEOMETRY :
				(GeomUtil.isLineGeometry(ft) ? LINE_GEOMETRY : POLYGON_GEOMETRY);
			
			notifyListeners(true, editedType);
		}
	}
	
	
	
	public String getEditableType() {
		return editedType;
	}

	
	public int getEditableTypeGeometry() {
		return editedTypeGeometry;
	}
	
	
	public boolean isWithSnapping() {
		return withSnapping;
	}

	public void setWithSnapping(boolean withSnapping) {
		this.withSnapping = withSnapping;
	}

	private class SnappingAction implements ActionListener {
		private boolean withControl = false;

		public void setWithControl(boolean c) {
			withControl = c;
		}

		public void actionPerformed(ActionEvent e) {
			snapController.makeSnap(withControl);
		}

		public void resetSnap() {
			snapController.resetSnap();
		}
	}

	public void snap(boolean isControlDown) {
		snapAction.resetSnap();
		snapAction.setWithControl(isControlDown);

		if (timer.isRunning())
			timer.restart();
		else
			timer.start();
	}



	public Coordinate getSnapCoordinate() {
		return snapController.getSnapCoordinate();
	}



	public void undo() {
		try{
			new BlockingSwingWorker(coreAccess.getIApplication().getMainGUI()){
	            protected void doNonUILogic() throws RuntimeException {
					try{
						mainController.getContUndo().undo();
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
			}.start();
		}
		catch (RuntimeException ex){
			logger.info(ex.getMessage());
			coreAccess.getIUserInterface().showError(ex);
		}
	}


	public boolean isEditing() {
		return editedType != null;
	}

	public void resetSnap() {
		snapAction.resetSnap();
	}
}
