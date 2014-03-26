package imm.gis.gui.actions.misc;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.IFeatureEditor;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.tool.ITool;
import imm.gis.tool.edition.SelectionTool;
import imm.gis.tool.misc.PointerTool;

import java.awt.event.ActionEvent;

public class PointerAction extends ExtendedAction implements IFeatureEditor {

	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;
	private EditionContext editionController;

	public PointerAction(ICoreAccess coreAccess, EditionContext editionController){
		super("", GuiUtils.loadIcon("Normal16.gif"));
		
		this.coreAccess = coreAccess;
		this.editionController = editionController;
		
		setToggle(true);
	}
	
	public void actionPerformed(ActionEvent e){
		ITool tool;
		
		if (editionController.getEditableType() != null)
			tool = new SelectionTool(coreAccess, editionController);
		else
			tool = new PointerTool(coreAccess);

		coreAccess.setActiveTool(tool);
	}

	public boolean requiresAddPermission() {
		return false;
	}

	public boolean requiresDeletePermission() {
		return false;
	}

	public boolean requiresModifyPermission() {
		return true;
	}

	public boolean requiresSelectVarious() {
		// TODO Auto-generated method stub
		return false;
	}
}

