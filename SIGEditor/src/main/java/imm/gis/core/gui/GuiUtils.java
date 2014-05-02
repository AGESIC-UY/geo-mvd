package imm.gis.core.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.ImageIcon;

public class GuiUtils {

	static public void centerWindowOnScreen(Window win){
		Dimension d1 = Toolkit.getDefaultToolkit().getScreenSize();
		
		int y = (int)Math.round((d1.getHeight() - win.getHeight())/2);
		int x = (int)Math.round((d1.getWidth() - win.getWidth())/2);
		
		win.setLocation(x, y);
	}

	public static void centerWindow(Window componentToCentreOn,
            Window componentToMove) {
        Dimension componentToCentreOnSize = componentToCentreOn.getSize();
        componentToMove.setLocation(
                componentToCentreOn.getX()
                        + ((componentToCentreOnSize.width - componentToMove
                                .getWidth()) / 2), componentToCentreOn.getY()
                        + ((componentToCentreOnSize.height - componentToMove
                                .getHeight()) / 2));
    }
	
	public static void fullScreen(Window win){
		java.awt.Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		win.setLocation(0, 0);
		win.setSize((int)Math.round(dim.getWidth()), (int)Math.round(dim.getHeight()));
		win.validate();
	}
	
	public static ImageIcon loadIcon(String name){
		ImageIcon res = null;
		
		java.net.URL url = GuiUtils.class.getClassLoader().getResource("images/" + name);
		if (url != null){
			res = new javax.swing.ImageIcon(url);
		}
		
		return res;
	}
	
	static public class ConstraintGroup extends GridBagConstraints {

		private static final long serialVersionUID = 1L;
		private static final int NUM_CONSTRAINTS = 4;
		private int[][] constraints;
		private double[][] weights;
		
		public ConstraintGroup(int[][] constraints, double[][] weights) {
			super();
			this.constraints = constraints;
			this.weights = weights;
		}
		
		public void setConstraints(int element) {
			int[] location = constraints[element * NUM_CONSTRAINTS];
			int[] area = constraints[element * NUM_CONSTRAINTS + 1];
			int[] size = constraints[element * NUM_CONSTRAINTS + 2];
			int[] insets = constraints[element * NUM_CONSTRAINTS + 3];
			double[] weights = this.weights[element];
			this.weightx = weights[0];
			this.weighty = weights[1];
			this.gridx = location[0];
			this.gridy = location[1];
			this.gridwidth = area[0];
			this.gridheight = area[1];
			this.fill = size[0];
			this.anchor = size[1];
			if (insets != null) {
				this.insets = new Insets(insets[0],insets[1],insets[2],insets[3]);
			}
		}
		
		public GridBagConstraints getConstraints(int element) {
			setConstraints(element);
			return this;
		}
	}	
	
	
	
	
}
