package imm.gis.core.layer.definition;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TableDefinition implements Serializable{

	private static final long serialVersionUID = 1L;
	private String schema= null;
	private String sequence = null;
	private String name;
	private String alias = null;
	private String fid = null;
	private Map<String, TableAttributeDefinition> attributes;
	
	public TableDefinition() {
		this.name = null;
		this.attributes = new HashMap<String, TableAttributeDefinition>();
	}
	
	public TableDefinition(String schema, String name, Map<String, TableAttributeDefinition> attributes, String sequence, String fid) {
		setName(name);
		setAttributes(attributes);
		setSchema(schema);
		setSequence(sequence);
		setFid(fid);
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getName() {
		return this.name;
	}
	/**
	 * 
	 * @return si getSchema()!=null getSchema()+"."+getName()
	 */
	public String getFullName(){
		return getSchema()==null ? getName() + " " + getAlias():getSchema()+"."+getName()+" " + getAlias();
	}
	
	public Map<String, TableAttributeDefinition> getAttributes() {
		return this.attributes;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setAttributes(Map<String, TableAttributeDefinition> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(TableAttributeDefinition _def) {
		this.attributes.put(_def.getLayerName(),_def);
	}
	
	public String toString() {
		String descripcion = "DefinicionTabla:\n" + 
			"\tNombre             - " + this.name + "\n" +
			"\tAtributos          -->\n\n";
		
		Iterator<TableAttributeDefinition> i = this.attributes.values().iterator();
		
		while (i.hasNext()) {
			TableAttributeDefinition a = i.next();
			descripcion += a + "\n";
		}
		
		return descripcion;
	}

	public String getSchema() {
		return schema;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = (sequence != null) ? sequence.toUpperCase() : null;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public void setAlias(String alias) {
		this.alias = (alias == null) ? name : alias;
	}

	public String getAlias() {
		return alias;
	}

	
	// Agregado por Gabriela G. 18/09/2009
	public String getNameSinAlias() {
		return getSchema()==null ? getName():getSchema()+"."+getName();
	}

}
