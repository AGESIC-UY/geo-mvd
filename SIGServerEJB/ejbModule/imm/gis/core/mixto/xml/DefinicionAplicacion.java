package imm.gis.core.mixto.xml;

import imm.gis.core.layer.Layer;
import imm.gis.core.layer.definition.LayerDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefinicionAplicacion implements Serializable {
	private static final long serialVersionUID = 1063236558237523612L;
	private String name;
	private Map<String, Layer> layersMap = new HashMap<String, Layer>();
	private ArrayList<Layer> layers = new ArrayList<Layer>();
	private Map<String, LayerDefinition> layerDefinitions;
	private String userFiltersProvider = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addLayer(Layer layer) {
		layers.add(layer);
		layersMap.put(layer.getNombre(), layer);
	}

	public Iterator<Layer> getLayers() {
		return layers.iterator();
	}

	public String toString() {
		StringBuffer str = new StringBuffer("Definicion aplicacion ");
		str.append(getName());
		for (Iterator<Layer> it = layers.iterator(); it
				.hasNext();) {
			str.append('\n');
			str.append(it.next());
		}

		return str.toString();
	}

	public Layer getLayer(String capa) {

		return (Layer) this.layersMap.get(capa);

	}
	
	public void setLayerDefinitions(Map<String, LayerDefinition> layerDefinitions) {
		this.layerDefinitions = layerDefinitions;
	}
	
	public Map<String, LayerDefinition> getLayerDefinitions() {
		return layerDefinitions;
	}
	
	public void setUserFiltersProvider(String userFiltersProvider) {
		this.userFiltersProvider = userFiltersProvider;
	}
	
	public String getUserFiltersProvider() {
		return this.userFiltersProvider;
	}
}
