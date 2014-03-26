package imm.gis.core.model;

import org.geotools.feature.Feature;

public class FeatureModifyOperation extends FeatureOperation {
	private Feature oldFeature;

	public FeatureModifyOperation(Feature f, Feature oldFeature, int o) {
		super(f, o);
		this.oldFeature = oldFeature;
		// TODO Auto-generated constructor stub
	}

	public Feature getOldFeature() {
		return oldFeature;
	}

}
