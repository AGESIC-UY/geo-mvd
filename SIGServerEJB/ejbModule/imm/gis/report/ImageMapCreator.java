package imm.gis.report;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.opengis.filter.Filter;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;

import com.vividsolutions.jts.geom.Envelope;

public class ImageMapCreator {
	private MapContext context;

	private GTRenderer renderer;

	private DataStore dataStore;

	private String[] layers;

	private Style[] styles;

	private Filter[] filters;

	

	private static Logger log = Logger.getLogger(ImageMapCreator.class
			.getName());

	public ImageMapCreator(DataStore ds, String[] l, Style[] s, Filter[] fils)
			throws IOException {
		dataStore = ds;
		renderer = new StreamingRenderer();
		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		renderer.setJava2DHints(hints);
		context = new DefaultMapContext(imm.gis.core.feature.Util.crs);
		renderer.setContext(context);
		filters = fils;
		setContextData(l, s);
	}

	private void loadLayers() throws IOException {
		FeatureSource fs;
		FeatureCollection fc;
		context.clearLayerList();
		for (int i = 0; i < layers.length; i++) {
			// dataStore.getFeatureSource(arg0)
			fs = dataStore.getFeatureSource(layers[i]);
			fc = fs.getFeatures(filters[i]);

			context.addLayer(fc, styles[i]);
			// context.ad
		}
	}

	public void drawImage(BufferedImage image, Envelope bbox) {

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(java.awt.Color.white);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		context.setAreaOfInterest(bbox, context.getCoordinateReferenceSystem());
		renderer.paint(graphics, new Rectangle(image.getWidth(), image
				.getHeight()), context.getAreaOfInterest());
		graphics.dispose();

		log.info("Listo");
	}

	public void setContextData(String[] layers, Style[] styles)
			throws IOException {
		this.layers = layers;
		this.styles = styles;
		loadLayers();
	}

	public void drawImageToFile(String fileName, Rectangle imgSize,
			Envelope bbox) throws IOException {
		BufferedImage bi = new BufferedImage(imgSize.width, imgSize.height,
				BufferedImage.TYPE_INT_RGB);
		drawImage(bi, bbox);
		
		File file = new File(fileName);
		// ObjectO
		ImageIO.write(bi, "png", file);
	}

	
}
