package imm.gis.gui.actions.edition;

import imm.gis.core.gui.GuiUtils;
import imm.gis.edition.EditionContext;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

import java.awt.event.ActionEvent;

public class UndoAction extends ExtendedAction {

	private static final long serialVersionUID = 1L;
	private EditionContext editionController;

	public UndoAction(EditionContext editionController) {
		super("Deshacer (Ctrl Z)", 
				GuiUtils.loadIcon("Undo16.gif"),
				GuiUtils.loadIcon("Undo16Dis.gif"));
		
		this.editionController = editionController;
	}
	
	public void actionPerformed(ActionEvent e) {
		editionController.undo();
	}

}
