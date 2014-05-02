package imm.gis.tool.edition;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.edition.EditionContext;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.Iterator;


import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;

public class EditDataTool extends AbstractEditionTool {

	private ICoreAccess coreAccess;
	private Cursor cursor;
	private EditionContext editionController;
	
	public EditDataTool(ICoreAccess coreAccess, EditionContext editionController) {
		this.coreAccess = coreAccess;
		this.editionController = editionController;
		
		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("EditInfo32.gif").getImage(), new Point(10, 10),"");
	}

	
	public Cursor getCursor() {
		return cursor;
	}
	
	public void mouseClicked(Coordinate c) {
		
		try {
			Collection<Feature> f = coreAccess.getIMap().getAllFeaturesOn(editionController.getEditableType(), c);
			
			if (f != null && !f.isEmpty()){		
				coreAccess.getIModel().notifyChangeListeners(new FeatureChangeEvent(f,
							((Feature)((Iterator)f.iterator()).next()).getFeatureType().getTypeName(), null, null,
							FeatureChangeEvent.OPEN_FORM_MODIFY_FEATURE),
							FeatureEventManager.BEFORE_MODIFY);
				
				coreAccess.getIForm().openEditCollectionFeatureForm(f);

				coreAccess.getIModel().notifyChangeListeners(new FeatureChangeEvent(f,
						((Feature)((Iterator)f.iterator()).next()).getFeatureType().getTypeName(), null, null,
						FeatureChangeEvent.OPEN_FORM_MODIFY_FEATURE),
						FeatureEventManager.AFTER_MODIFY);
			}
		}
		catch (Exception e) {
			coreAccess.getIUserInterface().showError("Error al consultar el feature",e);
			e.printStackTrace();
		}
	}
	
	public boolean requiresModifyPermission() {
		return true;
	}
}
