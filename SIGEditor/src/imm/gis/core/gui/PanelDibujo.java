package imm.gis.core.gui;

import imm.gis.core.controller.ContUtils;
import imm.gis.core.feature.Util;
import imm.gis.core.interfaces.IDrawPanel;
import imm.gis.core.interfaces.ISelection;
import imm.gis.core.model.ModelData;
import imm.gis.core.model.selection.FeatureSelectionEvent;
import imm.gis.core.model.selection.FeatureSelectionListener;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;

import org.geotools.feature.Feature;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.LabelCacheDefault;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.renderer.lite.StreamingRenderer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class PanelDibujo extends JPanel implements ActionListener,
		FeatureSelectionListener, IDrawPanel {
	private static final long serialVersionUID = 1L;
	public static final int DRAG_IMAGE_BUFFER_FACTOR = 2;
	private GTRenderer renderer;
	private BufferedImage image;
	private Graphics2D graphics;
	private Raster originalData = null;
	private Rectangle r = new Rectangle();
	private Rectangle r1 = new Rectangle();
	private Shape tmpShape = null;
	private Stroke dashedLineStroke = null;
	private Stroke blinkStroke = null;
	private int tmpLineType = NORMAL_LINE;
	private int zeroCoordX, zeroCoordY;
	private int imageXOffset, imageYOffset; // Offset dentro de la imagen para mostrar el BB
	private ISelection selectionModel;
	private ModelData model;
	private LabelCacheDefault cache;
	private boolean useDragCache = false;
//	private Logger log = Logger.getLogger(PanelDibujo.class.getName());
	
	public PanelDibujo(ModelData model, ISelection selectionModel) {
		this.model = model;
		this.selectionModel = selectionModel;
		
		renderer = new StreamingRenderer();
		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		renderer.setJava2DHints(hints);

		Map<String, Object> rendererParams = new HashMap<String, Object>();
		 rendererParams.put("dpi", Integer.valueOf((Double.valueOf(ContUtils.dpi).intValue())));
		 rendererParams.put("optimizedDataLoadingEnabled",new Boolean(true) );

		 // Seteo el cache de etiquetas con GROUPING en TRUE para que 
		 // no ocurra el efecto de que no limpia correctamente las etiquetas
		 cache = new LabelCacheDefault();
		 cache.DEFAULT_GROUP = true;
		 rendererParams.put(StreamingRenderer.LABEL_CACHE_KEY, cache);
		 
		 renderer.setRendererHints(rendererParams);

		dashedLineStroke = new BasicStroke(1.0f,// Width
				BasicStroke.CAP_SQUARE, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f, // Miter limit
				new float[] { 10.0f, 10.0f }, // Dash pattern
				0.0f); // Dash phase
		blinkStroke = new BasicStroke(3.0f,// Width
				BasicStroke.CAP_SQUARE, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f, // Miter limit
				new float[] { 10.0f, 10.0f }, // Dash pattern
				0.0f);
		image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
	}

	public void actionPerformed(ActionEvent e) {
		if (!r.getSize().equals(getSize())) {
			resizeImage(useDragCache);
		}
		try {
/*	
		ReferencedEnvelope re = model.getContext().getAreaOfInterest();
			
			if (!r.getSize().equals(getSize())) {
				resizeImage(useDragCache);
			}
			
			graphics.setColor(java.awt.Color.LIGHT_GRAY);
			graphics.fillRect(0, 0, r1.width, r1.height);
*/
			graphics.setColor(java.awt.Color.LIGHT_GRAY);
			graphics.fillRect(0, 0, r1.width, r1.height);
			ReferencedEnvelope re = model.getContext().getAreaOfInterest();
			renderer.setContext(model.getContext());
			renderer.paint(graphics, r1, re);				
			
			// Guardo una copia del raster para volver a la situacion
			// inicial sin tener que regenerar el mapa
			originalData = image.getData();
			
			paintSelected(model.getNotEmptyTypes(),
				graphics,
				RendererUtilities.worldToScreenTransform(model.getContext().getAreaOfInterest(), r1));
		} catch (Exception ioe) {
			ioe.printStackTrace();
		} finally {
			imageXOffset = imageYOffset = 0;
			cache.clear();
			repaint();
		}
	}

	private void resizeImage(boolean useDragCache){
		r.setSize(getSize());
		r1.setSize(useDragCache == true ? r.width * DRAG_IMAGE_BUFFER_FACTOR : r.width, 
				useDragCache == true ? r.height * DRAG_IMAGE_BUFFER_FACTOR : r.height);

		if (graphics != null){
			graphics.dispose();
		}
		image = new BufferedImage(r1.width, r1.height,
				BufferedImage.TYPE_INT_RGB);
		graphics = (Graphics2D) image.getGraphics();
		zeroCoordX = (useDragCache == true) ? (r1.width / (DRAG_IMAGE_BUFFER_FACTOR * 2)) * -1 : 0;
		zeroCoordY = (useDragCache == true) ? (r1.height / (DRAG_IMAGE_BUFFER_FACTOR * 2)) * -1 : 0;		
	}
	
	private void paintSelected(String types[], Graphics2D graphics, AffineTransform af) {
		Feature f;
		Coordinate coords[];
		Coordinate c;
		java.awt.geom.Point2D point = null;

		for (int i = 0; i < types.length; i++) {
			try {
				if (model.isEnabledLayer(types[i])) {
					Iterator it = selectionModel.getSelected(types[i]).iterator();
					
					while (it.hasNext()) {
						f = (Feature) it.next();
						coords = f.getDefaultGeometry().getCoordinates();
						for (int j = 0; j < coords.length; j++) {
							c = coords[j];

							point = af.transform(
									new java.awt.geom.Point2D.Double(c.x, c.y),
									null);

							if (r1.contains(point)) {	
								//paintVertex(graphics, point);
								
								if (coords.length == 1)
									paintPoint(graphics, point);
								else
									paintVertex(graphics, point);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//Metodo nuevo
	public void paintGeometry(Geometry geometry) {
		if (!r.getSize().equals(getSize())) {
			resizeImage(useDragCache);
		}
		
		try {
			graphics.setColor(java.awt.Color.LIGHT_GRAY);
			graphics.fillRect(0, 0, r1.width, r1.height);
			ReferencedEnvelope re = model.getContext().getAreaOfInterest();
			renderer.setContext(model.getContext());
			renderer.paint(graphics, r1, re);				
			
			//De aca para adelante es nuevo
			Coordinate coords[];
			Coordinate c;
			java.awt.geom.Point2D point = null;
			
			coords = geometry.getCoordinates();
			for (int j = 0; j < coords.length; j++) {
				c = coords[j];
			
				AffineTransform af = RendererUtilities.worldToScreenTransform(model.getContext().getAreaOfInterest(), r1);
				point = af.transform(
						new java.awt.geom.Point2D.Double(c.x, c.y),
						null);
									
				if (coords.length == 1)
					paintPoint(graphics, point);
				else
					paintVertex(graphics, point);			
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		} finally {
			imageXOffset = imageYOffset = 0;
			cache.clear();
			repaint();
		}
	}

	private void paintPoint(Graphics2D g, Point2D point) {
		Color verdeOscuro = new Color(21,185,40);
		Color verdeClaro = new Color(22,250,48);

		g.setColor(verdeClaro);
		
		Stroke stroke = new BasicStroke(2.0f,// Width
				BasicStroke.CAP_SQUARE, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f);
		
		g.setStroke(stroke);
		
		g.drawLine(Double.valueOf(point.getX()).intValue(),
				Double.valueOf(point.getY()).intValue() - 15,
				Double.valueOf(point.getX()).intValue(),
				Double.valueOf(point.getY()).intValue() -5);
		
		g.drawLine(Double.valueOf(point.getX()).intValue(),
				Double.valueOf(point.getY()).intValue() + 5,
				Double.valueOf(point.getX()).intValue(),
				Double.valueOf(point.getY()).intValue() + 15);
		
		g.drawLine(Double.valueOf(point.getX()).intValue() - 15,
				Double.valueOf(point.getY()).intValue(),
				Double.valueOf(point.getX()).intValue() -5,
				Double.valueOf(point.getY()).intValue());

		g.drawLine(Double.valueOf(point.getX()).intValue() + 5,
				Double.valueOf(point.getY()).intValue(),
				Double.valueOf(point.getX()).intValue() + 15,
				Double.valueOf(point.getY()).intValue());

		g.setColor(verdeOscuro);
		
		stroke = new BasicStroke(1.0f,// Width
				BasicStroke.CAP_SQUARE, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f);
		
		Ellipse2D ellipse = new Ellipse2D.Double(point.getX() - 7, point.getY() - 7, 14, 14);
		
		g.setStroke(stroke);
		g.draw(ellipse);	
		
		ellipse = new Ellipse2D.Double(point.getX() - 5, point.getY() - 5, 10, 10);
		g.draw(ellipse);
		
		ellipse = new Ellipse2D.Double(point.getX() - 6, point.getY() - 6, 12, 12);
		g.setColor(verdeClaro);
		g.draw(ellipse);


	}
	
	private void paintVertex(Graphics2D g, Point2D vertex) {
		Ellipse2D ellipse = new Ellipse2D.Double(vertex.getX() - 7, vertex.getY() - 7, 14, 14);
		
		Color verdeOscuro = new Color(21,185,40);
		Color verdeClaro = new Color(22,250,48);

		g.setColor(verdeOscuro);
		
		Stroke stroke = new BasicStroke(1.0f,// Width
				BasicStroke.CAP_SQUARE, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f);
		
		g.setStroke(stroke);
		g.draw(ellipse);		
		ellipse = new Ellipse2D.Double(vertex.getX() - 5, vertex.getY() - 5, 10, 10);
		g.draw(ellipse);
		
		ellipse = new Ellipse2D.Double(vertex.getX() - 6, vertex.getY() - 6, 12, 12);
		g.setColor(verdeClaro);
		g.draw(ellipse);
	}

	private void paintTemporalLine(Graphics2D g) {
		if (tmpShape != null) {
			Stroke actual = g.getStroke();
			Paint paintActual = g.getPaint();

			switch (tmpLineType) {
			case DASHED_LINE:
				g.setStroke(dashedLineStroke);
				break;
			case BLINK_LINE:
				g.setStroke(blinkStroke);
				break;
			default:
				break;
			}

			g.setPaint(Color.red);
			g.draw(tmpShape);
			g.setStroke(actual);
			g.setPaint(paintActual);
		}
	}

	public void update(Graphics g) {
		g.setColor(java.awt.Color.lightGray);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (image != null) {
			((Graphics2D)g).drawImage(image, zeroCoordX + imageXOffset, zeroCoordY + imageYOffset, this);
			paintTemporalLine((Graphics2D) g);
		}
	}

	public void paintComponent(Graphics g) {
		update(g);
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setTmpShape(Shape tmp) {
		setTmpShape(tmp, NORMAL_LINE);
	}

	public void setTmpShape(Shape tmp, int type) {
		tmpShape = tmp;
		tmpLineType = type;
	}

	public Shape getTmpShape() {
		return tmpShape;
	}

	public void selectionPerformed(FeatureSelectionEvent se) {
		if (se.getCommand() != FeatureSelectionEvent.SELECTED_LAYER) {
			if (image != null) {
				image.setData(originalData);
				Graphics2D g = (Graphics2D) image.getGraphics();
				String types[] = model.getNotEmptyTypes();

				paintSelected(types, g, RendererUtilities
						.worldToScreenTransform(model.getBbox(), r,Util.crs));
				g.dispose();
				repaint();
			}
		}
	}

	public void updateLayer() {
		repaint();
	}
	
	public void setImageOffset(int x, int y){
		imageXOffset = x;
		imageYOffset = y;
	}
	
	public int getXImageOffset(){
		return imageXOffset;
	}
	
	public int getYImageOffset(){
		return imageYOffset;
	}

	public boolean isUseDragCache() {
		return useDragCache;
	}

	public void setUseDragCache(boolean useDragCache) {
		this.useDragCache = useDragCache;
		resizeImage(useDragCache);
	}
}
