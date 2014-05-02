package imm.gis.gui.actions.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.navigation.NavigationController;

import java.awt.event.ActionEvent;

public class NextZoomAction extends ExtendedAction{
	
	private static final long serialVersionUID = 1L;
	private NavigationController navigator;

	public NextZoomAction(NavigationController navigator){
		super("Zoom próximo (-->)", 
				GuiUtils.loadIcon("ZoomNext16.gif"),
				GuiUtils.loadIcon("ZoomNext16Dis.gif"));
		
		this.navigator = navigator;
	}
	
	public void actionPerformed(ActionEvent e){
		navigator.nextZoom();
	}
}
