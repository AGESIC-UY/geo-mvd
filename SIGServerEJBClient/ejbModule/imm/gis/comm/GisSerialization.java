package imm.gis.comm;

import imm.gis.GisException;
import imm.gis.core.feature.MixtoAttributeTypeFactory;
import imm.gis.core.feature.OrderedCollectionFeatureReader;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;


import org.geotools.data.FeatureReader;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;
import org.geotools.filter.ExpressionBuilder;
import org.geotools.filter.LengthFunction;
import org.geotools.filter.parser.ParseException;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.expression.Literal;

public class GisSerialization implements Serializable {
    private static final long serialVersionUID = 1L;
    private static ExpressionBuilder eb = new ExpressionBuilder();

    public static class FeatureCollectionTransporter implements Serializable {
        private static final long serialVersionUID = 1L;
        FeatureTypeTransporter featureTypeTransporter;
        List<FeatureTransporter> featureTransporters = new LinkedList<FeatureTransporter>();
        
        public FeatureCollectionTransporter(){
        	
        }
        

        /**
		 * @param featureTypeTransporter
		 *            The FeaturType for this collection.
		 */
        public FeatureCollectionTransporter(FeatureTypeTransporter featureTypeTransporter) {
            this.featureTypeTransporter = featureTypeTransporter;
        }

        /**
		 * Add a feature to this collection. It will be wrapped in a
		 * FeatureTransporter.
		 * 
		 * @param feature
		 */
        public void addFeature(Feature feature) {
            featureTransporters.add(new FeatureTransporter(feature));
        }

        /**
		 * @param features
		 *            Will all be added in FeatureTransporter wrappers.
		 */
        public void addFeatures(FeatureCollection features) {
            for (FeatureIterator iter = features.features(); iter.hasNext(); 
            ) {
                Feature element = iter.next();
                addFeature(element);
            }
        }

        /**
		 * @param reader
		 *            Features will all be added in FeatureTransporter wrappers.
		 * @throws GisException
		 *             if reader throws an exception.
		 */
        public void addFeatures(FeatureReader reader) throws GisException {
            try {
            	Feature feature = null;
            	
                while (reader.hasNext()) {
                	feature = reader.next();
                    addFeature(feature);
                }
            } catch (NoSuchElementException e) {
                throw new GisException(e);
            } catch (IOException e) {
                throw new GisException(e);
            } catch (IllegalAttributeException e) {
                throw new GisException(e);
            } finally{
            	try{reader.close();}catch(IOException e){e.printStackTrace();};
            }
        }

        /**
		 * @param results
		 *            Features will all be added in FeatureTransporter wrappers.
		 * @throws GisException
		 *             if results throws an exception.
		 */
        public void addFeatures(Iterator<Feature> results){
           	while (results.hasNext()){
                   addFeature(results.next());            		
           	}
        }

        /**
		 * @return An unwrapped FeatureCollection.
		 * @throws GisException
		 *             if there's a bug.
		 */
        @SuppressWarnings("unchecked")
		public FeatureCollection toFeatureCollection() throws GisException {
            try {
                FeatureType featureType = 
                    featureTypeTransporter.toFeatureType();
                FeatureCollection collection = 
                    FeatureCollections.newCollection();
                FeatureTransporter element;
                
                for (Iterator<FeatureTransporter> iter = featureTransporters.iterator(); 
                     iter.hasNext(); ) {
                    element = iter.next();
                    collection.add(element.toFeature(featureType));
                }
                return collection;
            } catch (NoSuchElementException e) {
                throw new GisException(e);
            }
        }

        public FeatureReader toOrderedFeatureReader() throws GisException{
        	List<Feature> list = new ArrayList<Feature>();
            FeatureTransporter element;
            FeatureType featureType = featureTypeTransporter.toFeatureType();

            for (Iterator<FeatureTransporter> iter = featureTransporters.iterator(); iter.hasNext(); ) {
            	element = iter.next();
            	list.add(element.toFeature(featureType));
            }
            
            return new OrderedCollectionFeatureReader(list, featureType);
        }        
    }
    

    /**
	 * Serializable wrapper around a FeatureType.
	 */
    public static class FeatureTypeTransporter implements Serializable {
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;

        /**
		 * 
		 */


        String name;

        URI namespaceURI;

        AttributeTypeTransporter[] attributeTypeTransporters;
        AttributeType atributos[];

        /**
		 * @param featureType
		 *            The type to wrap.
		 */
        public FeatureTypeTransporter(FeatureType featureType) {
            name = featureType.getTypeName();
            namespaceURI = featureType.getNamespace();
            AttributeType[] attributeTypes = featureType.getAttributeTypes();
            attributeTypeTransporters = 
                    new AttributeTypeTransporter[attributeTypes.length];
            for (int i = 0; i < attributeTypes.length; i++) {
                attributeTypeTransporters[i] = 
                        new AttributeTypeTransporter(attributeTypes[i]);
            }
        }

        /**
		 * @return An unwrapped FeatureType.
		 * @throws GisException
		 *             if there's a bug.
		 */
        public FeatureType toFeatureType() throws GisException {
            try {
                
            	atributos = new AttributeType[attributeTypeTransporters.length];
            	
            	
            	
                for (int i = 0; i < attributeTypeTransporters.length; i++) {
                	atributos[i]  = attributeTypeTransporters[i].toAttributeType();
              
                    }
                FeatureType ft = FeatureTypeBuilder.newFeatureType(atributos, name);
                return ft;
                
            } catch (SchemaException e) {
                throw new GisException(e);
            } catch (NullPointerException e) {
                throw new GisException(e);
            } catch (IllegalArgumentException e) {
                throw new GisException(e);
            } catch (NoSuchElementException e) {
                throw new GisException(e);
            }
        }
    }

