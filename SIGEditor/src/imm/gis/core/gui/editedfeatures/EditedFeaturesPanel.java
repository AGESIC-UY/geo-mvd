package imm.gis.core.gui.editedfeatures;

import imm.gis.core.interfaces.IModel;
import imm.gis.core.model.ModifiedData;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureChangeListener;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.edition.EditionContext;
import imm.gis.edition.EditionContextListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Iterator;

public class EditedFeaturesPanel extends JPanel implements FeatureChangeListener, EditionContextListener {
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private EditionContext editionContext;
	private IModel model;
	
	public EditedFeaturesPanel(IModel model, EditionContext editionContext) {
		super(new BorderLayout());
		this.model = model;
		init(model, editionContext);
		this.editionContext = editionContext;
		
		try {
			refreshHistory();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public JTree getTree(){
		return tree;
	}
	
	public void init(IModel model, EditionContext editionContext){
		tree = new JTree();
		tree.setShowsRootHandles(false);
		tree.setToggleClickCount(0);
		tree.setExpandsSelectedPaths(true);
		tree.putClientProperty("JTree.lineStyle", "None");
		tree.setVisibleRowCount(15);
		tree.setCellRenderer(new EditedFeaturesRenderer(model, editionContext));
		tree.getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}
	
	public void refreshHistory() throws IOException {
		ModifiedData modifiedFeatures;
		DefaultMutableTreeNode treeRoot;
		
		if (editionContext.isEditing()) {
			treeRoot = new DefaultMutableTreeNode(model.getSchema(editionContext.getEditableType()));
		
			modifiedFeatures = model.getModifiedFeatures(editionContext.getEditableType());
					
			for (Iterator it = modifiedFeatures.getCreatedData().iterator(); it.hasNext();){
				treeRoot.add(new DefaultMutableTreeNode(it.next()));				
			}
			
			for (Iterator it = modifiedFeatures.getDeletedData().iterator(); it.hasNext();){
				treeRoot.add(new DefaultMutableTreeNode(it.next()));				
			}
			for (Iterator it = modifiedFeatures.getUpdatedData().iterator(); it.hasNext();){
				treeRoot.add(new DefaultMutableTreeNode(it.next()));				
			}
	
		}
		else
			treeRoot = new DefaultMutableTreeNode("Ninguna capa en edicion");
		
		DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot); 
		tree.setModel(treeModel);

		repaint();
	}
	
	public void changePerformed(FeatureChangeEvent fc) {
		try {
			refreshHistory();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLayer() {
		return FeatureEventManager.ALL_LAYERS;
	}

	public void isChanging(FeatureChangeEvent fc) {
		// No me importa
	}

	public void editionEntered(String type) {
		try {
			refreshHistory();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void editionExited(String type) {
		try {
			refreshHistory();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}