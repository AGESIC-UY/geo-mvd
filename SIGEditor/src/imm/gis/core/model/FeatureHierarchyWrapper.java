package imm.gis.core.model;

import imm.gis.GisException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.feature.Feature;

public class FeatureHierarchyWrapper implements IFeatureHierarchyWrapper {

	Map featuresH;

	Map<String, FeatureHNode> featuresStruct;

	String rootLayerName;

	private static FeatureHierarchyWrapper instance = null;

	public static FeatureHierarchyWrapper getInstance(String layerName,Map data) {
		if (instance == null) {
			instance = new FeatureHierarchyWrapper(data,layerName);

		}
		else{
			instance.featuresH =data;
			instance.rootLayerName = layerName;
		}
		instance.processH(instance.rootLayerName, "", instance.featuresH, null);
		return instance;
	}
	
	public static FeatureHierarchyWrapper getInstance() throws GisException{
		if(instance==null) throw new GisException("FHW no inicializado");
		return instance;
	}

	public FeatureHierarchyWrapper(Map feature) {

		featuresH = feature;
	}

	private FeatureHierarchyWrapper(Map feature, String _rootLayerName) {

		featuresH = feature;
		rootLayerName = _rootLayerName;
		featuresStruct = new HashMap<String, FeatureHNode>();
		processH(rootLayerName, "", featuresH, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see imm.gis.core.model.IFeatureHierarchyWrapper#getRoots()
	 */
	public Feature[] getRoots(String layerName) {
		ArrayList<Feature> roots = new ArrayList<Feature>();

		Iterator iterator = featuresH.keySet().iterator();
		while (iterator.hasNext()) {
			String currFID = (String) iterator.next();
			Feature currRoot = (Feature) ((Map) ((Map) featuresH.get(currFID))
					.get(layerName)).get(currFID);
			roots.add(currRoot);
		}
		return (Feature[]) roots.toArray(new Feature[roots.size()]);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see imm.gis.core.model.IFeatureHierarchyWrapper#getParent(java.lang.String,
	 *      java.lang.String)
	 */
	public Feature getParent(String childFID) {
		if(!exists(childFID)) return null;
		FeatureHNode node = (FeatureHNode) featuresStruct.get(childFID);
		Feature toReturn = node.getParent();
		if(toReturn == null){
			String parentID = node.getParentFID();
			toReturn = ((FeatureHNode)featuresStruct.get(parentID)).getFeature();
		}
		return toReturn;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see imm.gis.core.model.IFeatureHierarchyWrapper#getChildren(java.lang.String,
	 *      java.lang.String)
	 */
	public Feature[] getChildren(String parentFID, String childLayer) {
		FeatureHNode node = (FeatureHNode) featuresStruct.get(parentFID);
		return node.getChildrenByLayer(childLayer);
		// Map currParenMap = (Map) featuresH.get(parentFID);
		// Map childMap = (Map) currParenMap.get(childLayer);
		// Iterator iter = childMap.keySet().iterator();
		// ArrayList toReturn = new ArrayList();
		// while (iter.hasNext()) {
		// String element = (String) iter.next();
		// Map innerChildMap = (Map) childMap.get(element);
		// Map leafChildMap = (Map) innerChildMap.get(childLayer);
		// // debe haber una sola clave
		// Iterator iter2 = leafChildMap.keySet().iterator();
		// String key = (String) iter2.next();
		// toReturn.add(leafChildMap.get(key));
		//
		// }
		// return (Feature[]) toReturn.toArray(new Feature[toReturn.size()]);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see imm.gis.core.model.IFeatureHierarchyWrapper#getChildrenLayerNames(java.lang.String)
	 */
	public String[] getChildrenLayerNames(String fID) {
		return null;
	}

	public void addChild(Feature childF,Map childInfo, String parentFID) {
			FeatureHNode newChildNode = new FeatureHNode(childInfo,parentFID,childF);
			FeatureHNode parentNode = (FeatureHNode)featuresStruct.get(parentFID);
			parentNode.addChild(newChildNode);
			featuresStruct.put(childF.getID(), newChildNode);
			
	}

	public void removeChild(Feature childF, String parentFID) {
		FeatureHNode parentNode = (FeatureHNode) featuresStruct.get(parentFID);
		parentNode.removeChild(childF.getID(), childF.getFeatureType().getTypeName());
	}	

	private void processH(String layerName, String parentLayerName, Map info,
			Map parentInfo) {

		Iterator iter = info.keySet().iterator();
		while (iter.hasNext()) {
			// Proceso los roots
			String element = (String) iter.next();
			Map currentParentMap = (Map) info.get(element);
			FeatureHNode node = new FeatureHNode(parentInfo, currentParentMap,
					layerName, parentLayerName);
			String currFID = node.getFeature().getID();
			this.featuresStruct.put(currFID, node);
			if (node.hasChildren()) {
				// Si tiene hijo los procesos recursivamente.
				Iterator iter2 = currentParentMap.keySet().iterator();
				while (iter2.hasNext()) {
					String childLayerKey = (String) iter2.next();
					if (childLayerKey.equals(layerName))
						continue;
					Map childMap = (Map) currentParentMap.get(childLayerKey);
					processH(childLayerKey, layerName, childMap,
							currentParentMap);
				}
			}
		}
	}

	// private Feature[] getFeaturesByMap(Map info, String layerName,
	// boolean isChild) {
	//
	// ArrayList toReturn = new ArrayList();
	// Feature tmp = null;
	// if (!isChild) {
	// tmp = (Feature) ((Map) info.get(layerName)).values().iterator()
	// .next();
	// toReturn.add(tmp);
	// } else {
	// Map childMap = (Map) info.get(layerName);
	// Iterator iter = childMap.values().iterator();
	// while (iter.hasNext()) {
	// Map innerChildMap = (Map) iter.next();
	// tmp = (Feature) ((Map) innerChildMap.get(layerName)).values()
	// .iterator().next();
	// toReturn.add(tmp);
	//
	// }
	//
	// return (Feature[]) toReturn.toArray(new Feature[toReturn.size()]);
	//
	// }
	//
	// return null;
	// }

	public Map getOriginalMap() {
		return featuresH;
	}

	public boolean exists(String fID) {
		// TODO Auto-generated method stub
		return featuresStruct.containsKey(fID);
	}

}
