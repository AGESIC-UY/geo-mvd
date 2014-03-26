package imm.gis.gui.actions.application;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.AbstractNonUILogic;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

public class CancelAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;

	private ICoreAccess coreAccess;

	private EditionContext editionContext;
	
	public CancelAction(ICoreAccess coreAccess, EditionContext editionContext) {
		super("Cancelar (Esc)", 
				GuiUtils.loadIcon("CancelEdit16.gif"),
				GuiUtils.loadIcon("CancelEdit16Dis.gif"));
		
		this.coreAccess = coreAccess;
		this.editionContext = editionContext;
	}
	
	public void actionPerformed(ActionEvent e){
		if (JOptionPane.showConfirmDialog(null, 
				"Cancela los cambios realizados?", 
				"Cancelar edicion", 
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			
			AbstractNonUILogic l = new AbstractNonUILogic() {
				public void logic() {
					try {
						coreAccess.getIModel().undoAllModified(editionContext.getEditableType());
					} catch (Exception e) {
						coreAccess.getIUserInterface().showError("Error al deshacer los cambios",e);
						e.printStackTrace();
					}	
				}
			};
			
			coreAccess.getIUserInterface().doNonUILogic(l);
            /*
            protected void doUIUpdateLogic() throws RuntimeException {
				getCancelAction().setEnabled(false);
				getSaveAction().setEnabled(false);
				contPpal.getModel().cleanSelection(editionController.getEditableType());
				contPpal.getContUndo().clean();
            	
				if (res != null) {
            		coreAccess.getIUserInterface().showError("Cancelando cambios", res);
            	}
            }
            */			            
		}
	}
}
