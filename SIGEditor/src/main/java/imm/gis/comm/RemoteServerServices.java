package imm.gis.comm;

import imm.gis.AppContext;
import imm.gis.GisException;
import imm.gis.comm.GisSerialization.FeatureCollectionTransporter;
import imm.gis.comm.GisSerialization.FeatureTransporter;
import imm.gis.comm.commons.IGis;
import imm.gis.comm.commons.IGisMethods;
import imm.gis.comm.datatypes.datainput.ISaveDataType;
import imm.gis.comm.interfaces.IClientFacade;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.LayerTransporter;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureReader;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.geotools.filter.FilterTransformer;

public class RemoteServerServices implements IServerServices {
	private GisSerialization.FilterTransporter filterTrans = new GisSerialization.FilterTransporter(
			"");
	private FilterTransformer transformer = new FilterTransformer();
	private IClientFacade clientFacade = FactoryClientFacade.getIClientFacade();
	private String concreteLoadLayerMethod = IGisMethods.LOADLAYER;
	private static RemoteServerServices _instance = null;
	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
	private static Logger logger = Logger.getLogger(RemoteServerServices.class.getName());

	private RemoteServerServices() {
	}

	public static RemoteServerServices getInstance() {
		if (_instance == null) {
			_instance = new RemoteServerServices();
		}
		return _instance;
	}

	public FeatureCollection loadLayer(FeatureType ft, Filter filter,
			boolean clientFilter) throws GisException {
		Filter userFilter = AppContext.getInstance().getCapa(ft.getTypeName())
				.getFilter();

		return loadLayer(ft, (userFilter != null && clientFilter) ? ff.and(filter, userFilter)
				: filter, null);
	}

	private FeatureCollection loadLayer(FeatureType ft, Filter filter,
			String properties[]) throws GisException {
		return loadLayerByMethod(ft, filter, properties).toFeatureCollection();
	}

	public void saveData(ISaveDataType data) throws GisException {
		String appId = AppContext.getInstance().getId();
		Object[] parameters = { appId, data };
		Class[] parametersClazz = { String.class, ISaveDataType.class };

		clientFacade.execute(IGis.GIS_INTERFACE, IGisMethods.SAVEDATA,
				parameters, parametersClazz);

	}

	public String createFeatureID(Feature f) throws GisException {
		String appId = AppContext.getInstance().getId();
		Object[] parameters = { appId, new FeatureTransporter(f) };
		Class[] parametersClazz = { String.class, FeatureTransporter.class };

		return (String) clientFacade.execute(IGis.GIS_INTERFACE,
				IGisMethods.CREATE_FEATURE_ID, parameters, parametersClazz);
	}

	public void initApp(String idApp) throws GisException {
		AppContext appContext = AppContext.getInstance();
		LayerTransporter capas[] = null;
		IClientFacade clientFacade = FactoryClientFacade.getIClientFacade();
		Object[] parameters = new Object[] { idApp };
		Class[] parameterTypes = new Class[] { String.class };
		
		logger.debug("Invocando InitApp");
		capas = (LayerTransporter[]) clientFacade.execute(IGis.GIS_INTERFACE,
				IGisMethods.INITAPP, parameters, parameterTypes);

		Layer res[] = new Layer[capas.length];
		String styles[] = new String[capas.length];
		for (int i = 0; i < capas.length; i++) {
			res[i] = capas[i].toCapa();
			styles[i] = capas[i].getSldFileName();
		}
		appContext.setId(idApp);
		appContext.setLayers(res, styles);
		logger.debug("==== InitApp=====Invocada ");
	}

	public FeatureReader loadLov(FeatureType ft, Filter filter,
			String[] properties, String sortBy) throws GisException {
		String previousMethod = this.concreteLoadLayerMethod;
		this.setConcreteLoadLayerMethod(IGisMethods.LOADLOV);

		FeatureCollectionTransporter fct = loadLayerByMethod(ft, filter, properties, sortBy);
		
		this.setConcreteLoadLayerMethod(previousMethod);
		return fct.toOrderedFeatureReader();
	}

	public FeatureReader loadLov(FeatureType ft, Filter filter,
			String[] properties) throws GisException {
		return loadLov(ft, filter, properties, null);
	}

	private FeatureCollectionTransporter loadLayerByMethod(FeatureType ft, Filter filter,
			String properties[]) throws GisException {
		return loadLayerByMethod(ft, filter, properties, null);
	}

	private FeatureCollectionTransporter loadLayerByMethod(FeatureType ft, Filter filter,
			String properties[], String sortBy) throws GisException {
		String appId = AppContext.getInstance().getId();
		String layerName = ft.getTypeName();
		String filter_Str;
		try {
			logger.debug("Cargando capa " + layerName + ", Filtro =" + filter);
			if (filter == Filter.EXCLUDE)
				filter_Str = "";
			else
				filter_Str = transformer.transform(filter);
			filterTrans.setFilterStr(filter_Str);
			logger.debug("Filtro transformado: " + filter_Str);
			Object[] parameters = (sortBy != null) ? new Object[] { appId, layerName, filterTrans, properties, sortBy }:
													 new Object[] { appId, layerName, filterTrans, properties};
			Class[] parameterTypes = (sortBy != null) ? new Class[] { String.class, String.class, filterTrans.getClass(), String[].class, String.class}:
													    new Class[] { String.class, String.class, filterTrans.getClass(), String[].class};

			FeatureCollectionTransporter fct = (FeatureCollectionTransporter) clientFacade
					.execute(IGis.GIS_INTERFACE, concreteLoadLayerMethod,
							parameters, parameterTypes);

			return fct;
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new GisException(e);
		}
	}

	public String getEventLocation(String appID, double x, double y)
			throws GisException {

		Object[] parameters = { appID, new Double(x), new Double(y) };
		Class[] parametersClazz = { String.class, Double.TYPE, Double.TYPE };

		return (String) clientFacade.execute(IGis.GIS_INTERFACE,
				IGisMethods.GETLOCATIONDESC, parameters, parametersClazz);
	}

	public void setConcreteLoadLayerMethod(String methodName) {

		concreteLoadLayerMethod = methodName;

	}

	public void closeApp(String idApp) throws GisException {
		IClientFacade clientFacade = FactoryClientFacade.getIClientFacade();
		Object[] parameters = new Object[] { idApp };
		Class[] parameterTypes = new Class[] { String.class };
		
		logger.info("Invocando closeApp para " + idApp);
		clientFacade.execute(IGis.GIS_INTERFACE, IGisMethods.CLOSEAPP, parameters, parameterTypes);		
	}

	public String getRemoteUser() throws GisException {
		return "anonymous";
	}

	public Boolean isInRol(String rol) throws GisException {
		return null;
	}
}
