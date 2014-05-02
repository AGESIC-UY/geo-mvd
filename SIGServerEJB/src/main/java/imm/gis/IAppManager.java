package imm.gis;

import java.io.Serializable;
import java.util.ArrayList;

import imm.gis.core.datasource.IDataSource;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.definition.LayerDefinition;
import imm.gis.core.layer.filter.IUserFiltersProvider;

import org.geotools.data.DataStore;
import org.geotools.data.jdbc.fidmapper.FIDMapper;
//import org.geotools.data.jdbc.fidmapper.FIDMapperFactory;
import org.geotools.feature.FeatureType;
import org.geotools.styling.Style;

public interface IAppManager extends Serializable {
	public Layer getLayer(String name);
	public LayerDefinition getLayerDefinition(String name);
	public FeatureType getSchema(String layer);
	public String[] getTypeNames();
	public DataStore getDataStore();
	public Style getStyle(String layer);
	public String getIdApp();
	public ArrayList<String> getLayerNamesFilteredByUser();
	public FIDMapper getFIDMapper(String layerName);
	public IDataSource getDataSource(String layerName);
	public IUserFiltersProvider getUserFiltersProvider();
	public void dispose();
}