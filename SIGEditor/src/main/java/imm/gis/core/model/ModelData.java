package imm.gis.core.model;

import imm.gis.AppContext;
import imm.gis.GisException;
import imm.gis.comm.IServerServices;
import imm.gis.comm.datatypes.datainput.ISaveDataType;
import imm.gis.comm.datatypes.datainput.SaveDataType;
import imm.gis.core.controller.ContUtils;
import imm.gis.core.feature.Util;
import java.util.EventObject;
import imm.gis.core.interfaces.IModel;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.metadata.ChildMetadata;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureChangeListener;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.core.model.event.FeatureEventManager.EventType;
import imm.gis.core.model.undo.UndoListener;
import imm.gis.core.model.undo.UndoableOperation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.geotools.data.CollectionFeatureReader;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;
import org.geotools.filter.ExpressionBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.DefaultMapLayer;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.identity.Identifier;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import static imm.gis.core.gui.PanelDibujo.DRAG_IMAGE_BUFFER_FACTOR;

public class ModelData implements ILinePoints, IModel, IStatus {
	private Envelope bbox = null;
	private MyMemoryDataStore memoryDataStore;
	private FilterFactory2 filterFactory = null;
	private List<ActionListener> guiListeners;
	private GeometryFactory geometryFactory;
	private Dimension mapDimension;
	private UndoListener undoListener;
	private ModelContext modelContext;
	private IServerServices services;
	private static Logger log = Logger.getLogger(ModelData.class.getName());
	private FeatureEventManager feventManager = FeatureEventManager.getInstance();
	private MapContext context;
	private AppContext appContext = AppContext.getInstance();
	private ClientFeaturesCache cache;
	private Map<String, Boolean> layersEnabled;
	private boolean usePanCache;
	
	private List<String> layersNotEditables;

	public ModelData(IServerServices ss, Envelope bboxInicial)
			throws Exception {
		services = ss;
		bbox = bboxInicial;
		filterFactory = CommonFactoryFinder.getFilterFactory2( GeoTools.getDefaultHints() );
		geometryFactory = new GeometryFactory();
		guiListeners = new ArrayList<ActionListener>();
		cache = new ClientFeaturesCache();
		createMemoryDataStore();
		context = createMapContext(); // Debe ir siempre despues de loadStyles
		appContext.getStyleModel().setMapContext(context);
		modelContext = new ModelContextImpl(this);
		initlayersEnabled();
		
		layersNotEditables = new ArrayList<String>();

	}

	public void setLayerNotEditable(String layer){
		layersNotEditables.add(layer);		
	}
	
	private void initlayersEnabled() {
		layersEnabled = new HashMap<String, Boolean>();
		for (Iterator<String> layerNames = appContext.getTypeNames(); layerNames.hasNext();){
			layersEnabled.put(layerNames.next(), new Boolean(true));			
		}
	}

	public void setUndoListener(UndoListener ul) {
		undoListener = ul;
	}

	/**
	 * Registra un escucha para las operaciones sobre feature
	 * 
	 * @param al
	 *            Listener que se quiere registrar
	 */
	public void addFeatureChangeListener(FeatureChangeListener al,
			EventType eventType) {
		feventManager.addListener(eventType, al);
	}

	/**
	 * Elimina un escucha de operaciones de la lista de escuchas a notificar
	 * 
	 * @param al
	 *            Listener que se quiere quitar
	 */
	public void removeFeatureChangeListener(FeatureChangeListener al,
			EventType eventType) {
		feventManager.removeListener(eventType, al);
	}

	/**
	 * Avisa a quienes se hayan registrado de una cierta operaci???n revertible
	 * 
	 * @param uo
	 *            Operacion de la cual se quiere dar aviso
	 */
	public void notifyChangeListeners(FeatureChangeEvent fe,
			EventType eventType) {
		feventManager.notifyListener(eventType, fe);
	}

	public void addGUIListener(ActionListener al) {
		guiListeners.add(al);
	}

	public void removeGUIListener(ActionListener al) {
		guiListeners.remove(al);
	}

	public DataStore getDataStore() {
		return memoryDataStore;
	}

	public FeatureType getSchema(String layer) throws IOException {
		return memoryDataStore.getSchema(layer);
	}

	public FeatureSource getFeatureSource(String layer) throws IOException {
		return memoryDataStore.getFeatureSource(layer);
	}

	public void setBbox(Envelope e) throws Exception {
		log.info("BoundingBox: " + e);
		bbox.init(e);
		loadMemoryDataStore();
		fireChange();
	}

	public void refreshViews() {
		refreshViews(false);
	}

