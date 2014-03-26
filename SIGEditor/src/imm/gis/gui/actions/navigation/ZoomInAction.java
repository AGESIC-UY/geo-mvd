package imm.gis.gui.actions.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.navigation.NavigationController;
import imm.gis.tool.ITool;
import imm.gis.tool.navigation.ZoomInTool;

import java.awt.event.ActionEvent;

public class ZoomInAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;
	private NavigationController navigator;

	public ZoomInAction(ICoreAccess coreAccess, NavigationController navigator){
		super("Acercarse", GuiUtils.loadIcon("ZoomIn16.gif"));
		setToggle(true);
		
		this.coreAccess = coreAccess;
		this.navigator = navigator;
	}
	
	public void actionPerformed(ActionEvent e){
		ITool zoomIn = new ZoomInTool(coreAccess, navigator);
		coreAccess.setActiveTool(zoomIn);
	}
}	

