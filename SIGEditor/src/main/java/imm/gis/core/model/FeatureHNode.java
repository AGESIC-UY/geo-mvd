package imm.gis.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.feature.Feature;

public class FeatureHNode {
	Map parentMap;

	Map myMap;

	String layerName;

	String parentLayerName;
	
	String parentFID;

	Feature feature = null;

	ArrayList<Feature>  children = null;

	public FeatureHNode(Map parentMap, Map myMap, String layerName,
			String parentLayerName) {
		super();
		this.parentMap = parentMap;
		this.myMap = myMap;
		this.layerName = layerName;
		this.parentLayerName = parentLayerName;
	}

	public FeatureHNode(Map myMap, String parentFID, Feature feature) {
		super();
		this.myMap = myMap;
		this.layerName = feature.getFeatureType().getTypeName();
		this.parentFID = parentFID;
		this.feature = feature;
	}

	public Feature getFeature() {
		if (feature == null) {
			feature = (Feature) ((Map) this.myMap.get(layerName)).values()
					.iterator().next();
		}

		return feature;
	}

	public Collection<Feature> getChildren() {
		ArrayList<Feature> toReturn = new ArrayList<Feature>();
		if (children == null) {
			Iterator iter = this.myMap.keySet().iterator();
			while (iter.hasNext()) {
				String childLayer = (String) iter.next();
				if (childLayer.equals(layerName))
					continue;
				Map childMap = (Map) this.myMap.get(childLayer);
				Iterator iter2 = childMap.keySet().iterator();
				while (iter2.hasNext()) {
					String childFID = (String) iter2.next();

					Map innerChildMap = (Map) childMap.get(childFID);
					Feature child = (Feature) ((Map) innerChildMap
							.get(childLayer)).values().iterator().next();
					toReturn.add(child);
				}

			}
			children = toReturn; 
		}
		return children;
	}
	
	
	

	public Feature[] getChildrenByLayer(String childLayer) {
		Map childMap = (Map) this.myMap.get(childLayer);
		Iterator iter = childMap.values().iterator();
		ArrayList<Feature> toReturn = new ArrayList<Feature>();
		while (iter.hasNext()) {
			Map innerChildMap = (Map) childMap.get(iter.next());
			Feature tmp = (Feature) ((Map) innerChildMap.get(childLayer))
					.values().iterator().next();
			toReturn.add(tmp);
		}

		return (Feature[]) toReturn.toArray(new Feature[toReturn.size()]);
	}

	public boolean hasChildren() {
		// Si exsite mas de 1 clave al menos ese feature tiene un hijo ya que
		// tiene una clave como minimo
		// que es el nombre de su capa.
		return myMap.keySet().size() > 1;

	}
	
	public Feature getParent(){
		if(parentMap==null) return null;
		Map innerParentMap = (Map)parentMap.get(this.parentLayerName);
		return (Feature)innerParentMap.values().iterator().next();
	}
	
	public String getParentFID(){
		return parentFID;
	}
	
	public void removeChild(String childFID,String childLayer){
		Map childMap = (Map) this.myMap.get(childLayer);
		Map innerChildMap = (Map)childMap.remove(childFID);
		
		Feature fChildRemoved = (Feature)((Map)innerChildMap.get(childLayer)).remove(childFID);
		getChildren().remove(fChildRemoved);
		//this.children
	}
	
	@SuppressWarnings("unchecked")
	public void addChild(FeatureHNode child){
		String childLayer = child.feature.getFeatureType().getTypeName();
		String childFID = child.feature.getID();
		if(!myMap.containsKey(childLayer)){
			myMap.put(childLayer, new HashMap());
		}
		Map childMap = (Map) this.myMap.get(childLayer);
		childMap.put(childFID, child.myMap);
		getChildren().add(child.feature);
	}
	
}
