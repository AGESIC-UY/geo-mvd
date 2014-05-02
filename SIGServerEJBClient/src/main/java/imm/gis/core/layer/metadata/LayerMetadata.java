package imm.gis.core.layer.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * La idea es meter aca lo que tenga que ver mas con metadata descriptiva sobre
 * la capa. En el ori habria que dejar solo lo referente a la estructura de la
 * capa.
 */
public class LayerMetadata implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, LayerAttributeMetadata> attributesMetadata = new LinkedHashMap<String, LayerAttributeMetadata>();
	private String userIdAttribute = null;
	private String name = null;
	private List<ChildMetadata> childrenMetadata = new ArrayList<ChildMetadata>();
	private boolean isChild = false;
	private boolean fIDQueryCapable = false;
	private String fidAttributeName ="FID";

	public void setAttributesMetadata(Map<String, LayerAttributeMetadata> attributesMetadata) {
		this.attributesMetadata = attributesMetadata;
	}

	public Map<String, LayerAttributeMetadata> getAttributesMetadata() {
		return this.attributesMetadata;
	}

	public LayerAttributeMetadata getAttributeMetadata(String attributeName) {
		return attributesMetadata.get(attributeName);
	}

	public void setUserIdAttribute(String userIdAttribute) {
		this.userIdAttribute = userIdAttribute;
	}

	public String getUserIdAttribute() {
		return this.userIdAttribute;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public List<ChildMetadata> getChildrenMetadata() {
		return this.childrenMetadata;
	}

	public void setChildrenMetadata(List<ChildMetadata> childrenMetadata) {
		this.childrenMetadata = childrenMetadata;
	}

	public boolean hasChildren() {
		return !this.childrenMetadata.isEmpty();
	}

	public boolean isChild() {
		return isChild;
	}

	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}

	/**
	 * 
	 * @return Una lista de Maps donde la 'key' es el nombre del atributo que
	 *         referencia al padre y el 'value' es el nombre de la capa del
	 *         padre
	 */
	public Collection<Map<String, String>> getReferencedParentsAttributeInfo() {
		Collection<Map<String, String>> toReturn = new ArrayList<Map<String, String>>();
		Map<String, String> tmp;

		for (Iterator<LayerAttributeMetadata> iter = attributesMetadata.values().iterator(); iter
				.hasNext();) {
			LayerAttributeMetadata element = iter.next();
			if (element.isParentReference()) {
				tmp = new HashMap<String, String>();
				tmp.put(element.getName(), element.getReferencedLayers());
				toReturn.add(tmp);
			}

		}
		return toReturn;
	}

	public boolean isFIDQueryCapable() { 
		return fIDQueryCapable;
	}

	public void setFIDQueryCapable(boolean queryCapable) {
		fIDQueryCapable = queryCapable;
	}

	public String getFidAttributeName() {
		return fidAttributeName;
	}

	public void setFidAttributeName(String fidAttributeName) {
		this.fidAttributeName = fidAttributeName;
	}
	
	public String[] getVisibleAttributes(){
		String attName;
		ArrayList<String> atts = new ArrayList<String>();
		
		for (Iterator<String> it = attributesMetadata.keySet().iterator(); it.hasNext();){
			attName = it.next();
			if (attributesMetadata.get(attName).getShow()){
				atts.add(attName);
			}
		}
		
		return atts.toArray(new String[]{});
	}
}
