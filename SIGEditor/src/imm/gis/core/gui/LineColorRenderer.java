package imm.gis.core.gui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JTextField;

public class LineColorRenderer extends JTextField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color color = null;
	
	public LineColorRenderer(){
		super();
	}
	
	public void setColor(Color c){
		color = c;
		repaint();
	}
	
	public Color getColor(){
		return color;
	}
	
	public void update(Graphics g){
		if (color == null) return;
		
		int beginX = (int)Math.round(getWidth() * 0.3);
		int endX = getWidth() - 2*beginX;
		int beginY = (int)Math.round(getHeight() * 0.3);
		int endY = getHeight() - 2*beginY;
		
		g.setColor(color);
		g.fillRect(beginX, beginY, endX, endY);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		update(g);
	}
}
