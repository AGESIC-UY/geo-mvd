package imm.gis.core.layer.definition;

import java.io.Serializable;

public class DataSourceDefinition implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static public final int TYPE_POSTGIS = 1;
	static public final int TYPE_ORACLE = 2;
	
	static public final String STR_TYPE_POSTGIS = "POSTGIS";
	static public final String STR_TYPE_ORACLE = "ORACLE";
	
	private String jndiName;
	private int type;
	private boolean withProxy = false;
	
	public boolean isWithProxy() {
		return withProxy;
	}

	public void setWithProxy(boolean withProxy) {
		this.withProxy = withProxy;
	}

	private int getDataSourceType(String type) throws IllegalArgumentException {
		
		if (type.equals(STR_TYPE_POSTGIS))
			return TYPE_POSTGIS;
		else if (type.equals(STR_TYPE_ORACLE))
			return TYPE_ORACLE;
		else
			throw new IllegalArgumentException("Unknown DataSource type: "+type);
	}
	
	public DataSourceDefinition(String jndiName, String type, boolean proxy) {
		this.jndiName = jndiName;
		this.type = getDataSourceType(type);
		this.withProxy = proxy;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		if ((type == TYPE_ORACLE) || (type == TYPE_POSTGIS)){
			this.type = type;
		} else {
			throw new IllegalArgumentException("Unknown DataSource type: "+type);
		}
	}

	public String getJNDIName() {
		return this.jndiName;
	}
	
	public void setJNDIName(String name) {
		this.jndiName = name;
	}
}
