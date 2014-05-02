package imm.gis.core.gui.layermanager;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map;

import imm.gis.AppContext;
import imm.gis.core.controller.ContFilterLayerDialog;
import imm.gis.core.controller.ContStyleDialog;
import imm.gis.core.interfaces.AbstractNonUILogic;
import imm.gis.core.interfaces.IModel;
import imm.gis.core.interfaces.IUserInterface;
import imm.gis.core.layer.Layer;
import imm.gis.edition.EditionContext;
import imm.gis.tool.misc.MenuScroller;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureEventManager;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.opengis.filter.Filter;


public class LayerManagerEventManager extends MouseAdapter{

	private Object userObject;
	private JPopupMenu popup;
	private JMenuItem editLayerItem;
	private JMenu subMenuFilter;
	private JTree tree;
	private TreePath selPath;
	

	private AbstractAction editLayerAction;
	private AbstractAction editLayerStyleAction;
	private AbstractAction filterLayerAction;
	private AbstractAction quitFilterAction;
	private EditionContext editionContext;
	private IModel model;
	private IUserInterface userInterface;
	
	private ContFilterLayerDialog filterDialog;
	private ContStyleDialog styleDialog;
	
	private String clickedLayerName;
	
	private int iconSize = UIManager.getIcon("Tree.openIcon").getIconWidth();
	
	private AppContext applicationContext = AppContext.getInstance();
	
	public LayerManagerEventManager(IModel model, IUserInterface userInterface,
			EditionContext editionContext, ContFilterLayerDialog filterDialog, 
			ContStyleDialog styleDialog, JTree layerManagerTree) {
		this.model = model;
		this.userInterface = userInterface;
		this.editionContext = editionContext;
		this.filterDialog = filterDialog;
		this.styleDialog = styleDialog;
		
		tree = layerManagerTree;
		createActions();
		popup = new JPopupMenu();
		
		editLayerItem = popup.add(editLayerAction);
		
		if (styleDialog != null)
			popup.add(editLayerStyleAction);
		
		subMenuFilter = new JMenu("Filtrado");
		popup.add(subMenuFilter);
		tree.add(popup);	
		MenuScroller.setScrollerFor(subMenuFilter, 20, 50, 1, 1);
	}
	
	private void createActions(){
		editLayerAction = new AbstractAction("Editar capa"){

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e){
				try {
					editionContext.setEditableType(clickedLayerName);
				}
				catch (IOException e1) {
					e1.printStackTrace();
            		userInterface.showError("Editando capa", e1);			            					            		
				}
			}			
		};

