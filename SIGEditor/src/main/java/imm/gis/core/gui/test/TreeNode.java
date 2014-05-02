package imm.gis.core.gui.test;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	private DefaultMutableTreeNode alim;
	private DefaultMutableTreeNode linea;
	private DefaultMutableTreeNode puesta;
	

	public TreeNode(){
		super();
		puesta = new DefaultMutableTreeNode("Puesta X1.Y1.Z1");
		linea = new DefaultMutableTreeNode("Linea X1.Y1");
		linea.add(puesta);
		alim = new DefaultMutableTreeNode("Punto Alimentacion X1");
		alim.add(linea);
		add(alim);

		puesta = new DefaultMutableTreeNode("Puesta X2.Y1.Z1");
		linea = new DefaultMutableTreeNode("Linea X2.Y1");
		linea.add(puesta);
		linea.add(new DefaultMutableTreeNode("Puesta X2.Y1.Z2"));
		alim = new DefaultMutableTreeNode("Punto Alimentacion X2");
		alim.add(linea);
		linea = new DefaultMutableTreeNode("Linea X2.Y2");
		linea.add(new DefaultMutableTreeNode("Puesta X2.Y2.Z1"));
		alim.add(linea);
		add(alim);
	
	}
}
