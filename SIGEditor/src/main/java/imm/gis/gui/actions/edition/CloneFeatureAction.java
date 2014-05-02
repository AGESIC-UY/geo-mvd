package imm.gis.gui.actions.edition;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.IFeatureEditor;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.tool.ITool;
import imm.gis.tool.edition.CloneFeatureTool;

import java.awt.event.ActionEvent;

public class CloneFeatureAction extends ExtendedAction implements IFeatureEditor {
	
	private static final long serialVersionUID = 1L;
	
	private ICoreAccess coreAccess;

	private EditionContext editionController;

	public CloneFeatureAction(ICoreAccess coreAccess, EditionContext editionController) {
		super("Clonar elemento (con nueva geometría)",
				GuiUtils.loadIcon("Copy16.gif"),
				GuiUtils.loadIcon("Copy16Dis.gif"));
	
		this.coreAccess = coreAccess;
		this.editionController = editionController;
		
		setToggle(true);
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		ITool cloneTool = new CloneFeatureTool(coreAccess, editionController);
		coreAccess.setActiveTool(cloneTool);
		
	}

	public boolean requiresAddPermission() {
		return true;
	}

	public boolean requiresDeletePermission() {
		return false;
	}

	public boolean requiresModifyPermission() {
		return false;
	}

	public boolean requiresSelectVarious() {
		// TODO Auto-generated method stub
		return false;
	}

}
