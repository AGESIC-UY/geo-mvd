package imm.gis.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Action;

import imm.gis.core.controller.ContBusqueda;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.EditionContextListener;
import imm.gis.gui.actions.misc.CoordinatesKMLAction;
import imm.gis.gui.actions.misc.MeasureAction;
import imm.gis.gui.actions.misc.PointerAction;
import imm.gis.gui.actions.misc.QueryAction;
import imm.gis.gui.actions.misc.SearchAction;
import imm.gis.gui.actions.navigation.DragWithCacheOption;
import imm.gis.gui.actions.navigation.HomeAction;
import imm.gis.gui.actions.navigation.MoveEastAction;
import imm.gis.gui.actions.navigation.MoveNorthAction;
import imm.gis.gui.actions.navigation.MoveSouthAction;
import imm.gis.gui.actions.navigation.MoveWestAction;
import imm.gis.gui.actions.navigation.NextZoomAction;
import imm.gis.gui.actions.navigation.PanAction;
import imm.gis.gui.actions.navigation.PreviousZoomAction;
import imm.gis.gui.actions.navigation.RefreshAction;
import imm.gis.gui.actions.navigation.ZoomInAction;
import imm.gis.gui.actions.navigation.ZoomOutAction;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.navigation.NavigationController;

public class GeneralToolBar extends ConstructionToolBar implements EditionContextListener {

	private static final long serialVersionUID = 1L;
	
	private PointerAction pointerAction = null;
	private Action homeAction = null;
	private Action moveNorthAction = null;
	private Action moveSouthAction = null;
	private Action moveWestAction = null;
	private Action moveEastAction = null;
	private Action previousZoomAction = null;
	private Action nextZoomAction = null;
	private Action searchAction = null;
	private Action measureAction = null;
	private ExtendedAction zoomInAction = null;
	private ExtendedAction zoomOutAction = null;
	private ExtendedAction refreshMapAction = null;
	private ExtendedAction queryAction = null;
	private ExtendedAction panAction = null;
	private ExtendedAction dragWithCacheOption = null;
	private ExtendedAction coordinatesKMLAction = null;

	private ICoreAccess coreAccess;

	private Map<String, Action> actionMap;
	
	public GeneralToolBar(ICoreAccess coreAccess,
			NavigationController navigationController,
			ContBusqueda searchController,
			EditionContext editionContext) {
		
		super(true);
		
		this.coreAccess = coreAccess;
		
		pointerAction = new PointerAction(coreAccess, editionContext);
		homeAction = new HomeAction(navigationController);
		moveNorthAction = new MoveNorthAction(navigationController);
		moveSouthAction = new MoveSouthAction(navigationController);
		moveWestAction = new MoveWestAction(navigationController);
		moveEastAction = new MoveEastAction(navigationController);
		panAction = new PanAction(coreAccess, navigationController);
		zoomInAction = new ZoomInAction(coreAccess, navigationController);
		zoomOutAction = new ZoomOutAction(coreAccess, navigationController);
		previousZoomAction = new PreviousZoomAction(navigationController);
		nextZoomAction = new NextZoomAction(navigationController);
		refreshMapAction = new RefreshAction(navigationController);
		queryAction = new QueryAction(coreAccess);
		searchAction = new SearchAction(searchController);
		measureAction = new MeasureAction(coreAccess);
		dragWithCacheOption = new DragWithCacheOption(coreAccess);
		coordinatesKMLAction = new CoordinatesKMLAction(coreAccess);
		
		Object actions[] = new Object[]{
			pointerAction,
			homeAction,
			new Object[] {
				moveWestAction,
				moveEastAction,
				moveNorthAction,
				moveSouthAction
			},
			panAction,
			dragWithCacheOption,
			zoomInAction,
			zoomOutAction,
			previousZoomAction,
			nextZoomAction,
			queryAction,
			searchAction,
			measureAction,
			refreshMapAction,
			coordinatesKMLAction
		};
		
		addItemsToToolBar(actions, true);
		setFloatable(true);
		
		actionMap = new HashMap<String, Action>();
		
		actionMap.put("POINTER", pointerAction);
		actionMap.put("HOME", homeAction);
		actionMap.put("MOVE_NORTH", moveNorthAction);
		actionMap.put("MOVE_SOUTH", moveSouthAction);
		actionMap.put("MOVE_WEST", moveWestAction);
		actionMap.put("MOVE_EAST", moveEastAction);
		actionMap.put("PAN", panAction);
		actionMap.put("ZOOM_IN", zoomInAction);
		actionMap.put("ZOOM_OUT", zoomOutAction);
		actionMap.put("PREVIOUS_ZOOM", previousZoomAction);
		actionMap.put("NEXT_ZOOM", nextZoomAction);
		actionMap.put("REFRESH_MAP", refreshMapAction);
		actionMap.put("QUERY", queryAction);
		actionMap.put("SEARCH", searchAction);
		actionMap.put("MEASURE", measureAction);
		actionMap.put("COORDINATES_KML", coordinatesKMLAction);
	
		editionContext.addContextListener(this);
	}

	public Map getActions() {
		return actionMap;
	}

	public void editionEntered(String type) {
		pointerAction.setEnabled(coreAccess.getIPermission().getPermissionManager(type).hasPermissions(pointerAction));
	}

	public void editionExited(String type) {
		pointerAction.setEnabled(true);
		((AbstractButton) getComponent(0)).setSelected(true);
		pointerAction.actionPerformed(null);
	}
	

}
