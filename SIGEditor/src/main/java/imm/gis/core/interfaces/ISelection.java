package imm.gis.core.interfaces;

import imm.gis.core.model.selection.FeatureSelectionListener;

import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.geotools.feature.Feature;
import org.geotools.feature.IllegalAttributeException;

public interface ISelection {

	
	public void addFeatureSelectionListener(FeatureSelectionListener listener);
	public void removeFeatureSelectionListener(FeatureSelectionListener al);
	
	public void setSelectedType(String type);
	public String getSelectedType();
	
	public void selectFeature(Feature f);
	public void unselectFeature(Feature f);
	public boolean isSelected(Feature f);
	
	public Collection getSelected(String type) throws NoSuchElementException, IOException, IllegalAttributeException;

	public void cleanSelection(String editedType);
	public void unselectAll();
}
