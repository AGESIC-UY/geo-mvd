package imm.gis.tool.navigation;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.interfaces.IDrawPanel;
import imm.gis.navigation.NavigationController;
import imm.gis.tool.AbstractTool;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Coordinate;

public class ZoomInTool extends AbstractTool {

	private Coordinate initialCoordinate;
	
	private ICoreAccess coreAccess;
	private Cursor cursor;

	private NavigationController navigator;
	
	public ZoomInTool(ICoreAccess coreAccess, NavigationController navigator) {
		this.coreAccess = coreAccess;
		this.navigator = navigator;
		
		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("ZoomIn16.gif").getImage(), new Point(7, 7),"");
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void mousePressed(int x, int y, Coordinate c) {
		initialCoordinate = c;		
	}
	
	public void mouseDragged(int x, int y, Coordinate c) {
		Point2D p = coreAccess.getIMap().worldToPixel(initialCoordinate);
		
		Rectangle2D r = new Rectangle2D.Double(p.getX(), p.getY(), 0, 0);
		
		r.add(coreAccess.getIMap().worldToPixel(c));

		coreAccess.getIDrawPanel().setTmpShape(r, IDrawPanel.NORMAL_LINE);
		coreAccess.getIDrawPanel().updateLayer();
	}

	public void mouseReleased(int x, int y, final Coordinate c) {
		navigator.zoomIn(initialCoordinate, c);
	}
}
