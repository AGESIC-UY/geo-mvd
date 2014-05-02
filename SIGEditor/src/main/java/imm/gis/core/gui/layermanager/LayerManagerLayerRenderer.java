package imm.gis.core.gui.layermanager;

import imm.gis.AppContext;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

class LayerManagerLayerRenderer extends JPanel implements javax.swing.tree.TreeCellRenderer {

	private static final long serialVersionUID = 1L;
	
	/** if the note is a layer */
	private javax.swing.JLabel layerName;        
	private javax.swing.JLabel layerIcon;
	JCheckBox layerVisible;
	
	AppContext applicationContext = AppContext.getInstance();
	
	/**
	 * Creates a new instance of LayerManagerLayerRenderer
	 */
	public LayerManagerLayerRenderer() {
	    super();
	    initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the
	 * form.
	 */
	private void initComponents() {
	    layerVisible = new JCheckBox(); //new MyCheckBox(UIManager.getIcon("Tree.openIcon").getIconWidth());
	    layerIcon = new javax.swing.JLabel();
	    layerName = new javax.swing.JLabel();
	
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	
	    
	    layerVisible.setBackground(new java.awt.Color(255, 255, 255));
	    add(layerVisible);
	    
	    layerIcon.setBackground(new java.awt.Color(255, 255, 255));
	    layerIcon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
	    add(layerIcon);
	
	    layerName.setBackground(new java.awt.Color(255, 255, 255));
	    add(layerName);
	    
	    setBackground(new java.awt.Color(255, 255, 255));
	}


	/*
	public String getText() {
        return this.layerName.getText();
    }
    */
	
    public java.awt.Component getTreeCellRendererComponent(
        javax.swing.JTree tree, Object value, boolean selected,
        boolean expanded, boolean leaf, int row, boolean hasFocus) {

    	LayerNodeInfo userObject = (LayerNodeInfo) ((DefaultMutableTreeNode) value).getUserObject();
    	
		String displayText = (applicationContext.getCapa(userObject.getLayerName()).getFilter() == null) ?
				userObject.getLayerName() :
					userObject.getLayerName() + " (Filtro activo)";

    	layerVisible.setSelected(userObject.isVisibleLayer());
//    	layerVisible.setEnabled(userObject.isEnabledLayer());
	    layerVisible.setBackground(new java.awt.Color(255, 255, 255));
    	
    	layerIcon.setIcon(userObject.getGeometryIcon());
	    layerIcon.setBackground(new java.awt.Color(255, 255, 255));

	    /*
        set the note name in the checkbox also remove and readd,
        since the component was initialise with the string "" that might
        shorter than the note's real name
		*/
	    
	    remove(layerName);
	    
	    layerName.setText(displayText);
	    layerName.setBackground(new java.awt.Color(255, 255, 255));
//	    layerName.setEnabled(userObject.isEnabledLayer());
	    
	    add(layerName);
	    
        return this;
    }
}
