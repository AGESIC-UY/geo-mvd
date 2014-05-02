package imm.gis.core.interfaces;

import java.util.Collection;

import org.geotools.feature.Feature;

public interface IForm {

	public void openShowFeatureForm(Feature f) throws Exception;
	public void openShowFeaturesForm(Collection col) throws Exception;
	public void openEditCollectionFeatureForm(Collection col) throws Exception;	
	public void openEditFeatureForm(Feature f) throws Exception;	
	public void openNewFeatureForm(Feature f) throws Exception;
	public void openEditFeaturesForm(Collection features) throws Exception;
	public void enableEditFeatureForm() throws Exception;
	public void disableEditFeatureForm() throws Exception;
	public void setUneditableFeatures(Collection col);
	public void setCalculatedAttribute(String featureID, String nameAtt, Object value);
	public void setInsDelMultipleChild(boolean insDelMultipleChild);
}
