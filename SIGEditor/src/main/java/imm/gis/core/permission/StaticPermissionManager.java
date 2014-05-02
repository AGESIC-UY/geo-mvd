package imm.gis.core.permission;

import imm.gis.edition.IFeatureEditor;

public class StaticPermissionManager implements IPermissionManager {

	private int permissions;
	
	public static final int ADD_PERMISSION = 1;
	public static final int MODIFY_PERMISSION = 2;
	public static final int DELETE_PERMISSION = 4;
	public static final int SELECT_VARIOUS = 8;
	
	public StaticPermissionManager(int permissions) {
		this.permissions = permissions;
	}
	
	public boolean canAdd() {
		return (permissions & ADD_PERMISSION) != 0;
	}

	public boolean canModify() {
		return (permissions & MODIFY_PERMISSION) != 0;
	}

	public boolean canDelete() {
		return (permissions & DELETE_PERMISSION) != 0;
	}
	public boolean canSelectVarious() {
		return (permissions & SELECT_VARIOUS) != 0;
	}

	public boolean hasPermissions(IFeatureEditor featureEditor) {
		boolean hasPermissions = true;
		
		if (featureEditor.requiresAddPermission())
			hasPermissions &= canAdd();
		
		if (featureEditor.requiresModifyPermission())
			hasPermissions &= canModify();
		
		if (featureEditor.requiresDeletePermission())
			hasPermissions &= canDelete();
		
		if (featureEditor.requiresSelectVarious())
			hasPermissions &= canSelectVarious();
		
		return hasPermissions;
	}
}
