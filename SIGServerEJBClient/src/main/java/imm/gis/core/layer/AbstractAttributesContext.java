package imm.gis.core.layer;

import imm.gis.core.model.IFeatureHierarchyWrapper;

import org.geotools.feature.Feature;

public abstract class AbstractAttributesContext {

 protected IFeatureHierarchyWrapper fwh;



	public AbstractAttributesContext() {
		super();
	}

	public abstract  Object getAttributeProperty(Feature f, String attName, String attProperty);
		// TODO Auto-generated method stub	

	public void setFeatureHierarchyWrapper(IFeatureHierarchyWrapper _fhw) {
		this.fwh = _fhw;
		
	}

}