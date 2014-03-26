package imm.gis.core.interfaces;

import imm.gis.core.permission.IPermissionManager;

public interface IPermission {

	public void setPermissionManager(String type, IPermissionManager manager);
	public IPermissionManager getPermissionManager(String type);
}
