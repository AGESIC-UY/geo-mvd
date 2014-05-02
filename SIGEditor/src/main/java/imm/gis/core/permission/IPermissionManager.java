package imm.gis.core.permission;

import imm.gis.edition.IFeatureEditor;

public interface IPermissionManager {

	public boolean canAdd();
	public boolean canModify();
	public boolean canDelete();
	
	public boolean hasPermissions(IFeatureEditor featureEditor);
}
