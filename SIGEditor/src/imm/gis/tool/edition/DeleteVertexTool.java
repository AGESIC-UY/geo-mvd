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

public class DeleteVertexTool extends AbstractEditionTool {

	private ICoreAccess coreAccess;
	private Cursor cursor;
	private EditionContext editionController;
	
	public DeleteVertexTool(ICoreAccess coreAccess, EditionContext editionController) {
		this.coreAccess = coreAccess;
		this.editionController = editionController;
		
		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		cursor = tkt.createCustomCursor(GuiUtils.loadIcon("DelVertex32.gif").getImage(), new Point(10, 10),"");
	}

	
	public Cursor getCursor() {
		return cursor;
	}
	
	public void mouseClicked(Coordinate c) {
		
		try {
			Iterator featureIterator = coreAccess.getISelection().getSelected(editionController.getEditableType()).iterator();
			
			GeometryAccesor ga = null;
			Feature f = null;
			int vertexIndex = -1;
			
			while (featureIterator.hasNext()) {
				f = (Feature) featureIterator.next();
				ga = new GeometryAccesor(f.getDefaultGeometry());
				
				vertexIndex = ga.getNearestVertex(c, coreAccess.getIMap().getAccuracyTolerance());
				
				if (vertexIndex != -1)
					break;
			}
			
			if (vertexIndex != -1) {
				
				Geometry newGeometry = ga.deleteVertex(vertexIndex);
				
				coreAccess.getIModel().modifyFeature(f,
						new String[] { f.getFeatureType().getDefaultGeometry().getLocalName() },
						new Object[] { newGeometry });

			}
		}
		catch (Exception ex) {
			coreAccess.getIUserInterface().showError("Error al eliminar el vertice",ex);
			ex.printStackTrace();
		}
		
	}
	
	public boolean requiresModifyPermission() {
		return true;
	}
}
