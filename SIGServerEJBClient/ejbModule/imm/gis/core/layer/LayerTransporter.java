package imm.gis.core.layer;

import imm.gis.GisException;
import imm.gis.comm.GisSerialization.FeatureTypeTransporter;
import imm.gis.core.layer.metadata.LayerMetadata;

import java.io.Serializable;
import java.util.Map;

public class LayerTransporter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sldFileName = null;
	private String dataFileName = null;
	private FeatureTypeTransporter ft;
	private boolean editable = false;
	private String nombre;
	private boolean visible = true;
	private boolean isChild = false;
	private LayerMetadata metadata = null;
	
	public LayerTransporter(Layer c){
        
        
        sldFileName = c.getSldFile();
        dataFileName = c.getDataFile();
        ft = new FeatureTypeTransporter(c.getFt());  
         editable = c.isEditable();
        nombre = c.getNombre();
        visible = c.isVisible();
        metadata = c.getMetadata();
    }

	public String getDataFileName() {
		return dataFileName;
	}
	
	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public FeatureTypeTransporter getFt() {
		return ft;
	}
	
	public void setFt(FeatureTypeTransporter ft) {
		this.ft = ft;
	}
	
	public Map<?,?> getFuentesDatos() {
		return null;
	}
	
	public boolean isChild() {
		return isChild;
	}
	
	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getSldFileName() {
		return sldFileName;
	}
	
	public void setSldFileName(String sldFileName) {
		this.sldFileName = sldFileName;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Layer toCapa() throws GisException{
		
		Layer c = new Layer(
			null,
			dataFileName,
			ft.toFeatureType(),
			editable,
			nombre,
			visible
		);
		
		c.setMetadata(metadata);
		return c;
	}
}
