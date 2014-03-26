package imm.gis.core.layer.definition;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



/**
 * Guarda la información sobre la conformación de una capa geográfica
 * @author sig
 *
 */
public class LayerDefinition implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4857810294174580367L;
	private Map<String, LayerAttributeDefinition> layerAttributes;
	private Map<String, Integer> attributeOrder;
	private int attributeIndex = 0;
	
	private DataOriginDefinition dataOriginDefinition;
	private String name;
	private boolean readOnly = true;
	
	/**
	 * Constructor por defecto
	 *
	 */
	public LayerDefinition() {
		layerAttributes = new HashMap<String, LayerAttributeDefinition>();
		attributeOrder = new HashMap<String, Integer>();
		
		dataOriginDefinition = null;
		name = null;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Map<String, LayerAttributeDefinition> getLayerAttributeDefinitions() {
		return this.layerAttributes;
	}
	
	public LayerAttributeDefinition[] getOrderedLayerAttributeDefinitions() {
		LayerAttributeDefinition attributes[] = new LayerAttributeDefinition[layerAttributes.size()];
		
		Iterator<String> i = layerAttributes.keySet().iterator();
		
		while (i.hasNext()) {
			Object key = i.next();
			int index = attributeOrder.get(key).intValue();
			attributes[index] = layerAttributes.get(key);
		}
		
		return attributes;
	}
	
	public DataOriginDefinition getDataOriginDefinition() {
		return this.dataOriginDefinition;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDataOriginDefinition(DataOriginDefinition dataOriginDefinition) {
		this.dataOriginDefinition = dataOriginDefinition;
	}
	
	public void addLayerAttributeDefinition(LayerAttributeDefinition def) {
		this.layerAttributes.put(def.getName(),def);
		attributeOrder.put(def.getName(), new Integer(attributeIndex));
		attributeIndex++;
	}
	
	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public String toString() {
		String descripcion = "DefinicionCapa:\n" +
			"\tNombre          - " + this.name + "\n" +
			"\tAtributos       -->\n\n";
		
		Iterator<LayerAttributeDefinition> i = this.layerAttributes.values().iterator();
		
		while (i.hasNext()) {
			LayerAttributeDefinition a = i.next();
			descripcion += a + "\n";
		}
		
		descripcion += "\tOrigen de datos -->\n\n";
		descripcion += this.dataOriginDefinition + "\n";
		
		return descripcion;
	}
	
	
}
