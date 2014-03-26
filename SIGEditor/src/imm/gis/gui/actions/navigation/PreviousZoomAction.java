package imm.gis.gui.actions.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.navigation.NavigationController;

import java.awt.event.ActionEvent;

public class PreviousZoomAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;
	private NavigationController navigator;

	public PreviousZoomAction(NavigationController navigator){
		super("Zoom previo (<--)", 
				GuiUtils.loadIcon("ZoomPrevious16.gif"),
				GuiUtils.loadIcon("ZoomPrevious16Dis.gif"));
		
		this.navigator = navigator;
	}
	
	public void actionPerformed(ActionEvent e){
		navigator.previousZoom();
	}
}
