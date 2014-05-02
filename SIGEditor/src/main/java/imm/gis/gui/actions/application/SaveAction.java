package imm.gis.gui.actions.application;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.AbstractNonUILogic;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

public class SaveAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;
	private EditionContext editionController;

	public SaveAction(ICoreAccess coreAccess, EditionContext editionController){
		super("Confirmar (F10)", 
				GuiUtils.loadIcon("Save16.gif"),
				GuiUtils.loadIcon("Save16Dis.gif"));
		
		this.coreAccess = coreAccess;
		this.editionController = editionController;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (JOptionPane.showConfirmDialog(null,
				"Confirma los cambios realizados?", 
				"Guardar edicion", 
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

			AbstractNonUILogic l = new AbstractNonUILogic() {
				public void logic() {
					try {
						coreAccess.getIModel().confirmModified(editionController.getEditableType());
					} catch (Exception e) {
						coreAccess.getIUserInterface().showError("Error al salvar los cambios",e);
						e.printStackTrace();
					}
				}
			};
			
			coreAccess.getIUserInterface().doNonUILogic(l);
		}
	}
}	
