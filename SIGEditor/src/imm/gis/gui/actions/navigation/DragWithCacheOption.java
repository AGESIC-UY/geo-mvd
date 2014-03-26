package imm.gis.gui.actions.navigation;

import java.awt.event.ActionEvent;

import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

public class DragWithCacheOption extends ExtendedAction {
	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;
	
	public DragWithCacheOption(ICoreAccess ca){
		super("Extendido?");
		setToggle(true);
		coreAccess = ca;
	}
	
	public void actionPerformed(ActionEvent e) {
		javax.swing.AbstractButton ab = (javax.swing.AbstractButton)e.getSource();
		coreAccess.getIModel().setUsePanCache(ab.isSelected());
		coreAccess.getIDrawPanel().setUseDragCache(ab.isSelected());
		coreAccess.getNavigationController().setUseDragCache(ab.isSelected());
		coreAccess.getIModel().refreshViews(true);
	}

}
