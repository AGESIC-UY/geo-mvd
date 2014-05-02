package imm.gis.core.gui.layermanager;

import imm.gis.AppContext;
import imm.gis.core.controller.IGeometryType;
import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.IMap;
import imm.gis.edition.util.GeomUtil;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.geotools.gui.swing.legend.LegendRuleNodeInfo;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.map.event.MapLayerEvent;
import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;
import org.geotools.renderer.LegendIconMaker;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;

public class LayerManagerPanel extends JPanel implements MapLayerListListener, ActionListener {
	
	private static final long serialVersionUID = 1L;
//	private static Logger log = Logger.getLogger(LayerManagerPanel.class);
	private final int iconWidth = UIManager.getIcon("Tree.openIcon").getIconWidth();
    private JTree tree;
    private IMap map;
    private AppContext applicationContext = AppContext.getInstance();
    private Icon lineIcon = GuiUtils.loadIcon("LineLayer16.gif");
    private Icon pointIcon = GuiUtils.loadIcon("PointLayer16.gif");
    private Icon polyIcon = GuiUtils.loadIcon("PolyLayer16.gif");
    private long scale;
    
	public LayerManagerPanel(IMap m){
		super(new BorderLayout());
		this.map = m;
		
		tree = new JTree(new Object[]{});
		
		tree.setToggleClickCount(0);
		tree.setCellRenderer(new LayerManagerRenderer());
        tree.setShowsRootHandles(false);
        tree.setRootVisible(false);
        tree.putClientProperty("JTree.lineStyle", "None");

//		setContext();//coreAccess.getIMap().getContext());
        add(new JScrollPane(tree), BorderLayout.CENTER);
	}
	
	public JTree getTree() {
		return this.tree;
	}

	private void setContext() {
	//	if (map.getScale() != scale){
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("Leyenda");
			MapContext mc = map.getContext();

			MapLayer mapLayer = null;
/*
			Iterator<?> mapLayers = null;
			
			synchronized (mapLayers){
				for (mapLayers = mc.iterator(); mapLayers.hasNext();){
		        	mapLayer = (MapLayer)mapLayers.next();	
		        	root.add(createLayer(mapLayer.getTitle(), mapLayer));
				}
			}
*/
			for (Iterator<?> mapLayers = mc.iterator(); mapLayers.hasNext();){
	        	mapLayer = (MapLayer)mapLayers.next();	
	        	root.add(createLayer(mapLayer.getTitle(), mapLayer));
			}
			tree.setModel(new DefaultTreeModel(root));
			for (int i = 0; i < tree.getRowCount(); i++) {
		         tree.expandRow(i);
			}
			
			scale = map.getScale();
	//	} else {
		//	tree.repaint();
	//	}
	}
	
	private DefaultMutableTreeNode createLayer(String layerName, MapLayer mapLayer) {
		DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode(layerName);
		Icon icon = null;

		switch (GeomUtil.getGeometryType(applicationContext.getCapa(layerName).getFt())) {
		case IGeometryType.LINE_GEOMETRY:
			icon = lineIcon;
			break;
		case IGeometryType.POINT_GEOMETRY:
			icon = pointIcon;
			break;
		case IGeometryType.POLYGON_GEOMETRY:
			icon = polyIcon;
			break;
		}

		LayerNodeInfo nodeInfo = new LayerNodeInfo(layerName, mapLayer.isVisible(), icon);
		layerNode.setUserObject(nodeInfo);

		if (mapLayer != null && mapLayer.isVisible()) {
			Style style = mapLayer.getStyle();
	        FeatureTypeStyle[] fts = style.getFeatureTypeStyles();
	
	        for (int i = 0; i < fts.length; i++) {
	            Rule[] rules = fts[i].getRules();
	
	            for (int j = 0; j < rules.length; j++) {
	
	            	if (hasGraphicSymbolizers(rules[j]))
	            		layerNode.add(constructRuleNote(rules[j]));
	            }
	        }
		}
		
        return layerNode;
    }
	
	private boolean hasGraphicSymbolizers(Rule r) {
		Symbolizer symbolizers[] = r.getSymbolizers();
		
		for (int i = 0; i < symbolizers.length; i++)
			if (symbolizers[i] instanceof PolygonSymbolizer ||
				symbolizers[i] instanceof LineSymbolizer ||
				symbolizers[i] instanceof PointSymbolizer)
				return true;
		
		return false;
	}

	private DefaultMutableTreeNode constructRuleNote(Rule rule) {
//		if (rule.getFilter() != null){
//	        rule.setTitle(rule.getFilter().toString());
//	    }
	
	    DefaultMutableTreeNode elementNode;
	    LegendRuleNodeInfo userObject;
	
	    elementNode = new DefaultMutableTreeNode(rule.getTitle());
	    userObject = new LegendRuleNodeInfo(rule.getTitle(), null, rule);
	
    	Icon icon = LegendIconMaker.makeLegendIcon(iconWidth, rule);
	    userObject.setIcon(icon);
	    
	    elementNode.setUserObject(userObject);
	
	    return elementNode;
	}

	public void layerAdded(MapLayerListEvent event) {
	}

	public void layerChanged(MapLayerListEvent event) {
		if (event.getMapLayerEvent().getReason() == MapLayerEvent.STYLE_CHANGED){
			setContext();
		}
	}

	public void layerMoved(MapLayerListEvent event) {
	}

	public void layerRemoved(MapLayerListEvent event) {
	}

	public void actionPerformed(ActionEvent e) {
		setContext();
	}
}
