package imm.gis.tool.edition;

import imm.gis.edition.IFeatureEditor;
import imm.gis.tool.AbstractTool;

public class AbstractEditionTool extends AbstractTool implements IFeatureEditor {

	public boolean requiresAddPermission() {
		return false;
	}

	public boolean requiresDeletePermission() {
		return false;
	}

	public boolean requiresModifyPermission() {
		return false;
	}

	public boolean requiresSelectVarious() {
		return false;
	}
}
