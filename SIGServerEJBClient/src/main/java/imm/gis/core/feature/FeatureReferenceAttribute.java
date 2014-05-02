package imm.gis.core.feature;

import java.io.Serializable;

public class FeatureReferenceAttribute implements Serializable {

	private static final long serialVersionUID = 4262910585181137341L;

	private String FID;

	private String referencedLayer;

	public FeatureReferenceAttribute(String FID, String referencedLayer) {
		this.FID = FID;
		this.referencedLayer = referencedLayer;
	}

	public String getReferencedFeatureID() {
		return this.FID;
	}

	public void setReferencedFeatureID(String FID) {
		this.FID = FID;
	}

	public String getReferencedLayer() {
		return this.referencedLayer;
	}

	public void setReferencedLayer(String referencedLayer) {
		this.referencedLayer = referencedLayer;
	}

	public String toString() {
		return this.FID;
	}

	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		String rFID;
		if (obj == null) {
			return false;
		}
		if (obj instanceof FeatureReferenceAttribute) {
			FeatureReferenceAttribute fra = (FeatureReferenceAttribute) obj;
			rFID = fra.getReferencedFeatureID();
		} else {
			rFID = obj.toString();
		}

		if (rFID == null)
			return this.getReferencedFeatureID() == null;
		else
			return rFID.equals(this.getReferencedFeatureID());
	}
}
