package imm.gis.core.model;

import imm.gis.AppContext;
import imm.gis.core.feature.FeatureReferenceAttribute;
import imm.gis.core.interfaces.IModel;
import imm.gis.core.layer.Layer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.geotools.feature.Feature;

public class FeatureHierarchyUtil {
	private IModel model;
	public FeatureHierarchyUtil(IModel _model){
		this.model= _model;
	}
	/**
	 * Deveulve el Feature padre mas cercano en la jerarquia 
	 * que tenga geometria no nula
	 * @param child Feature hijo
	 * @return
	 */
	public Feature getNearestParentWithGeometry(Feature child) {

		// //si el Feature ya tiene geometria y alwaysSeachParent = false
		// devuelvo el mismo

		String childlayerName = child.getFeatureType().getTypeName();
		Layer chidLayer = AppContext.getInstance().getCapa(childlayerName);

		Collection parentsInfo = chidLayer.getMetadata()
				.getReferencedParentsAttributeInfo();
		/*
		 * Obtengo el primero pero podrian haber mas
		 * 
		 * 
		 */
		if (parentsInfo.isEmpty()) return null;
		Map info = (Map) parentsInfo.iterator().next();
		Iterator iter = info.entrySet().iterator();
		Map.Entry entry = (Map.Entry) iter.next();
		String parentFid = ((FeatureReferenceAttribute) child
				.getAttribute((String) entry.getKey()))
				.getReferencedFeatureID();
		String parentTypeName = (String) entry.getValue();
		Feature f = model.getFeature(parentTypeName, parentFid, false);
		if (f.getDefaultGeometry() == null) {
					// El padre no tiene geometria
					// voy al nono.
					return getNearestParentWithGeometry(f);
		} 

		
		return f;

		/*
		 * FeatureType ft = am.getCapa(q.getTypeName()).getFt();
		 * FeatureCollection fc = services.loadLayer(ft, q.getFilter(), true);
		 * fr = new CollectionFeatureReader(fc, ft);
		 */

		// memoryDataStore.get
	}

}
