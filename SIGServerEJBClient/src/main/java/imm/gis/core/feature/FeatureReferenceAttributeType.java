package imm.gis.core.feature;


import java.io.Serializable;

import org.opengis.filter.Filter;


/**
 * AttributeType para atributos que simplemente hacen referencia a otros features.
 * 
 * @author agrassi
 *
 */
public class FeatureReferenceAttributeType extends MixtoDefaultAttributeType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8763995264263232597L;
	private Filter filter;
    
    
    
    public FeatureReferenceAttributeType(String name, Class<?> type, boolean nillable, int min, int max,
    		Object defaultValue, Filter filter) throws IllegalArgumentException {
        
    	super(name, type, nillable, min, max,  defaultValue);
        
    	this.filter = filter;
        
    	if (!FeatureReferenceAttribute.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(
                "FeatureReferenceAttributeType requires FeatureReferenceAttribute class, not " + type);
        }
    }
    
    
    public FeatureReferenceAttributeType(String name, Class<?> type, boolean nillable,
         Object defaultValue,Filter filter)
        throws IllegalArgumentException {
        
    	super(name, type, nillable, defaultValue);
        
    	this.filter = filter;
        
    	if (!FeatureReferenceAttribute.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(
            		"FeatureReferenceAttributeType requires FeatureReferenceAttribute class, not " + type);
        }
    }

    
    
    public Object parse(Object value) throws IllegalArgumentException {
    	return value;
    }

    
    
    public Object duplicate(Object o) {
    	if (o == null)
    		return null;
    	
    	FeatureReferenceAttribute att = (FeatureReferenceAttribute) o;
    	
    	return new FeatureReferenceAttribute(
    			att.getReferencedFeatureID() == null ? null : att.getReferencedFeatureID().toString(),
    			att.getReferencedLayer() == null ? null: att.getReferencedLayer().toString());
    }

	public Filter getRestriction() {
		return filter;
	}
}
