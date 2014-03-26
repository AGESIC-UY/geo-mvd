package imm.gis.core.layer.metadata;

import java.io.Serializable;

public class LayerAttributePresentation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int ATTR_PRESENTATION_DEFAULT = 1;
	public final static int ATTR_PRESENTATION_TEXT_AREA = 2;
	public static final int ATTR_PRESENTATION_URL = 3;
	
	private int type = ATTR_PRESENTATION_DEFAULT;

	private int columns = 0;
	private int rows = 0;
	private boolean unique = false;
	private boolean locking = false;
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	public int getColumns() {
		return this.columns;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public int getRows() {
		return this.rows;
	}
	
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	public boolean isUnique() {
		return unique;
	}
	
	public void setLocking(boolean locking) {
		this.locking = locking;
	}
	
	public boolean isLocking() {
		return locking;
	}
}
