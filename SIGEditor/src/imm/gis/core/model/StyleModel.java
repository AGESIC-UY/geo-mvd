package imm.gis.core.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactoryImpl;

public class StyleModel {
	static private Logger log = Logger.getLogger(StyleModel.class.getName());
	static public final String DEFAULT = "DEFAULT";
	private Map<String, Map<String, Style>> styles = new HashMap<String, Map<String, Style>>();
	private Map<String, String> currentStyles = new HashMap<String, String>();
	private MapContext mapContext;

	public StyleModel() {

	}

	public StyleModel(MapContext context) {
		mapContext = context;
	}

	public void setDefaultStyle(String layer, Style style) {
		setNamedStyle(layer, DEFAULT, style);
	}

	public Style getDefaultStyle(String layer) {
		return getNamedStyle(layer, DEFAULT);
	}

	public void setCurrentStyle(String layer, String name) throws Exception {
		Map m = styles.get(layer);
		if (!m.keySet().contains(name))
			throw new IllegalArgumentException("La capa " + layer
					+ " no tiene el estilo " + name);
		log.info("Seteando estilo actual de capa " + layer + ": " + name);
		currentStyles.put(layer, name);
	}

	public void updateCurrentStyle(String layer, Style s) {
		Map<String, Style> m = styles.get(layer);
		String styleName = (String) currentStyles.get(layer);
		log.info("Actualizando estilo " + styleName + " de capa " + layer);
		m.put(styleName, s);
		MapLayer mapLayer = getMapLayer(layer);
		if (mapLayer != null) {
			mapLayer.setStyle(s);
		}
	}

	private MapLayer getMapLayer(String layerName) {
		MapLayer layers[] = mapContext.getLayers();
		for (int i = 0; i < layers.length; i++) {
			if (layers[i].getTitle().equals(layerName)) {
				return layers[i];
			}
		}

		return null;
	}

	public Style getCurrentStyle(String layer) {
		return getNamedStyle(layer, (String) currentStyles.get(layer));
	}

	public String getCurrentStyleName(String layer) {
		return (String) currentStyles.get(layer);
	}

	public void setNamedStyle(String layer, String name, Style style) {
		Map<String, Style> map = styles.get(layer);
		if (map == null) {
			map = new HashMap<String, Style>();
			styles.put(layer, map);
			currentStyles.put(layer, DEFAULT);
		}

		map.put(name, style);
	}

	public Style getNamedStyle(String layer, String name) {
		Style res = null;

		try {
			res = (Style) ((Map) styles.get(layer)).get(name);
		} catch (NullPointerException e) {
		}

		return res;
	}

	public String[] getUserStyles(String layer) {
		List<String> res = new ArrayList<String>();
		Map<String, Style> m = styles.get(layer);

		res.addAll(m.keySet());
		res.remove(DEFAULT);

		return (String[]) res.toArray(new String[res.size()]);
	}

	public boolean isInDefaultStyle(String layer) {
		return getCurrentStyleName(layer).equals(DEFAULT);
	}

	public boolean containsStyle(String layer, String style) {
		Map m = (Map) styles.get(layer);

		return m.containsKey(style);
	}

	public void setMapContext(MapContext mapContext) {
		this.mapContext = mapContext;
	}

	public void parseStyles(Map<String, String> styles) {
		String types[] = (String[]) styles.keySet().toArray(
				new String[styles.size()]);
		SLDParser sldParser = new SLDParser(new StyleFactoryImpl());
		String sldFile;
		Style style = null;
		FeatureTypeStyle fts;

		// Cargando estilos de usuario del directorio user
		// Se asume que fueron creados por modificacion de los estilos por
		// defecto y entonces ya tienen la regla para features eliminados
		log.info("cargando estilos personalizados");
		java.io.File dir = new java.io.File("user/");
		java.io.FilenameFilter fileNameFilter = new java.io.FilenameFilter() {
			public boolean accept(java.io.File file, String name) {
				return name.toLowerCase().endsWith(".sld");
			}
		};

		String userStyles[] = dir.list(fileNameFilter);

		if (userStyles != null) {
			String userSldFileName;
			String userStyleName;
			String featureType;
			java.io.File file;
			for (int j = 0; j < userStyles.length; j++) {
				userSldFileName = userStyles[j];
				userStyleName = userSldFileName.split("[.]")[0];
				file = new java.io.File("user/");
				try {
					style = loadStyle(sldParser, file.toURI().toString(),
							userSldFileName);
					fts = style.getFeatureTypeStyles()[0];
					featureType = fts.getFeatureTypeName();
					setNamedStyle(featureType, userStyleName, style);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else
			log.info("No hay estilos personalizados");

		// cargando estilos por defecto del directorio xml
		log.info("Cargando estilos por defecto");
		for (int i = 0; i < types.length; i++) {
			sldFile = styles.get(types[i]);
			if (sldFile != null) {
				log.debug("Parseando estilo para capa " + types[i]);
				style = loadStyle(sldParser, sldFile);
			}
			setDefaultStyle(types[i], style);

			// Verifico si el usuario tiene una preferencia por el estilo
			imm.gis.conf.PrefManager prefs = imm.gis.conf.PrefManager
					.getInstance();
			String stylePref = prefs.getString("Style" + types[i]);
			if (stylePref != null && stylePref.length() > 0) {
				log.info("Capa " + types[i]
						+ " tiene preferencia del usuario para estilo "
						+ stylePref);
				try {
					setCurrentStyle(types[i], stylePref);
				} catch (Exception e) {
					log
							.info("Ese nombre de estilo no existe en el modelo de estilos, usando estilo por defecto...");
					e.printStackTrace();
				}
			}

//			fts = style.getFeatureTypeStyles()[0];
		}
	}

	private Style loadStyle(SLDParser styleReader, String sldFile){
		Style s = null;

		StringReader sr = new StringReader(sldFile);
		styleReader.setInput(sr);
		s = styleReader.readXML()[0];
		sr.close();
		sr = null;

		return s;
	}

	  private Style loadStyle(SLDParser styleReader, String uri, String
			  sldFile) { 
		  Style s = null;
			  
			  log.info("Cargando " + uri + sldFile);
			  java.net.URL url;
			try {
				url = new java.net.URL(uri + sldFile);
				styleReader.setInput(url);
			  s = styleReader.readXML()[0];
			} catch (Exception e) {
				e.printStackTrace();
			}
			  
			  return s; 
	  }	
}
