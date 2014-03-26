package imm.gis.core.controller;

import imm.gis.AppContext;
import imm.gis.comm.IServerServices;
import imm.gis.comm.ServerServicesFactory;
import imm.gis.core.gui.GuiUtils;
import imm.gis.core.gui.VistaBusqueda;
import imm.gis.core.gui.worker.BlockingSwingWorker;
import imm.gis.core.model.FeatureHierarchyUtil;
import imm.gis.form.search.SearchCriteriaPanel;
import imm.gis.form.search.SearchFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.geotools.data.CollectionFeatureReader;
import org.geotools.data.FeatureReader;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;
import org.opengis.filter.Filter;
import org.geotools.filter.IllegalFilterException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class ContBusqueda {

	private ContPpal contPpal;

	private SearchFactory searchFactory;

	private VistaBusqueda vista;

	private FeatureCollection fcCapa1;

	private FeatureCollection fcCapa2;

	private SearchCriteriaPanel busquedaIzquierda;

	private SearchCriteriaPanel busquedaDerecha;

	private Logger logger = Logger.getLogger(ContBusqueda.class);

	IServerServices iss;
	
	FeatureHierarchyUtil fhu;
	Map chidlrenWithouGeom;

	public ContBusqueda(ContPpal p) {
		logger.debug("ConstBusqueda Constructor");
		contPpal = p;
		searchFactory = new SearchFactory(AppContext.getInstance().getLayers(),p);
		JFrame tmp = new JFrame();
		tmp.setIconImage(GuiUtils.loadIcon("Search16.gif").getImage());

		vista = new VistaBusqueda(tmp);

		vista.habilitarCentradoIzquierdo(false);
		vista.habilitarCentradoDerecho(false);
		vista.habilitarCombinacion(false);
		vista.setCantidadIzquierda(0);
		vista.setCantidadDerecha(0);
		vista.botonBuscarIzquierdo().setEnabled(false);
		vista.botonBuscarDerecho().setEnabled(false);
		vista.setLocationRelativeTo(p.getVistaPrincipal());
		iss = ServerServicesFactory.getServerServices();
		// TODO: Corregir este problema
		/*
		 * vista.setSize( (int)Math.round(p.getVistaPrincipal().getWidth() /
		 * 1.8), (int)Math.round(p.getVistaPrincipal().getHeight()/ 1.4) );
		 */

		// GuiUtils.centerWindow(p.getVistaPrincipal(), vista);
		cargarCapas();
		clean();
		listeners();
		fhu = new FeatureHierarchyUtil(p.getModel());
		chidlrenWithouGeom = new HashMap();
	}

	private void cargarCapas() {
		try {
			String types[] = searchFactory.getSearchableTypes();

			vista.setCapasIzquierda(types);
			vista.setCapasDerecha(types);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void listeners() {
		ActionListener close = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vista.setVisible(false);
			}
		};

		vista.addOkActionListener(close);

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		vista.getRootPane().registerKeyboardAction(close, stroke,
				JComponent.WHEN_IN_FOCUSED_WINDOW);

		vista.addComboIzquierdoListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(vista.comboIzquierdo());
			}
		});

		vista.addComboDerechoListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPanel(vista.comboDerecho());
			}
		});

		vista.addBuscarIzquierdoListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BlockingSwingWorker(vista) {
					protected void doNonUILogic() throws RuntimeException {
						buscar(vista.botonBuscarIzquierdo());
					}
				}.start();
			}
		});

		vista.addBuscarDerechoListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BlockingSwingWorker(vista) {
					protected void doNonUILogic() throws RuntimeException {
						buscar(vista.botonBuscarDerecho());
					}
				}.start();
			}
		});

		vista.addCentrarIzquierdoListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BlockingSwingWorker(vista) {
					protected void doNonUILogic() throws RuntimeException {
						centrar(vista.botonCentrarIzquierdo());
					}
				}.start();
			}
		});

		vista.addCentrarDerechoListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BlockingSwingWorker(vista) {
					protected void doNonUILogic() throws RuntimeException {
						centrar(vista.botonCentrarDerecho());
					}
				}.start();
			}
		});

		vista.addCombinarListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BlockingSwingWorker(vista) {
					Exception ex = null;

					protected void doNonUILogic() throws RuntimeException {
						try {
							combinar();
						} catch (Exception x) {
							ex = x;
							x.printStackTrace();
						}
					}

					protected void doUIUpdateLogic() throws RuntimeException {
						if (ex instanceof EmptyCombinationException) {
							JOptionPane.showMessageDialog(vista,
									"La combinacion no genero resultados...");
						} else if (ex != null) {
							logger.error(ex);
							JOptionPane.showMessageDialog(vista, ex,
									"Error combinando resultados",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}.start();
			}
		});
		
		vista.setLeftResultListener(new FeatureTableSelectionListener("LEFT"));
		vista.setRightResultListener(new FeatureTableSelectionListener("RIGHT"));
	}

	private void setPanel(javax.swing.JComboBox combo) {
		String type = (String) combo.getSelectedItem();

		if (type != null) {
			
			SearchCriteriaPanel searchPanel;

			try {

				searchPanel = searchFactory.getCriteriaPanel(type, vista);

				if (combo == vista.comboIzquierdo()) {
					vista.limpiarBusquedaIzquierda();
					busquedaIzquierda = searchPanel;
					vista.setBusquedaIzquierda(busquedaIzquierda);
					vista.habilitarBusquedaIzquierda(true);
					vista.botonBuscarIzquierdo().setEnabled(true);
					//vista.pn
				} else if (combo == vista.comboDerecho()) {
					vista.limpiarBusquedaDerecha();
					busquedaDerecha = searchPanel;
					vista.setBusquedaDerecha(busquedaDerecha);
					vista.habilitarBusquedaDerecha(true);
					vista.botonBuscarDerecho().setEnabled(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			vista.validate();
		//	vista.doLayout();
		} else {
			if (combo == vista.comboIzquierdo())
				vista.habilitarBusquedaIzquierda(false);
			else if (combo == vista.comboDerecho())
				vista.habilitarBusquedaDerecha(false);
		}
	}

	/**
	 * Invoca la busqueda en base a los criterios ingresados
	 * 
	 * @param but
	 *            Boton que se presiono, para saber cual de los dos conjuntos de
	 *            criterios usar
	 */
	private void buscar(javax.swing.JButton but) {
		// FeatureReader fr = null;

		if (logger.isDebugEnabled())
			logger.debug("Buscando...");

		FeatureCollection fc = FeatureCollections.newCollection();

		try {
			if (but == vista.botonBuscarIzquierdo()) {
				/*
				 * fr = new CollectionFeatureReader(iss.loadLayer(
				 * busquedaIzquierda.getFeatureType(), busquedaIzquierda
				 * .getFilter(), true), busquedaIzquierda .getFeatureType());
				 * 
				 * 
				 * while (fr.hasNext()) fc.add(fr.next()); fcCapa1 = fc;
				 */
				fcCapa1 = getFeatureCollectionWithGeom(busquedaIzquierda);
				fc = fcCapa1;

				vista.habilitarCentradoIzquierdo(!fc.isEmpty());
				vista.setCantidadIzquierda(fc.size());
			} else if (but == vista.botonBuscarDerecho()) {

				/*
				 * fr = new
				 * CollectionFeatureReader(iss.loadLayer(busquedaDerecha
				 * .getFeatureType(), busquedaDerecha.getFilter(), true),
				 * busquedaDerecha.getFeatureType());
				 * 
				 * 
				 * while (fr.hasNext()) fc.add(fr.next());
				 * 
				 * fcCapa2 = fc;
				 * 
				 */
				fcCapa2 = getFeatureCollectionWithGeom(busquedaDerecha);
				fc = fcCapa2;
				vista.habilitarCentradoDerecho(!fc.isEmpty());
				vista.setCantidadDerecha(fc.size());
			}

			if (fc.isEmpty()) {
				vista.habilitarCombinacion(false);
				JOptionPane.showMessageDialog(vista,
						"La consulta no obtuvo resultados");
			} else {
				vista.habilitarCombinacion(!(fcCapa1.isEmpty() || fcCapa2
						.isEmpty()));

				if (but == vista.botonBuscarIzquierdo())
					vista.setResultadosIzquierda(fc);
				else if (but == vista.botonBuscarDerecho())
					vista.setResultadosDerecha(fc);

				/*
				contPpal.getSelectionModel().unselectAll();
				
				Iterator i = fc.iterator();
				
				while (i.hasNext())
					contPpal.getSelectionModel().selectFeature((Feature) i.next());
				*/
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	/**
	 * Centra el mapa y cambia la dimension de este para abarcar todos los
	 * features seleccionados en la tabla correspondiente, o todos los features
	 * de la tabla si no se selecciono ninguno.
	 * 
	 * @param but
	 *            El boton de centrado que se presiono, para saber cual de los
	 *            dos resultados usar
	 */
	private void centrar(javax.swing.JButton but) {
		GeometryFactory gf = contPpal.getModel().getGeometryFactory();
		Envelope bbox = null;
		Geometry g;

		try {

			if (but == vista.botonCentrarIzquierdo())
				g = crearGeometria(gf, collectionToArray(fcCapa1, vista
						.resultadosIzquierda()));
			else if (but == vista.botonCentrarDerecho())
				g = crearGeometria(gf, collectionToArray(fcCapa2, vista
						.resultadosDerecha()));
			else
				return;

			bbox = g.getEnvelopeInternal();

			bbox = ContUtils.enfocar(contPpal.getContMapa().getBoundingBox(), bbox);

			//contPpal.getContAccionesNavegacion().setBboxPrevious(
			//		contPpal.getContMapa().getBoundingBox());
			contPpal.getContMapa().setBoundingBox(bbox);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void combinar() throws Exception {
		GeometryFactory gf = contPpal.getModel().getGeometryFactory();
		Geometry col1 = null;
		Geometry col2 = null;
		Geometry res = null;

		col1 = crearGeometria(gf, collectionToArray(fcCapa1, vista
				.resultadosIzquierda()));
		col2 = crearGeometria(gf, collectionToArray(fcCapa2, vista
				.resultadosDerecha()));

		if (vista.combinarElementos())
			res = combinarPorElementos(col1, col2);
		else
			res = combinarPorZona(gf, col1, col2);

		if (res.isEmpty())
			throw new EmptyCombinationException();

		//Cambios
		contPpal.getISelection().unselectAll();
		//Fin cambios
		Envelope e = res.getEnvelopeInternal();

		e = ContUtils.enfocar(contPpal.getContMapa().getBoundingBox(), e);

		//contPpal.getContAccionesNavegacion().setBboxPrevious(
		//		contPpal.getContMapa().getBoundingBox());
		contPpal.getContMapa().setBoundingBox(e);

		//Cambios
		contPpal.getIDrawPanel().paintGeometry(res);
		//Fin cambios
	}

	/**
	 * Devuelve la combinacion de dos geometrias, discriminando la operacion de
	 * combinacio en base a la opcion seleccionada por el usuario
	 * 
	 * @param g1
	 * @param g2
	 * 
	 * @return La interseccion o union de ambas geometrias
	 */
	private Geometry combinarPorElementos(Geometry g1, Geometry g2) {
		return (vista.combinarIntersectando()) ? g1.intersection(g2) : g1
				.union(g2);
	}

	private Geometry combinarPorZona(GeometryFactory gf, Geometry g1,
			Geometry g2) {
		Envelope env1 = g1.getEnvelopeInternal();
		Envelope env2 = g2.getEnvelopeInternal();

		Polygon poly1 = envelopeToPolygon(gf, env1);
		Polygon poly2 = envelopeToPolygon(gf, env2);

		return (vista.combinarIntersectando()) ? poly1.intersection(poly2)
				: poly1.union(poly2);
	}

	/**
	 * Convierte un Envelope al poligono equivalente
	 * 
	 * @param gf
	 *            GeometryFactory para construir el poligono
	 * @param e
	 *            Envelope del cual se desea obtener el poligono
	 * 
	 * @return El Envelope equivalente
	 */
	private Polygon envelopeToPolygon(GeometryFactory gf, Envelope e) {
		Coordinate[] coords = new Coordinate[] {
				new Coordinate(e.getMinX(), e.getMinY()),
				new Coordinate(e.getMinX(), e.getMaxY()),
				new Coordinate(e.getMaxX(), e.getMaxY()),
				new Coordinate(e.getMaxX(), e.getMinY()),
				new Coordinate(e.getMinX(), e.getMinY()) };

		return gf.createPolygon(gf.createLinearRing(coords), null);
	}

	/**
	 * Devuelve una lista con las geometrias de los features seleccionados de la
	 * lista de resultados
	 * 
	 * @param fc
	 *            Coleccion de features resultado de la busqueda
	 * @param tbl
	 *            Tabla de resultados, para obtener los features seleccionados
	 * @return
	 */
	private List<Geometry> collectionToArray(FeatureCollection fc, JTable tbl) {
		List<Geometry> res = null;

		if (tbl.getSelectedRowCount() > 0) {
			int col = 0;

			for (int i = 0; i < tbl.getColumnCount(); i++)
				if (tbl.getColumnName(i).equalsIgnoreCase("the_geom"))
					col = i;

			int[] rows = tbl.getSelectedRows();
			res = new ArrayList<Geometry>(rows.length);

			for (int i = 0; i < rows.length; i++)
				res.add((Geometry) tbl.getValueAt(rows[i], col));
		} else {
			FeatureIterator fi = fc.features();
			Feature f;
			res = new ArrayList<Geometry>(fc.size());

			for (int i = 0; i < fc.size(); i++) {
				f = fi.next();
				res.add(f.getDefaultGeometry());
			}
		}

		return res;
	}

	/**
	 * Crea una multigeometria del tipo adecuado a partir de una lista de
	 * geometrias del mismo tipo.
	 * 
	 * @param gf
	 *            GeometryFactory a utilizar para crear la geometria
	 * @param lista
	 *            Lista de geometrias a combinar
	 * @return Una MultiGeometria del tipo correcto
	 * @throws Exception
	 */
	private Geometry crearGeometria(GeometryFactory gf, List<Geometry> lista)
			throws Exception {
		Geometry res = null;
		String type = lista.get(0).getClass().getName();
		int len = lista.size();

		if (type.equals("com.vividsolutions.jts.geom.Point"))
			res = gf
					.createMultiPoint((com.vividsolutions.jts.geom.Point[]) lista
							.toArray(new com.vividsolutions.jts.geom.Point[len]));
		else if (type.equals("com.vividsolutions.jts.geom.Polygon"))
			res = gf
					.createMultiPolygon((com.vividsolutions.jts.geom.Polygon[]) lista
							.toArray(new com.vividsolutions.jts.geom.Polygon[len]));
		else if (type.equals("com.vividsolutions.jts.geom.LineString"))
			res = gf
					.createMultiLineString((com.vividsolutions.jts.geom.LineString[]) lista
							.toArray(new com.vividsolutions.jts.geom.LineString[len]));
		else if (type.equals("com.vividsolutions.jts.geom.MultiPoint")) {

			res = gf
					.createMultiPoint(new com.vividsolutions.jts.geom.Point[] {});

			for (Iterator it = lista.iterator(); it.hasNext();)
				res = res.union((com.vividsolutions.jts.geom.MultiPoint) it
						.next());

		} else if (type.equals("com.vividsolutions.jts.geom.MultiLineString")) {

			res = gf
					.createMultiLineString(new com.vividsolutions.jts.geom.LineString[] {});

			for (Iterator it = lista.iterator(); it.hasNext();)
				res = res
						.union((com.vividsolutions.jts.geom.MultiLineString) it
								.next());

		} else {
			throw new RuntimeException(
					"Geometria no soportada en la combinacion de busquedas : "
							+ type);
		}

		return res;
	}

	public void show() {
		clean();
		vista.setVisible(true);
	}

	private void clean() {
		vista.limpiarResultadosIzquierda();
		vista.limpiarResultadosDerecha();
		fcCapa1 = FeatureCollections.newCollection();
		fcCapa2 = FeatureCollections.newCollection();
		vista.habilitarCombinacion(false);
		vista.habilitarCentradoIzquierdo(false);
		vista.habilitarCentradoDerecho(false);
	}

	private class EmptyCombinationException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	private FeatureCollection getFeatureCollectionWithGeom(
			SearchCriteriaPanel panel) {
		FeatureReader fr;
		FeatureCollection fc = FeatureCollections.newCollection();
		Feature tmpFeature;

		try {
			Filter fil = panel.getFilter();
			logger.info("Filtro busqueda: " + fil.toString());
			fr = new CollectionFeatureReader(iss.loadLayer(panel
					.getFeatureType(), fil, false), panel
					.getFeatureType());
			
			while(fr.hasNext()){
				// ME fijo si la coleccion es de Feature sin geometria
				tmpFeature = fr.next();
				if (tmpFeature.getDefaultGeometry() == null){
					addFeatureWithoutGeom(tmpFeature, fc);
				}else {
					fc.add(tmpFeature);
				}
				
			}
			
		} catch (IllegalFilterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fc;

	}

	private static FeatureType addGeom(FeatureType ft, Class geomClazz)
			throws SchemaException {
		FeatureTypeBuilder ftf = FeatureTypeBuilder.newInstance(ft
				.getTypeName());
		ftf.importType(ft, false);

		ftf.addType(AttributeTypeFactory.newAttributeType("the_geom",
				geomClazz, false));

		return ftf.getFeatureType();

	}

	private void addFeatureWithoutGeom(Feature childFeature, FeatureCollection fc)
			throws SchemaException, IllegalAttributeException {
		Feature nearestP = fhu.getNearestParentWithGeometry(childFeature);
		
		if(nearestP == null) return;// si no encontre padre con geom no nula no agrego nada.
		chidlrenWithouGeom.put(childFeature.getID(), nearestP);
		Geometry geom2add = nearestP.getDefaultGeometry();
		// Creo un feauture type on the fly con geom
		FeatureType tmpFtWithGeom = addGeom(childFeature.getFeatureType(),
				com.vividsolutions.jts.geom.Point.class);
		Object[] oldAttributes = childFeature.getAttributes(null);
		Object[] newAttrbutes = new Object[oldAttributes.length + 1];
		System.arraycopy(oldAttributes, 0, newAttrbutes, 0, oldAttributes.length);
		newAttrbutes[oldAttributes.length] = geom2add;
		Feature tmpFeatureWithGeom = tmpFtWithGeom.create(newAttrbutes,childFeature.getID());
		fc.add(tmpFeatureWithGeom);
	}
	
	private class FeatureTableSelectionListener implements ListSelectionListener {

		private String cualUsar = null;
		
		public FeatureTableSelectionListener(String cualUsar) {
			this.cualUsar = cualUsar;
		}
		
		public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            
            Feature featureArray[];
            int rows[];
            
            // Deselecciono todo en el mapa
            contPpal.getSelectionModel().unselectAll();
            
            if (cualUsar.equals("LEFT")) {
                rows = vista.resultadosIzquierda().getSelectedRows();
            	featureArray = (Feature[]) fcCapa1.toArray(new Feature[fcCapa1.size()]);
            }
            else {
            	rows = vista.resultadosDerecha().getSelectedRows();
            	featureArray = (Feature[]) fcCapa2.toArray(new Feature[fcCapa2.size()]);
            }
            
            // Selecciono en el mapa los seleccionados en la tabla
            for (int i = 0; i < rows.length; i++){
            	Feature currentF = featureArray[rows[i]];
            	if(chidlrenWithouGeom.containsKey(currentF.getID())){
            		//si el Feature seleccionado no tiene geometria selecciono
            		//el padre con gemoetria mas cercano a el.
            		currentF = (Feature)chidlrenWithouGeom.get(currentF.getID());
            	}
                	contPpal.getSelectionModel().selectFeature(currentF);
            }
            	
            	
		}
		
	}
}
