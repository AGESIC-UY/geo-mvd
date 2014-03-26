package imm.gis.core.controller;

import imm.gis.AppContext;
import imm.gis.GisException;
import imm.gis.comm.IServerServices;
import imm.gis.comm.ServerServicesFactory;
import imm.gis.core.gui.PanelDibujo;
import imm.gis.core.gui.VistaPpal;
import imm.gis.core.gui.editedfeatures.EditedFeaturesEventManager;
import imm.gis.core.gui.editedfeatures.EditedFeaturesPanel;
import imm.gis.core.gui.layermanager.LayerManagerEventManager;
import imm.gis.core.interfaces.IApplication;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.interfaces.IDrawPanel;
import imm.gis.core.interfaces.IForm;
import imm.gis.core.interfaces.IMap;
import imm.gis.core.interfaces.IModel;
import imm.gis.core.interfaces.IPermission;
import imm.gis.core.interfaces.ISelection;
import imm.gis.core.interfaces.IUserInterface;
import imm.gis.core.model.ModelData;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.core.model.selection.SelectionModel;
import imm.gis.core.permission.PermissionController;
import imm.gis.edition.EditionContext;
import imm.gis.edition.EditionContextListener;
import imm.gis.form.IFeatureForm;
import imm.gis.navigation.NavigationController;
import imm.gis.tool.ITool;
import imm.gis.tool.misc.PointerTool;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.geotools.filter.ExpressionBuilder;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

public class ContPpal extends Observable  implements ICoreAccess, IApplication {
	private VistaPpal vista = null;
	private ContMapa contMapa = null;
	private NavigationController contNavegacion = null;
	private ContAccionesAplicacion contApp = null;
	private ContUndo contUndo = null;
	private ModelData model = null;
	private ContBusqueda contBusqueda = null;
	private ContMousePanelDibujo mousePanelDibujo;
	private ContStyleDialog contStyleDialog = null;
	private ContFilterLayerDialog contFilterLayerDialog = null;
	private SelectionModel selectionModel = null;
	private EditionContext editionController = null;
	private ContForms formController = null;
	private ToolController toolController = null;
	private PermissionController permissionController = null;
	private boolean exitOnClose = false;
	private LayerManagerEventManager layerEventManager = null;
	private Logger logger = Logger.getLogger(ContPpal.class.getName());

	public ContPpal(Envelope init) throws Exception {
		IServerServices services = ServerServicesFactory.getServerServices();
		AppContext.getInstance().setCoreAccess(this);
		logger.info("Creando Modelo ");
		model = new ModelData(services, init);
		
		logger.info("Creando Controladores auxiliares ");
		// controladores auxiliares
		formController = new ContForms(this);
		contMapa = new ContMapa(this);
		selectionModel = new SelectionModel(model);
		editionController = new EditionContext(this);
		contStyleDialog = new ContStyleDialog(this);
		contFilterLayerDialog = new ContFilterLayerDialog(this);
		contApp = new ContAccionesAplicacion(this);
		contUndo = new ContUndo(this);
		contBusqueda = new ContBusqueda(this);
		toolController = new ToolController(this);
		permissionController = new PermissionController(null);
		contNavegacion = new NavigationController();
		
		logger.info("Creando Vista principal");
		// vista principal
		vista = new VistaPpal(this);

		contNavegacion.setDrawPanel(vista.getPanelDibujo());
		contNavegacion.setFrame(vista);
		contNavegacion.setMap(contMapa);
		contNavegacion.setUserInterface(vista);
		
		mousePanelDibujo = new ContMousePanelDibujo(this);
		new ContKeyboardEvents(this);
		editionController.setUpListeners();
		vista.setUpListeners();
		
		logger.info("Creando listeners");
		// listeners
		model.addGUIListener(vista.getPanelDibujo());
		//model.addGUIListener(vista.getTreePanel());
		model.addGUIListener(vista.getScalePanel());
		model.addGUIListener(vista.getLeftPanel().getLegendPanel());
		
		selectionModel.addFeatureSelectionListener(vista.getPanelDibujo());
		//selectionModel.addFeatureSelectionListener(vista.getTreePanel());
		editionController.addContextListener(selectionModel);
		editionController.addContextListener(vista.getEditionHistoryPanel());
		model.addFeatureChangeListener(vista.getEditionHistoryPanel(), FeatureEventManager.AFTER_MODIFY);
		model.setUndoListener(contUndo.getModel());
		vista.getPanelDibujo().addMouseListener(mousePanelDibujo);
		vista.getPanelDibujo().addMouseMotionListener(mousePanelDibujo);
		vista.getPanelDibujo().addComponentListener(
				new ContPanelResize(this, vista.getSize()));
		vista.addWindowListener(new VistaPpalListener());
		vista.getEditionHistoryPanel().getTree().addMouseListener(
				new EditedFeaturesEventManager(getFormController(), getContMapa(),
						getSelectionModel(), getModel(), vista.getEditionHistoryPanel().getTree()));
		layerEventManager = new LayerManagerEventManager(
				getModel(), 
				getVistaPrincipal(),
				editionController,
				contFilterLayerDialog,
				contStyleDialog,
				vista.getLeftPanel().getLegendPanel().getTree());
		model.getContext().addMapLayerListListener(vista.getLeftPanel().getLegendPanel());
		vista.getLeftPanel().getLegendPanel().getTree().addMouseListener(
				layerEventManager);
		ITool pointerTool = new PointerTool(this);
		
		toolController.activateTool(pointerTool);
	}

