package imm.gis.core.layer.definition;

import java.io.Serializable;

public class DataOriginDefinition implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6434042860778699207L;

//	static public final String TIPO_POSTGIS = "POSTGIS";
	
	private TableDefinition td;
	private DataSourceDefinition ds;
	
	public DataOriginDefinition() {
		this.td = null;
		this.ds = null;
	}
	
	public DataOriginDefinition(DataSourceDefinition ds, TableDefinition td) {
		this.ds = ds;
		this.td = td;
	}
	
	
	public TableDefinition getTableDefinition() {
		return this.td;
	}
	
	public void setTableDefinition(TableDefinition td) {
		this.td = td;
	}
	
	public DataSourceDefinition getDataSourceDefinition() {
		return this.ds;
	}
	
	public void setDataSourceDefinition(DataSourceDefinition ds) {
		this.ds = ds;
	}
	

	public String toString() {
		String descripcion = "Tabla   --> \n\n";
		descripcion += this.td;
		descripcion += "\n DataSourceDefinition -> \n\n";
		descripcion += this.ds;
		
		return descripcion;
	}

	/*
	public int hashCode(){
		return nombre.hashCode();
	}
	*/
	
	public boolean equals(Object o){
		if (!(o instanceof DataOriginDefinition)){
			return false;
		}
		
		DataOriginDefinition dod = (DataOriginDefinition)o;
		
		return 	this.ds.equals(dod.getDataSourceDefinition()) && this.td.equals(dod.getTableDefinition());
	}
}
