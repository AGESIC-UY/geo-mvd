package imm.gis.core.layer;

import java.io.Serializable;

import org.geotools.feature.Feature;

import imm.gis.core.layer.metadata.LayerAttributeMetadata;
import imm.gis.core.model.IFeatureHierarchyWrapper;
public interface ILayerAtributesContext extends Serializable {

	Object getAttributeProperty(Feature f,String attName,String attProperty);
	Object getAttributeProperty(int mode,Feature f,LayerAttributeMetadata la,String attProperty);
	void setFeatureHierarchyWrapper(IFeatureHierarchyWrapper fhw);
}
