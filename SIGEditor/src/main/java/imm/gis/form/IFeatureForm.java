package imm.gis.form;

import java.util.Map;

import imm.gis.core.model.IFeatureHierarchyWrapper;

import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

public interface IFeatureForm {

	public static final int NEW_FEATURE = 1;

	public static final int SHOW_FEATURE = 2;

	public static final int MODIFY_FEATURE = 3;

	public static final int SEARCH_FEATURE = 4;

	public static final int MODIFY_FEATURES = 5;

	public Feature getFeature();

	public void saveChanges();

	public void show(IFeatureHierarchyWrapper data) throws IllegalAttributeException;
	
	public void show(IFeatureHierarchyWrapper data, Map<String,Map<String,Object>> calculatedAttributes, final boolean insertDeleteMultiple) throws IllegalAttributeException;

	public void setFeatureType(FeatureType ft);

	public FeatureType getFeatureType();

	public void close();
}