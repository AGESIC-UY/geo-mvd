package imm.gis.gui.actions.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.gui.toolbar.toolbutton.AbstractButtonAction;
import imm.gis.navigation.NavigationController;

import java.awt.event.ActionEvent;

public class MoveEastAction extends AbstractButtonAction{

	private static final long serialVersionUID = 1L;
	private NavigationController navigator;

	public MoveEastAction(NavigationController navigator){
		super("Derecha", GuiUtils.loadIcon("Forward16.gif"));
		this.navigator = navigator;
	}
	
	public void actionPerformed(ActionEvent e){
		navigator.moveEast();
	}
}	
