package imm.gis.core.model;

import org.geotools.feature.Feature;

public class FeatureOperation {
	protected Feature feature;
	protected int originalStatus;
	protected int actualStatus;
	
	public FeatureOperation(Feature f, int o){
		if (f == null) throw new IllegalArgumentException("El feature es nulo");
		feature = f;
		originalStatus = o;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public int getOriginalStatus() {
		return originalStatus;
	}

	public void setOriginalStatus(int status) {
		this.originalStatus = status;
	}
	
	public String toString(){
		StringBuffer tmp = new StringBuffer("Feature operation ");
		tmp.append("feature: ");
		tmp.append(feature.getID());
		tmp.append(", ");
		tmp.append("estado : ");
		tmp.append(feature.getAttribute("modified"));
		tmp.append(", ");
		tmp.append("estado original: ");
		tmp.append(originalStatus);
		tmp.append('\n');
		
		return tmp.toString();
	}

	public int getActualStatus() {
		return actualStatus;
	}

	public void setActualStatus(int actualStatus) {
		this.actualStatus = actualStatus;
	}
}
