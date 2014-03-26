package imm.gis.server.ejb;

import imm.gis.AppManager;
import imm.gis.GisException;
import imm.gis.IAppManager;
import imm.gis.ServerAppManager;
import imm.gis.comm.FilterDecoder;
import imm.gis.comm.GisSerialization;
import imm.gis.comm.GisSerialization.FeatureCollectionTransporter;
import imm.gis.comm.GisSerialization.FeatureTransporter;
import imm.gis.comm.GisSerialization.FeatureTypeTransporter;
import imm.gis.comm.GisSerialization.FilterTransporter;
import imm.gis.comm.datatypes.datainput.ISaveDataType;
import imm.gis.core.data.mixto.MixtoFIDFeature;
import imm.gis.core.feature.ExternalAttribute;
import imm.gis.core.feature.FeatureReferenceAttribute;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.LayerTransporter;
import imm.gis.core.layer.filter.IUserFiltersProvider;
import imm.gis.server.ejb.VO.VOLocation;
import imm.gis.server.ejb.interfaces.EJBGISServicesLocal;
import imm.gis.server.ejb.interfaces.EJBGISServicesRemote;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultQuery;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.jdbc.fidmapper.FIDMapper;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.SchemaException;
import org.geotools.filter.SortByImpl;
import org.geotools.styling.SLDTransformer;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.filter.spatial.Intersects;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Stateless
public class EJBGISServicesBean implements EJBGISServicesLocal, EJBGISServicesRemote{
	private FilterDecoder fDecoder = new FilterDecoder();
	private GeometryFactory geometryFactory = new GeometryFactory();
	private FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2( GeoTools.getDefaultHints() );
	private String currentUserName;
	private static Logger log = Logger.getLogger(EJBGISServicesBean.class);

	@Resource SessionContext _ctx;

	public FeatureCollectionTransporter loadLayer(String appID,
			String layerName, FilterTransporter filter) throws SchemaException,
			GisException {
		return loadLayer(appID, layerName, filter, null);
	}

	public FeatureCollectionTransporter loadLayer(String appID,
			String layerName, FilterTransporter filter, String properties[])
			throws SchemaException, GisException {

		IAppManager appManager = ServerAppManager.getInstance().getAppManager(
				appID);

		boolean withUserFilter = (appManager.getLayerNamesFilteredByUser()
				.contains(layerName));

		return loadLayerImpl(appID, layerName, filter, properties,
				withUserFilter);
	}

	public FeatureCollectionTransporter loadLov(String appID, String layerName,
			FilterTransporter filter, String properties[])
			throws SchemaException, GisException {
		return loadLov(appID, layerName, filter, properties, null);
	}

	public FeatureCollectionTransporter loadLov(String appID, String layerName,
			FilterTransporter filter, String properties[], String sortBy)
			throws SchemaException, GisException {

		return loadLayerImpl(appID, layerName, filter, properties, false, sortBy);

	}

