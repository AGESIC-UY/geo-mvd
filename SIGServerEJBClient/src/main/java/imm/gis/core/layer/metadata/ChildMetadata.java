package imm.gis.core.layer.metadata;

import java.io.Serializable;


/**
 * Para la definición de las tablas hijas
 */
public class ChildMetadata implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6404552260734428318L;
	/**
	 * 
	 */
	
	private String layerName;
	private String parentIdAttribute;
	
	public ChildMetadata(String layerName, String parentIdAttribute) {
		this.layerName = layerName;
		this.parentIdAttribute = parentIdAttribute;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getParentIdAttribute() {
		return parentIdAttribute;
	}

	public void setParentIdAttribute(String parentIdAttribute) {
		this.parentIdAttribute = parentIdAttribute;
	}
	
}
