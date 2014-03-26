package imm.gis.gui.actions.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.navigation.NavigationController;

import java.awt.event.ActionEvent;

public class HomeAction extends ExtendedAction{
	private static final long serialVersionUID = 1L;
	private NavigationController navigator;

	public HomeAction(NavigationController navigator){
		super("Home", GuiUtils.loadIcon("Home16.gif"));
		this.navigator = navigator;
	}
	
	public void actionPerformed(ActionEvent e){
		navigator.goToHome();
	}
}	

