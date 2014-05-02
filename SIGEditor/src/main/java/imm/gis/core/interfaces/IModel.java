package imm.gis.core.interfaces;

import imm.gis.core.model.ModelContext;
import imm.gis.core.model.ModifiedData;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureChangeListener;
import imm.gis.core.model.event.FeatureEventManager.EventType;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;

import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;


/**
 * Interfase con las operaciones sobre el modelo
 */
public interface IModel {

	public void addFeature(Feature f) throws IOException, IllegalAttributeException;
	public void delFeature(Feature f) throws IOException, IllegalAttributeException;
	public Feature modifyFeature(Feature f, String attributes[], Object values[]) throws Exception;
	public Feature modifyFeature(Feature f, String attrs[], Object values[], boolean refresh, boolean avisarCambios) throws Exception;
	
	public Feature createFeature(String editedType, Object attributes[]) throws IOException, IllegalAttributeException;
	public Feature createEmptyFeature(String editedType, Geometry g) throws IOException, IllegalAttributeException;
	
	public FeatureType getSchema(String layer) throws IOException;
	public Feature getFeature(String type, String FID, boolean updateModel);
	public boolean isFeatureEditable(Feature f);
	
	public GeometryFactory getGeometryFactory();
	
	public void addFeatureChangeListener(FeatureChangeListener listener, EventType eventType);
	public void removeFeatureChangeListener(FeatureChangeListener listener, EventType eventType);
	public void notifyChangeListeners(FeatureChangeEvent fe,
			EventType eventType);
	
	public ModelContext getModelContext();
	
	public boolean isModified(String type);
	public void confirmModified(String type) throws Exception;
	public void undoAllModified(String type) throws Exception;
	
	public void addGUIListener(ActionListener al);
	public void removeGUIListener(ActionListener al);

	public ModifiedData getModifiedFeatures(String title);
	public int getStatus(Feature f);
	public void refreshViews(boolean loadDB);
	public Iterator queryData(String layer, String textFilter) throws Exception;
	
	public boolean isEnabledLayer(String typeName);
	public void setLayerEnabled(String typeName, boolean enabled);
	public void setLayerEnabled(String layer, boolean enabled, boolean refresh);
	public void setBboxFilterLayer(String layer);

	public boolean isUsePanCache();
	public void setUsePanCache(boolean panCache);
	public void setLayerNotEditable(String layer);
}
