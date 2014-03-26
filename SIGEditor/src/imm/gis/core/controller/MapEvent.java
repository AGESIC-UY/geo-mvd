package imm.gis.core.controller;

import java.util.EventObject;

import org.geotools.feature.Feature;

public class MapEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private Feature feature;
	

	public MapEvent(Object s, Feature f) {
		super(s);
		feature = f;
	}
	
	public Feature getFeature() {
		return feature;
	}
}
