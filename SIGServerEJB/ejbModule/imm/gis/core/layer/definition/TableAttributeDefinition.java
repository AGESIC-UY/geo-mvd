package imm.gis.core.layer.definition;

import java.io.Serializable;

public class TableAttributeDefinition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6153094197780965239L;
	private String tableName;
	private String layerName;
	
	public TableAttributeDefinition() {
		this.layerName = null;
		this.tableName = null;
	}
	
	public TableAttributeDefinition(String layerName, String tableName) {
		this.layerName = layerName;
		this.tableName = tableName;
	}
	
	public String getLayerName() {
		return this.layerName;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String toString() {
		String descripcion = "DefinicionAtributoTabla:\n" +
			"\tNombre en la capa  - " + this.layerName + "\n" +
			"\tNombre en la tabla - " + this.tableName;
		
		return descripcion;
	}
}
