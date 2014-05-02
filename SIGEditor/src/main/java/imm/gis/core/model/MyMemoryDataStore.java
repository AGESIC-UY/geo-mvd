package imm.gis.core.model;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.data.CollectionFeatureReader;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;

public class MyMemoryDataStore extends MemoryDataStore {
	
	public void cleanData(){
		synchronized(memory){
			
			Iterator i = memory.keySet().iterator();
			
			while (i.hasNext()){
				((HashMap)memory.get((String) i.next())).clear();
			//	memory.put((String) i.next(), new HashMap());
			}
		}
	}
	
	public boolean isEmpty(String type) {
		Map m = (Map)memory.get(type);
		
		return (m == null) ? true : m.keySet().isEmpty();
	}
	
	public boolean contains(Feature f){
		boolean res = false;
		
		try{
			getFeature(f.getFeatureType().getTypeName(), f.getID());
			res = true;
		}
		catch (Exception e){
		}
		
		return res;
	}
	
	public Feature getFeature(String typeName, String fid){
		Map type;
		Feature f;
		
		synchronized(memory){
			type = (Map)memory.get(typeName);
			f = (Feature)type.get(fid);
		}

		return f;
	}
	
	public void removeFeature(Feature f){
		synchronized(memory){
			Map type = (Map)memory.get(f.getFeatureType().getTypeName());
			type.remove(f.getID());
		}
	}

	
    
	public void addFeatureType(FeatureType ft){
		synchronized (memory){
			schema.put(ft.getTypeName(), ft);
			memory.put(ft.getTypeName(), new HashMap());			
		}
	}
	
	public void addFeature(Feature f) {
        try {
            //FeatureType featureType;
            //featureType = getSchema(f.getFeatureType().getTypeName());

            //int cant = featureType.getAttributeCount();
            //Object atts[] = new Object[cant];
            //f = featureType.create(f.getAttributes(atts), f.getID());
            //f.setAttribute("selected", new Integer(0));
            //f.setAttribute("modified", new Integer(0));
            
            ((Map)memory.get(f.getFeatureType().getTypeName())).put(f.getID(), f);

        } catch (Exception e) {
        	e.printStackTrace();
        	throw new RuntimeException(e);
		}
	}
	
	public void addFeatures(FeatureCollection fc)  throws IOException {
		if (fc.isEmpty()){
			addFeatureType(fc.getFeatureType());
		} else {
			addFeatures(new CollectionFeatureReader(fc, fc.getFeatureType()));			
		}
	}
	

    Collection getFeatures(String typeName){
    	try {
			return features(typeName).values();
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.EMPTY_LIST;
		}
    }
}
