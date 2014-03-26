package imm.gis.core.model;

import java.io.IOException;

import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;

public class ModelContextImpl implements ModelContext {
	private ModelData modelData;
	
	public ModelContextImpl(ModelData md){
		modelData = md;
	}
	
	public Feature[] getFeatures(String type) throws IOException {
		FeatureSource fs = modelData.getDataStore().getFeatureSource(type);
		
		return (Feature[])fs.getFeatures().toArray(new Feature[fs.getFeatures().size()]);
	}
	
	//public boolean isSelected(Feature f) {
	//	return modelData.isSelected(f);
	//}
	
	public boolean isVisible(String layer){
		return modelData.isEnabledLayer(layer);
	}
}
