package imm.gis.client.fidmapper;

import java.util.HashMap;
import java.util.Map;

public class FidMapperUtil {
	Map<String, ILayerFIDMapper> _layersFIDMappers = new HashMap<String, ILayerFIDMapper>();
	private static FidMapperUtil instance = null;

	/**
	 * 
	 * @param layersFIDMappers
	 *            Mapa que contiene (nombreLayer,ILayerFIDMapper) por cada capa
	 */
	private FidMapperUtil() {

	}
	public static FidMapperUtil getInstance(){
		if(instance == null){
			instance = new FidMapperUtil();
		}
		return instance;
	}
	

	/**
	 * 
	 * @param layerName
	 * @param id
	 * @return
	 */
	public String getFID(String layerName, Object id) {
		return getLayeFIDMapper(layerName).getFID(id);

	}
	
	public Object getID(String layerName, String fID){
		return getLayeFIDMapper(layerName).getID(fID);

	}
	public void addFidMapper(String layerName,ILayerFIDMapper ilfMapper){
		_layersFIDMappers.put(layerName,ilfMapper);
	}
	
	private ILayerFIDMapper getLayeFIDMapper(String layerName) {

		if (!_layersFIDMappers.containsKey(layerName)) {

			_layersFIDMappers.put(layerName, new PreffixFIDMapper(layerName));
		}

		return ((ILayerFIDMapper) _layersFIDMappers.get(layerName));

	}

}
