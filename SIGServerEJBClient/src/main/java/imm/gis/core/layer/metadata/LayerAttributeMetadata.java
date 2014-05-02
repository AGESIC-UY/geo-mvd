package imm.gis.core.layer.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class LayerAttributeMetadata implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public final static int ATTR_USAGE_NORMAL = 1;
	public final static int ATTR_USAGE_FEATURE_REFERENCE = 2;
	public final static int ATTR_USAGE_LOV = 3;
	public final static int ATTR_USAGE_LOV_PANEL = 4;
	
	private boolean readOnly = false;
	private boolean isLazy;
	private boolean queryCapable;
	private boolean show = true;
	private String referencedLayer = null;
	private int usage = ATTR_USAGE_NORMAL;
	private LayerAttributePresentation presentation;
	private Collection<String> dependences = new ArrayList<String>();
	private boolean lockedDependence = false;
	private String name;
	private String label;
	private boolean orderByDescription = false;
	
	public LayerAttributeMetadata() {
		this.presentation = new LayerAttributePresentation();
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	public void setUsage(int usage) {
		this.usage = usage;
	}
	
	public int getUsage() {
		return this.usage;
	}

	public void setReferencedLayers(String referencedLayer) {
		this.referencedLayer = referencedLayer;
	}
	
	public String getReferencedLayers() {
		return this.referencedLayer;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	
	public boolean isQueryCapable() {
		return this.queryCapable;
	}
	
	public void setQueryCapable(boolean queryCapable) {
		this.queryCapable = queryCapable;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setPresentation(LayerAttributePresentation presentation) {
		this.presentation = presentation;
	}
	
	public LayerAttributePresentation getPresentation() {
		return this.presentation;
	}

	public void addDependence(String attributeName) {
		dependences.add(attributeName);
	}
	
	public Iterator<String> getDependences() {
		return dependences.iterator();
	}

	public boolean getShow() {
		return this.show;
	}
	
	public void setShow(boolean show) {
		this.show = show;
	}
	
	public boolean isParentReference(){
		return ATTR_USAGE_FEATURE_REFERENCE == usage && referencedLayer!=null && !referencedLayer.equals("");
	}
	
	public void setLockedDependence(boolean lockedDependence) {
		this.lockedDependence = lockedDependence;
	}
	
	public boolean isLockedDependence() {
		return lockedDependence;
	}

	public String getLabel() {
		return label == null ? name: label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isOrderByDescription() {
		return orderByDescription;
	}
	
	public void setOrderByDescription(boolean b){
		orderByDescription = b;
	}
}
