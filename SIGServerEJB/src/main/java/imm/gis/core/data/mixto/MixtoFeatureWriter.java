package imm.gis.core.data.mixto;

import java.io.IOException;
import java.sql.SQLException;

import org.geotools.data.DataSourceException;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.feature.AttributeType;
import org.geotools.feature.DefaultFeatureType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

public class MixtoFeatureWriter implements FeatureWriter {
    private FilasResultado datos = null;
    private MixtoFeatureReader rd = null;
    private Feature actual = null;
    private Feature live = null;
    
    /** Creates a new instance of MixtoFeatureWriter */
    public MixtoFeatureWriter(FilasResultado fr) {
        datos = fr;
        rd = new MixtoFeatureReader(fr);
    }
    
    //Release the underlying resources.
    public void close() throws IOException{
        rd.close();
    }
    
    //FeatureType this reader has been configured to create. 
    public FeatureType getFeatureType(){
        return rd.getFeatureType();
    }
    
    /**
     * @see org.geotools.data.FeatureWriter#hasNext()
     *Query whether this FeatureWriter has another Feature. 
     */
    public boolean hasNext() throws IOException {
        return rd.hasNext();
    }
    
    
    /**
     * @see org.geotools.data.FeatureWriter#next()
     *Reads a Feature from the underlying AttributeReader. 
     */
    public Feature next() throws IOException {
        if (rd == null) {
            throw new IOException("FeatureWriter has been closed");
        }

        FeatureType featureType = getFeatureType();
        
        if (hasNext()) {
            try {
                live = rd.next();
                actual = featureType.duplicate(live);
            } catch (IllegalAttributeException e) {
                throw new DataSourceException("No se pudo duplicar el Feature" 
                        + " en " + featureType.getTypeName() + ": "+e.getMessage(), e);
            }
        } else {
            //Nuevo Feature
            live = null;

            try {
               Feature temp = template(featureType);
               actual = new MixtoFIDFeature((DefaultFeatureType) featureType,
                        temp.getAttributes(new Object[temp.getNumberOfAttributes()]), null);
            } catch (IllegalAttributeException e) {
                throw new DataSourceException(
                    "No se pudo agregar un feature a '"
                    + featureType.getTypeName() + "': " + e.getMessage(), e);
            }
        }

        return actual;
    }
    
    
    
    
    //Removes current Feature, must be called before hasNext. 
    public void remove() throws IOException {
        if (actual == null) {
            throw new DataSourceException("No feature available to remove");
        }

        if (live != null) {
            
        	try {
        		datos.remove(actual);
        	}
        	catch (SQLException e) {
                if (datos.getTransaction() != Transaction.AUTO_COMMIT)
                 //   datos.getTransaction().rollback();
                
                throw new DataSourceException("Problem deleting row", e);
        	}
            
            
            live = null;
            actual = null;        
        }
        else{
            actual = null;        
        }
    }

    //Wrties the current Feature, must be called before hasNext. 
    public void write() throws IOException {

        if (actual == null) {
            throw new IOException("No feature available to write");
        }

        if (live != null) {
            if (!live.equals(actual)) {
                
            	try {
            		datos.write(actual);
            	}
            	catch (Exception e) {
                    if (datos.getTransaction() != Transaction.AUTO_COMMIT)
                     //   datos.getTransaction().rollback();
                    
                    throw new DataSourceException("Problem updating row",e);
            	}
            }
        }
        else {
        	try {
        		datos.insert(actual);
        	}
        	catch (Exception e) {
                if (datos.getTransaction() != Transaction.AUTO_COMMIT)
               //     datos.getTransaction().rollback();
                
                throw new DataSourceException("Problem inserting row",e);        		
        	}
        }
    }
    
    private Feature template(FeatureType type) throws IllegalAttributeException {
    	Object atts[] = new Object[type.getAttributeCount()];
    	AttributeType at;
    	Object val;
    	Class<?> cl;
    	
    	for (int i = 0; i < type.getAttributeCount(); i++){
    		at = type.getAttributeType(i);
    		if (at.isNillable()){
    			val = null;
    		} else {
    			val = at.createDefaultValue();
    			if (val == null){
    				cl = at.getClass();
    				try{
    					val = cl.newInstance();
    				} catch (Exception e){
    					throw new IllegalAttributeException(e.getMessage());
    				}
    			}
    		}
    		atts[i] = val;
    	}
    	
    	return type.create(atts, null);
    }
}
