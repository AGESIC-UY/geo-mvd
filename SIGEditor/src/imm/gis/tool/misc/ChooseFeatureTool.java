package imm.gis.tool.misc;

import imm.gis.core.controller.IMapListener;
import imm.gis.core.controller.MapEvent;
import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.tool.AbstractTool;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;

public class ChooseFeatureTool extends AbstractTool {

	private ICoreAccess coreAccess;
	private Cursor cursor;
	private IMapListener listener;
	
	public ChooseFeatureTool(ICoreAccess coreAccess, IMapListener listener) {
		this.coreAccess = coreAccess;
		this.listener = listener;
		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("Drag32.gif").getImage(), new Point(15, 15),"");
	}

	
	public Cursor getCursor() {
		return cursor;
	}
	
	public void mouseClicked(Coordinate c) {
		
		try {
			Feature f = coreAccess.getIMap().getFeatureOn(listener.getListenedSchema(), c);
			
			if (f != null)
				listener.mapEvent(new MapEvent(coreAccess.getIMap(), f));
		}
		catch (Exception e) {
			coreAccess.getIUserInterface().showError("Error al consultar el feature",e);
			e.printStackTrace();
		}
	}
	

}
