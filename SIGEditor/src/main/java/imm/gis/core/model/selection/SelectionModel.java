package imm.gis.core.model.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import imm.gis.core.interfaces.ISelection;
import imm.gis.core.model.ModelData;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureChangeListener;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.edition.EditionContextListener;

import org.geotools.feature.Feature;

/**
 * Maneja la seleccion de features. 
 */
public class SelectionModel implements ISelection, FeatureChangeListener, EditionContextListener {

	private ModelData model;
	
	private String selectedType;
	
	private List<FeatureSelectionListener> featureSelectionListeners;

	private SelectedFeaturesCache cache;

	private String listenedType = null;
	
	public SelectionModel(ModelData model) {
		featureSelectionListeners = new ArrayList<FeatureSelectionListener>();
		this.model = model;
		selectedType = null;
		cache = new SelectedFeaturesCache();
	}
	
	public void setSelectedType(String layer) {
		String oldSelectedType = selectedType;
		
		selectedType = layer;
		
		if (oldSelectedType != null)
			cleanSelection(oldSelectedType);

		notifySelectionListeners(FeatureSelectionEvent.SELECTED_LAYER, layer, null);
	}
	
	public String getSelectedType() {
		return selectedType;
	}

	public boolean isSelected(Feature f) {
		return cache.isSelected(f);
	}

	public void addFeatureSelectionListener(FeatureSelectionListener al) {
		featureSelectionListeners.add(al);
	}

	public void removeFeatureSelectionListener(FeatureSelectionListener al) {
		featureSelectionListeners.remove(al);
	}
	
	public Collection getSelected(String type){
		return cache.getSelectedFeatures(type);
	}
	
	public void selectFeature(Feature f) {
		cache.setSelected(f, true);
		notifySelectionListeners(FeatureSelectionEvent.SELECTED_FEATURE, f.getFeatureType().getTypeName(), f);
	}

	public void unselectFeature(Feature f) {
		cache.setSelected(f, false);
		notifySelectionListeners(FeatureSelectionEvent.UNSELECTED_FEATURE, f.getFeatureType().getTypeName(), f);
	}
	
	public void notifySelectionListeners(int op, String layer, Feature lastSel) {
		final FeatureSelectionEvent ae = new FeatureSelectionEvent(this, op, layer, lastSel);

		for (Iterator it = featureSelectionListeners.iterator(); it.hasNext();) {
			((FeatureSelectionListener) it.next()).selectionPerformed(ae);
		}
	}

	public void cleanSelection(String type) {
		cache.unselectAll(type);
		notifySelectionListeners(FeatureSelectionEvent.UNSELECTED_ALL_FEATURES, type, null);
	}

	public void unselectAll() {
		String types[] = model.getNotEmptyTypes();

		for (int i = 0; i < types.length; i++)
			cleanSelection(types[i]);

		notifySelectionListeners(FeatureSelectionEvent.UNSELECTED_ALL_FEATURES, null, null);
	}

	public void changePerformed(FeatureChangeEvent fc) {
		if (fc.getOperation() == FeatureChangeEvent.DELETED_FEATURE)
			unselectFeature(fc.getOldFeature());
		else if (fc.getOperation() == FeatureChangeEvent.MODIFIED_FEATURE)
			selectFeature(fc.getNewFeature());
		else if (fc.getOperation() == FeatureChangeEvent.CLEANED_CACHE) {
			notifySelectionListeners(FeatureSelectionEvent.UNSELECTED_ALL_FEATURES, listenedType, null);
			cache.clean();
		}
	}

	public String getLayer() {
		return listenedType;
	}

	public void isChanging(FeatureChangeEvent fc) {
		// TODO Auto-generated method stub
		
	}

	public void editionEntered(String type) {
		listenedType = type;
		model.addFeatureChangeListener(this, FeatureEventManager.AFTER_MODIFY);
	}

	public void editionExited(String type) {
		model.removeFeatureChangeListener(this, FeatureEventManager.AFTER_MODIFY);
		listenedType = null;
	}
}
