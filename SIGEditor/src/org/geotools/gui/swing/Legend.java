/*
 *    Geotools2 - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */

package org.geotools.gui.swing;

import imm.gis.core.feature.Util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.geotools.gui.swing.legend.LegendLayerNodeInfo;
import org.geotools.gui.swing.legend.LegendNodeInfo;
import org.geotools.gui.swing.legend.LegendRootNodeInfo;
import org.geotools.gui.swing.legend.LegendRuleNodeInfo;
import org.geotools.gui.swing.legend.LegendTreeCellNameEditor;
import org.geotools.gui.swing.legend.LegendTreeCellRendererSwitcher;
import org.geotools.gui.swing.sldeditor.SLDEditor;
import org.geotools.gui.swing.sldeditor.style.StyleDialog;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.map.event.MapLayerEvent;
import org.geotools.map.event.MapLayerListListener;
import org.geotools.renderer.LegendIconMaker;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactoryImpl;
import org.opengis.filter.Filter;


/**
 * <p>The Legend component is a JTree-like component designed to show the layers
 * and the styles contained in a @link org.geotools.map.MapContext.</p> 
 * <p>The component is listening to the map context layer list, so it will show
 * automatically any change to the layer list or to the style of a single layer.
 * On double click over a layer or rule node it will show up the style editing
 * dialog if the style editing is not turned off.</p>
 * <p>You can turn off the default
 * style editing dialog and intercept the selection and mouse events to override
 * the default behaviour.</p> 
 * 
 *
 * @author jianhuij
 * @author aaime
 */
