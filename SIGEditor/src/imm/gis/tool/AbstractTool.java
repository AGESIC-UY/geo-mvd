package imm.gis.tool;

import java.awt.Cursor;

import com.vividsolutions.jts.geom.Coordinate;

public abstract class AbstractTool implements ITool {

	public Cursor getCursor() {
		return null;
	}

	public void mouseClicked(Coordinate c) {
	}

	public void mouseDoubleClicked(Coordinate c) {
	}
	
	public void mouseDragged(int x, int y, Coordinate c) {
	}

	public void mouseMoved(Coordinate c) {
	}

	public void mousePressed(int x, int y, Coordinate c) {
	}

	public void mouseReleased(int x, int y, Coordinate c) {
	}
	
	public void deactivate() {
	}
}
