package imm.gis.gui.actions.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.navigation.NavigationController;
import imm.gis.tool.ITool;
import imm.gis.tool.navigation.ExtendedPanTool;

import java.awt.event.ActionEvent;

public class PanAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;
	private NavigationController navigator;

	public PanAction(ICoreAccess coreAccess, NavigationController navigator) {
		super("Pan", GuiUtils.loadIcon("Pan16.gif"));
		setToggle(true);
		
		this.coreAccess = coreAccess;
		this.navigator = navigator;
	}
	
	public void actionPerformed(ActionEvent e){
		ITool pan = new ExtendedPanTool(coreAccess.getIDrawPanel(), navigator);

		coreAccess.setActiveTool(pan);
	}
}	

