package imm.gis.core.gui.layermanager;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.geotools.gui.swing.legend.LegendRuleNodeInfo;
import org.geotools.gui.swing.legend.LayerManagerLegendRenderer;

class LayerManagerRenderer implements TreeCellRenderer {
    LayerManagerLegendRenderer ruleRenderer = new LayerManagerLegendRenderer();
    LayerManagerLayerRenderer layerRenderer = new LayerManagerLayerRenderer();
    DefaultTreeCellRenderer rootRenderer = new DefaultTreeCellRenderer();


    public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        rootRenderer.setLeafIcon(null);
        rootRenderer.setClosedIcon(null);
        rootRenderer.setOpenIcon(null);
        
        TreeCellRenderer tcr = rootRenderer;
        
        if (((DefaultMutableTreeNode) value).getUserObject() instanceof LegendRuleNodeInfo) {
            tcr = ruleRenderer;
        }
        else if (((DefaultMutableTreeNode) value).getUserObject() instanceof LayerNodeInfo) {
            tcr = layerRenderer;
        }
        else {
        	tcr = rootRenderer;
        }

        return tcr.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}
