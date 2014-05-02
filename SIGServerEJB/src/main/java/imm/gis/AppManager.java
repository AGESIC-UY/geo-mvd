package imm.gis;

import imm.gis.core.data.mixto.fidmapper.MixtoFIDMapperFactory;
import imm.gis.core.datasource.DataSourceFactory;
import imm.gis.core.datasource.IDataSource;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.definition.LayerDefinition;
import imm.gis.core.layer.filter.IUserFiltersProvider;
import imm.gis.core.mixto.xml.DefinicionAplicacion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.jdbc.fidmapper.FIDMapper;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactoryImpl;
import org.geotools.styling.StyleBuilder;

public class AppManager implements IAppManager {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AppManager.class);
	private Map<String, Layer> layersMap = new HashMap<String, Layer>();
	private ArrayList<Layer> layers = new ArrayList<Layer>();
	private Map<String, FIDMapper> fidMappers = new HashMap<String, FIDMapper>();
	private Map<String, IDataSource> layerDataSources = new HashMap<String, IDataSource>();
	private Map<String, LayerDefinition> layerDefinitions;
	private Map<String, Style> styles = new HashMap<String, Style>();
	private String idApp;
	private DataStore dataStore;
	private IUserFiltersProvider userFiltersProvider;
	
	public AppManager(String id) {
		idApp = id;
		
		//TODO aca se deberian setear las capas que son filtradas por usuario,depende de cada aplicacion.
	}

	public void init(DefinicionAplicacion da, String sldPath) throws IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		this.layerDefinitions = da.getLayerDefinitions();
		Iterator<Layer> layers = da.getLayers();
		
		Layer layer;
		layersMap.clear();
		styles.clear();
		this.layers.clear();
		SLDParser parser = new SLDParser(new StyleFactoryImpl());
		StyleBuilder styleBuilder = new StyleBuilder();
		
		// Cargo los estilos
		while (layers.hasNext()) {
			layer = layers.next();
			layersMap.put(layer.getNombre(), layer);
			this.layers.add(layer);
			try {
				if (layer.getSldFile() != null) {
					log.debug("Cargando estilo binario desde " + sldPath + layer.getSldFile());
					parser.setInput(sldPath + layer.getSldFile());
					styles.put(layer.getNombre(), parser.readXML()[0]);
				}
				else{
					log.debug("Cargando estilo por defecto para " + layer.getNombre());
					styles.put(layer.getNombre(), getDefaultStyle(styleBuilder,	layer.getFt()));					
				}
			}
			catch (FileNotFoundException e) {
				log.debug("No se encontro archivo, cargando estilo por defecto...");
				try {
					styles.put(layer.getNombre(), getDefaultStyle(styleBuilder,
							layer.getFt()));
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		// Instancio la clase encargada de agregar los filtros del usuario
		String userFiltersClass;
		
		if (da.getUserFiltersProvider() == null || da.getUserFiltersProvider().equals(""))
			userFiltersClass = "imm.gis.server.filter.DummyFilter";
		else
			userFiltersClass = da.getUserFiltersProvider();
		
		userFiltersProvider = (IUserFiltersProvider) Thread.currentThread().getContextClassLoader().loadClass(userFiltersClass).newInstance();
		
		// Creo los datasources
		createDataSources();
		
		// Creo los fidmappers
		createFIDMappers();
	}

	private void clearMaps() {
		layersMap.clear();
		styles.clear();
		layers.clear();
		fidMappers.clear();
		layerDataSources.clear();
		layerDefinitions.clear();
		
	}

	private Style getDefaultStyle(StyleBuilder styleBuilder, FeatureType type)
			throws IOException {
		Style style = null;

		GeometryAttributeType g = type.getDefaultGeometry();
		if (g == null) {
			style = styleBuilder.createStyle();
		}
		else {
			Class<?> cl = g.getType();
			if (cl.isAssignableFrom(com.vividsolutions.jts.geom.Point.class))
				style = styleBuilder.createStyle(styleBuilder.createPointSymbolizer());
			else if (cl.isAssignableFrom(com.vividsolutions.jts.geom.MultiLineString.class)
					|| cl.isAssignableFrom(com.vividsolutions.jts.geom.LineString.class))
				style = styleBuilder.createStyle(styleBuilder.createLineSymbolizer());
			else
				style = styleBuilder.createStyle(styleBuilder.createPolygonSymbolizer());
		}

		return style;
	}

	public Layer getLayer(String name) {
		return (Layer) layersMap.get(name);
	}

	public FeatureType getSchema(String layer) {
		return getLayer(layer).getFt();
	}

	public String[] getTypeNames() {
		return (String[]) layersMap.keySet().toArray(new String[layersMap.size()]);
	}

	public boolean isChild(String typeName) {
		return ((Layer) layersMap.get(typeName)).getMetadata().isChild();
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	public Iterator<Layer> getLayers() {
		return this.layers.iterator();
	}

	public int cantLayers(){
		return layers.size();
	}
	
	public Style getStyle(String layer) {
		return (Style) styles.get(layer);
	}

	public String getIdApp() {
		return idApp;
	}

	public ArrayList<String> getLayerNamesFilteredByUser() {
		// TODO Sacar este hardcodeo!!!
		String layerName = "Problemas";
		ArrayList<String> toReturn = new ArrayList<String>();
		toReturn.add(layerName);
		return toReturn;
	}

	private void createFIDMappers() {
		Layer layer;
		
		MixtoFIDMapperFactory mapperFactory = new MixtoFIDMapperFactory(this);
		
		Iterator<Layer> i = layersMap.values().iterator();
		
		while (i.hasNext()) {
			layer = i.next();
			fidMappers.put(layer.getNombre(), mapperFactory.getMapper(layer.getFt()));
		}
	}
	
	private void createDataSources() throws IOException, SQLException {
		LayerDefinition layerDefinition;
		
		DataSourceFactory dsf = new DataSourceFactory();
		
		Iterator<LayerDefinition> i = layerDefinitions.values().iterator();
		
		while (i.hasNext()) {
			layerDefinition = i.next();
			layerDataSources.put(layerDefinition.getName(), dsf.createDataSource(layerDefinition.getDataOriginDefinition().getDataSourceDefinition()));
		}
	}

	public FIDMapper getFIDMapper(String layerName) {
		return fidMappers.get(layerName);
	}
	
	public LayerDefinition getLayerDefinition(String name) {
		return layerDefinitions.get(name);
	}
	
	public IDataSource getDataSource(String layerName) {
		return layerDataSources.get(layerName);
	}

	public IUserFiltersProvider getUserFiltersProvider() {
		return userFiltersProvider;
	}

	public void dispose() {
		clearMaps();
		this.setDataStore(null);
		//	Map[] myMaps = new Map{this.dataStore
		
	}
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}
}
