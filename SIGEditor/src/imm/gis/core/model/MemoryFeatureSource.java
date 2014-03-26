package imm.gis.core.model;

import java.io.IOException;
import java.util.Collection;

import org.geotools.data.AbstractFeatureSource;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureListener;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureType;

public class MemoryFeatureSource extends AbstractFeatureSource {
	private String layer;
	private MyMemoryDataStore dataStore;
	
	public MemoryFeatureSource(MyMemoryDataStore ds, String l){
		super();
		dataStore = ds;
		layer = l;
	}
	
	public void addFeatureListener(FeatureListener listener) {
	}

	public void removeFeatureListener(FeatureListener listener) {
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public FeatureCollection getFeatures() throws IOException {
		Collection col = dataStore.getFeatures(layer);
		FeatureCollection fc = FeatureCollections.newCollection();
		fc.addAll(col);
		
		return fc;
	}

	public FeatureType getSchema() {
		FeatureType ft = null;
		try {
			ft = dataStore.getSchema(layer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ft;
	}
}
