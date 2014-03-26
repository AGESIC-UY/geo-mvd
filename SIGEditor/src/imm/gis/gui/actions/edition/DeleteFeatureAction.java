package imm.gis.gui.actions.edition;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.geotools.feature.Feature;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.AbstractNonUILogic;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.IFeatureEditor;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

public class DeleteFeatureAction extends ExtendedAction implements IFeatureEditor {

	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;
	private EditionContext editionController;

	public DeleteFeatureAction(ICoreAccess coreAccess, EditionContext editionController) {

		super("Eliminar elemento (Supr)",
				GuiUtils.loadIcon("DeleteFeature16.gif"),
				GuiUtils.loadIcon("DeleteFeature16Dis.gif"));

		this.coreAccess = coreAccess;
		this.editionController = editionController;
				
		setToggle(false);
		setEnabled(false);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		final String editedType = editionController.getEditableType();
		
		if (JOptionPane.showConfirmDialog(null, 
				"Confirma la operacion?", 
				"Eliminar elemento(s)", 
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

			AbstractNonUILogic nonui = new AbstractNonUILogic() {

				public void logic() {
					
					try {
						Iterator featureIterator = coreAccess.getISelection().getSelected(editedType).iterator();
	
						while (featureIterator.hasNext()) {
							coreAccess.getIModel().delFeature((Feature) featureIterator.next());
						}
					}
					catch (Exception e) {
						coreAccess.getIUserInterface().showError("Error en la eliminacion", e);
						e.printStackTrace();						
					}
				}					
			};
			
			coreAccess.getIUserInterface().doNonUILogic(nonui);
		}
	}


	public boolean requiresAddPermission() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean requiresDeletePermission() {
		// TODO Auto-generated method stub
		return true;
	}


	public boolean requiresModifyPermission() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean requiresSelectVarious() {
		// TODO Auto-generated method stub
		return false;
	}

}