	/**
	 * Cierra la aplicacion, ofreciendo guardar los cambios en caso de que esto
	 * no se haya hecho.
	 * 
	 */
	public void cerrar() {
		cerrar(null, true);
	}

	public void cerrar(boolean confirm){
		cerrar(null, confirm);
	}
	/**
	 * Cierra la aplicacion, ofreciendo guardar los cambios en caso de que esto
	 * no se haya hecho si se solicita.
	 * 
	 * @param e
	 *            Si no es null, la excepcion que causo que se cierre la
	 *            aplicacion.
	 */
	public void cerrar(Exception e, boolean confirm) {
			if (!confirm){
				// Quiero salir y listo, no me importa nada...
				logger.info("Saliendo sin confirmar");
			} else if (editionController.isEditing()
					&& model.isModified(editionController.getEditableType())) {
				// Se est� editando y la capa en edici�n est� modificada...
				
				int res = JOptionPane.showConfirmDialog(vista,
						"Desea guardar los cambios antes de salir?",
						"Salir del sistema", JOptionPane.YES_NO_CANCEL_OPTION);

				if (res == JOptionPane.CANCEL_OPTION){
					return;
				} else if (res == JOptionPane.YES_OPTION){
					try{
						model.confirmModified(editionController.getEditableType());
					} catch (Exception ex) {
						vista.showError("Error al guardar las modificaciones",ex);
						ex.printStackTrace();
						return;
					}
				}
			} else {
				// No hay cambios pendientes pero quiero confimar la salida...
				int res = JOptionPane.showConfirmDialog(vista, "Desea salir?",
						"Salir del sistema", JOptionPane.YES_NO_OPTION);

				if (res == JOptionPane.NO_OPTION)
					return;
			}

			// Todo OK, salgo...
			
			// Primero cierro referencias con el servidor...
			// Por ejemplo, si usamos HTTP se invalida la sesion
			
			try {
				logger.info("Cerrando aplicacion en el servidor...");
				ServerServicesFactory.getServerServices().closeApp(AppContext.getInstance().getId());
			} catch (GisException e1) {
				e1.printStackTrace();
			}

			vista.setVisible(false);
			if (e == null) {
				logger.info("Cerrando aplicacion local...");
			} else {
				logger.error("Cerrando aplicacion " ,e);
				e.printStackTrace();
			}

			model.close();
	
			//Se notifica a los Observers que se cierra el editor
			this.setChanged();
			this.notifyObservers();
			
			if (exitOnClose){
				// Si est� seteado que cierre toda la VM, vamo' arriba...
				System.exit(0);
			}
	}	
	
	public void setExitOnClose(boolean exitOnClose) {
		this.exitOnClose = exitOnClose;
	}
	
	
	public VistaPpal getVistaPrincipal(){
		return vista;
	}

	public EditedFeaturesPanel getTreePanel() {
		return vista.getEditionHistoryPanel();
	}

	public PanelDibujo getPanelDibujo() {
		return vista.getPanelDibujo();
	}

	java.awt.image.BufferedImage getImage() {
		return vista.getPanelDibujo().getImage();
	}

	public ContMapa getContMapa() {
		return contMapa;
	}

	public ContAccionesAplicacion getContAccionesAplicacion() {
		return contApp;
	}

	public ContUndo getContUndo() {
		return contUndo;
	}

