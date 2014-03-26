package imm.gis.gui.actions.edition;

import java.awt.event.ActionEvent;

import javax.swing.AbstractButton;

import imm.gis.core.gui.GuiUtils;
import imm.gis.edition.EditionContext;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

public class SnapAction extends ExtendedAction {

	private static final long serialVersionUID = 1L;
	private EditionContext editionContext;

	public SnapAction(EditionContext editionContext) {
		super("Snapping", 
				GuiUtils.loadIcon("Snapping16.gif"),
				GuiUtils.loadIcon("Snapping16Dis.gif"));
		
		this.editionContext = editionContext;
		
		setToggle(true);
	}
		
	public void actionPerformed(ActionEvent e){
		AbstractButton ab = (AbstractButton)e.getSource();

		editionContext.setWithSnapping(ab.isSelected());
	}
}
