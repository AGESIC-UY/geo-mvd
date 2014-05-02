package imm.gis.core.interfaces;

import java.util.Map;
import java.util.Observer;

import imm.gis.core.controller.ContAccionesAplicacion;
import imm.gis.edition.EditionContextListener;
import imm.gis.form.IFeatureForm;
import imm.gis.navigation.NavigationController;
import imm.gis.tool.ITool;

public interface ICoreAccess {

	public IModel getIModel();
	public IMap getIMap();
	public IForm getIForm();
	public IDrawPanel getIDrawPanel();
	public IUserInterface getIUserInterface();
	public ISelection getISelection();
	public IApplication getIApplication();
	public IPermission getIPermission();
	
	public ITool getActiveTool();
	public void setActiveTool(ITool tool);
	public void addEdtionContextListener(EditionContextListener eCL);
	
	public void addObserverForClose(Observer o);
	public void removeObserverForClose(Observer o);
	
	public void setUserDefaultForm(String layer, IFeatureForm form);
	public void setLayerPredefinedFilters(String layer, Map<String, String> filters) throws Exception;
	public void addLayerPredefinedFilter(String layer, String filterName, String filter) throws Exception;
	public void addLayerPredefinedFilter(String layer, String filterParentName, String filterName, String filter, boolean centerFilter) throws Exception;
	public void applyPredefinedFilter(String layer, String filterName);
	public void setEditable(boolean editable);
	public void setEditableLayer(String layer) throws Exception;
	public NavigationController getNavigationController();
	public ContAccionesAplicacion getContAccionesAplicacion();
	
}
