package imm.gis.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.geotools.feature.Feature;

public class ClientFeaturesCache implements IStatus {
	private Map<String, LayerData> layers = Collections.synchronizedMap(new HashMap<String, LayerData>());

	public String[] getLayerNames() {
		return (String[]) layers.keySet().toArray(
				new String[layers.keySet().size()]);
	}

	public void setUnmodified(String layerName, String fid) {
		LayerData layer = layers.get(layerName);
		layer.setUnmodified(fid);
	}

	public void setStatus(Feature f, int status) {
		String layerName = f.getFeatureType().getTypeName();

		LayerData layer = layers.get(layerName);

		if (layer == null) {
			layer = new LayerData();
			layers.put(layerName, layer);
		}

		layer.setStatus(f, status);
	}

	public void addFeature(Feature f, int status) {
		setStatus(f, status);
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

	public ModifiedData getModifiedFeatures(String layerName) {
		Collection<Feature> created = new ArrayList<Feature>();
		Collection<Feature> updated = new ArrayList<Feature>();
		Collection<Feature> deleted = new ArrayList<Feature>();

		LayerData layer = layers.get(layerName);

		if (layer != null) {

			Collection<Feature> modified = layer.getModified();
			Feature feature;

			for (Iterator<Feature> it = modified.iterator(); it.hasNext();) {
				feature = it.next();
				if (layer.getStatus(feature) == CREATED_ATTRIBUTE) {
					created.add(feature);
				} else if (layer.getStatus(feature) == UPDATED_ATTRIBUTE) {
					updated.add(feature);
				} else if (layer.getStatus(feature) == DELETED_ATTRIBUTE) {
					deleted.add(feature);
				}
			}
		}

		return new ModifiedData(created, updated, deleted);
	}

	public Collection getCreatedAndModifiedData(String layerName) {
		LayerData layer = (LayerData) layers.get(layerName);

		return layer.getCreatedAndModified();
	}

	public boolean isModified(String layerName) {
		LayerData layer = (LayerData) layers.get(layerName);

		return layer != null && layer.hasModified();
	}

	public boolean isModified(Feature f) {
		LayerData layer = (LayerData) layers.get(f.getFeatureType()
				.getTypeName());

		return layer != null && layer.isModified(f);
	}

	public boolean isDeleted(Feature f) {
		return getStatus(f) == DELETED_ATTRIBUTE;
	}

	public int getStatus(Feature f) {
		LayerData layer = (LayerData) layers.get(f.getFeatureType()
				.getTypeName());

		return layer == null ? UNMODIFIED_ATTRIBUTE : layer.getStatus(f);
	}

	private class LayerData {
		private Map<String, Feature> data = new HashMap<String, Feature>();
		private Map<String, Integer> modified = new HashMap<String, Integer>();

		public LayerData() {
		}

		public void addFeature(Feature f) {
			addFeature(f, UNMODIFIED_ATTRIBUTE);
		}

		public void addFeature(Feature f, int status) {

			switch (status) {
			case UNMODIFIED_ATTRIBUTE:
				break;
			case DELETED_ATTRIBUTE:
			case UPDATED_ATTRIBUTE:
			case CREATED_ATTRIBUTE:
				data.put(f.getID(), f);
				modified.put(f.getID(), new Integer(status));
				break;
			default:
				throw new RuntimeException("Status no considerado");
			}
		}

		public void removeFeature(Feature f) {
			modified.remove(f.getID());
			data.remove(f.getID());
		}

		private void setStatus(String fid, int status) {
			if (status == UNMODIFIED_ATTRIBUTE) {
				modified.remove(fid);
				data.remove(fid);
			} else {
				modified.put(fid, new Integer(status));
			}
		}

		public void setStatus(Feature f, int status) {
			if (status == UPDATED_ATTRIBUTE || !data.containsKey(f.getID()) ) {
				addFeature(f, status);
			} else {
				setStatus(f.getID(), status);
			}
		}

		public void setUnmodified(String fid) {
			setStatus(fid, UNMODIFIED_ATTRIBUTE);
		}

		public int getStatus(Feature f) {
			return getStatus(f.getID());
		}

		public int getStatus(String fid) {
			return modified.containsKey(fid) ? ((Number) modified.get(fid))
					.intValue() : UNMODIFIED_ATTRIBUTE;
		}

		public boolean isModified(Feature f) {
			return modified.containsKey(f.getID());
		}

		public Collection getCreatedAndModified() {
			String fid;
			Collection<Feature> res = new ArrayList<Feature>();
			int status;

			for (Iterator<String> it = modified.keySet().iterator(); it.hasNext();) {
				fid = it.next();
				status = getStatus(fid);
				if (status == CREATED_ATTRIBUTE || status == UPDATED_ATTRIBUTE) {
					res.add((Feature)data.get(fid));
				}
			}

			return res;
		}

		public Collection<Feature> getModified() {
			String fid;
			Collection<Feature> res = new ArrayList<Feature>();

			for (Iterator<String> it = modified.keySet().iterator(); it.hasNext();) {
				fid = it.next();
				res.add((Feature)data.get(fid));
			}

			return res;
		}

		public boolean hasModified() {
			return !modified.isEmpty();
		}

		public boolean contains(Feature f) {
			return data.containsKey(f.getID());
		}

		public void clear() {
			modified.clear();
			data.clear();
		}
	}
}
