package imm.gis.gui.actions.misc;


import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.tool.ITool;
import imm.gis.tool.misc.CoordinatesKMLTool;

import java.awt.event.ActionEvent;


public class CoordinatesKMLAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;

	public CoordinatesKMLAction(ICoreAccess coreAccess){
		super("Ir a Google Earth",GuiUtils.loadIcon("CoordinatesKML.gif"));
		setToggle(true);
		
		this.coreAccess = coreAccess;
	}
	
	public void actionPerformed(ActionEvent e){
		
		ITool coord = new CoordinatesKMLTool(coreAccess);
		coreAccess.setActiveTool(coord);
		
	}
}