public class Legend extends javax.swing.JPanel implements MapLayerListListener, SLDEditor,
    java.io.Serializable {
	private static final long serialVersionUID = 1L;

	/** The logger for the Legend module. */
    private static final Logger LOGGER = Logger.getLogger("org.geotools.gui.swing");

    // map context get from mapPane
    private MapContext context;
    private String title;

    // if a node is selected in the JTree, then second right click will bring up
    // a pop up menu
    protected TreePath oldSelectedPath = null;

    // use layerName as key to get layer object;
//    private Hashtable layerStore = null;

    // initial legend note icon width, the height will be the same as width
    private int iconWidth = UIManager.getIcon("Tree.openIcon").getIconWidth();
    private javax.swing.JTree legendTree;
    private javax.swing.JScrollPane legendViewerJScrollPane;

    /**
     * Creates a new Legend editor based on a map context and a title
     * @param context
     * @param title
     */
    public Legend(MapContext context, String title) {
        legendTree = new javax.swing.JTree();
        setLayout(new java.awt.BorderLayout());
        legendViewerJScrollPane = new javax.swing.JScrollPane(legendTree);
        add(legendViewerJScrollPane, java.awt.BorderLayout.CENTER);

        legendTree.setCellRenderer(new LegendTreeCellRendererSwitcher());
        legendTree.setCellEditor(new LegendTreeCellNameEditor(this));

        legendTree.setShowsRootHandles(true);
        legendTree.putClientProperty("JTree.lineStyle", "None");
        legendTree.setToggleClickCount(3);

        legendTree.setEditable(true);
        legendTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        legendTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    oldSelectedPath = e.getPath();
                }
            });

        legendTree.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    treeClicked(e);
                }
            });

        this.setTitle(title);
        setMapContext(context);
        this.repaint();
    }

    /**
     * Override in subclasses to get a different behaviour on node click
     * @param e 
     */
    protected void treeClicked(MouseEvent e) {
        if (e.getClickCount() >= 2 && legendTree.getSelectionPath() != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) legendTree.getSelectionPath()
                                                                             .getLastPathComponent();

            if (node.getUserObject() instanceof LegendRuleNodeInfo) {
                node = (DefaultMutableTreeNode) node.getParent();
            }

            if (node.getUserObject() instanceof LegendLayerNodeInfo) {
                LegendLayerNodeInfo info = (LegendLayerNodeInfo) node.getUserObject();
                MapLayer layer = info.getMapLayer();

                StyleDialog sd = StyleDialog.createDialog(this, layer.getFeatureSource(),
                        layer.getStyle());
                sd.setVisible(true);

                if (sd.exitOk()) {
                    layer.setStyle(sd.getStyle());
                }
            }
        }
    }

    
    /**
     * Builds the tree representation of the <code>context</code> parameter
     * @param context The context to be depicted by a tree
     * @return A tree model that represents the <code>context</code> object
     */
    private DefaultTreeModel contructTreeModelAndLayerList(MapContext context) {
        DefaultTreeModel model = null;

        MapLayer[] layers = context.getLayers();
        boolean isRootSelected = false;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(context.getTitle());

        for (int i = 0; i < layers.length; i++) {
        	LOGGER.info("Construyendo capa " + layers[layers.length - i - 1].getTitle());
            root.add(contructLayer(layers[layers.length - i - 1]));

            // if all layers are invisible, root will be unselected in its checkbox selection
            if (layers[i].isVisible()) {
                isRootSelected = true;
            }
        }

        root.setUserObject(new LegendRootNodeInfo(context, context.getTitle() + " Legend",
                isRootSelected));
        model = new DefaultTreeModel(root);

        return model;
    }

    /**
     * Build the node representation of a map layer
     */
    private DefaultMutableTreeNode contructLayer(MapLayer layer) {
        DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode(layer.getStyle().getTitle());

        // layerNode.setUserObject( new LegendNoteObject( true, StyleFactory.createStyleFactory().getDefaultGraphic(), LegendNoteObject.LAYER, layer.getStyle().getTitle() ) );
        //layerNode.setUserObject( new LegendTreeCellRender( true, null, LegendTreeCellRender.LAYERNOTE, layer.getStyle().getTitle(), layer ) );
        // if invisable, set the checkbox unselected
        layerNode.setUserObject(new LegendLayerNodeInfo(layer.getStyle().getTitle(),
                layer.isVisible(), layer));

        Style style = layer.getStyle();
        FeatureTypeStyle[] fts = style.getFeatureTypeStyles();

        for (int i = 0; i < fts.length; i++) {
            Rule[] rules = fts[i].getRules();

            for (int j = 0; j < rules.length; j++) {
                // make legend layer style element here
                layerNode.add(constructRuleNote(rules[j]));
            }
        }

        return layerNode;
    }

    private DefaultMutableTreeNode constructRuleNote(Rule rule) {
        //Graphic[] legendGraphic = rule.getLegendGraphic();
        Filter filter = rule.getFilter();

        if (rule.getTitle().equalsIgnoreCase(new StyleFactoryImpl().createRule()
                                                             .getTitle()) && (filter != null)) {
            rule.setTitle(filter.toString());
        }

        //Symbolizer[] symb = rule.getSymbolizers();
        DefaultMutableTreeNode elementNode;
        LegendRuleNodeInfo userObject;

        //elementNode = new DefaultMutableTreeNode( filter.toString() );
        //userObject = new LegendStyleElementNodeInfo( filter.toString(), null );
        elementNode = new DefaultMutableTreeNode(rule.getTitle());
        userObject = new LegendRuleNodeInfo(rule.getTitle(), null, rule);

        userObject.setIcon(LegendIconMaker.makeLegendIcon(iconWidth, rule));
        elementNode.setUserObject(userObject);

        return elementNode;
    }

    public JMenuItem constructSetSelectionMenu(final DefaultMutableTreeNode node) {
        //final LegendTreeCellRender userObject = (LegendTreeCellRender) node.getUserObject();
        final LegendNodeInfo userObject = (LegendNodeInfo) node.getUserObject();

        JMenuItem changeSelectStateMenuItem = new JMenuItem();

        if (userObject.isSelected()) {
            changeSelectStateMenuItem.setText("UnSelect");
        } else {
            changeSelectStateMenuItem.setText("Select");
        }

        changeSelectStateMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    //if ( userObject.getNoteType().equalsIgnoreCase( userObject.LEGENDROOT) ) {
                    if (userObject instanceof LegendRootNodeInfo) {
                        int layerCount = node.getChildCount();

                        for (int i = 0; i < layerCount; i++) {
                            DefaultMutableTreeNode layerNode = (DefaultMutableTreeNode) node
                                .getChildAt(i);
                            LegendLayerNodeInfo layerUserObject = (LegendLayerNodeInfo) layerNode
                                .getUserObject();

                            //if ( userObject.isLayerSelected() && layerUserObject.getNoteType().equalsIgnoreCase( userObject.LAYERNOTE ) ) {
                            if (userObject.isSelected() && (layerUserObject.getMapLayer() != null)) {
                                layerUserObject.getMapLayer().setVisible(false);
                            } else if (!userObject.isSelected()
                                    && (layerUserObject.getMapLayer() != null)) {
                                layerUserObject.getMapLayer().setVisible(true);
                            }
                        }
                    } else {
                        if (userObject.isSelected()) {
                            if (((LegendLayerNodeInfo) userObject).getMapLayer() != null) {
                                ((LegendLayerNodeInfo) userObject).getMapLayer().setVisible(false);
                            }
                        } else if (!userObject.isSelected()) {
                            if (((LegendLayerNodeInfo) userObject).getMapLayer() != null) {
                                ((LegendLayerNodeInfo) userObject).getMapLayer().setVisible(true);
                            }
                        }
                    }

                    contextChanged();
                }
            });

        return changeSelectStateMenuItem;
    }

    private void setMapContext(MapContext context) {
        if (this.context != null) {
            context.removeMapLayerListListener(this);
        }

        if (context == null) {
            context = new DefaultMapContext(Util.crs);
        }

        this.context = context;
        context.addMapLayerListListener(this);
        contextChanged();
    }

    public MapContext getMapContext() {
        return this.context;
    }
    
    public Object getSelectedObject() {
        TreePath selectionPath = legendTree.getSelectionPath();
        if(selectionPath == null)
            return null;
        
        if(selectionPath.getPathCount() == 1) {
            return null;
        } else if(selectionPath.getPathCount() == 2){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            LegendLayerNodeInfo layerInfo = (LegendLayerNodeInfo) node.getUserObject();
            return layerInfo.getMapLayer();
        } else {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            LegendRuleNodeInfo ruleInfo = (LegendRuleNodeInfo) node.getUserObject();
            return ruleInfo.getRule();
        }
    }
    
    private DefaultMutableTreeNode getRoot() {
        DefaultTreeModel model = (DefaultTreeModel) legendTree.getModel();
        return (DefaultMutableTreeNode) model.getRoot();
    }
    
    public MapLayer getSelectedLayer() {
        TreePath selectionPath = legendTree.getSelectionPath();
        if(selectionPath == null)
            return null;
        
        while(selectionPath.getPathCount() > 2) {
            selectionPath = selectionPath.getParentPath();
        }
        if(selectionPath.getPathCount() == 1) {
            return null;
        } else {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            LegendLayerNodeInfo layerInfo = (LegendLayerNodeInfo) node.getUserObject();
            return layerInfo.getMapLayer();
        }
    }

    public void layerListChanged(EventObject layerListChangedEvent) {
        DefaultTreeModel treeModel = contructTreeModelAndLayerList(context);

        if (treeModel != null) {
            legendTree.setModel(treeModel);
        }

        legendTree.repaint();
    }

    public void contextChanged() {
        MapLayer selection = getSelectedLayer();
        DefaultTreeModel treeModel = contructTreeModelAndLayerList(context);

        if (treeModel != null) {
            legendTree.setModel(treeModel);
            if(selection != null) 
                setSelectedLayer(selection);
        }

    }

    /**
     * @param selection
     */
    private boolean setSelectedLayer(MapLayer selection) {
        DefaultMutableTreeNode root = getRoot();
        DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getFirstChild();
        while(child != null) {
            if(((LegendLayerNodeInfo) child.getUserObject()).getMapLayer() == selection) {
                legendTree.setSelectionPath(new TreePath(child.getPath()));
                return true;
            }
            child = child.getNextSibling();
        }
        return false;
    }

    /**
     * @param selection
    private void setSelectedObject(Object selection) {
    }
     */

    public void layerAdded(org.geotools.map.event.MapLayerListEvent event) {
        contextChanged();
    }

    public void layerChanged(org.geotools.map.event.MapLayerListEvent event) {
        if (event.getMapLayerEvent() != null) {
            if (event.getMapLayerEvent().getReason() == MapLayerEvent.STYLE_CHANGED || event.getMapLayerEvent().getReason() == MapLayerEvent.VISIBILITY_CHANGED) {
                contextChanged();
            }
        }
    }

    public void layerMoved(org.geotools.map.event.MapLayerListEvent event) {
        contextChanged();
    }

    public void layerRemoved(org.geotools.map.event.MapLayerListEvent event) {
        contextChanged();
    }

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
