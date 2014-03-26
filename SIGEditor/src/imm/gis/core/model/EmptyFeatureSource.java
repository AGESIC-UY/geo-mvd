package imm.gis.core.model;

import java.io.IOException;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

public class EmptyFeatureSource implements FeatureSource {

	private FeatureType ft;

	public EmptyFeatureSource(FeatureType ft){
		this.ft= ft;
	}
	
	public void addFeatureListener(FeatureListener listener) {
		// TODO Auto-generated method stub

	}

	public Envelope getBounds() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Envelope getBounds(Query query) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCount(Query query) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public DataStore getDataStore() {
		// TODO Auto-generated method stub
		return null;
	}

	public FeatureCollection getFeatures() throws IOException {
		return FeatureCollections.newCollection();
		
		
	}

	public FeatureCollection getFeatures(Query query) throws IOException {
		// TODO Auto-generated method stub
		return FeatureCollections.newCollection();
	}

	public FeatureCollection getFeatures(Filter filter) throws IOException {
		// TODO Auto-generated method stub
		return FeatureCollections.newCollection();
	}

	public FeatureType getSchema() {
		// TODO Auto-generated method stub
		return this.ft;
	}

	public void removeFeatureListener(FeatureListener listener) {
		// TODO Auto-generated method stub

	}

	public QueryCapabilities getQueryCapabilities() {
		return null;
	}

	public Set getSupportedHints() {
		return null;
	}

}
