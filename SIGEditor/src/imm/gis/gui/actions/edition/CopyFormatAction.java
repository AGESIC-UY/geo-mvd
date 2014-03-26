package imm.gis.gui.actions.edition;

import java.awt.event.ActionEvent;

import imm.gis.core.gui.GuiUtils;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

public class CopyFormatAction extends ExtendedAction {

	private static final long serialVersionUID = 1L;

	public CopyFormatAction(){
		super("Copiar datos",
				GuiUtils.loadIcon("Copy16.gif"),
				GuiUtils.loadIcon("Copy16Dis.gif"));
		
		setToggle(true);
		setEnabled(false);
	}
		
	public void actionPerformed(ActionEvent e){
	}
}
