package imm.gis.gui.actions.edition;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.linemerge.LineMerger;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.edition.IFeatureEditor;
import imm.gis.edition.util.GeomUtil;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

public class JoinFeaturesAction extends ExtendedAction implements IFeatureEditor {

	private static final long serialVersionUID = 1L;
	
	private ICoreAccess coreAccess;

	private EditionContext editionController;

	public JoinFeaturesAction(ICoreAccess coreAccess, EditionContext editionController) {
		
		super("Pegar poligonos",
				GuiUtils.loadIcon("JoinPoly16.gif"),
				GuiUtils.loadIcon("JoinPoly16Dis.gif"));

		this.coreAccess = coreAccess;
		this.editionController = editionController;
		setToggle(false);
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		String editedType = editionController.getEditableType();
		
		Geometry newGeom = null;
		Feature newFeature = null;
		LineMerger lineMerger = null;
		
		try {
			Iterator featureIterator = coreAccess.getISelection().getSelected(editedType).iterator();
			
			Feature f1 = null;
			Feature f2 = null;

			if (featureIterator.hasNext())
				f1 = (Feature) featureIterator.next();
			
			if (featureIterator.hasNext())
				f2 = (Feature) featureIterator.next();
			
			if (f1 == null || f2 == null)
				coreAccess.getIUserInterface().showError("Uniendo features...", "Debe seleccionar al menos dos features");
			else {
				// Diferencio entre lineas y polígonos
				if (GeomUtil.isLineGeometry(f1.getFeatureType())) {
					LineString l1 = (LineString) f1.getDefaultGeometry();
					LineString l2 = (LineString) f2.getDefaultGeometry();
					
					// Para unir lineas, la interseccion debe ser un unico punto
					Geometry intersection = l1.intersection(l2);
					
					if (intersection.getGeometryType().equals("Point") && l1.touches(l2)) {
						lineMerger = new LineMerger();
						lineMerger.add(l1);
						lineMerger.add(l2);
						newGeom = (Geometry) lineMerger.getMergedLineStrings().iterator().next();
					}
					else if (intersection.getGeometryType().equals("MultiPoint")) {
						coreAccess.getIUserInterface().showError("Uniendo lineas...", "Las lineas deben intersectarse en un unico punto");
					}
					else {
						coreAccess.getIUserInterface().showError("Uniendo lineas...", "Las lineas deben intersectarse en los extremos");
					}
					
				}
				else {
					Polygon p1 = (Polygon)f1.getDefaultGeometry();
					Polygon p2 = (Polygon)f2.getDefaultGeometry();
					
					// Para unir poligonos, la interseccion debe ser un segmento de recta (y uno solo, pues si no habria huecos)
					Geometry intersection = p1.intersection(p2);
					
					if (intersection.getGeometryType().equals("LineString")) {
						newGeom = new Polygon((LinearRing)p1.union(p2).getBoundary(), null, p1.getFactory());
					}
					else if (intersection.getGeometryType().equals("MultiLineString")) {
						coreAccess.getIUserInterface().showError("Uniendo poligonos...", "No se soportan poligonos con mas de un anillo");
					}
					else {
						coreAccess.getIUserInterface().showError("Uniendo poligonos...", "Los poligonos deben ser contiguos y sin solapamiento");
					}
				}
				
				if (newGeom != null) {
					newFeature = f1.getFeatureType().create(f1.getAttributes(null));
					newFeature.setDefaultGeometry(newGeom);
	
					coreAccess.getIModel().delFeature(f1);
					coreAccess.getIModel().delFeature(f2);
					
					coreAccess.getIModel().addFeature(coreAccess.getIModel().createFeature(editedType, newFeature.getAttributes(null)));					
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}		
	}

	public boolean requiresAddPermission() {
		return  true;
	}

	public boolean requiresDeletePermission() {
		return true;
	}

	public boolean requiresModifyPermission() {
		return false;
	}

	public boolean requiresSelectVarious() {
		return false;
	}
	

	
}
