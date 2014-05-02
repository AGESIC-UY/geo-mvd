package imm.gis.gui.actions.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.navigation.NavigationController;
import imm.gis.tool.ITool;
import imm.gis.tool.navigation.ZoomOutTool;

import java.awt.event.ActionEvent;

public class ZoomOutAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;
	private NavigationController navigator;

	public ZoomOutAction(ICoreAccess coreAccess, NavigationController navigator){
		super("Alejarse", GuiUtils.loadIcon("ZoomOut16.gif"));
		setToggle(true);
		
		this.navigator = navigator;
		this.coreAccess = coreAccess;
	}
	
	public void actionPerformed(ActionEvent e){
		ITool zoomOut = new ZoomOutTool(navigator);
		
		coreAccess.setActiveTool(zoomOut);
	}
}

