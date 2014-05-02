package imm.gis.core.gui.layermanager;

import javax.swing.Icon;

class LayerNodeInfo {

	private String layerName;
	private boolean isVisibleLayer;
	private Icon geometryIcon;
	
	public LayerNodeInfo(String layer, boolean isVisibleLayer, Icon icon) {
		this.layerName = layer;
		this.isVisibleLayer = isVisibleLayer;
		geometryIcon = icon;		
	}
	
	public boolean isVisibleLayer() {
		return isVisibleLayer;
	}
	
	public void setVisibleLayer(boolean isVisibleLayer) {
		this.isVisibleLayer = isVisibleLayer;
	}

	public String getLayerName() {
		return layerName;
	}
	
	public Icon getGeometryIcon() {
		return this.geometryIcon;
	}
}
