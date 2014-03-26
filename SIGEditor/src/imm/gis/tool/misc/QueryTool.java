package imm.gis.tool.misc;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.tool.AbstractTool;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.Iterator;

import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;

public class QueryTool extends AbstractTool {

	private Cursor cursor;
	private ICoreAccess coreAccess;
	
	public QueryTool(ICoreAccess coreAccess) {
		this.coreAccess = coreAccess;

		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("ShowInfo32.gif").getImage(), new Point(7, 7),"");
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void mouseClicked(Coordinate c) {
		try {
			Collection<Feature> f = coreAccess.getIMap().getAllFeaturesOn(c);
			coreAccess.getIModel().notifyChangeListeners(new FeatureChangeEvent(f,
					((Feature)((Iterator)f.iterator()).next()).getFeatureType().getTypeName(), (Feature)((Iterator)f.iterator()).next(), null,
					FeatureChangeEvent.OPEN_FORM_SHOW_FEATURE),
					FeatureEventManager.BEFORE_MODIFY);
			
			coreAccess.getIForm().openShowFeaturesForm(f);
		}
		catch (Exception e) {
			coreAccess.getIUserInterface().showError("Error al consultar el feature",e);
			e.printStackTrace();
		}
	}
}
