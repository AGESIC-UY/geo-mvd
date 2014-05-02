package imm.gis.comm;

import org.geotools.data.FeatureReader;
import org.geotools.feature.FeatureType;
import org.opengis.filter.Filter;
import imm.gis.comm.datatypes.datainput.ISaveDataType;

public interface IServerServices {
	public FeatureReader loadLayer(FeatureType ft, Filter filter) throws Exception;
	public void saveData(ISaveDataType data) throws Exception;
}
