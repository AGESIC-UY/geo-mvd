package imm.gis.core.model;

import java.io.IOException;

import org.geotools.feature.Feature;

public interface ModelContext {
	public Feature[] getFeatures(String type) throws IOException;
	//public boolean isSelected(Feature f);
	public boolean isVisible(String layer);
}
