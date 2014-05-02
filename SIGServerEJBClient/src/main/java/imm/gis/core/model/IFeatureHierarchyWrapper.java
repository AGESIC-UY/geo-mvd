package imm.gis.core.model;

import java.util.Map;

import org.geotools.feature.Feature;

public interface IFeatureHierarchyWrapper {

	public  Feature[] getRoots(String layerName);

	public  Feature getParent(String childFID);

	public  Feature[] getChildren(String parentFID, String childLayer);

	public  String[] getChildrenLayerNames(String fID);
	
	public  Map<?,?> getOriginalMap();
	
	public boolean exists(String fID);
	
	

}