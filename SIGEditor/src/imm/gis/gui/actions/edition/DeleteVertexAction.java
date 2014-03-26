package imm.gis.gui.actions.edition;

import java.awt.event.ActionEvent;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.IFeatureEditor;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.tool.ITool;
import imm.gis.tool.edition.DeleteVertexTool;

public class DeleteVertexAction extends ExtendedAction implements IFeatureEditor {

	private static final long serialVersionUID = 1L;
	private ICoreAccess coreAccess;
	private EditionContext editionController;

	public DeleteVertexAction(ICoreAccess coreAccess, EditionContext editionController) {
		super("Eliminar vertice",
				GuiUtils.loadIcon("DelVertex16.gif"),
				GuiUtils.loadIcon("DelVertex16Dis.gif"));
		
		this.coreAccess = coreAccess;
		this.editionController = editionController;
		
		setToggle(true);
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		ITool deleteVertexTool = new DeleteVertexTool(coreAccess, editionController);
		coreAccess.setActiveTool(deleteVertexTool);
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
