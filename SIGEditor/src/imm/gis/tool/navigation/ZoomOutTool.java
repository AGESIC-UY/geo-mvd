package imm.gis.tool.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.navigation.NavigationController;
import imm.gis.tool.AbstractTool;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import com.vividsolutions.jts.geom.Coordinate;

public class ZoomOutTool extends AbstractTool {

	private Cursor cursor;
	private NavigationController navigator;
	
	public ZoomOutTool(NavigationController navigator) {
		this.navigator = navigator;
		
		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("ZoomOut16.gif").getImage(), new Point(7, 7),"");
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void mouseClicked(final Coordinate c) {
		navigator.zoomOut(c);
	}
}
