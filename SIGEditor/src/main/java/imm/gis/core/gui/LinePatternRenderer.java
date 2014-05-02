package imm.gis.core.gui;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JTextField;
import javax.swing.ListCellRenderer;


public class LinePatternRenderer extends JTextField implements ListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float dash[];
	private Stroke stroke;
	private Stroke normalStroke;
	
	public LinePatternRenderer(){
		super();
		normalStroke = new BasicStroke(5.0f, 
                BasicStroke.CAP_BUTT, 
                BasicStroke.JOIN_BEVEL);
	}
	
	public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value,
            										int index, boolean isSelected,	boolean cellHasFocus){
    	dash = (float[])value;
    	setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
    	return this;
    }	

    public void update(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		
		stroke = g.getStroke();
		g.setColor(Color.BLACK);
		int beginX = (int)Math.round(getWidth() * 0.1);
		int endX = (int)Math.round(getWidth() * 0.9);
		int beginY = (int)Math.round(getHeight() / 2);

		if (dash !=  null && dash.length > 0){
			g.setStroke(new BasicStroke(5.0f, 
					                    BasicStroke.CAP_BUTT, 
					                    BasicStroke.JOIN_BEVEL, 
					                    10.0f, 
					                    dash, 
					                    0.0f));
		} else {
			g.setStroke(normalStroke);
		}
		
		g.drawLine(beginX, beginY, endX, beginY);
		g.setStroke(stroke);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		update(g);
	}
}
