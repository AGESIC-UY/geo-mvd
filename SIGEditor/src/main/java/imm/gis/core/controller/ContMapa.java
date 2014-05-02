package imm.gis.core.controller;

import imm.gis.AppContext;
import imm.gis.core.gui.worker.BlockingSwingWorker;
import imm.gis.core.interfaces.CoordinateListener;
import imm.gis.core.interfaces.IDrawPanel;
import imm.gis.core.interfaces.IMap;
import imm.gis.core.model.ModelData;

import java.awt.Component;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureReader;
import org.geotools.feature.Feature;
import org.geotools.map.MapContext;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * Clase encargada del manejo del mapa.
 *
 * @author agrassi
 * 
 */
public class ContMapa implements IMap, ActionListener {
	private ModelData modelData;

	private ContPpal contPpal = null;

	private static Logger logger = Logger.getLogger(ContMapa.class.getName());

	private double bufferInPixels = 6;
	private Timer blinkTimer = new Timer(300, this);
	private int veces = 0;
	private Shape blinkShape;
	
	public ContMapa(ContPpal ppal) {
		contPpal = ppal;
		modelData = ppal.getModel();
	}

	
	/**
	 * Convierte una coordenada a su ubicacion dentro del mapa actual en
	 * pixels
	 * 
	 * @param c
	 *            Coordenada a transformar
	 * 
	 * @return Un punto representando la ubicacion dentro del mapa
	 */
	public java.awt.geom.Point2D worldToPixel(Coordinate c) {
		Envelope mapExtent = modelData.getBbox();
		java.awt.Dimension screenSize = contPpal.getPanelDibujo().getSize();

		return ContUtils.worldToPixel(c, mapExtent, screenSize);
	}


	public GeneralPath worldToPixel(Geometry g){
		if (g.getNumPoints() < 2){
			throw new IllegalArgumentException("La geometria no puede ser de punto");
		}
		
		Envelope mapExtent = modelData.getBbox();
		java.awt.Dimension screenSize = contPpal.getPanelDibujo().getSize();
		Coordinate coords[] = g.getCoordinates();
		Point2D pInit = ContUtils.worldToPixel(coords[0], mapExtent, screenSize);
		Point2D p0 = pInit;
		Point2D p1 = ContUtils.worldToPixel(coords[1], mapExtent, screenSize);
		GeneralPath res = new GeneralPath(new Line2D.Double(p0, p1));
		
		if (g.getNumPoints() > 2){
			for (int i = 2; i < coords.length; i++){
				p0 = p1;
				p1 = ContUtils.worldToPixel(coords[i], mapExtent, screenSize);
				res.append(new Line2D.Double(p0, p1), true);
			}
			
			p0 = p1;
			p1 = pInit;
			res.append(new Line2D.Double(p0, p1), true);			
		}
		
		return res;
	}
	
	/**
	 * Convierte una ubicacion en el mapa actual dada por sus desplazamientos
	 * x e y en pixels, a su representacion en coordenadas
	 * 
	 * @param x
	 *            Desplazamiento horizontal en pixels
	 * @param y
	 *            Desplazamiento vertical en pixels
	 * 
	 * @return Las coordenadas del punto indicado
	 */
	public Coordinate pixelToWorld(double x, double y) {
		return ContUtils.pixelToWorld(x, y, modelData.getBbox(), contPpal
				.getPanelDibujo().getSize());
	}
	
	public double scaleToWorld(double distance) {
		return ContUtils.scaleToWorld(distance, modelData.getBbox(), contPpal.getPanelDibujo().getSize());
	}


