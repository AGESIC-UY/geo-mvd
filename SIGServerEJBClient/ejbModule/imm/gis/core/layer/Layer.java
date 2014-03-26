package imm.gis.core.layer;

import org.geotools.feature.FeatureType;
import org.opengis.filter.Filter;

import imm.gis.core.layer.metadata.LayerMetadata;


/**
 * Simplemente centraliza toda la informacion referente a una capa,
 * tanto la definicion de esta como la informacion que se necesite
 * en tiempo de ejecucion.
 * 
 * @author sig
 *
 */
public class Layer {
	private String sldFileName = null;
	private String dataFileName = null;
	private String metadataFileName = null;
	
	private FeatureType ft;
	private boolean editable = false;
	private String nombre;
	private boolean visible = true;
	private Filter filter = null;
	private LayerContextManager ctxAttrManager = new LayerContextManager();
	private LayerMetadata metadata;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Layer(String sld, String dfn, FeatureType _ft, boolean e, String n, boolean v){
		this.sldFileName = sld;
		this.dataFileName = dfn;
		this.ft = _ft;
		this.editable = e;
		this.nombre = n;
		this.visible = v;
	}
	
	public Layer() {
	}

	public FeatureType getFt() {
		return ft;
	}

	public void setFt(FeatureType ft) {
		this.ft = ft;
	}

	public String getDataFile() {
		return dataFileName;
	}

	public void setDataFile(String dataFile) {
		this.dataFileName = dataFile;
	}

	public String getSldFile() {
		return sldFileName;
	}

	public void setSldFile(String sldFile) {
		this.sldFileName = sldFile;
	}
	
	public String toString(){
		return nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setMetadata(LayerMetadata layerMetadata) {
		this.metadata = layerMetadata;
	}
	
	public LayerMetadata getMetadata() {
		return this.metadata;
	}
	
	public void setMetadataFileName(String metadataFileName) {
		this.metadataFileName = metadataFileName;
	}
	
	public String getMetadataFileName() {
		return this.metadataFileName;
	}
	
	public void addAttributeContext(ILayerAtributesContext iac){
		this.ctxAttrManager.addAttributeContext(iac);
	}

	public LayerContextManager getCtxAttrManager() {
		return ctxAttrManager;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
}
