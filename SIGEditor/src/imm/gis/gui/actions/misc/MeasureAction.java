package imm.gis.gui.actions.misc;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.tool.ITool;
import imm.gis.tool.misc.MeasureTool;

import java.awt.event.ActionEvent;

public class MeasureAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;

	
	public MeasureAction(ICoreAccess coreAccess){
		super("Medir (F4)", GuiUtils.loadIcon("Medir16.gif"));
		
		this.coreAccess = coreAccess;
		
		setToggle(true);
	}
	
	public void actionPerformed(ActionEvent e){
		ITool measure = new MeasureTool(coreAccess);
		coreAccess.setActiveTool(measure);
	}
}
