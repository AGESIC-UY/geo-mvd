package imm.gis.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Action;

import org.geotools.feature.FeatureType;

import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureChangeListener;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.core.model.selection.FeatureSelectionEvent;
import imm.gis.core.model.selection.FeatureSelectionListener;
import imm.gis.core.permission.IPermissionManager;
import imm.gis.gui.actions.application.CancelAction;
import imm.gis.gui.actions.application.EditAction;
import imm.gis.gui.actions.application.SaveAction;
import imm.gis.gui.actions.edition.AddFeatureAction;
import imm.gis.gui.actions.edition.AddVertexAction;
import imm.gis.gui.actions.edition.CloneFeatureAction;
import imm.gis.gui.actions.edition.DeleteFeatureAction;
import imm.gis.gui.actions.edition.DeleteVertexAction;
import imm.gis.gui.actions.edition.EditDataAction;
import imm.gis.gui.actions.edition.EditSelectedDataAction;
import imm.gis.gui.actions.edition.JoinFeaturesAction;
import imm.gis.gui.actions.edition.SnapAction;
import imm.gis.gui.actions.edition.SplitFeatureAction;
import imm.gis.tool.edition.SelectionTool;
import imm.gis.tool.misc.PointerTool;
import imm.gis.edition.EditionContext;
import imm.gis.edition.EditionContextListener;
import imm.gis.edition.IFeatureEditor;
import imm.gis.edition.util.GeomUtil;

public class EditionToolBar extends ConstructionToolBar implements FeatureSelectionListener, EditionContextListener, FeatureChangeListener {
	private static final long serialVersionUID = 1L;
	
	private Action editAction = null;
	private AddFeatureAction addFeatureAction = null;
	private DeleteFeatureAction deleteFeaturesAction = null;
	private AddVertexAction addVertexAction = null;
	private DeleteVertexAction deleteVertexAction = null;
	private SplitFeatureAction splitFeatureAction = null;
	private Action joinFeaturesAction = null;
	private Action snapAction = null;
	private EditDataAction editDataAction = null;
	private Action saveAction = null;
	private Action cancelAction = null;
	private Action editSelectedDataAction = null;
	private CloneFeatureAction cloneFeatureAction = null;
	
	private EditionContext editionContext;
	private ICoreAccess coreAccess;
	
	private Map<String, Action> actionMap;
	
	public EditionToolBar(ICoreAccess coreAccess, EditionContext editionContext){
		super(true);
		
		this.coreAccess = coreAccess;
		this.editionContext = editionContext;
		
		editAction = new EditAction(coreAccess, editionContext);
		addFeatureAction = new AddFeatureAction(coreAccess, editionContext);
		deleteFeaturesAction = new DeleteFeatureAction(coreAccess, editionContext);
		addVertexAction = new AddVertexAction(coreAccess, editionContext);
		deleteVertexAction = new DeleteVertexAction(coreAccess, editionContext);
		splitFeatureAction = new SplitFeatureAction(coreAccess, editionContext);
		joinFeaturesAction = new JoinFeaturesAction(coreAccess, editionContext);
		snapAction = new SnapAction(editionContext);
		editDataAction = new EditDataAction(coreAccess, editionContext);
		saveAction = new SaveAction(coreAccess, editionContext);
		cancelAction = new CancelAction(coreAccess, editionContext);
		editSelectedDataAction = new EditSelectedDataAction(coreAccess, editionContext);
		cloneFeatureAction = new CloneFeatureAction(coreAccess, editionContext);
		
		saveAction.setEnabled(false);
		cancelAction.setEnabled(false);
		snapAction.setEnabled(false);
		editSelectedDataAction.setEnabled(false);
		
		Object actions[] = new Object[] {
			editAction,
			addFeatureAction,
			deleteFeaturesAction,
			cloneFeatureAction,
			null,
			addVertexAction,
			deleteVertexAction,
			splitFeatureAction,
			joinFeaturesAction,
			snapAction,
			null,
			editDataAction,
			editSelectedDataAction,
			null,
			saveAction,
			cancelAction
		};
		
		addItemsToToolBar(actions, true);
		setFloatable(true);

		
		actionMap = new HashMap<String, Action>();
		
		actionMap.put("EDIT", editAction);
		actionMap.put("ADD_FEATURE", addFeatureAction);
		actionMap.put("DELETE_FEATURE", deleteFeaturesAction);
		actionMap.put("CLONE_FEATURE", cloneFeatureAction);
		actionMap.put("ADD_VERTEX", addVertexAction);
		actionMap.put("DELETE_VERTEX", deleteVertexAction);
		actionMap.put("SPLIT_FEATURE", splitFeatureAction);
		actionMap.put("JOIN_FEATURES", joinFeaturesAction);
		actionMap.put("SNAP", snapAction);
		actionMap.put("EDIT_DATA", editDataAction);
		actionMap.put("SAVE", saveAction);
		actionMap.put("CANCEL", cancelAction);
		actionMap.put("EDIT_SELECTED_DATA", editSelectedDataAction);
	}

	public void selectionPerformed(FeatureSelectionEvent e){
		if (e.getCommand() == FeatureSelectionEvent.UNSELECTED_ALL_FEATURES){ // se deselecciono todo
			setWithSelectionActions(e, false);
		}
		else {
			if (e.getCommand() == FeatureSelectionEvent.SELECTED_FEATURE){
				setWithSelectionActions(e, true);
			}
			else if (editionContext.isEditing()) {
				try {
					if (e.getAllSelected().isEmpty()){
						setWithSelectionActions(e, false);
					}
				}
				catch (Exception ex) {
					ex.printStackTrace();
					coreAccess.getIUserInterface().showError(ex);
				}
			}
		}
	}
	
