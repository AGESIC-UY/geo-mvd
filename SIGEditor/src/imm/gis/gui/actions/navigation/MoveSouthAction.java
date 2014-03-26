package imm.gis.gui.actions.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.gui.toolbar.toolbutton.AbstractButtonAction;
import imm.gis.navigation.NavigationController;

import java.awt.event.ActionEvent;

public class MoveSouthAction extends AbstractButtonAction{

	private static final long serialVersionUID = 1L;
	private NavigationController navigator;

	public MoveSouthAction(NavigationController navigator) {
		super("Abajo", GuiUtils.loadIcon("Down16.gif"));
		
		this.navigator = navigator;
	}
	
	public void actionPerformed(ActionEvent e){
		navigator.moveSouth();
	}
}
	
