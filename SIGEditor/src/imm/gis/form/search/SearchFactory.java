package imm.gis.form.search;

import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.feature.AttributeType;

import com.vividsolutions.jts.geom.Geometry;

import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.metadata.LayerAttributeMetadata;

/**
 * Devuelve implementaciones de busquedas para cada capa
 * 
 * @author agrassi
 *
 */
public class SearchFactory {

	Map<String, Layer> searchableLayers = new HashMap<String, Layer>();
	ICoreAccess coreAccess;
	
	public SearchFactory(Layer appLayers[], ICoreAccess coreAccess) {
	
		this.coreAccess = coreAccess; 
		
		for (int i=0; i < appLayers.length; i++) {
			
			// Para cada capa, me fijo si tiene algun atributo consultable
//			if (appLayers[i].isVisible()) {
				
				Iterator it = appLayers[i].getMetadata().getAttributesMetadata().values().iterator();
				LayerAttributeMetadata da;
				boolean encontre = false;
				
				while (it.hasNext() && !encontre) {

					da = (LayerAttributeMetadata) it.next();

					AttributeType attributeType = appLayers[i].getFt().getAttributeType(da.getName());
					
					if (!Geometry.class.isAssignableFrom(attributeType.getBinding())) {
						if (da.isQueryCapable()) {
							encontre = true;
							searchableLayers.put(appLayers[i].getNombre(),appLayers[i]);
						}
					}
				}
		//	}
		}
	}
	
	
	public SearchCriteriaPanel getCriteriaPanel(String typeName, Component topLevelContainer) {
		return new SearchCriteriaPanel(coreAccess, searchableLayers.get(typeName), topLevelContainer);
	}
	
	public String[] getSearchableTypes() {
		return (String []) searchableLayers.keySet().toArray(new String[searchableLayers.size()]);
	}
	

}
