package imm.gis.core.layer;

import imm.gis.core.layer.metadata.LayerAttributeMetadata;

import org.geotools.feature.Feature;

public class NonEditableAttributesContext extends AbstractAttributesContext implements ILayerAtributesContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Object getAttributeProperty(Feature f, String attName,
			String attProperty) {
		// TODO Auto-generated method stub
		return new Boolean(false);
	}

	public Object getAttributeProperty(int mode, Feature f, LayerAttributeMetadata la, String attProperty) {
		// TODO Auto-generated method stub
		return new Boolean(false);
	}

}
