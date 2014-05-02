package imm.gis.tool.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.IDrawPanel;
import imm.gis.navigation.NavigationController;
import imm.gis.tool.AbstractTool;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;

public class ExtendedPanTool extends AbstractTool {
	private int origX, origY;
	private int x, y;
	private IDrawPanel drawPanel;
	private Cursor cursor;
	private NavigationController navigator;
	private Coordinate originalTemp;
	private static Logger log = Logger.getLogger(ExtendedPanTool.class.getName());
	
	public ExtendedPanTool(IDrawPanel drawPanel, NavigationController navigator) {
		this.drawPanel = drawPanel;
		this.navigator = navigator;
		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("Pan32.gif").getImage(), new Point(7, 7),"");
	}

	
	public Cursor getCursor() {
		return cursor;
	}
	
	
	@Override
	public void mousePressed(int xTemp, int yTemp, Coordinate c) {
		origX = xTemp;
		origY = yTemp;
		originalTemp = c;
	}

	
	@Override
	public void mouseDragged(int xTemp, int yTemp, Coordinate c) {
		x = (xTemp > origX) ? x + (xTemp - origX) : x - (origX - xTemp);
		y = (yTemp > origY) ? y + (yTemp - origY) : y - (origY - yTemp);

		origX = xTemp;
		origY = yTemp;

		drawPanel.setImageOffset(x, y);
		drawPanel.updateLayer();
	}

	@Override
	public void mouseReleased(int xTemp, int yTemp, Coordinate c){
		if (navigator.pan(originalTemp, c)){ // Necesito recalcular la imagen
			x = y = 0;
		}	
	}
}
