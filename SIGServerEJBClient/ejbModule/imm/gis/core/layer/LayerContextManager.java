package imm.gis.core.layer;

import imm.gis.core.layer.metadata.LayerAttributeMetadata;
import imm.gis.core.model.IFeatureHierarchyWrapper;

import java.util.ArrayList;
import java.util.Iterator;

import org.geotools.feature.Feature;

public class LayerContextManager {

	private ArrayList<ILayerAtributesContext> layerCtxAtrs = new ArrayList<ILayerAtributesContext>();

	private IFeatureHierarchyWrapper ifhw;

	public Object getAttributeProperty(int mode,Feature f, LayerAttributeMetadata la, String attProperty) {

		if ("editable".equals(attProperty)) {
			boolean boolVal = !la.isReadOnly();
			for (Iterator<ILayerAtributesContext> iter = layerCtxAtrs.iterator(); boolVal && iter.hasNext();) {
				ILayerAtributesContext element = iter.next();
				boolVal = ((Boolean) element.getAttributeProperty(mode, f,la, attProperty)).booleanValue() 
						&& boolVal;
			}
			return new Boolean(boolVal);
		}
		return null;
	}
	
	
	public Object getAttributeProperty(Feature f,String attName,String attProperty){
		
		if ("editable".equals(attProperty)) {
			boolean boolVal = true;
			for (Iterator<ILayerAtributesContext> iter = layerCtxAtrs.iterator(); iter.hasNext();) {
				ILayerAtributesContext element = iter.next();
				boolVal = ((Boolean) element.getAttributeProperty(f,attName, attProperty)).booleanValue() 
						&& boolVal;
			}
			return new Boolean(boolVal);
		}
		return null;
		
	}

	public ArrayList<ILayerAtributesContext> getAttributesContext() {
		return layerCtxAtrs;
	}

	public void addAttributeContext(ILayerAtributesContext iac) {
		this.layerCtxAtrs.add(iac);
	}

	public IFeatureHierarchyWrapper getIfhw() {
		return ifhw;
	}

	public void setIfhw(IFeatureHierarchyWrapper ifhw) {
		this.ifhw = ifhw;
		for (Iterator<ILayerAtributesContext> iter = layerCtxAtrs.iterator(); iter.hasNext();) {
			ILayerAtributesContext element = iter.next();
			element.setFeatureHierarchyWrapper(ifhw);
		}
	}
}
