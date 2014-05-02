package imm.gis.gui.actions.edition;

import java.awt.event.ActionEvent;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.IFeatureEditor;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.tool.ITool;
import imm.gis.tool.edition.AddVertexTool;

public class AddVertexAction extends ExtendedAction implements IFeatureEditor {

	private static final long serialVersionUID = 1L;
	
	private ICoreAccess coreAccess;

	private EditionContext editionContext;

	public AddVertexAction(ICoreAccess coreAccess, EditionContext editionContext) {
		super("Agregar vertice",
				GuiUtils.loadIcon("AddVertex16.gif"),
				GuiUtils.loadIcon("AddVertex16Dis.gif"));
		
		this.coreAccess = coreAccess;
		this.editionContext = editionContext;
		
		setToggle(true);
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		ITool addVertexTool = new AddVertexTool(coreAccess, editionContext);
		coreAccess.setActiveTool(addVertexTool);
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
