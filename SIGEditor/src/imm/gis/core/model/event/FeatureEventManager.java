package imm.gis.core.model.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FeatureEventManager {
	public static EventType BEFORE_MODIFY = new EventType("BEFORE_MODIFY");
	public static EventType AFTER_MODIFY = new EventType("AFTER_MODIFY");
	public static String ALL_LAYERS = "ALL_LAYERS";
	private Collection<FeatureChangeListener> allLayerListener = null;
	private static FeatureEventManager instance = null;
	Map<String, Map<EventType, List<FeatureChangeListener>>> listenerByLayer = null;

	private FeatureEventManager() {
		listenerByLayer = new HashMap<String, Map<EventType, List<FeatureChangeListener>>>();
		allLayerListener = new ArrayList<FeatureChangeListener>();
	}

	public static FeatureEventManager getInstance() {
		if (instance == null) {
			instance = new FeatureEventManager();
		}
		return instance;
	}

	public void addListener(EventType eventType, FeatureChangeListener fcl) {
		List<FeatureChangeListener> listenerList;
		Map<EventType, List<FeatureChangeListener>> listenersSet;
		if(fcl.getLayer() ==ALL_LAYERS ){
			allLayerListener.add(fcl);
		}
		else{
			
		
		if(!listenerByLayer.containsKey(fcl.getLayer())){
			listenerByLayer.put(fcl.getLayer(),new HashMap<EventType, List<FeatureChangeListener>>());
		}
		listenersSet = listenerByLayer.get(fcl.getLayer());
		
		if (!listenersSet.containsKey(eventType)) {
			listenersSet.put(eventType, new ArrayList<FeatureChangeListener>());
		}
		listenerList = listenersSet.get(eventType);
		listenerList.add(fcl);
		}
	}
	
	public void removeListener(EventType eventType, FeatureChangeListener fcl) {
		List<FeatureChangeListener> listenerList;
		Map<EventType, List<FeatureChangeListener>> listenersSet;
		if(fcl.getLayer() ==ALL_LAYERS ){
			allLayerListener.remove(fcl);
			return;
		}
		if(!listenerByLayer.containsKey(fcl.getLayer())){
			return;
		}
		listenersSet = listenerByLayer.get(fcl.getLayer());
		if (!listenersSet.containsKey(eventType))
			return;
		listenerList = listenersSet.get(eventType);
		listenerList.remove(fcl);
		if(listenerList.size()==0){
			/*
			 * Si no hay mas listener para la capa elimino la lista
			 */
			listenersSet.remove(fcl.getLayer());
		}
		
		
	}

	public void notifyListener(EventType eventType, FeatureChangeEvent fc) {
		String layer = fc.getTypeName();
		
		List<FeatureChangeListener> listenerList = Collections.synchronizedList(new ArrayList<FeatureChangeListener>());
		Map<EventType, List<FeatureChangeListener>> listenersSet = listenerByLayer.get(layer);
		if(listenersSet == null) {
			listenerList.addAll(allLayerListener);
		}else{
			List<FeatureChangeListener> events = listenersSet.get(eventType);
			if (events != null){ 
				listenerList.addAll(events);				
			}
			listenerList.addAll(allLayerListener);							
		}
		
		synchronized(listenerList){
		for (int i = 0; i < listenerList.size(); i++) {
			FeatureChangeListener element = listenerList.get(i);
			if (eventType.equals(AFTER_MODIFY)) {
				element.changePerformed(fc);
			} else {
				element.isChanging(fc);
			}

		}
		}
	}

	public static class EventType {
		String _type;

		EventType(String type) {
			_type = type;
		}

		public String getType() {
			return _type;
		}

		public boolean equals(Object o) {
			if (!(o instanceof EventType))
				return false;
			return ((EventType) o).getType().equals(_type);

		}

	}
}
