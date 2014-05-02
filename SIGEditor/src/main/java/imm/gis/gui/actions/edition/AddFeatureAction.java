package imm.gis.gui.actions.edition;

import java.awt.event.ActionEvent;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.IFeatureEditor;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.tool.ITool;
import imm.gis.tool.edition.AddFeatureTool;

public class AddFeatureAction extends ExtendedAction implements IFeatureEditor {

	private static final long serialVersionUID = 1L;
	
	private ICoreAccess coreAccess;

	private EditionContext editionContext;

	public AddFeatureAction(ICoreAccess coreAccess, EditionContext editionContext) {
		super("Agregar elemento (Ins)",
				GuiUtils.loadIcon("AddFeature16.gif"),
				GuiUtils.loadIcon("AddFeature16Dis.gif"));

		this.coreAccess = coreAccess;
		this.editionContext = editionContext;
		
		setToggle(true);
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		ITool addTool = new AddFeatureTool(coreAccess, editionContext);

		coreAccess.setActiveTool(addTool);
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
