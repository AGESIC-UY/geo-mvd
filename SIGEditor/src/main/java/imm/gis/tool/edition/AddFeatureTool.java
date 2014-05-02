package imm.gis.tool.edition;

import imm.gis.core.controller.IGeometryType;
import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.util.CoordinateCollector;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class AddFeatureTool extends AbstractEditionTool implements ActionListener {

	private ICoreAccess coreAccess;
	private Cursor cursor;
	
	private CoordinateCollector featureCoordinates;
	private EditionContext editionContext;
	
	public AddFeatureTool(ICoreAccess coreAccess, EditionContext editionContext) {
		this.coreAccess = coreAccess;
		this.editionContext = editionContext;
		
		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("AddFeature32.gif").getImage(), new Point(10, 10),"");
		
		featureCoordinates = new CoordinateCollector(coreAccess.getIModel().getGeometryFactory(), coreAccess.getIMap());
		coreAccess.getIModel().addGUIListener(this);
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void mouseClicked(Coordinate c) {
		
		if (editionContext.getEditableTypeGeometry() == IGeometryType.POINT_GEOMETRY)
			addFeature(coreAccess.getIModel().getGeometryFactory().createPoint(c));
		else
			featureCoordinates.addCoordinate(c);
	}
	
	public void mouseDoubleClicked(Coordinate c) {
		if (editionContext.getEditableTypeGeometry() == IGeometryType.LINE_GEOMETRY && featureCoordinates.numCoordinates() > 1)
			addFeature(featureCoordinates.createLinestring());
		else if (editionContext.getEditableTypeGeometry() == IGeometryType.POLYGON_GEOMETRY && featureCoordinates.numCoordinates() > 2)
			addFeature(featureCoordinates.createPolygon());
	}
	
	public void mouseMoved(Coordinate c) {
		updateFeatureShape(c);
	}
	
	private void updateFeatureShape(Coordinate c) {
		
		GeneralPath featurePath;
		
		if (!featureCoordinates.isEmpty()) {
			if (featureCoordinates.numCoordinates() > 1) {
				featurePath = featureCoordinates.createLinestringShape();
				featurePath.append(new Line2D.Double(coreAccess.getIMap().worldToPixel(featureCoordinates.getLastCoordinate()), coreAccess.getIMap().worldToPixel(c)), true);
			}
			else
				featurePath = new GeneralPath(new Line2D.Double(coreAccess.getIMap().worldToPixel(featureCoordinates.getLastCoordinate()), coreAccess.getIMap().worldToPixel(c)));
			
			coreAccess.getIDrawPanel().setTmpShape(featurePath);
			coreAccess.getIDrawPanel().updateLayer();
		}
	}

	private void addFeature(Geometry g) {
		try {
			featureCoordinates.clearCoordinates();
			
			Feature f = coreAccess.getIModel().createEmptyFeature(editionContext.getEditableType(), g);
			
			coreAccess.getIDrawPanel().setTmpShape(null);
			coreAccess.getIDrawPanel().updateLayer();
			
			coreAccess.getIForm().openNewFeatureForm(f);
		}
		catch (Exception e) {
			coreAccess.getIUserInterface().showError("Error al crear el feature", e);
			e.printStackTrace();
		}		
	}
	
	public boolean requiresAddPermission() {
		return true;
	}
	
	public void deactivate() {
		coreAccess.getIDrawPanel().setTmpShape(null);
		coreAccess.getIModel().removeGUIListener(this);
		coreAccess.getIDrawPanel().updateLayer();
	}

	public void actionPerformed(ActionEvent e) {
		if (!featureCoordinates.isEmpty())
			updateFeatureShape(featureCoordinates.getLastCoordinate());
	}
}
