package imm.gis.gui.actions.edition;

import java.awt.event.ActionEvent;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.IFeatureEditor;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.tool.ITool;
import imm.gis.tool.edition.SplitTool;

public class SplitFeatureAction extends ExtendedAction implements IFeatureEditor {

	private static final long serialVersionUID = 1L;
	
	private ICoreAccess coreAccess;

	private EditionContext editionController;
	public SplitFeatureAction(ICoreAccess coreAccess, EditionContext editionController) {

		super("Partir",
				GuiUtils.loadIcon("Split16.gif"),
				GuiUtils.loadIcon("Split16Dis.gif"));
		
		this.coreAccess = coreAccess;
		this.editionController = editionController;
		
		setToggle(true);
		setEnabled(false);

	}
	
	public void actionPerformed(ActionEvent e) {
		ITool splitTool = new SplitTool(coreAccess, editionController);
		coreAccess.setActiveTool(splitTool);
	}

	public boolean requiresAddPermission() {
		return true;
	}

	public boolean requiresDeletePermission() {
		return true;
	}

	public boolean requiresModifyPermission() {
		return false;
	}

	public boolean requiresSelectVarious() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