	public void refreshViews(boolean loadDB) {
		if (loadDB) {
			try {
				setBbox(getBbox());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			fireChange();
		}
	}

	private MapContext createMapContext() {
		MapContext mc = new DefaultMapContext(Util.crs);
		Layer layers[] = appContext.getLayers();
		Layer layer;
		String layerName;
		DefaultMapLayer mapLayer;

		for (int i = 0; i < layers.length; i++) {
			layer = layers[i];
			if (layer.isVisible()) {
				layerName = layer.getNombre();
				mapLayer = new DefaultMapLayer(new MemoryFeatureSource(
						memoryDataStore, layerName), appContext.getStyleModel().getCurrentStyle(layerName),
						layerName);
				mc.addLayer(mapLayer);
			}
		}

		return mc;
	}

	private void createMemoryDataStore() throws SchemaException, IOException {
		Iterator<String> types = appContext.getTypeNames();
		FeatureType ft;
		FeatureTypeBuilder ftf;
		memoryDataStore = new MyMemoryDataStore();

		while (types.hasNext()) {
			ft = appContext.getSchema(types.next());
			ftf = FeatureTypeBuilder.newInstance(ft.getTypeName());
			ftf.importType(ft, false);

			if (ft.getDefaultGeometry() != null) {
				// Si es una línea le agrego cuatro atributos para poder
				// dibujar las flechitas en las puntas
				/*
				 * if (isLineGeometry(ft)) {
				 * ftf.addType(AttributeTypeFactory.newAttributeType(
				 * INIT_POINT_ATTR, com.vividsolutions.jts.geom.Point.class,
				 * true)); ftf.addType(AttributeTypeFactory.newAttributeType(
				 * END_POINT_ATTR, com.vividsolutions.jts.geom.Point.class,
				 * true)); ftf.addType(AttributeTypeFactory.newAttributeType(
				 * INIT_ROTATION_ATTR, java.lang.Double.class, true));
				 * ftf.addType(AttributeTypeFactory.newAttributeType(
				 * END_ROTATION_ATTR, java.lang.Double.class, true)); }
				 */
			}
			memoryDataStore.createSchema(ftf.getFeatureType());
		}
	}

	private synchronized void loadMemoryDataStore() throws IllegalAttributeException,
			IOException {
		ReferencedEnvelope re;
		
		// Borro todos los datos...
		((MyMemoryDataStore) memoryDataStore).cleanData();
		context.clearLayerList();
		
		if (usePanCache){
			double deltaX = bbox.getWidth() / (DRAG_IMAGE_BUFFER_FACTOR);
			double deltaY = bbox.getHeight() / (DRAG_IMAGE_BUFFER_FACTOR);
			
			re = new ReferencedEnvelope(
					bbox.getMinX() - deltaX,
					bbox.getMaxX() + deltaX,
					bbox.getMinY() - deltaY,
					bbox.getMaxY() + deltaY, 
					context.getCoordinateReferenceSystem());			
		} else {
			re = new ReferencedEnvelope(bbox, context.getCoordinateReferenceSystem());			
		}

		context.setAreaOfInterest(re);

		double scale;
		try {
			// Calculo escala del nuevo mapa...
			scale = ContUtils.getScale(bbox, mapDimension);
			String type;
			for (Iterator<String> types = AppContext.getInstance().getTypeNames(); types.hasNext();){
				type = types.next();
				if (isVisibleLayer(type))// && isEnabledLayer(types[i]))
					loadLayer(type, scale);
			}
			type = null;
			
			// Actualizo los modificados...
			String modificableTypes[] = cache.getLayerNames();
			
			for (int i = 0; i < modificableTypes.length; i++) {
				ModifiedData modified = cache
						.getModifiedFeatures(modificableTypes[i]);
				Collection modifiedData = modified.getCreatedData();
				if (modifiedData != null && !modifiedData.isEmpty()) {
					memoryDataStore.addFeatures(modifiedData);
				}
				modifiedData = modified.getUpdatedData();
				if (modifiedData != null && !modifiedData.isEmpty()) {
					memoryDataStore.addFeatures(modifiedData);
				}
				for (Iterator it = modified.getDeletedData().iterator(); it
						.hasNext();) {
					memoryDataStore.removeFeature((Feature) it.next());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Layer getCapa(String layer) {
		return imm.gis.AppContext.getInstance().getCapa(layer);
	}

	private void loadLayer(String layer, double scale) {
		FeatureCollection fr = null;
		Style style = appContext.getStyleModel().getCurrentStyle(layer);
		Rule rules[] = style.getFeatureTypeStyles()[0].getRules();
		Rule r;
		
		boolean load = false;
		//boolean load = true;
		
		for (int i = 0; i < rules.length; i++) {
			r = rules[i];			
			if (scale < r.getMaxScaleDenominator() && scale >= r.getMinScaleDenominator()) {
				load = true;
				break;
			}
		}

		if (!load) {
			log.info("Capa " + layer + " fuera de escala, no se carga");
			return;
		}

		log.debug("Cargando capa " + layer);

		try {
			FeatureType ft = AppContext.getInstance().getSchema(layer);
			DefaultMapLayer dml =null;
			

				Filter bBoxFilter = createBboxFilter(ft.getDefaultGeometry()
						.getLocalName());
				log.debug("Filtro para cargar la capa" + bBoxFilter);

				fr = services.loadLayer(ft, bBoxFilter, true);
				// if (!fr.isEmpty()) {
				memoryDataStore.addFeatures(fr);
				dml = new DefaultMapLayer(
						new MemoryFeatureSource(memoryDataStore, layer), style,
						layer);
			
			
			dml.setVisible(isEnabledLayer(layer));
			context.addLayer(dml);
			
			// Si es una capa de lineas, calculo los extremos
			/*
			 * if (isLineGeometry(layer) && isEditable(layer))
			 * memoryDataStore.actualizarExtremos(layer);
			 */
			// Si tiene atributos derivados, los actualizo
			/*
			 * if (!getCapa(layer).getDefCapa().getDerivados().isEmpty())
			 * memoryDataStore.actualizarDerivados(layer, getCapa(layer)
			 * .getDefCapa().getDerivados());
			 */
			// }
		} catch (Exception e) {
			log.info("Error cargando capa " + layer + " (" + e.getMessage()
					+ ") estilo: " + style);
			e.printStackTrace();
		} finally {
			try {
				if (fr != null)
					fr.purge();
			} catch (Exception e) {
				log.info(e.getMessage());
			}
		}
	}

	private Filter createBboxFilter(String geomAttributeName){
/*		ReferencedEnvelope re = new ReferencedEnvelope(
				bbox.getMinX(), 
				bbox.getMaxX(), 
				bbox.getMinY(), 
				bbox.getMaxY(),
				context.getCoordinateReferenceSystem());
				*/
		Filter bBoxFilter = filterFactory.bbox(filterFactory.property(geomAttributeName), context.getAreaOfInterest());

		return bBoxFilter;
	}

	private void fireChange() {
		ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
		ActionListener al = null;
		
		for (Iterator<ActionListener> it = guiListeners.iterator(); it.hasNext();) {
			al = it.next();
			al.actionPerformed(ae);
		}
	}

	public Envelope getBbox() {
		return bbox;
	}

	public boolean isEditable(String layer) {
		return !layersNotEditables.contains(layer) && getCapa(layer).isEditable();
	}

	public boolean isFeatureEditable(Feature f) {
		String layerName = f.getFeatureType().getTypeName();
		return ((Boolean) appContext.getCapa(layerName).getCtxAttrManager()
				.getAttributeProperty(f, "", "editable")).booleanValue();
	}

	public String getUserIdName(String layer) {
		return getCapa(layer).getMetadata().getUserIdAttribute();
	}

	public String[] getNotEmptyEditableTypes() {
		java.util.List<String> list = new ArrayList<String>();
		Layer capa;

		for (Iterator<String> layers = AppContext.getInstance().getTypeNames(); layers.hasNext();) {
			capa = getCapa(layers.next());
			if (!layersNotEditables.contains(capa.getNombre()) && capa.isEditable() && !isEmptyLayer(capa.getNombre()))
				list.add(capa.getNombre());
		}

		return (String[]) list.toArray(new String[] {});
	}

	public String[] getEditableTypes() {
		java.util.List<String> list = new ArrayList<String>();
		Layer capa;

		for (Iterator<String> layers = AppContext.getInstance().getTypeNames(); layers.hasNext();) {
			capa = getCapa(layers.next());
			if (!layersNotEditables.contains(capa.getNombre()) && capa.isEditable())
				list.add(capa.getNombre());
		}

		return (String[]) list.toArray(new String[] {});
	}

	public FilterFactory2 getFilterFactory() {
		return filterFactory;
	}

	public GeometryFactory getGeometryFactory() {
		return geometryFactory;
	}

	public Feature getFeature(String type, String fid) {
		return getFeature(type, fid, false);
	}
	
	public Feature getFeature(String type, String fid, boolean refreshModel) {
		Feature f;
		
		f = memoryDataStore.getFeature(type, fid);
		
		if (f == null) {
			Set<Identifier> set = new HashSet<Identifier>();
			set.add(filterFactory.featureId(fid));
			Filter fidf = filterFactory.id(set);
			AppContext applicationContext = AppContext.getInstance();			
			FeatureType ft = applicationContext.getCapa(type).getFt();
			FeatureReader fr = null;
			
			try {
				FeatureCollection fc = services.loadLayer(ft, fidf, false);
				fr = new CollectionFeatureReader(fc, ft);
				
				if (fr.hasNext()){
					f = fr.next();
					memoryDataStore.addFeature(f);
				}
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			} finally{
				try {
					if (fr != null) fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return f;
			
	}

	/*
	 * ------------------------------ OPERACIONES SOBRE LOS FEATURES
	 * ------------------------------
	 */

	/**
	 * Modifica los atributos de un feature.
	 * 
	 * @param f
	 *            f El feature cuyos atributos se quieren modificar
	 * @param values
	 *            Un array con los nuevos valores para sus atributos
	 * @param refresh
	 *            Indica si se quiere refrescar las vistas o no luego de
	 *            modificar el feature
	 * @param avisarCambios
	 *            Indica si se quiere avisar de los cambios sobre el feature
	 * 
	 * @return El feature con los nuevos atributos
	 * @throws IllegalAttributeException
	 */
	public Feature modifyFeature(Feature f, Object values[], boolean refresh,
			boolean avisarCambios) throws Exception {

		int count = values.length;

		String atts[] = new String[count];

		for (int i = 0; i < count; i++) {
			atts[i] = f.getFeatureType().getAttributeType(i).getLocalName();
		}

		return modifyFeature(f, atts, values, refresh, avisarCambios);
	}

	/**
	 * Modifica los atributos de un feature.
	 * 
	 * @param f
	 *            f El feature cuyos atributos se quieren modificar
	 * @param attrs
	 *            Un array con los nombres de los atributos que se quieren
	 *            modificar
	 * @param values
	 *            Un array con los nuevos valores para los atributos
	 *            especificados
	 * 
	 * @return El feature con los nuevos atributos
	 * @throws IllegalAttributeException
	 */
	public Feature modifyFeature(Feature f, String attrs[], Object values[])
			throws Exception {
		return modifyFeature(f, attrs, values, true, true);
	}

	/**
	 * 
	 * Modifica los atributos de un feature.
	 * 
	 * @param f
	 *            f El feature cuyos atributos se quieren modificar
	 * @param attrs
	 *            Un array con los nombres de los atributos que se quieren
	 *            modificar
	 * @param values
	 *            Un array con los nuevos valores para los atributos
	 *            especificados
	 * @param refresh
	 *            Indica si se quiere refrescar las vistas o no
	 * @param avisarCambios
	 *            Indica si se quiere avisar de los cambios sobre el feature o
	 *            no
	 * 
	 * @return El feature con los nuevos atributos
	 * @throws IllegalAttributeException
	 * 
	 * @throws Exception
	 */
	public Feature modifyFeature(Feature f, String attrs[], Object values[],
			boolean refresh, boolean avisarCambios) throws Exception {

		String layer = f.getFeatureType().getTypeName();
		AppContext am = AppContext.getInstance();

		Feature tmp = memoryDataStore.getFeature(layer, f.getID());

		if ((tmp == null) && (am.getCapa(layer).getMetadata().isChild())) {
			tmp = f;
			memoryDataStore.addFeature(tmp);
		}

		Feature oldFeature = tmp.getFeatureType().create(
				tmp.getAttributes(null));

		if (avisarCambios)
			notifyChangeListeners(new FeatureChangeEvent(modelContext,
					oldFeature.getFeatureType().getTypeName(), tmp, oldFeature,
					FeatureChangeEvent.MODIFIED_FEATURE),
					FeatureEventManager.BEFORE_MODIFY);

		log.debug("Modificando " + tmp + " de capa " + layer);

		int originalStatus = getStatus(f);

		for (int i = 0; i < attrs.length; i++) {
			log.debug(attrs[i] + ": " + tmp.getAttribute(attrs[i]) + " -> "
					+ values[i]);

			tmp.setAttribute(attrs[i], values[i]);

			if (values[i] instanceof com.vividsolutions.jts.geom.Geometry)
				tmp.getDefaultGeometry().geometryChanged();
		}

		if (originalStatus != CREATED_ATTRIBUTE) {
			setStatus(tmp, UPDATED_ATTRIBUTE);
		}

		// Si es una linea, le calculo los extremos
		// if (f.getDefaultGeometry() != null
		// && isLineGeometry(f.getFeatureType()))
		// Util.calcularExtremosLinea(tmp);

		// Si tiene atributos derivados, los actualizo
		/*
		 * if (!getCapa(f.getFeatureType().getTypeName()).getDefCapa()
		 * .getDerivados().isEmpty()) Util.calcularDerivados(tmp, getCapa(
		 * f.getFeatureType().getTypeName()).getDefCapa() .getDerivados());
		 */

		// Aviso al mundo de los cambios
		if (avisarCambios) {
			UndoableOperation uo = new UndoableOperation("Elemento modificado");
			FeatureOperation fo = new FeatureModifyOperation(f, oldFeature,
					originalStatus);
			uo.addFeatureOperation(fo);
			fo.setActualStatus(getStatus(f));

			undoListener.changedPerformed(uo);

			notifyChangeListeners(new FeatureChangeEvent(modelContext,
					oldFeature.getFeatureType().getTypeName(), tmp, oldFeature,
					FeatureChangeEvent.MODIFIED_FEATURE),
					FeatureEventManager.AFTER_MODIFY);
		}

		if (refresh) {
			refreshViews();
		}

		return tmp;
	}

	public void addFeature(Feature f) throws IOException,
			IllegalAttributeException {
		addFeature(f, true, true);
	}

	private void addFeature(Feature f, boolean selected, boolean avisarCambios)
			throws IOException, IllegalAttributeException {

		FeatureType ft = memoryDataStore.getSchema(f.getFeatureType()
				.getTypeName());

		log.info("Agregando " + f + " en capa " + ft.getTypeName());

		// Si es una linea, le calculo los extremos
		// if ((ft.getDefaultGeometry() != null) && isLineGeometry(ft))
		// Util.calcularExtremosLinea(f);

		// Si tiene atributos derivados, los actualizo
		/*
		 * if (!getCapa(ft.getTypeName()).getDefCapa().getDerivados().isEmpty())
		 * Util.calcularDerivados(f, getCapa(ft.getTypeName()).getDefCapa()
		 * .getDerivados());
		 */

		if (avisarCambios)
			notifyChangeListeners(new FeatureChangeEvent(modelContext, f
					.getFeatureType().getTypeName(), f, null,
					FeatureChangeEvent.ADDED_FEATURE),
					FeatureEventManager.BEFORE_MODIFY);

		memoryDataStore.addFeature(f);
		setStatus(f, CREATED_ATTRIBUTE);

		if (avisarCambios) {
			log.info("Avisando cambios por feature nuevo...");
			UndoableOperation uo = new UndoableOperation("Elemento agregado");
			FeatureOperation fo = new FeatureOperation(f,
					ModelData.CREATED_ATTRIBUTE);
			fo.setActualStatus(getStatus(f));
			uo.addFeatureOperation(fo);

			undoListener.changedPerformed(uo);

			notifyChangeListeners(new FeatureChangeEvent(modelContext, f
					.getFeatureType().getTypeName(), f, null,
					FeatureChangeEvent.ADDED_FEATURE),
					FeatureEventManager.AFTER_MODIFY);
		}

		refreshViews();
		//this.
	}

	/**
	 * Elimina un feature.
	 * 
	 * @param f
	 *            Feature a eliminar
	 * 
	 * @return El feature en modo eliminado
	 * @throws IllegalAttributeException
	 * @throws IOException
	 * @throws Exception
	 */
	public void delFeature(Feature f) throws IOException,
			IllegalAttributeException {
		delFeature(f, true);
	}

	/**
	 * Elimina un feature.
	 * 
	 * @param f
	 *            Feature a eliminar
	 * @param refresh
	 *            Indica si se quiere refrescar las vistas o no
	 * 
	 * @return El feature en modo eliminado
	 * @throws IllegalAttributeException
	 * @throws IOException
	 * @throws Exception
	 */
	public void delFeature(Feature f, boolean refresh) throws IOException,
			IllegalAttributeException {
		delFeature(f, refresh, true);
	}

	/**
	 * Elimina un feature.
	 * 
	 * @param f
	 *            Feature a eliminar
	 * @param refresh
	 *            Indica si se quiere refrescar las vistas o no
	 * @param avisarCambios
	 *            Indica si se quiere avisar que se elimin??? el feature o no
	 * 
	 * @return El feature en modo eliminado
	 * @throws IllegalAttributeException
	 * @throws IOException
	 * 
	 * @throws Exception
	 */
	public void delFeature(Feature f, boolean refresh, boolean avisarCambios)
			throws IOException, IllegalAttributeException {
		String type = null;
		Feature tmp = null;
		int originalStatus;

		type = f.getFeatureType().getTypeName();
		tmp = memoryDataStore.getFeature(type, f.getID());

		AppContext am = AppContext.getInstance();

		if ((tmp == null)
				&& (am.getCapa(f.getFeatureType().getTypeName()).getMetadata()
						.isChild())) {
			tmp = f;
			memoryDataStore.addFeature(tmp);
		}

		notifyChangeListeners(new FeatureChangeEvent(modelContext, f
				.getFeatureType().getTypeName(), null, f,
				FeatureChangeEvent.DELETED_FEATURE),
				FeatureEventManager.BEFORE_MODIFY);

		log.debug("Borrando de forma logica " + tmp);

		originalStatus = getStatus(tmp);
		setStatus(tmp, DELETED_ATTRIBUTE);
		// selectFeature(tmp, false);
		memoryDataStore.removeFeature(tmp);

		if (avisarCambios) {
			UndoableOperation uo = new UndoableOperation("Elemento eliminado");
			FeatureOperation fo;
			fo = new FeatureOperation(f, originalStatus);
			fo.setActualStatus(getStatus(f));
			uo.addFeatureOperation(fo);
			undoListener.changedPerformed(uo);

			notifyChangeListeners(new FeatureChangeEvent(modelContext, f
					.getFeatureType().getTypeName(), null, f,
					FeatureChangeEvent.DELETED_FEATURE),
					FeatureEventManager.AFTER_MODIFY);
		}

		if (refresh)
			refreshViews();
	}

	public void undoAllModified(String type) throws Exception {
		cache.clean();

		notifyChangeListeners(new FeatureChangeEvent(modelContext, type, null,
				null, FeatureChangeEvent.CLEANED_CACHE),
				FeatureEventManager.AFTER_MODIFY);

		loadMemoryDataStore();
		refreshViews();
	}

	public void confirmModified(String type) throws Exception {
		confirmModified(type, new SaveDataType(), true);
	}

	public boolean isModified(String type) {
		return cache.isModified(type) || anyChildModified(type);
	}

	private boolean anyChildModified(String type) {
		boolean childModified = false;
		Iterator childDefIterator = AppContext.getInstance().getCapa(type)
				.getMetadata().getChildrenMetadata().iterator();
		while (!childModified && childDefIterator.hasNext()) {
			ChildMetadata element = (ChildMetadata) childDefIterator.next();
			childModified = isModified(element.getLayerName());//cache.isModified(element.getLayerName());

		}
		return childModified;
	}

	public ModifiedData getModifiedFeatures(String layerName) {
		return cache.getModifiedFeatures(layerName);
	}
	
	private void confirmModified(String type, SaveDataType data, boolean apply)
			throws Exception {
		try {

			Iterator it = AppContext.getInstance().getCapa(type).getMetadata()
					.getChildrenMetadata().iterator();
			ChildMetadata def;
			while (it.hasNext()) {
				def = (ChildMetadata) it.next();
				log.info("Salvando cambios de chidren " + def.getLayerName());
				confirmModified(def.getLayerName(), data, false);
			}

			log.info("Salvando cambios de " + type);
			ModifiedData md = cache.getModifiedFeatures(type);
			for (Iterator del = md.getDeletedData().iterator(); del.hasNext();) {
				data.addDeleteFID(type, ((Feature) del.next()).getID());
			}

			prepareDataType(type, data, md.getUpdatedData().iterator(),
					ISaveDataType.UPDATE);
			prepareDataType(type, data, md.getCreatedData().iterator(),
					ISaveDataType.INSERT);

			if (apply) {
				notifyChangeListeners(new FeatureChangeEvent(data,
						type, null, null, FeatureChangeEvent.CONFIRM_CHANGES),
						FeatureEventManager.AFTER_MODIFY);
				services.saveData(data);
				unmarkModified(data);
				notifyChangeListeners(new FeatureChangeEvent(modelContext,
						type, null, null, FeatureChangeEvent.CLEANED_CACHE),
						FeatureEventManager.AFTER_MODIFY);
			}

		} catch (GisException e) {
			throw new Exception("Error al grabar las modificaciones", e);
		} finally {
			if (apply)
				refreshViews();
		}
	}

	private void unmarkModified(ISaveDataType data)
			throws IllegalAttributeException {
		String layers[] = data.getLayers();
		Iterator it;

		for (int j = 0; j < layers.length; j++) {
			FeatureType ft = AppContext.getInstance().getSchema(layers[j]);

			// Desmarco los features que se actualizaron...
			Feature updates[] = data.getUpdateFeatures(ft);
			for (int i = 0; i < updates.length; i++) {
				setStatus(updates[i], UNMODIFIED_ATTRIBUTE);
			}

			// Borro los features que estaban borrados logicamente
			Collection colDeleted = data.getDeleteFIDs(layers[j]);
			if (colDeleted != null) {
				it = colDeleted.iterator();

				while (it.hasNext()) {
					cache.setUnmodified(ft.getTypeName(), (String) it.next());
				}
			}

			// Desmarco los features que se crearon...
			updates = data.getInsertFeatures(ft);
			for (int i = 0; i < updates.length; i++) {
				setStatus(updates[i], UNMODIFIED_ATTRIBUTE);
			}
		}
	}

	public String[] getNotEmptyTypes() {
		List<String> res = new ArrayList<String>();
		String type;
		
		for (Iterator<String> types = AppContext.getInstance().getTypeNames(); types.hasNext();) {
			type = types.next();
			if (!isEmptyLayer(type) && isVisibleLayer(type))
				res.add(type);
		}

		return (String[]) res.toArray(new String[res.size()]);
	}

	public String[] getNotEmptyTypesOrEditableTypes() throws Exception {
		List<String> res = new ArrayList<String>();
		String type;

		for (Iterator<String> types = AppContext.getInstance().getTypeNames(); types.hasNext();) {
			type = types.next();
			if ((!layersNotEditables.contains(type) && isEditable(type) || !isEmptyLayer(type))
					&& isVisibleLayer(type))
				res.add(type);
		}

		return (String[]) res.toArray(new String[res.size()]);
	}

	public String[] getNotEmptyTypesAndEditableTypes() throws Exception {
		String type;
		List<String> res = new ArrayList<String>();

		for (Iterator<String> types = AppContext.getInstance().getTypeNames(); types.hasNext();) {
			type = types.next();
			if (!layersNotEditables.contains(type) && isEditable(type) && isVisibleLayer(type))
				res.add(type);
		}

		return (String[]) res.toArray(new String[res.size()]);
	}

	public List<String> getNotEmptyTypesAndVisibleTypes() throws Exception {
		String type;
		List<String> res = new ArrayList<String>();

		for (Iterator<String> types = AppContext.getInstance().getTypeNames(); types.hasNext();) {
			type = types.next();
			// MODIFICACION TAMBIEN SE TOMA EN CUENTA CAPAS CON ENABLED FALSE
			if (!isEmptyLayer(type) && isVisibleLayer(type)
					&& isEnabledLayer(type))
				res.add(type);
		}

		return res;
	}

	public boolean isEmptyLayer(String layer) {
		return memoryDataStore.isEmpty(layer);
	}

	public boolean isEnabledLayer(String layer) {
		/*
		 * MapLayer mp = getMapLayer(layer); return mp == null ?
		 * false:mp.isVisible();
		 */
		return ((Boolean) layersEnabled.get(layer)).booleanValue();
	}

	public void setLayerEnabled(String layer, boolean enabled, boolean refresh) {
		layersEnabled.put(layer, new Boolean(enabled));
		MapLayer tmpMapLayer = getMapLayer(layer);
		if (tmpMapLayer != null) {
			tmpMapLayer.setVisible(enabled);
		}
		if (refresh) refreshViews();
	}

	public void setLayerEnabled(String layer, boolean enabled) {
		setLayerEnabled(layer, enabled, true);
	}	
	
	public void setBboxFilterLayer(String layer) {
		//Toma como nuevo bounding box el envelope de los elementos de la capa 'layer'
		//aplicando el filtro que esta pudiese tener
		
		Envelope evp = null;

		FeatureCollection fr = null;
		try {
			FeatureType ft = AppContext.getInstance().getSchema(layer);
			Layer capa = AppContext.getInstance().getCapa(layer);
			Filter filter = capa.getFilter();
			
			fr = services.loadLayer(ft, filter, false);
			if (!fr.isEmpty()) {
				evp = ContUtils.enfocar(bbox, fr.getBounds());
				appContext.getCoreAccess().getNavigationController().setPreviousBoundingBox();
				setBbox(evp);				
			} else {
				refreshViews(true);
			}	
		} catch (Exception e) {		
			e.printStackTrace();
		} finally {
			if (fr != null)
				fr.clear();
		}		
	}	
		
	public boolean isVisibleLayer(String layer) {
		// getM
		return appContext.getCapa(layer).isVisible();
	}

	private MapLayer getMapLayer(String layer) {
		MapLayer[] layers = context.getLayers();
		for (int i = 0; i < layers.length; i++) {
			if (layers[i].getTitle().equals(layer))
				return layers[i];
		}
		log.debug("WARNING! getMapLayer esta devolviendo null para la capa = "
				+ layer + ", capas cargadas en el context = " + layers.length);

		return null;
	}

	public boolean isModified(Feature f) {
		return cache.isModified(f);
	}

	public boolean isDeleted(Feature f) {
		return cache.isDeleted(f);
	}

	public int getStatus(Feature f) {
		return cache.getStatus(f);
	}

	public void setStatus(Feature f, int status) {
		cache.setStatus(f, status);
	}

	public void close() {
	}

	public void setMapDimension(Dimension mapDimension) {
		this.mapDimension = mapDimension;
	}

	public Map<String, Map> getFormData(String type, String fid) throws Exception {
		Set<Identifier> set = new HashSet<Identifier>();
		set.add(filterFactory.featureId(fid));
		
		Filter fidf = filterFactory.id(set);

		return getFormData(new DefaultQuery(type, fidf), false);
	}

	public Map<String, Map> getFormData(Query q, boolean fromDB) throws Exception {

		Map<String, Map> cFinal = new HashMap<String, Map>();

		AppContext am = AppContext.getInstance();
		List children = am.getCapa(q.getTypeName()).getMetadata()
				.getChildrenMetadata();

		Feature f;
		Map<String, Map> item;
		Iterator i;
		ChildMetadata cd;
		Map<String, Feature> hijos;
		PropertyIsEqualTo filter;

		// Obtengo feature reader para la capa y meto todo en una coleccion
		FeatureReader fr;

		if (fromDB) {
			FeatureType ft = am.getCapa(q.getTypeName()).getFt();
			FeatureCollection fc = services.loadLayer(ft, q.getFilter(), true);
			fr = new CollectionFeatureReader(fc, ft);

		} else {
			fr = memoryDataStore.getFeatureReader(q, Transaction.AUTO_COMMIT);
		}

		while (fr.hasNext()) {

			f = fr.next();

			item = new HashMap<String, Map>();

			hijos = new HashMap<String, Feature>();
			hijos.put(f.getID(), f);
			// hijos.put(f.getID(), (fromDB) ? f : convertFromMemoryFeature(f));
			item.put(q.getTypeName(), hijos);

			// Si tiene hijos los agrego al mapa
			i = children.iterator();

			while (i.hasNext()) {
				cd = (ChildMetadata) i.next();

				filter = filterFactory.equals(
						filterFactory.property(cd.getParentIdAttribute()), 
						filterFactory.literal(f.getID())
				);
				
				Map<String, Map> child = getFormData(new DefaultQuery(cd.getLayerName(),
						filter), true);
				child.putAll(getFormData(new DefaultQuery(cd.getLayerName(),
						filter), false));

				item.put(cd.getLayerName(), child);
			}

			cFinal.put(f.getID(), item);
		}

		fr.close();
		return cFinal;
	}

	/**
	 * Crea un feature con el tipo y atributos especificados.
	 * 
	 * @param editedType
	 *            Nombre del FeatureType
	 * @param attributes
	 *            Valores de los atributos del feature
	 * 
	 * @return El feature creado
	 * 
	 * @throws IOException
	 * @throws IllegalAttributeException
	 */
	public Feature createFeature(String editedType, Object attributes[])
			throws IOException, IllegalAttributeException {

		FeatureType ft;
		Feature f;
		String id = null;

		ft = getCapa(editedType).getFt();
		f = ft.create(attributes);

		try {
			id = services.createFeatureID(f);
		} catch (GisException e) {
			e.printStackTrace();
		}

		f = ft.create(attributes, id);

		notifyChangeListeners(new FeatureChangeEvent(modelContext, editedType,
				f, null, FeatureChangeEvent.CREATED_FEATURE),
				FeatureEventManager.AFTER_MODIFY);

		return f;
	}

	/**
	 * Crea un Feature vacio, donde cada atributo tiene su valor por defecto.
	 * 
	 * @param editedType
	 *            El nombre del FeatureType del cual queremos obtener un feature
	 * @param g
	 *            Geometria a asignarle al feature
	 * 
	 * @return El feature construido
	 * 
	 * @throws IOException
	 * @throws IllegalAttributeException
	 */
	public Feature createEmptyFeature(String editedType, Geometry g)
			throws IOException, IllegalAttributeException {

		String id = null;

		FeatureType ft = getCapa(editedType).getFt();

		Object defaults[] = new Object[ft.getAttributeCount()];

		for (int i = 0; i < defaults.length; i++) {
			if (ft.getAttributeType(i) == ft.getDefaultGeometry())
				defaults[i] = g;
			else
				defaults[i] = null; // ft.getAttributeType(i).createDefaultValue();
		}

		Feature f = ft.create(defaults);

		try {
			id = services.createFeatureID(f);
		} catch (GisException e) {
			e.printStackTrace();
		}

		f = ft.create(defaults, id);

		notifyChangeListeners(new FeatureChangeEvent(modelContext, editedType,
				f, null, FeatureChangeEvent.CREATED_FEATURE),
				FeatureEventManager.AFTER_MODIFY);

		return f;
	}

	private void prepareDataType(String type, SaveDataType data, Iterator fr,
			int action) throws IOException, NoSuchElementException,
			IllegalAttributeException {

		while (fr.hasNext()) {
			Feature feature = (Feature) fr.next();
			Object[] attr;
			FeatureType ft = feature.getFeatureType();
			int cont = 0;

			// for (int i = 0; i < ft.getAttributeCount(); i++)
			// if (isNativeAttribute(ft.getAttributeType(i).getName()))
			// cont++;

			// attr = new Object[cont];
			attr = new Object[ft.getAttributeCount()];
			cont = 0;

			for (int i = 0; i < ft.getAttributeCount(); i++) {
				// if (!isNativeAttribute(ft.getAttributeType(i).getName()))
				// continue;

				attr[cont] = feature.getAttribute(i);
				cont++;
			}

			data.addModifiedFeature(action, type, feature.getID(), attr);

		}
	}

	public MapContext getContext() {
		return context;
	}

	public ModelContext getModelContext() {
		return modelContext;
	}

	public boolean isUsePanCache() {
		return usePanCache;
	}

	public void setUsePanCache(boolean panCache) {
		usePanCache = panCache;
	}
	
	public Iterator queryData(String layer, String textFilter) throws Exception {
		FeatureType ft = appContext.getSchema(layer);
		//ExpressionBuilder builder = new ExpressionBuilder();
		//Filter filter = (Filter)builder.parser(textFilter);
		Filter filter = CQL.toFilter(textFilter);
		return services.loadLayer(ft, filter, false).iterator();
	}
}
