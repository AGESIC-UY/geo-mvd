package imm.gis.core.model.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.geotools.feature.Feature;

public class SelectedFeaturesCache {
	private Map<String, LayerData> layers = Collections.synchronizedMap(new HashMap<String, LayerData>());

	public String[] getLayerNames() {
		return (String[]) layers.keySet().toArray(
				new String[layers.keySet().size()]);
	}

	public void setSelected(Feature f, boolean selected) {
		String layerName = f.getFeatureType().getTypeName();
		LayerData layer = layers.get(layerName);

		if (layer == null) {
			layer = new LayerData();
			layers.put(layerName, layer);
		}

		layer.setSelected(f, selected);
	}

	public void unselectAll(String layerName) {
		LayerData layer = layers.get(layerName);
		if (layer != null) {
			layer.unselectAll();
		}
	}

	public void clean() {
		String typeName;
		LayerData layer;

		for (Iterator<String> it = layers.keySet().iterator(); it.hasNext();) {
			typeName = it.next();
			layer = layers.get(typeName);
			layer.clear();
		}
	}

	public Collection getSelectedFeatures(String layerName) {
		LayerData layer = layers.get(layerName);

		return layer == null ? new ArrayList() : layer.getSelected();
	}


	public boolean isSelected(Feature f) {
		LayerData layer = layers.get(f.getFeatureType().getTypeName());

		return layer != null && layer.isSelected(f);
	}

	private class LayerData {
		private Map<String, Feature> selected = new HashMap<String, Feature>();

		public LayerData() {
		}


		public void setSelected(Feature f, boolean sel) {
			boolean isSelected = selected.containsKey(f.getID());
			
			if (!isSelected && sel) {
				selected.put(f.getID(), f);
			}
			else if (isSelected && !sel) {
				selected.remove(f.getID());
			}
			else if (isSelected && sel)
				selected.put(f.getID(), f);
		}

		public boolean isSelected(Feature f) {
			return selected.containsKey(f.getID());
		}

		public Collection getSelected() {
			return selected.values();
		}

		public void unselectAll() {
			selected.clear();
		}

		public void clear() {
			selected.clear();
		}
	}
}
