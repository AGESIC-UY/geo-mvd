package imm.gis.comm;

import java.util.HashMap;

import imm.gis.AppContext;
import imm.gis.GisException;
import imm.gis.comm.GisSerialization.FeatureCollectionTransporter;
import imm.gis.comm.GisSerialization.FeatureTransporter;
import imm.gis.comm.datatypes.datainput.ISaveDataType;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.LayerTransporter;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.geotools.data.CollectionFeatureReader;
import org.geotools.data.FeatureReader;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.filter.FilterTransformer;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;


import com.vividsolutions.jts.geom.Point;

public class HttpServerServices extends HttpObjectConnection implements IServerServices {
	private Logger log = Logger.getLogger(HttpServerServices.class);
	private static HttpServerServices instance;
	private GisSerialization.FilterTransporter filterTrans = new GisSerialization.FilterTransporter(
			"");
	private FilterTransformer transformer = new FilterTransformer();
	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());

	private HttpServerServices() {
		log.info("Creando instancia de servicios a traves de HTTP...");
		setContext("/SIGServerWeb/ServletGISServices");
		setCodebase("http://localhost:8080");
	}

	public void closeApp(String idApp) throws GisException {
		String appId = AppContext.getInstance().getId();
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("SERVICIO", "closeApp");
		map.put("appID", appId);

		callService(map);
	}

	public String createFeatureID(Feature f) throws GisException {
		String appId = AppContext.getInstance().getId();
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("SERVICIO", "createFeatureID");
		map.put("appID", appId);
		map.put("feature", new FeatureTransporter(f));

		return (String)callService(map);
	}

	public String getEventLocation(String appID, double x, double y) {
		return null;
	}

	public void initApp(String idApp) throws GisException {
		log.info("Inciando aplicacion por HTTP...");
		AppContext appContext = AppContext.getInstance();
		LayerTransporter capas[] = null;

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("SERVICIO", "initApp");
		map.put("appID", idApp);
		capas = (LayerTransporter[]) callService(map);

		Layer res[] = new Layer[capas.length];
		String styles[] = new String[capas.length];
		for (int i = 0; i < capas.length; i++) {
			res[i] = capas[i].toCapa();
			styles[i] = capas[i].getSldFileName();
		}
		appContext.setId(idApp);
		appContext.setLayers(res, styles);
	}

	public FeatureCollection loadLayer(FeatureType ft, Filter filter,
			boolean clientFilter) throws Exception {
		Filter userFilter = AppContext.getInstance().getCapa(ft.getTypeName())
				.getFilter();

		return _loadLayer("loadLayer", ft,
				(userFilter != null && clientFilter) ? ff.and(filter, userFilter)
						: filter, null).toFeatureCollection();
	}

	public FeatureReader loadLov(FeatureType ft, Filter filter,
			String[] properties) throws GisException {
		FeatureCollectionTransporter toReturn = null;

		toReturn = (FeatureCollectionTransporter) this._loadLayer("loadLov", ft,
				filter, properties);

		return toReturn.toOrderedFeatureReader();
	}

	public FeatureReader loadLov(FeatureType ft, Filter filter,
			String[] properties, String sortBy) throws GisException {
		FeatureCollection toReturn = null;

		toReturn = (FeatureCollection) this._loadLayer("loadLov", ft,
				filter, properties, sortBy);

		return new CollectionFeatureReader(toReturn, ft);
	}

	public void saveData(ISaveDataType data) throws Exception {
		String appId = AppContext.getInstance().getId();
		HashMap<String, Object> params = new HashMap<String, Object>();

		params.put("SERVICIO", "saveData");
		params.put("appID", appId);
		params.put("data", data);

		callService(params);
	}

	public void setConcreteLoadLayerMethod(String methodName) {
	}

	public static IServerServices getInstance() {
		if (instance == null) {
			instance = new HttpServerServices();
		}

		return instance;
	}

	private FeatureCollectionTransporter _loadLayer(String servicio, FeatureType ft,
			Filter filter, String properties[]) throws GisException {
		return _loadLayer(servicio, ft, filter, properties, null);
	}

	private FeatureCollectionTransporter _loadLayer(String servicio, FeatureType ft,
			Filter filter, String properties[], String sortBy) throws GisException {
		String appId = AppContext.getInstance().getId();
		String layerName = ft.getTypeName();
		String filter_Str;

		if (filter == Filter.EXCLUDE) {
			filter_Str = "";
		} else {
			try {
				filter_Str = transformer.transform(filter);
			} catch (TransformerException e) {
				e.printStackTrace();
				throw new GisException(e);
			}
		}

		filterTrans.setFilterStr(filter_Str);

		FeatureCollectionTransporter fct;
		HashMap<String, Object> params = new HashMap<String, Object>();

		params.put("SERVICIO", servicio);
		params.put("appID", appId);
		params.put("layerName", layerName);
		params.put("filter", filterTrans);
		params.put("properties", properties);
		if (sortBy != null){
			params.put("sortBy", sortBy);
		}

		fct = (FeatureCollectionTransporter) callService(params);

		return fct;
	}

	public String getRemoteUser() throws GisException {
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("SERVICIO", "remoteUser");

		return (String)callService(map);
	}

	public Boolean isInRol(String rol) throws GisException {
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("SERVICIO", "isInRol");
		map.put("rol", rol);

		return (Boolean)callService(map);
	}
	
	public Object getDataGeoUbicacion (Point p, boolean incluir_can_sj, boolean incluir_espacios_sn, boolean incluir_chapas) throws Exception{
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("SERVICIO", "getDataGeoUbicacion");
		map.put("punto", p);
		map.put("incluir_can_sj", incluir_can_sj);
		map.put("incluir_espacios_sn", incluir_espacios_sn);
		map.put("incluir_chapas", incluir_chapas);		
		return callService(map);		
	}

	public Object getDataGeoUbicacionEsquina (double x, double y) throws Exception{
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("SERVICIO", "getDataGeoUbicacionEsquina");
		map.put("x", x);
		map.put("y", y);
		return callService(map);		
	}
	
	public Object getNombreVia (int via) throws Exception{
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("SERVICIO", "getNombreVia");
		map.put("via", via);

		return callService(map);
		
	}

}