	/*
	 * -----------------------------------------------
	 * MeTODOS PARA REALIZAR
	 * LA NAVEGACION POR EL MAPA
	 * -----------------------------------------------
	 */

	
	public void setBoundingBox(Envelope bbox) {
		// Paso las coordenadas de ediciin a coordenadas mundiales antes de
		// moverme

		contPpal.getPanelDibujo().setTmpShape(null);

		// Actualizo el bounding box
		try {
			modelData.setBbox(bbox);

			logger.debug("Escala del mapa: " + getScale());
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public Envelope getBoundingBox() {
		return modelData.getBbox();
	}
	

	
	public void setScale(long scale) {
		Envelope newBBox = ContUtils.centerAt(modelData.getBbox().centre(), contPpal.getPanelDibujo().getSize(), scale);
		
		try {
			AppContext appContext = AppContext.getInstance();
			appContext.getCoreAccess().getNavigationController().setPreviousBoundingBox();
			modelData.setBbox(newBBox);
		}
		catch (Exception e) {
			contPpal.showError("No se pudo modificar la escala", e);
		}
	}
	
	public long getScale() {
		return ContUtils.getScale(modelData.getBbox(), contPpal.getPanelDibujo().getSize());
	}
	
	
	/**
	 * Actualiza lo que ???
	 * 
	 */
	public void refresh() {
		modelData.refreshViews(true);
	}

	
	public Coordinate[] toArrayWorld(ArrayList coordsEdicion) {
		int i = 0;
		Point2D point;
		Coordinate c[] = new Coordinate[coordsEdicion.size()];

		for (java.util.Iterator it = coordsEdicion.iterator(); it.hasNext(); i++) {
			point = (Point2D) it.next();
			c[i] = pixelToWorld(point.getX(), point.getY());
		}

		return c;
	}

	public Feature getFeatureOn(String type, Coordinate c) throws Exception {
		
		DataStore ds = modelData.getDataStore();

		GeometryFactory geomFactory = modelData.getGeometryFactory();
		FeatureReader fr = ContUtils.elementsAt(ds, type, geomFactory.createPoint(c).buffer(getAccuracyTolerance()));
		
		try {
			if (fr.hasNext())
				return fr.next();
			else
				return null;
		}
		catch (Exception e) {
			logger.debug(e.getMessage());
		}
		finally {
			try {
				fr.close();
			}
			catch (Exception ex) {
				logger.debug(ex.getMessage());
			}
		}
		
		return null;
	}
	
	public Collection<Feature> getAllFeaturesOn(Coordinate c) throws Exception {
		DataStore ds = modelData.getDataStore();
		GeometryFactory geomFactory = modelData.getGeometryFactory();
		Collection<Feature> res = null;
		List<String> types = modelData.getNotEmptyTypesAndVisibleTypes();
		String type;
		
		for (int i = types.size() - 1; i >= 0; i--) {
			type = types.get(i);
			res = getAllFeaturesOn(type, c, ds, geomFactory);
			
			if (res != null && !res.isEmpty())
				break;
		}
		
		return res;
	}

	public Collection<Feature> getAllFeaturesOn(String type, Coordinate c) throws Exception {
		DataStore ds = modelData.getDataStore();
		GeometryFactory geomFactory = modelData.getGeometryFactory();
		return getAllFeaturesOn(type, c, ds, geomFactory);
	}

	private Collection<Feature> getAllFeaturesOn(String type, Coordinate c, DataStore ds, GeometryFactory geomFactory) throws Exception{
		FeatureReader fr = ContUtils.elementsAt(ds, type, geomFactory.createPoint(c).buffer(getAccuracyTolerance()));
		ArrayList<Feature> features = new ArrayList<Feature>();
		
		try {
			while (fr.hasNext()){ 
				features.add(fr.next());
			}
		} finally {
			try { fr.close(); }	catch (Exception ex) {
				logger.debug(ex.getMessage());
			}
		}
		
		return features;		
	}
	
	public Feature getFeatureOn(Coordinate c) throws Exception {
		List<String> layers = modelData.getNotEmptyTypesAndVisibleTypes();

		return getFeatureOn(layers.get(layers.size() - 1), c);
	}
	
	public double getAccuracyTolerance() {
		return scaleToWorld(bufferInPixels);
	}
	
	public void center(Geometry g){
		new CenterBlink(contPpal.getPanelDibujo(), g, true).start();
	}
	
	public void center(Coordinate coord) {
		//setBboxPrevious(getContMapa().getBoundingBox());
		final Envelope evp = ContUtils.centrar(getBoundingBox(), coord);

		new BlockingSwingWorker(contPpal.getPanelDibujo()) {
			Exception res = null;

			protected void doNonUILogic() throws RuntimeException {
				try {
					modelData.setBbox(evp);
				} catch (Exception e) {
					res = e;
				}
			}

			protected void doUIUpdateLogic() throws RuntimeException {
				if (res != null) {
					contPpal.getVistaPrincipal().showError("Centrando mapa", res);
				}
			}
		}.start();
	}


	public void addCoordinateListener(CoordinateListener listener) {
		contPpal.getContMousePanelDibujo().addCoordinateListener(listener);
	}


	public void removeCoordinateListener(CoordinateListener listener) {
		contPpal.getContMousePanelDibujo().removeCoordinateListener(listener);
	}


	public MapContext getContext() {
		return modelData.getContext();
	}
	
	private void flashGeometry(Geometry g){
		try{
			blinkShape = worldToPixel(g);
			veces = 0;
			blinkTimer.start();
		} catch (IllegalArgumentException e){
			logger.info(e);
		}
	}
	
	private class CenterBlink extends BlockingSwingWorker{
		private Geometry geometry;
		private boolean blink;
		private Envelope evp;
		private Exception res;
		
		public CenterBlink(Component comp, Geometry g, boolean b){
			super(comp);
			
			if (g instanceof Point){
				geometry = g.buffer(5);
			} else
				geometry = g;
			
			//logger.debug("La Clase es: "+ g.getClass());			
			
			blink = b;
//evp = ContUtils.centrar(getBoundingBox(), geometry.getCentroid().getCoordinate());
			
			evp = ContUtils.enfocar(getBoundingBox(), new Envelope(g.getEnvelopeInternal().getMinX() - 3*g.getEnvelopeInternal().getWidth()
																  ,g.getEnvelopeInternal().getMaxX() + 3*g.getEnvelopeInternal().getWidth()
																  ,g.getEnvelopeInternal().getMinY() - 3*g.getEnvelopeInternal().getHeight()
																  ,g.getEnvelopeInternal().getMaxY() + 3*g.getEnvelopeInternal().getHeight()));

		}
		
		protected void doNonUILogic() throws RuntimeException {
			try {
			//	if (getScale() > 1000) { // Si estoy muy lejos para ver el centrado, me acerco...
			//		setScale(1000);
			//	}
				
				modelData.setBbox(evp);
			} catch (Exception e) {
				res = e;
			}
		}

		protected void doUIUpdateLogic() throws RuntimeException {
			if (res != null) {
				contPpal.getVistaPrincipal().showError("Centrando mapa", res);
			} else if (blink){
				flashGeometry(geometry);
			}
		}
	}
	
		public void actionPerformed(ActionEvent e){
			if ((++veces % 2) == 0){
				contPpal.getIDrawPanel().setTmpShape(blinkShape, IDrawPanel.BLINK_LINE);
			} else {
				contPpal.getIDrawPanel().setTmpShape(blinkShape, IDrawPanel.NORMAL_LINE);					
			}
			contPpal.getIDrawPanel().updateLayer();
			
			if (veces > 8){
				veces = 0;
				blinkTimer.stop();
				contPpal.getIDrawPanel().setTmpShape(null, IDrawPanel.NORMAL_LINE);					
			}			
		}
}
 