package imm.gis.core.gui.test;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private ImageIcon iconPuesta;
	private ImageIcon iconPtoAlim;
	private ImageIcon iconLinea;

	public TreeRenderer() {
		super();
		iconPuesta = new ImageIcon("images/Puesta16_1.gif"); 
		iconPtoAlim = new ImageIcon("images/PtoALim16_1.gif");
		iconLinea = new ImageIcon("images/Linea16.gif");
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		String text = value.toString();
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		
		if (text == null){ 
			setIcon(null);
		} else if (leaf) {
			setIcon(iconPuesta);
		} else if (text.startsWith("Linea")){
			setIcon(iconLinea);
		} else if (text.startsWith("Punto Alim")){
			setIcon(iconPtoAlim);
		} else {
			setIcon(null);
		}

		return this;
	}

}
