package imm.gis.core.data.mixto;

import imm.gis.core.data.mixto.attributeio.LayerIO;

class SQLBuilderResult {
	
	private LayerIO layerIO = null;
	private String sql = null;
	private int selectedColumnsCount = 0;
	
	public void setLayerIO(LayerIO layerIO) {
		this.layerIO = layerIO;
	}
	
	public LayerIO getLayerIO() {
		return this.layerIO;
	}
	
	public void setSQL(String sql) {
		this.sql = sql;
	}
	
	public String getSQL() {
		return this.sql;
	}
	
	public void setSelectedColumnsCount(int selectedColumnsCount) {
		this.selectedColumnsCount = selectedColumnsCount;
	}
	
	public int getSelectedColumnsCount() {
		return this.selectedColumnsCount;
	}
}