		editLayerStyleAction = new AbstractAction("Editar estilo actual"){
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e){
				styleDialog.editStyle(clickedLayerName);
			}
		};

		filterLayerAction = new AbstractAction("Filtro personalizado"){
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e){
				filterDialog.showDialog(clickedLayerName);
			}
		};

		quitFilterAction = new AbstractAction("Quitar filtro"){
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e){
					AbstractNonUILogic l = new AbstractNonUILogic() {
   						public void logic() {
							try {
			   					Layer capa = AppContext.getInstance().getCapa(clickedLayerName);
			   					capa.setFilter(null);
			   					
			   					//Aviso que se quitó el filtro, utilizo el evento APPLY_FILTER pasando como source el string vacio ""
			   					AppContext.getInstance().getCoreAccess().getIModel().notifyChangeListeners(new FeatureChangeEvent("",
										capa.getNombre(), null, null, FeatureChangeEvent.APPLY_FILTER),
										FeatureEventManager.AFTER_MODIFY);
			   					
			   					model.refreshViews(true);
							}
							catch (Exception e) {
								userInterface.showError(e);
								e.printStackTrace();
							}
						}
   					};
   					
   					userInterface.doNonUILogic(l);
			}
		};
	}
	
	public void mouseClicked(MouseEvent e){
		if (e.isPopupTrigger() || selPath == null || userObject == null) return;
		
		if (e.getClickCount() == 1) {
   			if (userObject instanceof LayerNodeInfo) {
   				if (e.getX() <= iconSize) { // Click sobre el check
   					AbstractNonUILogic l = new AbstractNonUILogic() {
   						public void logic() {
							try {
			   					TreePath path = tree.getSelectionPath();
			   					if (path != null){
				   					DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				   					LayerNodeInfo info = (LayerNodeInfo)node.getUserObject();
				   					info.setVisibleLayer(!info.isVisibleLayer());
			   					}
			   					model.setLayerEnabled(clickedLayerName, !model.isEnabledLayer(clickedLayerName));
							}
							catch (Exception e) {
								userInterface.showError(e);
								e.printStackTrace();
							}
						}
   					};
   					userInterface.doNonUILogic(l);
   				}
   			}
        }
	}

	public void mousePressed(MouseEvent e) {
		manejarPopup(e);
	}
	
	public void mouseReleased(MouseEvent e){
		manejarPopup(e);
	}
	
	private void manejarPopup(MouseEvent e) {
        selPath = tree.getPathForLocation(e.getX(), e.getY());
        
        userObject = selPath != null ? ((DefaultMutableTreeNode)selPath.getLastPathComponent()).getUserObject() : null;
    
        if (userObject != null && userObject instanceof LayerNodeInfo)
        	clickedLayerName = ((LayerNodeInfo) userObject).getLayerName();
        
        if (e.isPopupTrigger() && userObject != null && userObject instanceof LayerNodeInfo) {
        	Layer capa = applicationContext.getCapa(clickedLayerName);

        	editLayerAction.setEnabled(capa.isEditable());
    		subMenuFilter.removeAll();
    		if (capa.getFilter() != null){
    			subMenuFilter.add(quitFilterAction);
    			subMenuFilter.add(new JSeparator());
    		}
    		addLayersFilters(capa);
    		if (filterDialog != null){
    			subMenuFilter.add(filterLayerAction);
    		}

			popup.show(tree, e.getX(), e.getY());
        }
	}
	
	private void addLayersFilters(Layer layer){
    	Map filters = applicationContext.getPredefFilters(layer.getNombre());
		String filterName;
		Filter filter;
		String[] parentChild;
		JCheckBoxMenuItem item = null; 
		JMenu itemParent = null;
		
    	for (java.util.Iterator it = filters.keySet().iterator(); it.hasNext();){
    		filterName = (String)it.next();
    		filter = (Filter)filters.get(filterName);

    		if (filterName.startsWith("Parent")){
    			filterName = filterName.substring(6);    			
    			parentChild = filterName.split("->");
    			itemParent = (JMenu)getItem(parentChild[0]);
    			if (itemParent == null){
    				itemParent = new JMenu(parentChild[0]);
        			subMenuFilter.add(itemParent);
    			}
    			if ((parentChild.length == 3) && parentChild[2].equalsIgnoreCase("center"))
    				item = new JCheckBoxMenuItem(new LayerFilterAction(parentChild[1], parentChild[0], layer, filter, true));
    			else
    				item = new JCheckBoxMenuItem(new LayerFilterAction(parentChild[1], parentChild[0], layer, filter, false));
        		item.setSelected(layer.getFilter() != null && layer.getFilter().toString().equals(filter.toString()));
        		itemParent.add(item);
    		} else {
    			parentChild = filterName.split("->");
    			if ((parentChild.length == 2) && parentChild[1].equalsIgnoreCase("center"))
    				item = new JCheckBoxMenuItem(new LayerFilterAction(parentChild[0], layer, filter, true));
    			else
    				item = new JCheckBoxMenuItem(new LayerFilterAction(filterName, layer, filter, false));
        		
    			item.setSelected(layer.getFilter() != null && layer.getFilter().toString().equals(filter.toString()));
    			subMenuFilter.add(item);
    		}
    	}
    	
    	if (item != null){
			subMenuFilter.add(new JSeparator());    		
    	}
	}
	
	private JMenuItem getItem(String text){
		int i = 0;
		int encontre = 0;
		JMenuItem item = null;		
		while ((encontre == 0) && (i < subMenuFilter.getItemCount())){
			if ((subMenuFilter.getItem(i) != null) && 
				(subMenuFilter.getItem(i).getText().compareTo(text) == 0)){
				item = subMenuFilter.getItem(i);
				encontre = 1;				
			}
			else
				i++;
		}
		return item;
	}
	

	public void setCanEdit(boolean edit){
		if (!edit){
			popup.remove(editLayerItem);
		} else if (popup.getComponent(0) != editLayerItem){
			popup.insert(editLayerItem, 0);
		}
	}

	private class LayerFilterAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		private Filter filter;
		private Layer layer;
		private String filterName;
		private String filterParentName = "";
		
		private boolean centerFilter;
		
		public LayerFilterAction(String name, Layer layer, Filter filter, boolean centerFilter){
			super(name);
			this.layer = layer;
			this.filter = filter;
			this.centerFilter = centerFilter;
			this.filterName = name;
		}
		
		public LayerFilterAction(String name, String parentName, Layer layer, Filter filter, boolean centerFilter){
			super(name);
			this.layer = layer;
			this.filter = filter;
			this.centerFilter = centerFilter;
			this.filterName = name;
			this.filterParentName = parentName;
		}
		
		public void actionPerformed(ActionEvent e) {
				AbstractNonUILogic l = new AbstractNonUILogic() {
						public void logic() {
						try {
							if (filterParentName != "")
								AppContext.getInstance().getCoreAccess().getIModel().notifyChangeListeners(new FeatureChangeEvent("Parent"+filterParentName+"->"+filterName,
									layer.getNombre(), null, null, FeatureChangeEvent.APPLY_FILTER),
									FeatureEventManager.BEFORE_MODIFY);
							else
								AppContext.getInstance().getCoreAccess().getIModel().notifyChangeListeners(new FeatureChangeEvent(filterName,
										layer.getNombre(), null, null, FeatureChangeEvent.APPLY_FILTER),
										FeatureEventManager.BEFORE_MODIFY);
							
							layer.setFilter(filter);
							
							if (centerFilter) 
								model.setBboxFilterLayer(layer.getNombre());
							 else 
								model.refreshViews(true);	
							
							if (filterParentName != "")
								AppContext.getInstance().getCoreAccess().getIModel().notifyChangeListeners(new FeatureChangeEvent("Parent"+filterParentName+"->"+filterName,
									layer.getNombre(), null, null, FeatureChangeEvent.APPLY_FILTER),
									FeatureEventManager.AFTER_MODIFY);
							else
								AppContext.getInstance().getCoreAccess().getIModel().notifyChangeListeners(new FeatureChangeEvent(filterName,
										layer.getNombre(), null, null, FeatureChangeEvent.APPLY_FILTER),
										FeatureEventManager.AFTER_MODIFY);
						}
						catch (Exception e) {
							userInterface.showError(e);
							e.printStackTrace();
						}
					}
					};
					
					userInterface.doNonUILogic(l);			
		}		
	}
}