	private void setWithSelectionActions(FeatureSelectionEvent e, boolean sel){
		if (!editionContext.isEditing()) return;

		if (sel == true){
			try{
				
				String edited = editionContext.getEditableType(); 
				IPermissionManager permissionManager = coreAccess.getIPermission().getPermissionManager(edited);
					
				
				FeatureType ft = coreAccess.getIModel().getSchema(edited);
				
				deleteFeaturesAction.setEnabled(permissionManager.hasPermissions(deleteFeaturesAction));
				
				
				
				if (!GeomUtil.isPointGeometry(ft) && permissionManager.hasPermissions(splitFeatureAction))
					splitFeatureAction.setEnabled(true);
				else
					splitFeatureAction.setEnabled(false);
				
				if (!GeomUtil.isPointGeometry(ft) && permissionManager.hasPermissions(addVertexAction))
					addVertexAction.setEnabled(true);
				else
					addVertexAction.setEnabled(false);
					
				if (!GeomUtil.isPointGeometry(ft)  && permissionManager.hasPermissions(deleteVertexAction))
					deleteVertexAction.setEnabled(true);
				else
					deleteVertexAction.setEnabled(false);
					
				editSelectedDataAction.setEnabled(e.getAllSelected().size() >= 2
										&& permissionManager.hasPermissions((IFeatureEditor) editSelectedDataAction));
				
				
				
				joinFeaturesAction.setEnabled(!GeomUtil.isPointGeometry(ft)
										&& e.getAllSelected().size() == 2
										&& permissionManager.hasPermissions((IFeatureEditor) joinFeaturesAction));
				
				cloneFeatureAction.setEnabled(GeomUtil.isPointGeometry(ft)
										&& e.getAllSelected().size() == 1
										&& permissionManager.hasPermissions((IFeatureEditor) cloneFeatureAction));
			} catch (Exception ex) {
				ex.printStackTrace();
				splitFeatureAction.setEnabled(false);					
				addVertexAction.setEnabled(false);
				deleteVertexAction.setEnabled(false);
				joinFeaturesAction.setEnabled(false);
				editSelectedDataAction.setEnabled(false);
				cloneFeatureAction.setEnabled(false);
			}
		}
		else {
			splitFeatureAction.setEnabled(false);					
			addVertexAction.setEnabled(false);
			deleteVertexAction.setEnabled(false);
			joinFeaturesAction.setEnabled(false);
			editSelectedDataAction.setEnabled(false);
			cloneFeatureAction.setEnabled(false);
		}
	}
	
	private void setEditing(boolean editing) {
		
		String editedType = editionContext.getEditableType(); 
		
		IPermissionManager permissionManager = coreAccess.getIPermission().getPermissionManager(editedType);
	
	
		if (editing){
			((AbstractButton) getComponent(0)).setSelected(true);
			
			if (permissionManager.hasPermissions(addFeatureAction))
				addFeatureAction.setEnabled(true);
			else
				addFeatureAction.setEnabled(false);
			
			if (permissionManager.hasPermissions(editDataAction))
				editDataAction.setEnabled(true);
			else
				editDataAction.setEnabled(false);
			
			snapAction.setEnabled(true);
			
			if (coreAccess.getIModel().isModified(editionContext.getEditableType())) {
				saveAction.setEnabled(true);
				cancelAction.setEnabled(true);
			}
			else {
				saveAction.setEnabled(false);
				cancelAction.setEnabled(false);
			}
		}
		else {
			((AbstractButton) getComponent(0)).setSelected(false);
			addFeatureAction.setEnabled(false);
			deleteFeaturesAction.setEnabled(false);
			cloneFeatureAction.setEnabled(false);
			deleteVertexAction.setEnabled(false);
			addVertexAction.setEnabled(false);
			splitFeatureAction.setEnabled(false);
			joinFeaturesAction.setEnabled(false);
			snapAction.setEnabled(false);
			editDataAction.setEnabled(false);
			editSelectedDataAction.setEnabled(false);
			saveAction.setEnabled(false);
			cancelAction.setEnabled(false);
		}
		
		if (coreAccess.getActiveTool() instanceof PointerTool && editing) {
			coreAccess.setActiveTool(new SelectionTool(coreAccess, editionContext));
		}
		else if (coreAccess.getActiveTool() instanceof SelectionTool &&	!editing) {
			coreAccess.setActiveTool(new PointerTool(coreAccess));
		}
	}


	public void editionEntered(String type) {
		coreAccess.getIModel().addFeatureChangeListener(this, FeatureEventManager.AFTER_MODIFY);
		setEditing(true);
	}

	public void editionExited(String type) {
		coreAccess.getIModel().removeFeatureChangeListener(this, FeatureEventManager.AFTER_MODIFY);
		setEditing(false);
	}

	public Map getActions() {
		return actionMap;
	}

	public void changePerformed(FeatureChangeEvent fc) {
		if (editionContext.isEditing())
			setEditing(true);
	}

	public String getLayer() {
		return FeatureEventManager.ALL_LAYERS;//editionContext.getEditableType();
	}

	public void isChanging(FeatureChangeEvent fc) {
	}
}
