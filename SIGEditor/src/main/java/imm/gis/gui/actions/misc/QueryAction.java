package imm.gis.gui.actions.misc;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.tool.ITool;
import imm.gis.tool.misc.QueryTool;

import java.awt.event.ActionEvent;

public class QueryAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;

	public QueryAction(ICoreAccess coreAccess){
		super("Mostrar informacion (Ctrl I)", GuiUtils.loadIcon("ShowInfo16.gif"));
		setToggle(true);
		
		this.coreAccess = coreAccess;
	}
	
	public void actionPerformed(ActionEvent e) {
		ITool query = new QueryTool(coreAccess);
		coreAccess.setActiveTool(query);
	}
}