package imm.gis.comm;

import imm.gis.GisException;

import org.geotools.data.FeatureReader;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.opengis.filter.Filter;

public interface IServerServices {
	public void initApp(String idApp) throws GisException;
	public void closeApp(String idApp) throws GisException;
	public FeatureCollection loadLayer(FeatureType ft, Filter filter, boolean clientFilter) throws Exception;
	public void saveData(imm.gis.comm.datatypes.datainput.ISaveDataType data) throws Exception;
	public FeatureReader loadLov(FeatureType ft, Filter filter, String properties[]) throws GisException;
	public FeatureReader loadLov(FeatureType ft, Filter filter, String properties[], String sortBy) throws GisException;
	String getEventLocation(String appID, double x, double y)throws GisException;
	public void setConcreteLoadLayerMethod(String methodName);
	public String createFeatureID(Feature f) throws GisException;
	public Boolean isInRol(String rol) throws GisException;
	public String getRemoteUser() throws GisException;
}