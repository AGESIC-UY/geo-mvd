package imm.gis.comm.datatypes.datainput;

import imm.gis.comm.GisSerialization;
import imm.gis.comm.GisSerialization.FeatureTransporter;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

public class SaveDataType implements ISaveDataType {
	
	private static final long serialVersionUID = -1L;
	private Map<String, List<FeatureTransporter>> mapInsert=new HashMap<String, List<FeatureTransporter>>();
	private Map<String, List<String>> mapDelete=new HashMap<String, List<String>>();
	private Map<String, List<FeatureTransporter>> mapUpdate=new HashMap<String, List<FeatureTransporter>>();
	private static Logger log = Logger.getLogger(SaveDataType.class);
	
	public void addDeleteFID(String layer, String fid){
		log.info("Agregando FID para borrar: " + fid);
		List<String> list = mapDelete.get(layer);
		if (list == null){
			list = new ArrayList<String>();
			mapDelete.put(layer, list);
		}
		
		list.add(fid);
	}

	public void addModifiedFeature(int action,String layer, String id, Object[] atts){
		log.info("Agregando feature para modificar: " + id + " action: " + action);
		Map<String, List<FeatureTransporter>> map = (action == UPDATE) ? mapUpdate : mapInsert;
		List<FeatureTransporter> list = map.get(layer);
		
		if (list == null){
			list = new ArrayList<FeatureTransporter>();
			map.put(layer, list);
		}
		
		list.add(new GisSerialization.FeatureTransporter(id,atts, layer));
	}
	
	public String[] getLayers(){
	
		Set<String> res = new HashSet<String>(mapUpdate.keySet());
		
		res.addAll(mapDelete.keySet());
		res.addAll(mapInsert.keySet());
		
		return (String[])res.toArray(new String[res.size()]);
	}
	
	public Feature[] getUpdateFeatures(FeatureType type) throws IllegalAttributeException {
		return getModifiedFeatures(type, UPDATE);
	}
	
	public Collection<String> getDeleteFIDs(String layer){
		return mapDelete.get(layer); 
	}

	public Feature[] getInsertFeatures(FeatureType type) throws IllegalAttributeException {
		return getModifiedFeatures(type, INSERT);
	}
	
	private Feature[] getModifiedFeatures(FeatureType type,int action) throws IllegalAttributeException {
		String layer = type.getTypeName();
		Map<String, List<FeatureTransporter>> currentMap = (action == INSERT) ? mapInsert : mapUpdate;
		List<FeatureTransporter> list = currentMap.get(layer);
		Feature res[] = new Feature[list==null ? 0 : list.size()];
		FeatureTransporter ft;
		
		for (int i = 0; i < res.length; i++){
			ft = (FeatureTransporter)list.get(i);
			res[i] = type.create(ft.getAttributes(), ft.getFeatureId());
		}
		
		return res;
	}
}
