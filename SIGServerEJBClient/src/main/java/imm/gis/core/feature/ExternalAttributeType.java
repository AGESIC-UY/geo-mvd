package imm.gis.core.feature;


import java.io.Serializable;

import org.opengis.filter.Filter;


/**
 * AttributeType para nuestros LovValue's
 * 
 * @author agrassi
 *
 */
public class ExternalAttributeType extends MixtoDefaultAttributeType implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -936113301648705595L;
	private Filter filter;
    
    
    
    public ExternalAttributeType(String name, Class<?> type, boolean nillable, int min, int max,
    		Object defaultValue, Filter filter) throws IllegalArgumentException {
        
    	super(name, type, nillable, min, max,  defaultValue);
        
    	this.filter = filter;
        
    	if (!ExternalAttribute.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(
                "LovAttributeType requires LovValue class, not " + type);
        }
    }
    
    
    public ExternalAttributeType(String name, Class<?> type, boolean nillable,
         Object defaultValue,Filter filter)
        throws IllegalArgumentException {
        
    	super(name, type, nillable, defaultValue);
        
    	this.filter = filter;
        
    	if (!ExternalAttribute.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(
                "LovAttributeType requires LovValue class, not " + type);
        }
    }

    
    
    public Object parse(Object value) throws IllegalArgumentException {
    	return value;
    }

    
    
    public Object duplicate(Object o) {
    	
    	if (o == null)
    		return null;
    	
    	ExternalAttribute att = (ExternalAttribute) o;

    	// Esto creo no esta del todo bien, dado que el valor del external atribute no se sta duplicando 
    	return new ExternalAttribute(
    			att.getExternalFID() == null ? null : att.getExternalFID().toString(),
    			att.getValue(),
    			att.getOriginLayer() == null ? null : att.getOriginLayer().toString());
    }

	public Filter getRestriction() {
		return filter;
	}
}
