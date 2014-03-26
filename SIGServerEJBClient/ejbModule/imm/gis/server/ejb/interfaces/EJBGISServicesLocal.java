package imm.gis.server.ejb.interfaces;

import imm.gis.GisException;
import imm.gis.comm.GisSerialization.FeatureCollectionTransporter;
import imm.gis.comm.GisSerialization.FeatureTransporter;
import imm.gis.comm.GisSerialization.FilterTransporter;
import imm.gis.comm.datatypes.datainput.ISaveDataType;
import imm.gis.core.layer.LayerTransporter;

import java.sql.SQLException;

import javax.ejb.Local;

import org.geotools.feature.SchemaException;

@Local
public interface EJBGISServicesLocal {

	FeatureCollectionTransporter loadLayer(String appID, String layerName,
			FilterTransporter filter) throws SchemaException, GisException;

	FeatureCollectionTransporter loadLayer(String appID, String layerName,
			FilterTransporter filter, String properties[])
			throws SchemaException, GisException;

	LayerTransporter[] initApp(String appID);

	void saveData(String appID, ISaveDataType data) throws GisException;

	FeatureCollectionTransporter loadLov(String appID, String layerName,
			FilterTransporter filter, String properties[])
			throws SchemaException, GisException;

	FeatureCollectionTransporter loadLov(String appID, String layerName,
			FilterTransporter filter, String properties[], String sortBy)
			throws SchemaException, GisException;

	public String createFeatureID(String appID, FeatureTransporter ft)
			throws GisException;
	
	public String asKML(String geom) throws SQLException;
}
