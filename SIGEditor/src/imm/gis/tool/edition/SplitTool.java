package imm.gis.tool.edition;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.AbstractNonUILogic;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.interfaces.IDrawPanel;
import imm.gis.edition.EditionContext;
import imm.gis.edition.util.CoordinateCollector;
import imm.gis.edition.util.GeometryAccesor;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.Iterator;

import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

public class SplitTool extends AbstractEditionTool implements ActionListener {

	private CoordinateCollector splitCoordinates;
	
	private ICoreAccess coreAccess;
	private Cursor cursor;

	private EditionContext editionController;
	
	public SplitTool(ICoreAccess coreAccess, EditionContext editionController) {
		this.coreAccess = coreAccess;
		this.editionController = editionController;
		
		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("Split32.gif").getImage(), new Point(10, 10),"");
		
		splitCoordinates = new CoordinateCollector(coreAccess.getIModel().getGeometryFactory(), coreAccess.getIMap());
		
		coreAccess.getIModel().addGUIListener(this);
	}
	
	public Cursor getCursor() {
		return cursor;
	}
	
	public void mouseClicked(Coordinate c) {
		splitCoordinates.addCoordinate(c);
	}
	
	public void mouseMoved(Coordinate c) {
		updateSplitShape(c);
	}
	
	private void updateSplitShape(Coordinate c) {
		if (!splitCoordinates.isEmpty()) {
			
			GeneralPath measurePath;
			
			if (splitCoordinates.numCoordinates() > 1) {
				measurePath = splitCoordinates.createLinestringShape();
				measurePath.append(new Line2D.Double(coreAccess.getIMap().worldToPixel(splitCoordinates.getLastCoordinate()), coreAccess.getIMap().worldToPixel(c)), true);
			}
			else
				measurePath = new GeneralPath(new Line2D.Double(coreAccess.getIMap().worldToPixel(splitCoordinates.getLastCoordinate()), coreAccess.getIMap().worldToPixel(c)));
			
			coreAccess.getIDrawPanel().setTmpShape(measurePath, IDrawPanel.DASHED_LINE);
			coreAccess.getIDrawPanel().updateLayer();
		}
	}

	public void mouseDoubleClicked(Coordinate c) {

		final LineString splitLine = splitCoordinates.createLinestring();
		
		AbstractNonUILogic nonui = new AbstractNonUILogic() {

			public void logic() {
				try {
					Iterator featureIterator = coreAccess.getISelection().getSelected(editionController.getEditableType()).iterator();
					GeometryAccesor ga;
					
					while (featureIterator.hasNext()) {
					
						Feature f = (Feature) featureIterator.next();
						
						if (f.getDefaultGeometry().intersects(splitLine)) {
							
							// Parto la geometria
							ga = new GeometryAccesor(f.getDefaultGeometry());
							Geometry newGeometries[] = ga.split(splitLine);
							
							// Agrego los nuevos features
							Feature newFeature;
							
							for (int i = 0; i < newGeometries.length; i++) {
								newFeature = coreAccess.getIModel().createFeature(editionController.getEditableType(), f.getAttributes(null));
								newFeature.setDefaultGeometry(newGeometries[i]);
								
								coreAccess.getIModel().addFeature(newFeature);
							}
							
							// Borro el feature original
							coreAccess.getIModel().delFeature(f);
						}
						
						featureIterator = coreAccess.getISelection().getSelected(editionController.getEditableType()).iterator();
					}
				}
				catch (Exception e) {
					coreAccess.getIUserInterface().showError("Error al partir el feature",e);
					e.printStackTrace();
				}
			}
			
		};
		
		coreAccess.getIUserInterface().doNonUILogic(nonui);
		
		splitCoordinates.clearCoordinates();
		coreAccess.getIDrawPanel().setTmpShape(null);
	}
	
	public boolean requiresAddPermission() {
		return true;
	}
	
	public boolean requiresDeletePermission() {
		return true;
	}
	
	public void deactivate() {
		coreAccess.getIDrawPanel().setTmpShape(null);
		coreAccess.getIModel().removeGUIListener(this);
		coreAccess.getIDrawPanel().updateLayer();
	}

	public void actionPerformed(ActionEvent e) {
		if (!splitCoordinates.isEmpty())
			updateSplitShape(splitCoordinates.getLastCoordinate());
	}
}
