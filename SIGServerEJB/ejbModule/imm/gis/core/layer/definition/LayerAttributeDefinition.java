package imm.gis.core.layer.definition;

import java.io.Serializable;

public class LayerAttributeDefinition implements Serializable {
	
	private static final long serialVersionUID = -1080675337415543241L;
	
	private String name;
	private String type;
	private String referencedLayer;
	private boolean isNillable = false;
	
	public LayerAttributeDefinition(String name, String type, String referencedLayer, boolean isNillable) {
		super();
		this.name = name;
		this.type = type;
		this.referencedLayer = referencedLayer;
		this.isNillable = isNillable;
	}

	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getReferencedLayer() {
		return referencedLayer;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setReferencedLayer(String layer) {
		referencedLayer = layer;
	}
	
	public String toString() {
		String descripcion = "DefinicionAtributo:\n" +
			"\tNombre            - " + this.name + "\n" +
			"\tTipo              - " + this.type + "\n";
			
		return descripcion;
	}

	public boolean isNillable() {
		return isNillable;
	}

	public void setNillable(boolean isNillable) {
		this.isNillable = isNillable;
	}
}
