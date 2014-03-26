package imm.gis.tool.edition;

import imm.gis.core.gui.GuiUtils;
import imm.gis.core.gui.PanelDibujo;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.edition.EditionContext;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.edition.util.GeometryAccesor;

import java.awt.Cursor;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class SelectionTool extends AbstractEditionTool {

	private ICoreAccess coreAccess;
	private Cursor cursor;
	private Cursor dragCursor;
	
	private Feature dragFeature;
	private int dragVertex;
	private boolean dragAllTheFeature = false;
	
	private ChangeOriginCoordinate[] changeCoordinateFilter;
	private EditionContext editionController;

	private Coordinate firstCoordinate = null;
	private boolean hasDragged = false;
	private GeometryFactory gf = new GeometryFactory();
	
	private TranslateGeometry translateGeometry = new TranslateGeometry();
	private Geometry tmpGeometry;
	private Coordinate tmpCoordinate;
	
	private Logger log = Logger.getLogger(SelectionTool.class);
	
	
	public SelectionTool(ICoreAccess coreAccess, EditionContext editionController) {
		this.coreAccess = coreAccess;
		this.editionController = editionController;
		
		cursor = Cursor.getDefaultCursor();

		Toolkit tkt = Toolkit.getDefaultToolkit();
		
		dragCursor = tkt.createCustomCursor(GuiUtils.loadIcon("Drag32.gif").getImage(), new java.awt.Point(15, 15),"");
	}

	
	@Override
	public Cursor getCursor() {
		return cursor;
	}


	@Override
	public void mouseClicked(Coordinate c) {
		try {
			Collection<Feature> features = coreAccess.getIMap().getAllFeaturesOn(editionController.getEditableType(), c);
			
			if (features != null && !features.isEmpty()){
				for (Feature f : features){
					coreAccess.getISelection().selectFeature(f);					
				}
			} else
				coreAccess.getISelection().unselectAll();
		}
		catch (Exception e) {
			coreAccess.getIUserInterface().showError("Error al seleccionar el feature",e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void mouseDoubleClicked(Coordinate c) {
		// En cuanto a seleccion, el feature que eventualmente se edite aqui (los atributos),
		// ya fue seleccionado en mouseClicked.
		try {
			Feature f = coreAccess.getIMap().getFeatureOn(c);
			
			if (f != null){
				Collection col = coreAccess.getISelection().getSelected(f.getFeatureType().getTypeName());
				
				coreAccess.getIModel().notifyChangeListeners(new FeatureChangeEvent(col,
						f.getFeatureType().getTypeName(), null, null,
						FeatureChangeEvent.OPEN_FORM_MODIFY_FEATURE),
						FeatureEventManager.BEFORE_MODIFY);
				
				coreAccess.getIForm().openEditCollectionFeatureForm(col);
				
				coreAccess.getIModel().notifyChangeListeners(new FeatureChangeEvent(col,
						f.getFeatureType().getTypeName(), null, null,
						FeatureChangeEvent.OPEN_FORM_MODIFY_FEATURE),
						FeatureEventManager.AFTER_MODIFY);
			}
		}
		catch (Exception e) {
			coreAccess.getIUserInterface().showError("Error al consultar el feature",e);
			e.printStackTrace();
		}		
	}
	
	
	@Override
	public void mousePressed(int x, int y, Coordinate c) {
		dragFeature = null;
		dragVertex = -1;
		firstCoordinate = c;
		Point point = gf.createPoint(c);
		Feature feature = null;
		int vertex = -1;
		
		try {
			Iterator featureIterator = coreAccess.getISelection().getSelected(editionController.getEditableType()).iterator();
			GeometryAccesor ga;
			
			while (featureIterator.hasNext()) { // Recorro elementos seleccionados
				feature = (Feature) featureIterator.next();
				ga = new GeometryAccesor(feature.getDefaultGeometry());
				
				vertex = ga.getNearestVertex(c, coreAccess.getIMap().getAccuracyTolerance()); // se pinchó cerca de un vértice?....
				if (vertex != -1){ // Sí, se quiere modificar el vértice...
					dragAllTheFeature = false;
					dragVertex = vertex;
					dragFeature = feature;
					log.debug("Se quiere modificar el vertice " + vertex);
					break;
				}
				if (feature.getDefaultGeometry().contains(point)){
					dragAllTheFeature = true;
					dragFeature = feature;
					tmpGeometry = (Geometry)feature.getDefaultGeometry().clone();
					tmpCoordinate = c;
					log.debug("Se quiere modificar el feature " + feature.getID());
				}
			}
				
			if (dragAllTheFeature || vertex != -1) { // Se quiere modificar...
				if (!coreAccess.getIModel().isFeatureEditable(dragFeature)){
					coreAccess.getIUserInterface().showError("Este elemento no es editable");
				} else{
					coreAccess.getIDrawPanel().setCursor(dragCursor);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			coreAccess.getIUserInterface().showError("Error al seleccionar el vertice",e.getMessage());
		}
	}



	@Override
	public void mouseDragged(int x, int y, Coordinate c) {
		if (dragFeature == null)
			return;
		
		hasDragged = true;
		
		Coordinate coords[] = dragFeature.getDefaultGeometry().getCoordinates();

		GeometryAccesor ga = new GeometryAccesor(dragFeature.getDefaultGeometry());
		Shape tmp = null;
		
		if (dragAllTheFeature == false && !ga.isPoint()) { // Moviendo un vértice

			Coordinate c1 = null, c2 = null;

			if ((dragVertex % (coords.length - 1)) != 0) {
				c1 = coords[dragVertex - 1];
				c2 = coords[dragVertex + 1];
			}
			else {
				// Estoy en un extremo.
				if (ga.isLineString()) {
					c1 = dragVertex == 0 ? coords[dragVertex + 1] : coords[dragVertex - 1];
				}
				else {
					c1 = coords[coords.length - 2];
					c2 = coords[dragVertex + 1];
				}

			}
			
			Point2D p = coreAccess.getIMap().worldToPixel(c);
			tmp = new GeneralPath(new Line2D.Double(coreAccess.getIMap().worldToPixel(c1), p));
			
			if (c2 != null)
				((GeneralPath) tmp).append(new Line2D.Double(p, coreAccess.getIMap().worldToPixel(c2)), true);

			coreAccess.getIDrawPanel().setTmpShape(tmp, PanelDibujo.DASHED_LINE);
		} else if (dragAllTheFeature == true){ // Moviendo un feature
			translateGeometry.setX(c.x - tmpCoordinate.x);
			translateGeometry.setY(c.y - tmpCoordinate.y);
			tmpGeometry.apply(translateGeometry);
			tmp = coreAccess.getIMap().worldToPixel(tmpGeometry);
			tmpCoordinate = c;
			coreAccess.getIDrawPanel().setTmpShape(tmp, PanelDibujo.DASHED_LINE);
		}

		coreAccess.getIDrawPanel().updateLayer();
	}



	@Override
	public void mouseReleased(int x, int y, Coordinate newCoordinate) {
		coreAccess.getIDrawPanel().setCursor(cursor);
		coreAccess.getIDrawPanel().setTmpShape(null);
		
		if (!(hasDragged && firstCoordinate != null &&
				firstCoordinate.distance(newCoordinate) > coreAccess.getIMap().getAccuracyTolerance())) {
			coreAccess.getIDrawPanel().updateLayer();
			return;
		}
		
		
		if (dragFeature != null) {
			if (dragAllTheFeature){ // Drag un feature
				dragFeature(x, y, newCoordinate);
			} else { // Drag un vertice
				dragVertex(x, y, newCoordinate);
			}	
			coreAccess.getIDrawPanel().updateLayer();
		}

		firstCoordinate = null;
		hasDragged = false;
		dragAllTheFeature = false;
	}
	
	private void dragFeature(int x, int y, Coordinate newCoordinate){
		double distX = newCoordinate.x - firstCoordinate.x;
		double distY = newCoordinate.y - firstCoordinate.y;
		
		Geometry geom = (Geometry) dragFeature.getDefaultGeometry().clone();
		TranslateGeometry filter = new TranslateGeometry(distX, distY);
		
		try{
			geom.apply(filter);
			coreAccess.getIModel().modifyFeature(dragFeature, new String[] { dragFeature
											.getFeatureType().getDefaultGeometry().getLocalName() },
											new Object[] { geom });
		} catch (Exception e) {
			coreAccess.getIUserInterface().showError("Error al modificar el feature",e);
			e.printStackTrace();
		}		
	}
	
	private void dragVertex(int x, int y, Coordinate newCoordinate){
		log.debug("Drag del vertice");
		Geometry geom = (Geometry) dragFeature.getDefaultGeometry().clone();
		int coordLength = geom.getCoordinates().length;
		
		GeometryAccesor ga = new GeometryAccesor(dragFeature.getDefaultGeometry());
		
		// Contemplo el caso en que se quiere mover un vertice extremo (en ese caso
		// se mueve el simetrico tambien)
		boolean isPolygonExtreme = (coordLength == 1) ?	false : (dragVertex % (coordLength - 1)) == 0 && ga.isPolygon();
		
		int arraySize = isPolygonExtreme ? 2 : 1;
		
		Integer[] draggedVertexs = new Integer[arraySize];
		draggedVertexs[0] = new Integer(dragVertex);
		
		if (isPolygonExtreme) {
			draggedVertexs[1] = new Integer((dragVertex == 0 ? coordLength - 1 : 0));
		}
		
		changeCoordinateFilter = new ChangeOriginCoordinate[arraySize];
		
		try {
			for (int i = 0; i < draggedVertexs.length; i++) {
				int index = draggedVertexs[i].intValue();
				Coordinate c = geom.getCoordinates()[index];
				changeCoordinateFilter[i] = new ChangeOriginCoordinate(null,null);
				changeCoordinateFilter[i].setOriginalCoord(c);
				changeCoordinateFilter[i].setNewCoord(c);
				
				changeCoordinateFilter[i].setNewCoord(newCoordinate);

				if (!changeCoordinateFilter[i].getOriginalCoord().equals(
						changeCoordinateFilter[i].getNewCoord())) {
					geom.apply(changeCoordinateFilter[i]);

				}
			}
			coreAccess.getIModel().modifyFeature(dragFeature, new String[] { dragFeature
					.getFeatureType().getDefaultGeometry().getLocalName() },
					new Object[] { geom });
		}
		catch (Exception e) {
			coreAccess.getIUserInterface().showError("Error al modificar el vertice",e);
			e.printStackTrace();
		}		
	}
	
	private class ChangeOriginCoordinate implements CoordinateFilter {
		Coordinate original, newCoord;

		public ChangeOriginCoordinate(Coordinate o, Coordinate n) {
			original = o;
			newCoord = n;
		}

		public void setOriginalCoord(Coordinate c) {
			original = c;
		}

		public void setNewCoord(Coordinate c) {
			newCoord = c;
		}

		public Coordinate getOriginalCoord() {
			return original;
		}

		public Coordinate getNewCoord() {
			return newCoord;
		}

		public void filter(Coordinate c) {
			if (c == original)
				original.setCoordinate(newCoord);
		}
	}
	
	private class TranslateGeometry implements CoordinateFilter{
		private double distX, distY;
		
		public TranslateGeometry(){}
		
		public TranslateGeometry(double x, double y){
			setX(x);
			setY(y);
		}
		
		public void setX(double x){
			distX = x;
		}
		
		public void setY(double y){
		distY = y;	
		}
		
		public void filter(Coordinate c) {
			c.x += distX;
			c.y += distY;
		}		
	}
	
	public boolean requiresModifyPermission() {
		return true;
	}
}