    /**
	 * Serializable wrapper around an AttributeType.
	 */
    public static class AttributeTypeTransporter implements Serializable {
        /**
		 * 
		 */
        private static final long serialVersionUID = -4579004283017581628L;

        String name;

        String className;

        boolean isNillable;

        int fieldLength;

        Object defaultValue;

        String wkt;

        /**
		 * @param attributeType
		 *            The type to wrap.
		 */
        public AttributeTypeTransporter(AttributeType attributeType) {
            name = attributeType.getLocalName();
            className = attributeType.getBinding().getName();
            isNillable = attributeType.isNillable();
            fieldLength = getFieldLength(attributeType, 40);
            defaultValue = attributeType.createDefaultValue();
            if (GeometryAttributeType.class.isAssignableFrom(attributeType.getBinding())) {
                GeometryAttributeType geomType = 
                    (GeometryAttributeType)attributeType;
                if (geomType.getCoordinateSystem() == null) {
                    wkt = "";
                } else {
                    wkt = 
((GeometryAttributeType)attributeType).getCoordinateSystem().toWKT();
                }
            }
        }

        /**
		 * @return An unwrapped AttributeType.
		 * @throws GisException
		 *             if there's a bug.
		 */
        public AttributeType toAttributeType() throws GisException {
            try {
                if (wkt != null) { // geometry attribute
                    return MixtoAttributeTypeFactory.newAttributeType(name, 
                    		Thread.currentThread().getContextClassLoader().loadClass(className), 
                                                                 isNillable, 
                                                                 fieldLength, 
                                                                 defaultValue 
                                                                 );
                } // else it's an ordinary attribute
                return MixtoAttributeTypeFactory.newAttributeType(name, 
                		Thread.currentThread().getContextClassLoader().loadClass(className), 
                                                             isNillable, 
                                                             fieldLength, 
                                                             defaultValue);
            } catch (ClassNotFoundException e) {
                throw new GisException(e);
            }
        }
    }

    /**
	 * Serialzable wrapper around Features.
	 */
    public static class FeatureTransporter implements Serializable {


        /**
		 * 
		 */
    	private static final long serialVersionUID = 1188464574357088824L;


        String featureId;
        Object[] attributes;
        String typeName;
        /**
         * @param feature Will be wrapped.
         */
        public FeatureTransporter(){
        }
        
        public FeatureTransporter(String id, Object atts[], String typeName) {
        	setFeatureId(id);
        	setAttributes(atts);
        	setTypeName(typeName);
        }
        public FeatureTransporter(Feature feature) {
            featureId = feature.getID();
            attributes = feature.getAttributes(null);
            this.typeName = feature.getFeatureType().getTypeName();
        }
        
        public String getFeatureId(){
        	return featureId;
        }
        
        public Object[] getAttributes(){
        	return attributes;
        }
        
        public String getTypeName() {
        	return typeName;
        }
        
        public void setFeatureId(String fId){
        	featureId = fId;
        }
        
        public void setAttributes(Object atts[]){
        	attributes = atts;
        }
        
        public void setTypeName(String typeName) {
        	this.typeName = typeName;
        }
        /**
         * @param featureType The interpretation of this Feature.
         * @return An unwrapped feature.
         * @throws GisException If the FeatureType is incompatible with the data.
         */
        public Feature toFeature(FeatureType featureType) throws GisException {
            try {
                return featureType.create(attributes, featureId);
            } catch (IllegalAttributeException e) {
                throw new GisException(e);
            }            
        }
    }

    private static int getFieldLength(AttributeType attr, int defaultLength) {
        int length = -1;
        Filter f = attr.getRestriction();

        if (f != null && f != Filter.INCLUDE && f != Filter.EXCLUDE && 
            (PropertyIsLessThan.class.isAssignableFrom(f.getClass()) || 
             PropertyIsLessThanOrEqualTo.class.isAssignableFrom(f.getClass()))) {
                BinaryComparisonOperator bco = (BinaryComparisonOperator)f;
                Literal literal;
                
                if (LengthFunction.class.isAssignableFrom(bco.getExpression1().getClass())){
                	literal = (Literal)bco.getExpression2();
                } else {
                	literal = (Literal)bco.getExpression1();                	
                }
                length = ((Number)literal.getValue()).intValue();
        } else {
            length = defaultLength;
        }

        return length;
    }

    /**
	 * Serialzable wrapper around Filter.
	 */
    public static class FilterTransporter implements Serializable {
        /**
		 * 
		 */
        private static final long serialVersionUID = -3999408991129891087L;

        /**
		 * @param filter
		 *            Will be wrapped.
		 */
        private String _filterStr = null;

        public FilterTransporter(){
        	
        }
        public FilterTransporter(Filter _filter) {

        }

        public FilterTransporter(String filterStr) {
            _filterStr = filterStr;
        }

        public Filter getFilter() throws ParseException {
            String filterStr2Parse = getFilterStr();
            return (Filter)eb.parser(filterStr2Parse);
        }

        public String getFilterStr() {
            return this._filterStr;
        }

        public void setFilterStr(String filterStr) {
            _filterStr = filterStr;

        }


    }
}