	public String createFeatureID(String appID, FeatureTransporter ft)
			throws GisException {

		Layer lay;
		IAppManager appManager;
		Feature f;
		FIDMapper mapper;
		Connection con = null;
		Statement stat = null;
		String id;

		appManager = ServerAppManager.getInstance().getAppManager(appID);

		try {
			// Obtenemos el feature para el cual queremos crear un ID
			f = ft.toFeature(appManager.getSchema(ft.getTypeName()));

			lay = appManager.getLayer(ft.getTypeName());
			mapper = appManager.getFIDMapper(lay.getNombre());

			// Creo una conexion y un statement para que el fidmapper
			// utilice para consultar la base y crear el ID
			con = appManager.getDataSource(ft.getTypeName()).getConnection();
			stat = con.createStatement();

			id = mapper.createID(con, f, stat);

			return id;
		} catch (SQLException e) {
			throw new GisException(e);
		} catch (IOException e) {
			throw new GisException(e);
		} finally {
			// Libero
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e1) {
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException e1) {
			}
		}
	}

	public LayerTransporter[] initApp(String appID) {
		AppManager appManager = ServerAppManager.getInstance().getAppManager(
				appID);
		Iterator<Layer> capas = appManager.getLayers();
		LayerTransporter[] capaTrans = new LayerTransporter[appManager.cantLayers()];

		log.debug("Cargando SLD's...");
		SLDTransformer transformer = new SLDTransformer();
		
		Layer layer = null;
		for (int i = 0; i < capaTrans.length; i++) {
			layer = capas.next();
			capaTrans[i] = new LayerTransporter(layer);
				try {
					capaTrans[i].setSldFileName(transformer
							.transform(appManager
									.getStyle(layer.getNombre())));
				} catch (TransformerException e) {
					log.info("Error al transformar el SLD para capa "
							+ layer.getNombre());
					e.printStackTrace();
				}
		}
		transformer = null;
		log.debug("Finalizada carga de SLD's...");

		return capaTrans;
	}

	public void saveData(String appID, ISaveDataType data) throws GisException {

		String[] layers = data.getLayers();
		Transaction t = new DefaultTransaction();
		try {

			for (int i = 0; i < layers.length; i++) {
				saveData(appID, layers[i], data, t);
			}
			t.commit();
		} catch (Exception ge) {
			try {
				t.rollback();
			} catch (Exception e) {
				log.error("Error modificando datos-Error haciendo rollback", e);
				throw new GisException(
						"Error modificando datos-Error haciendo rollback", e);
			}
			throw new GisException("Error modificando datos", ge);
		} finally {
			try {
				t.close();
			} catch (IOException e) {
				log.error("Error cerrando transaccion", e);
				throw new GisException("Error cerrando transaccion", e);
			} // free resources
		}
	}

	public IAppManager getAppManager(String appID) {
		return ServerAppManager.getInstance().getAppManager(appID);
	}

	public void disposeApp(String appID) throws GisException {
		ServerAppManager.getInstance().cleanApplication(appID);
	}

	public VOLocation getLocationData(String appID, double x, double y)
			throws GisException {
		VOLocation vo = new VOLocation();
		Point event = geometryFactory.createPoint(new Coordinate(x, y));
		IAppManager mng = getAppManager(appID);

		try {
			Object datos[] = searchPuerta(mng, event);
			Integer puerta = (Integer) datos[0];
			String tramo = (String) datos[1];
			if (puerta == null) {
				throw new GisException(
						"No se pudo calcular el numero de puerta para esa ubicacion");
			}
			if (puerta.intValue() > 0) {
				Object data[] = searchCalle(mng.getDataStore(), tramo);
				vo.setCodCalle(((Number) data[0]).intValue());
				vo.setNombreCalle((String) data[1]);
				vo.setNroPuerta(puerta.intValue());
				vo.setCcz(searchCCZ(mng, event));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GisException(e);
		}

		return vo;
	}

	public String getEventLocation(String appID, double x, double y)
			throws GisException {
		StringBuffer res = new StringBuffer();
		Point event = geometryFactory.createPoint(new Coordinate(x, y));
		IAppManager mng = getAppManager(appID);

		try {
			Object datos[] = searchPuerta(mng, event);
			Integer puerta = (Integer) datos[0];
			String tramo = (String) datos[1];
			if (puerta == null) {
				throw new GisException(
						"No se pudo calcular el numero de puerta para esa ubicacion");
			}
			if (puerta.intValue() > 0) {
				String calle = (String) searchCalle(mng.getDataStore(), tramo)[1];
				res.append("Calle ");
				res.append(calle);
				res.append(" al ");
				res.append(puerta);
				res.append(" (aprox.)");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GisException(e);
		}

		return res.toString();
	}

	public ArrayList<HashMap<String, Object>> getLayerInteresectedBy(String appID, double x, double y,
			float eps, String layerName, String[] propertyNames)
			throws GisException {
		ArrayList<HashMap<String, Object>> toReturn = new ArrayList<HashMap<String, Object>>();
		AppManager appManager = ServerAppManager.getInstance().getAppManager(
				appID);
		Point p = geometryFactory.createPoint(new Coordinate(x, y));
		try {
			Feature[] res = getFeaturesIntersectedByLayer(appManager, p,
					layerName, eps);
			if (res == null)
				return toReturn;
			for (int i = 0; i < res.length; i++) {
				HashMap<String, Object> tmp = new HashMap<String, Object>();
				for (int j = 0; j < propertyNames.length; j++) {
					tmp.put(propertyNames[j], propertyNames[j] == "ID" ? res[i]
							.getID() : res[i].getAttribute(propertyNames[j]));
				}
				toReturn.add(tmp);

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GisException("Error al intersectar layers", e);
		}
		return toReturn;
	}

	private int searchCCZ(IAppManager mng, Point p) throws Exception {
		String geomName = mng.getLayer("CCZ").getFt().getDefaultGeometry().getLocalName();
		Filter gf = filterFactory.intersects(
				filterFactory.property(geomName),
				filterFactory.literal(p));
		
		Feature f = null, fMin = null;
		double min = Double.MAX_VALUE, tmp;
		FeatureReader fr = null;
		try {
			fr = mng.getDataStore().getFeatureReader(new DefaultQuery("CCZ", gf),
					Transaction.AUTO_COMMIT);
			while (fr.hasNext()) {
				f = fr.next();
				tmp = f.getDefaultGeometry().distance(p);
				if (tmp < min) {
					min = tmp;
					fMin = f;
				}
			}
		} finally {
			if (fr != null)
				fr.close();
		}

		if (fMin == null) {
			throw new Exception("No se encontro CCZ");
		}
		
		geomName = (String)fMin.getAttribute("nombre");
		geomName = geomName.substring(3);
		
		return 	Integer.valueOf(geomName).intValue();

	}

	private Object[] searchPuerta(IAppManager mng, Geometry event)
			throws Exception {
		Object res[] = new Object[2];
		Geometry buffer = event.buffer(100.0);
		String geomName = mng.getLayer("Direcciones").getFt()
				.getDefaultGeometry().getLocalName();
		DataStore ds = mng.getDataStore();
		Filter gf = filterFactory.intersects(
				filterFactory.property(geomName), 
				filterFactory.literal(buffer)
		);
		Feature f = null, fMin = null;
		double min = Double.MAX_VALUE, tmp;
		FeatureReader fr = null;

		try {
			fr = ds.getFeatureReader(new DefaultQuery("Direcciones", gf),
					Transaction.AUTO_COMMIT);
			while (fr.hasNext()) {
				f = fr.next();
				tmp = f.getDefaultGeometry().distance(event);
				if (tmp < min) {
					min = tmp;
					fMin = f;
				}
			}
		} finally {
			if (fr != null)
				fr.close();
		}

		if (fMin != null) {
			res[1] = ((FeatureReferenceAttribute) fMin.getAttribute("tramo"))
					.getReferencedFeatureID();
			res[0] = fMin.getAttribute("numero");
		}

		return res;
	}

	private Object[] searchCalle(DataStore ds, String FID) throws Exception {
		Id cf;
		Set<Identifier> id = new HashSet<Identifier>();
		Object[] res = new Object[2];
		FeatureReader fr = null;

		id.add(filterFactory.featureId(FID));
		cf = filterFactory.id(id);
		
		try {
			fr = ds.getFeatureReader(new DefaultQuery("Calles", cf),
					Transaction.AUTO_COMMIT);
			Feature f;
			if (fr.hasNext()) {
				f = fr.next();
				res[0] = f.getAttribute("codigo");
				res[1] = ((ExternalAttribute) f.getAttribute("nombre"))
						.getValue();
			}
		} finally {
			if (fr != null)
				fr.close();
		}

		return res;
	}

	private void saveData(String appID, String layer, ISaveDataType data,
			Transaction t) throws GisException {

		int i;
		String attName;
		Id filter;
		Feature f;
		Feature modified;
		FeatureWriter fw = null;
		try {
			log.debug("Persistiendo cambios en capa " + layer);

			IAppManager currentApp = ServerAppManager.getInstance()
					.getAppManager(appID);
			DataStore dataStore = currentApp.getDataStore();
			FeatureType ft = dataStore.getSchema(layer);

			HashMap<String, Feature> mapID = new HashMap<String, Feature>();
			Feature updates[] = data.getUpdateFeatures(ft);
			if (updates != null && updates.length > 0) {
				HashSet<FeatureId> set = new HashSet<FeatureId>();
				for (i = 0; i < updates.length; i++) {
					set.add(filterFactory.featureId(updates[i].getID()));
					mapID.put(updates[i].getID(), updates[i]);
				}
				filter = filterFactory.id(set);
				try {
					fw = dataStore.getFeatureWriter(layer, filter, t);
					while (fw.hasNext()) {
						f = fw.next();
						modified = (Feature) mapID.get(f.getID());
						log.debug("Modificando feature " + modified.getID());
						for (i = 0; i < f.getNumberOfAttributes(); i++) {
							attName = f.getFeatureType().getAttributeType(i)
									.getLocalName();
							f.setAttribute(i, modified.getAttribute(attName));
						}
						fw.write();
					}
				} finally {
					if (fw != null)
						fw.close();
					updates = null;
					mapID.clear();
					mapID = null;
					set.clear();
					set = null;
				}
			}
			
			Collection<String> deleteFIDs = data.getDeleteFIDs(layer);
			if (deleteFIDs != null && !deleteFIDs.isEmpty()) {
				HashSet<FeatureId> set = new HashSet<FeatureId>();
				for (Iterator<String> it = deleteFIDs.iterator(); it.hasNext();) {
					set.add(filterFactory.featureId(it.next()));
				}
				filter = filterFactory.id(set);
				try {
					fw = dataStore.getFeatureWriter(layer, filter,
					/* Transaction.AUTO_COMMIT */t);
					while (fw.hasNext()) {
						f = fw.next();
						log.debug("Removiendo feature " + f.getID());
						fw.remove();
						// filter.removeFid(f.getID());
					}

				} finally {
					if (fw != null)
						fw.close();
					deleteFIDs = null;
					set = null;
				}
			}

			MixtoFIDFeature mff;
			Feature inserts[] = data.getInsertFeatures(ft);
			if (inserts != null && inserts.length > 0) {
				try {
					fw = dataStore.getFeatureWriterAppend(layer, t);
					for (i = 0; i < inserts.length; i++) {
						modified = inserts[i];
						log.debug("Insertando feature " + modified.getID());

						mff = (MixtoFIDFeature) fw.next(); // Obtengo feature
						// nuevo en
						// blanco
						for (int j = 0; j < mff.getNumberOfAttributes(); j++) {
							attName = mff.getFeatureType().getAttributeType(j)
									.getLocalName();
							mff.setAttribute(j, modified.getAttribute(attName));
						}

						// Le seteo el id para que sea el mismo que en memoria
						mff.setID(modified.getID());
						fw.write();
					}
				} finally {
					if (fw != null)
						fw.close();
				}
			}
			inserts = null;
		} catch (Exception e) {
			try { t.rollback(); } catch (IOException e1) { e1.printStackTrace();}
			e.printStackTrace();
			throw new GisException(e);
		}
	}

	private FeatureCollectionTransporter loadLayerImpl(String appID,
			String layerName, FilterTransporter filter, String properties[],
			boolean withUserFilter) throws SchemaException, GisException {
		return loadLayerImpl(appID, layerName, filter, properties, withUserFilter, null);
	}

	private FeatureCollectionTransporter loadLayerImpl(String appID,
			String layerName, FilterTransporter filter, String properties[],
			boolean withUserFilter, String sortBy) throws SchemaException, GisException {

		GisSerialization.FeatureCollectionTransporter toReturn = null;
		IAppManager appManager = ServerAppManager.getInstance().getAppManager(
				appID);

		FeatureReader fr = null;
		try {
			DefaultQuery q;
			Filter fil;

			if (filter.getFilterStr().equals(""))
				fil = Filter.EXCLUDE;
			else
				fil = fDecoder.parse(filter.getFilterStr());

			if (withUserFilter) {
				// si es una capa "filtrable" por usuario se agrega dicho
				// filtro.

				currentUserName = (_ctx.getCallerPrincipal() == null) ? null : _ctx.getCallerPrincipal().getName();
				IUserFiltersProvider userFilter = getAppManager(appID)
						.getUserFiltersProvider();
				try {
					fil = userFilter.addUserFilters(fil, currentUserName);
				} catch (GisException e) {
					throw new GisException(
							"No se pudo aplicar el filtro de usuario", e);
				}
			}

			q = new DefaultQuery(layerName, fil);
			if (sortBy != null){
				SortBy sort = new SortByImpl(filterFactory.property(sortBy), SortOrder.ASCENDING);
				q.setSortBy(new SortBy[]{sort});				
			}
			
			q.setPropertyNames(properties);

			fr = appManager.getDataStore().getFeatureReader(q,
					Transaction.AUTO_COMMIT);
			toReturn = new GisSerialization.FeatureCollectionTransporter(
					new FeatureTypeTransporter(fr.getFeatureType()));
			toReturn.addFeatures(fr);
		}catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (fr != null){
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
			}
		}

		return toReturn;
	}

	private Feature[] getFeaturesIntersectedByLayer(IAppManager mng,
			Geometry p, String layerName, float eps) throws Exception {

		String geomName = mng.getLayer(layerName).getFt().getDefaultGeometry()
				.getLocalName();
		DataStore ds = mng.getDataStore();
		Intersects gf = filterFactory.intersects(
				filterFactory.property(geomName), 
				filterFactory.literal(p)
		);
		Feature f = null;
		FeatureReader fr = null;

		try {
			fr = ds.getFeatureReader(new DefaultQuery(layerName, gf),
					Transaction.AUTO_COMMIT);
			while (fr.hasNext()) {
				f = fr.next();
				// Chapo el primero y me fui.
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (fr != null)
				fr.close();
		}

		return f != null ? new Feature[] { f } : null;
	}

	public String asKML(String geom) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		DataSource ds = null;
		String kml = null;
		
		try {
			InitialContext ictx = new InitialContext();
			ds = (DataSource)ictx.lookup("java:DESGIS_PostgresDS"); 
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					"SELECT asKML(ST_GeometryFromText('" + geom + "',32721))");
			
			if (rs.next()) 
				kml = rs.getString(1);
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally{
			try{
				if (rs != null){
					rs.close();
					stmt.close();
					conn.close();
				}
			} 
			catch (Exception e1){
				e1.printStackTrace();
			}
		}
		
		return kml;
	}

	public void closeApp(String appID) {
	}
}