	private class VistaPpalListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			cerrar();
		}
	}

	public ModelData getModel() {
		return model;
	}

	public boolean isEditingMode() {
		return editionController.isEditing();
	}

	public void showError(String title, Exception e) {
		showError(title, e.getMessage());
	}

	public void showError(Exception e) {
		showError(e.getMessage());
	}

	public void showError(String title, String msg) {
		vista.showError(title, msg);
	}

	public void showError(String msg) {
		vista.showError(msg);
	}

	public ContBusqueda getContBusqueda() {
		return contBusqueda;
	}

	public ContMousePanelDibujo getContMousePanelDibujo() {
		return this.mousePanelDibujo;
	}

	public ContStyleDialog getContStyleDialog() {
		return this.contStyleDialog;
	}

	public ISelection getSelectionModel() {
		return selectionModel;
	}
	
	public EditionContext getEditionContext() {
		return editionController;
	}
	
	public ContFilterLayerDialog getContFilterLayerDialog() {
		return contFilterLayerDialog;
	}

	public ContForms getFormController() {
		return formController;
	}

	public ToolController getToolController() {
		return toolController;
	}
	
	public PermissionController getPermissionController() {
		return permissionController;
	}

	public void addEdtionContextListener(EditionContextListener ecl) {
		editionController.addContextListener(ecl);
	}

	public void addObserverForClose(Observer o) {
		addObserver(o);
	}

	public ITool getActiveTool() {
		return toolController.getActiveTool();
	}

	public NavigationController getNavigationController(){
		return contNavegacion;
	}
	
	public IApplication getIApplication() {
		return this;
	}

	public IDrawPanel getIDrawPanel() {
		return vista.getPanelDibujo();
	}

	public IForm getIForm() {
		return getFormController();
	}

	public IMap getIMap() {
		return contMapa;
	}

	public IModel getIModel() {
		return model;
	}

	public IPermission getIPermission() {
		return permissionController;
	}

	public ISelection getISelection() {
		return selectionModel;
	}

	public IUserInterface getIUserInterface() {
		return vista;
	}

	public void removeObserverForClose(Observer o) {
		deleteObserver(o);
	}

	public void setActiveTool(ITool tool) {
		toolController.activateTool(tool);
	}

	public JFrame getMainGUI() {
		return vista;
	}

	public void setUserDefaultForm(String layer, IFeatureForm form) {
		AppContext.getInstance().setUserForm(layer, form);
	}

	public void setLayerPredefinedFilters(String layer, Map<String, String> filters) throws Exception {
		String name;
		String textFilter;
		ExpressionBuilder builder = new ExpressionBuilder();
		AppContext app = AppContext.getInstance();
		Filter filter;
		Map<String, Filter> finalFilter = new HashMap<String, Filter>();
		
		for (java.util.Iterator it = filters.keySet().iterator(); it.hasNext();){
			name = (String)it.next();
			textFilter = (String)filters.get(name);
			filter = (Filter)builder.parser(app.getSchema(layer), textFilter);
			logger.info("agregando filtro " + name + " a capa " + layer + ": " + filter.toString());
			finalFilter.put(name, filter);
		}
		

		app.setPredefFilters(layer, finalFilter);
	}

	public void addLayerPredefinedFilter(String layer, String filterName,
			String textFilter) throws Exception {
		addLayerPredefinedFilter(layer, "", filterName, textFilter, false);
	}
	
	public void addLayerPredefinedFilter(String layer, String filterParentName, String filterName,
			String textFilter, boolean centerFilter) throws Exception {
		AppContext app = AppContext.getInstance();
		ExpressionBuilder builder = new ExpressionBuilder();
		Filter filter = (Filter)builder.parser(app.getSchema(layer), textFilter);		

		if (filterParentName != "")
			filterName = "Parent"+filterParentName+"->"+filterName;
		if (centerFilter)
			app.getPredefFilters(layer).put(filterName+"->center", filter);
		else
			app.getPredefFilters(layer).put(filterName, filter);
	}

	public void applyPredefinedFilter(String layer, String filterName){
		AppContext app = AppContext.getInstance();
		if (!app.getPredefFilters(layer).containsKey(filterName)){
			throw new IllegalArgumentException("El filtro" + 
					filterName + " no esta registrado en la capa" + layer);
		}
		
		app.getCapa(layer).setFilter(app.getPredefFilters(layer).get(filterName));
	}
	
	public void setEditable(boolean editable){
		vista.setEditable(editable);
		layerEventManager.setCanEdit(editable);
	}
	
	public void setEditableLayer(String layer) throws Exception{
		editionController.setEditableType(layer);
	}
}
