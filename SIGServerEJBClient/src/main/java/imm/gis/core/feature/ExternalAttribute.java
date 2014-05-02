package imm.gis.core.feature;

import java.io.Serializable;

public class ExternalAttribute implements Serializable {

	private static final long serialVersionUID = -3303570270228996274L;
	
	private String externalFID;
	private Object value;
	private String originLayer;
	
	public ExternalAttribute(String externalID, Object value, String originLayer){
		this.externalFID = externalID;
		this.value = value;
		this.originLayer = originLayer;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getExternalFID() {
		return externalFID;
	}

	public void setExternalFID(String externalFID) {
		this.externalFID = externalFID;
	}
	
	public String toString(){
		return value == null ? null : value.toString();
	}

	public boolean equals(Object o) {
		if (o instanceof ExternalAttribute) {
			ExternalAttribute ea = (ExternalAttribute) o;
			return (externalFID != null &&
					ea.getExternalFID() != null &&
					externalFID.equals(ea.getExternalFID()) &&
					value != null &&
					ea.getValue() != null &&
					value.equals(ea.getValue()));
		}
		else{
			if(o instanceof String){
				return value.equals(o.toString());
			}
		}
		
		return false;
	}

	public int hashCode(){
		return this.getExternalFID().hashCode();
	}
	
	
	public int compareTo(Object o){
		ExternalAttribute tmp = (ExternalAttribute)o;
		
		return this.getExternalFID().compareTo(tmp.getExternalFID());
	}

	public String getOriginLayer() {
		return this.originLayer;
	}
}
