package imm.gis.comm.datatypes.datainput;

import java.io.Serializable;
import java.util.Collection;

import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

public interface ISaveDataType extends Serializable {
	public final static int INSERT = 1;
	public final static int DELETE = 2;
	public final static int UPDATE = 3;
	public void addDeleteFID(String layer, String fid);
	public void addModifiedFeature(int action,String layer, String id, Object[] atts);
	public String[] getLayers();
	public Feature[] getUpdateFeatures(FeatureType layer) throws IllegalAttributeException;
	public Collection<String> getDeleteFIDs(String layer);
	public Feature[] getInsertFeatures(FeatureType layer) throws IllegalAttributeException;
}
