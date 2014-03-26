package imm.gis.core.controller;

import imm.gis.tool.ITool;

import java.util.HashMap;
import java.util.Map;

public class ToolController {

	private ITool activeTool;
	
	private Map<String, Object> contextValues;
	
	private ContPpal mainController;
	
	public ToolController(ContPpal mainController) {
		this.mainController = mainController;
		
		contextValues = new HashMap<String, Object>();
	}
	
	public void activateTool(ITool tool) {
		
		//if (!isToolEnabled(type, tool))
		//	throw new PermissionDeniedException();
		deactivateAllTools();
		
		mainController.getPanelDibujo().setCursor(tool.getCursor());
		
		activeTool = tool;
	}
	
	public ITool getActiveTool() {
		return activeTool;
	}
	
	public void deactivateAllTools() {
		
		if (activeTool != null)
			activeTool.deactivate();
	}
	
	public void putContextValue(String id, Object value) {
		contextValues.put(id, value);
	}
	
	public Object getContextValue(String id) {
		return contextValues.get(id);
	}
	
	public boolean isToolEnabled(String type, ITool tool) {
		/*
		if (type == null)
			return !(tool.requiresAddPermission() ||
					tool.requiresDeletePermission() ||
					tool.requiresModifyPermission());
		
		IPermissionManager manager = mainController.getPermissionController().getPermissionManager(type);
		
		boolean enabled = true;
		
		if (tool.requiresAddPermission())
			enabled &= manager.canAdd();
		
		if (tool.requiresDeletePermission())
			enabled &= manager.canDelete();
		
		if (tool.requiresModifyPermission())
			enabled &= manager.canModify();
		
		return enabled;
		*/
		
		return true;
	}
}
