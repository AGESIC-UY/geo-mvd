package imm.gis.core.interfaces;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Shape;
import com.vividsolutions.jts.geom.Geometry;


public interface IDrawPanel {

	public static final int NORMAL_LINE = 0;
	public static final int DASHED_LINE = 1;
	public static final int BLINK_LINE = 2;

	public void setTmpShape(Shape tmp);
	public void setTmpShape(Shape tmp, int type);
	
	public void updateLayer();
	public Dimension getSize();
	public void setCursor(Cursor c);
	public void setImageOffset(int x, int y);
	public int getXImageOffset();
	public int getYImageOffset();
	public void setUseDragCache(boolean useDragCache);
	public boolean isUseDragCache();
	
	public void paintGeometry(Geometry geometry);

}
