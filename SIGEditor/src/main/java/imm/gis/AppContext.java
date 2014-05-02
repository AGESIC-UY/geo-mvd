package imm.gis;

import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.layer.ILayerAtributesContext;
import imm.gis.core.layer.Layer;
import imm.gis.core.model.StyleModel;
import imm.gis.form.IFeatureForm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


//import org.apache.log4j.Logger;
import org.geotools.feature.FeatureType;
import org.opengis.filter.Filter;

public class AppContext {
//	private static Logger log = Logger.getLogger(AppContext.class);
	private static AppContext instance = null;
	private Map<String, Layer> capas = new HashMap<String, Layer>();
	private ArrayList<String> layerList = new ArrayList<String>();
	private String id;
	private StyleModel styleModel = new StyleModel();
	private ICoreAccess coreAccess;
	private Map<String, IFeatureForm> forms = new HashMap<String, IFeatureForm>();
	private Map<String, Map<String, Filter>> predefFilters = new HashMap<String, Map<String, Filter>>();
	
	private AppContext() {

	}

	public static AppContext getInstance() {
		if (instance == null) {
			instance = new AppContext();
		}

		return instance;
	}

	public void setLayers(Layer layers[], String st[]){
		Layer layer;
		Map<String, String> styles = new HashMap<String, String>();
		
		capas.clear();
		for (int i = 0; i < layers.length; i++){
			layer = layers[i];
			capas.put(layer.getNombre(), layer);
			if (layer.isVisible()) styles.put(layer.getNombre(), st[i]);
			if(!layerList.contains(layer.getNombre())){
				layerList.add(layer.getNombre());
			}
		}
		
		styleModel.parseStyles(styles);
		styles = null;
	}

	public Layer[] getLayers() {
		int length = capas.values().size();

		return (Layer[]) capas.values().toArray(new Layer[length]);
	}

	public Layer getCapa(String name) {
		return (Layer) capas.get(name);
	}

	public FeatureType getSchema(String layer) {
		return getCapa(layer).getFt();
	}

	public Iterator<String> getTypeNames() {
		return layerList.iterator();
	}

	public boolean isChild(String typeName) {
		return ((Layer) capas.get(typeName)).getMetadata().isChild();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void addAttributeContext2Layer(String layerName,ILayerAtributesContext aac){
		
		if(existsLayer(layerName)){
			Layer layer = getCapa(layerName);
			layer.addAttributeContext(aac);
		}
	}
	
	public boolean existsLayer(String layerName){
		 return capas.containsKey(layerName);
	}

	public StyleModel getStyleModel(){
		return styleModel;
	}

	public ICoreAccess getCoreAccess() {
		return coreAccess;
	}

	public void setCoreAccess(ICoreAccess coreAccess) {
		this.coreAccess = coreAccess;
	}
	
	public IFeatureForm getUserForm(String layer){
		return (IFeatureForm)forms.get(layer);
	}
	
	public void setUserForm(String layer, IFeatureForm form){
		forms.put(layer, form);
	}
	
	public void setPredefFilters(String layer, Map<String, Filter> filters){
		predefFilters.put(layer, filters);
	}
	
	public Map<String, Filter> getPredefFilters(String layer){
		if (predefFilters.get(layer) == null){
			predefFilters.put(layer, new TreeMap<String, Filter>(new Comparador()));
		}
		
		return (Map<String, Filter>)predefFilters.get(layer);
	}
	
	
	public class Comparador implements Comparator<String>{
		 
	    @Override
	    public int compare(String v1, String v2){
	    	String filterParent1 = "", filterChild1 = "";
	    	String filterParent2 = "", filterChild2 = "";
	    	String[] parentChild;
	    	int compareParents;
	    	
	    	if (v1.startsWith("Parent")){
    			v1 = v1.substring(6);    			
    			filterParent1 = v1.split("->")[0];
    			filterChild1 = v1.split("->")[1];    			
    		} else {
    			parentChild = v1.split("->");    			 
    			if ((parentChild.length == 2) && parentChild[1].equalsIgnoreCase("center"))
    				filterChild1 = parentChild[0];
    			else
    				filterChild1 = v1;    	
    		}
	    	
	    	if (v2.startsWith("Parent")){
    			v2 = v2.substring(6);    			
    			filterParent2 = v2.split("->")[0];
    			filterChild2 = v2.split("->")[1];     			
    		} else {
    			parentChild = v2.split("->");    			 
    			if ((parentChild.length == 2) && parentChild[1].equalsIgnoreCase("center"))
    				filterChild2 = parentChild[0];
    			else
    				filterChild2 = v2;
    		}
	    	
	    	if ((filterParent1 != "") && (filterParent2 != "")){
	    		compareParents = compareStringWithNumbers(filterParent1, filterParent2);
	    		if (compareParents == 0)
	    			return compareStringWithNumbers(filterChild1, filterChild2);
	    		else
	    			return compareParents;
	    	} else if ((filterChild1 != "") && (filterChild2 != "")){
	    		return compareStringWithNumbers(filterChild1, filterChild2);
	    	} else
	    		return 0;
		}
	    
	    private int compareStringWithNumbers(String s1, String s2){
	    	boolean tiene_letras_s1 = false;
			boolean tiene_letras_s2 = false;
			Integer numerico1 = 0;
			Integer numerico2 = 0;
			try{
				numerico1 = Integer.parseInt(s1);
			}
			catch(NumberFormatException e){
				tiene_letras_s1 = true;
			}
			try{
				numerico2 = Integer.parseInt(s2);
			}
			catch(NumberFormatException e){
				tiene_letras_s2 = true;
			}
			if (tiene_letras_s1 && tiene_letras_s2){
				if (s1.charAt(0)==s2.charAt(0)){
					try{
						Integer linea_s1 =Integer.parseInt(s1.substring(1));
						Integer linea_s2 = Integer.parseInt(s2.substring(1));
						return linea_s1.compareTo(linea_s2);
					}
					catch(NumberFormatException e){
						return s1.compareTo(s2);
					}
				}
				else
					return s1.compareTo(s2);
			}
			if (tiene_letras_s1 && !tiene_letras_s2)
				return 1;
			if (!tiene_letras_s1 && tiene_letras_s2)
				return -1;
			if (!tiene_letras_s1 && !tiene_letras_s2)
				return numerico1.compareTo(numerico2);
			return 0;
	    }
	     
	}
	
}
