package imm.gis.core.gui.editedfeatures;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.IForm;
import imm.gis.core.interfaces.IMap;
import imm.gis.core.interfaces.IModel;
import imm.gis.core.interfaces.ISelection;
import imm.gis.core.model.IStatus;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.geotools.feature.Feature;

public class EditedFeaturesEventManager extends MouseAdapter{

	private Object userObject;
	private JPopupMenu popup;
	private JTree tree;
	private TreePath selPath;
	private AbstractAction editInfoAction;
	private AbstractAction centerMapAction;
	
	private IForm form;
	private IMap map;
	private ISelection selection;
	private IModel model;

	public EditedFeaturesEventManager(IForm form, IMap map, ISelection selection, 
									  IModel model, JTree editionHistoryTree) {
		this.form = form;
		this.map = map;
		this.selection = selection;
		this.model = model;
		tree = editionHistoryTree;
		createActions();
		popup = new JPopupMenu();
		popup.add(editInfoAction);
		popup.add(centerMapAction);

		tree.add(popup);	
	}
	
	private void createActions(){
		editInfoAction = new AbstractAction("Editar datos", GuiUtils.loadIcon("EditInfo16.gif")){
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e){
				try {
					form.openEditFeatureForm((Feature)userObject);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		
		centerMapAction = new AbstractAction("Centrar mapa"){

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt){
				Feature f = (Feature)userObject;
				map.center(f.getDefaultGeometry().getCentroid().getCoordinate());
				selection.selectFeature(f);
			}			
		};
	}
	
	public void mouseClicked(MouseEvent e){
		if (e.isPopupTrigger() || selPath == null || userObject == null)
			return;
		
		/*
   		if (e.getClickCount() == 2){
   			if (userObject instanceof Feature) {
        		Feature f = (Feature)userObject;
        		try {
					coreAccess.getIForm().openEditFeatureForm(f);
				}
        		catch (Exception e1) {
					e1.printStackTrace();
				}	        			
    		}
   		}
   		*/
		
   		else if (e.getClickCount() == 1) {
   			if (userObject instanceof Feature) {
   	   			Feature f = (Feature)userObject;
   	   			selection.selectFeature(f);
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
        
        if (e.isPopupTrigger() && userObject != null && userObject instanceof Feature) {
        	editInfoAction.setEnabled(model.getStatus((Feature) userObject) != IStatus.DELETED_ATTRIBUTE);
			popup.show(tree, e.getX(), e.getY());
   		}		
	}
}
