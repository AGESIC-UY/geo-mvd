package imm.gis.core.permission;

import imm.gis.core.interfaces.IPermission;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PermissionController implements IPermission {

	public Map layerManagers;
	
	public PermissionController(String fileName) {
	
		layerManagers = new HashMap();
	
		if (fileName == null)
			return;
		
		try {
			Properties p = new Properties();

			p.load(new FileInputStream(fileName));
			
			Enumeration layers = p.propertyNames();
	
			String permissionManagerName;
			IPermissionManager permissionManager = null;
			String layerName;
			
			while (layers.hasMoreElements()) {
				layerName = (String) layers.nextElement();
				
				permissionManagerName = p.getProperty(layerName);
				
				if (permissionManagerName.startsWith("static:")) {
					String permissionsString = permissionManagerName.substring(7, permissionManagerName.length() - 1);
					int staticPermissions = 0;
					
					if (permissionsString.indexOf("A") != -1)
						staticPermissions |= StaticPermissionManager.ADD_PERMISSION;
					
					if (permissionsString.indexOf("M") != -1)
						staticPermissions |= StaticPermissionManager.MODIFY_PERMISSION;
					
					if (permissionsString.indexOf("D") != -1)
						staticPermissions |= StaticPermissionManager.DELETE_PERMISSION;
					
					permissionManager = new StaticPermissionManager(staticPermissions);
				}
				else {
					permissionManager = (IPermissionManager) Thread.currentThread().getContextClassLoader().loadClass(permissionManagerName).newInstance();
				}
				
				layerManagers.put(layerName, permissionManager);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}




	public IPermissionManager getPermissionManager(String type) {
		
		if (layerManagers.containsKey(type))
			return (IPermissionManager) layerManagers.get(type);
		else {
			IPermissionManager staticPermissionManager = new StaticPermissionManager(
					StaticPermissionManager.ADD_PERMISSION |
					StaticPermissionManager.DELETE_PERMISSION | 
					StaticPermissionManager.MODIFY_PERMISSION |
					StaticPermissionManager.SELECT_VARIOUS
					);
			
			layerManagers.put(type, staticPermissionManager);
			
			return staticPermissionManager;
		}
	}
	
	public void setPermissionManager(String type, IPermissionManager manager) {
		layerManagers.put(type, manager);
	}
}
