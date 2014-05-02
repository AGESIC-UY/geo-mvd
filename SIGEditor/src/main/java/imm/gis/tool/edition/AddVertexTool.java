package imm.gis.tool.edition;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.util.GeometryAccesor;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Iterator;

import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;

public class AddVertexTool extends AbstractEditionTool {

	private ICoreAccess coreAccess;
	private Cursor cursor;
	private EditionContext editionController;
	
	public AddVertexTool(ICoreAccess coreAccess, EditionContext editionController) {
		this.coreAccess = coreAccess;
		this.editionController = editionController;
		
		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("AddVertex32.gif").getImage(), new Point(10, 10),"");
	}
	
	public Cursor getCursor() {
		return cursor;
	}
	
	
	public void mouseClicked(Coordinate c) {
		
		try {
			Iterator featureIterator = coreAccess.getISelection().getSelected(editionController.getEditableType()).iterator();
			
			GeometryAccesor ga = null;
			Feature f = null;
			LineSegment nearestLineSegment = null;
			
			while (featureIterator.hasNext()) {
				f = (Feature) featureIterator.next();
				ga = new GeometryAccesor(f.getDefaultGeometry());
				
				nearestLineSegment = ga.getNearestLineSegment(c, coreAccess.getIMap().getAccuracyTolerance());
				
				if (nearestLineSegment != null)
					break;
			}

			if (nearestLineSegment != null) {
				int vertexIndex = ga.getNearestVertex(nearestLineSegment.p0);
				Coordinate snappedVertex = nearestLineSegment.closestPoint(c);
				
				Geometry newGeometry = ga.addVertex(vertexIndex, snappedVertex);
					
				coreAccess.getIModel().modifyFeature(f,
						new String[] { f.getFeatureType().getDefaultGeometry().getLocalName() },
						new Object[] { newGeometry });
			}
		}
		catch (Exception e) {
			coreAccess.getIUserInterface().showError("Error al agregar el vertice",e);
			e.printStackTrace();
		}
	}
	
	public boolean requiresModifyPermission() {
		return true;
	}

}
