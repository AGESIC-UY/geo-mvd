package imm.gis.tool.misc;

import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.interfaces.IForm;
import imm.gis.core.interfaces.IMap;
import imm.gis.core.interfaces.ISelection;
import imm.gis.core.interfaces.IUserInterface;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.tool.AbstractTool;

import java.awt.Cursor;
import java.util.Collection;
import java.util.Iterator;

import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;

public class PointerTool extends AbstractTool {
	private Cursor cursor;
	private IMap map; 
	private IForm form; 
	private ISelection selection;
	private IUserInterface userInterface;
	private ICoreAccess coreAccess;
	
	public PointerTool(ICoreAccess coreAccess) {
		this.coreAccess = coreAccess;
		this.map = coreAccess.getIMap();
		this.form = coreAccess.getIForm();
		this.selection = coreAccess.getISelection();
		this.userInterface = coreAccess.getIUserInterface();
		cursor = Cursor.getDefaultCursor();
	}

	
	public Cursor getCursor() {
		return cursor;
	}
	
	public void mouseClicked(Coordinate c) {
		selection.unselectAll();
	}
	
	public void mouseDoubleClicked(Coordinate c) {
		try {
			Collection<Feature> f = map.getAllFeaturesOn(c);
			
			coreAccess.getIModel().notifyChangeListeners(new FeatureChangeEvent(f,
					((Feature)((Iterator)f.iterator()).next()).getFeatureType().getTypeName(), (Feature)((Iterator)f.iterator()).next(), null,
					FeatureChangeEvent.OPEN_FORM_SHOW_FEATURE),
					FeatureEventManager.BEFORE_MODIFY);
			form.openShowFeaturesForm(f);
		}
		catch (Exception e) {
			userInterface.showError("Error al consultar el feature",e);
			e.printStackTrace();
		}
	}

}
