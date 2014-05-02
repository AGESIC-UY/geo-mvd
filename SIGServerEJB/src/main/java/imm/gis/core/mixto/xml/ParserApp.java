package imm.gis.core.mixto.xml;

import imm.gis.core.feature.FeatureReferenceAttribute;
import imm.gis.core.feature.ExternalAttribute;
import imm.gis.core.feature.MixtoAttributeTypeFactory;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.definition.LayerAttributeDefinition;
import imm.gis.core.layer.definition.LayerDefinition;
import imm.gis.core.layer.metadata.ChildMetadata;
import imm.gis.core.layer.metadata.LayerAttributeMetadata;
import imm.gis.core.layer.metadata.LayerAttributePresentation;
import imm.gis.core.layer.metadata.LayerMetadata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.geotools.feature.AttributeType;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.SchemaException;
import org.xml.sax.XMLReader;

import com.vividsolutions.jts.geom.Geometry;

public class ParserApp {
	private XMLReader parser = new SAXParser();
	private DefinicionAplicacion def = null;
	private String uri;
	
	private static Logger logger = Logger.getLogger(ParserApp.class.getName());
	
	public ParserApp(String u) {
		uri = u;
	}

	public void load(String app) throws Exception {
		AppContentHandler handlerApp = new AppContentHandler();

		logger.debug("ParserApp::load() - Parseando " + uri + app);
		
		parser.setContentHandler(handlerApp);
		parser.parse(uri + app);
		def = handlerApp.getDef();
		
		MixtoContentHandler handlerMixto = new MixtoContentHandler();
		
		MetadataContentHandler handlerMetadata;
		LayerMetadata layerMetadata;
		
		LayerDefinition layerDefinition;
		
		Layer layer;
		
		Map<String, LayerDefinition> layerDefinitions = new HashMap<String, LayerDefinition>();
		
		for (Iterator<Layer> layers = def.getLayers(); layers.hasNext();) {
			layer = layers.next();
			
			logger.debug(uri + layer.getDataFile());
		
			parser.setContentHandler(handlerMixto);
			parser.parse(uri + layer.getDataFile());
			
			layerDefinition = handlerMixto.getLayerDefinition(); 
			
			layerDefinitions.put(layer.getNombre(), layerDefinition);
			
			layer.setFt(createFeatureType(layerDefinition));
			
			
			layerMetadata = createDefaultMetadata(layerDefinition);
			
			// Cargo, de haberla, la metadata para la capa
			if (layer.getMetadataFileName() != null) {
				
				handlerMetadata = new MetadataContentHandler(layerMetadata);
				
				parser.setContentHandler(handlerMetadata);
				parser.parse(uri + layer.getMetadataFileName());
				layerMetadata = handlerMetadata.getLayerMetadata();
			}
			
			layer.setMetadata(layerMetadata);
		}
		
		// Seteo el tema de las capas hijas (poner isChild donde corresponda)
		for (Iterator<Layer> layers = def.getLayers(); layers.hasNext();) {
			layerMetadata = layers.next().getMetadata();
			
			Iterator<ChildMetadata> childIterator = layerMetadata.getChildrenMetadata().iterator();
			
			while (childIterator.hasNext())
				def.getLayer(childIterator.next().getLayerName()).getMetadata().setChild(true);
		}
		
		def.setLayerDefinitions(layerDefinitions);
		
		handlerApp = null;
		handlerMixto = null;
		layer = null;
	}

	
	private LayerMetadata createDefaultMetadata(LayerDefinition layerDefinition) {
		
		Map<String, LayerAttributeMetadata> attributesMetadata = new HashMap<String, LayerAttributeMetadata>();
		
		LayerAttributeDefinition attributeDefinition;
		
		LayerMetadata layerMetadata = new LayerMetadata();
		
		layerMetadata.setName(layerDefinition.getName());
		layerMetadata.setUserIdAttribute(null);
		
		LayerAttributeMetadata attributeMetadata;
		LayerAttributePresentation attributePresentation;
		
		Iterator<LayerAttributeDefinition> i = layerDefinition.getLayerAttributeDefinitions().values().iterator();
		
		while (i.hasNext()) {
			attributeDefinition = i.next();
			
			attributePresentation = new LayerAttributePresentation();
			
			attributeMetadata = new LayerAttributeMetadata();
			attributeMetadata.setName(attributeDefinition.getName());
			attributeMetadata.setReadOnly(false);
			attributeMetadata.setUsage(LayerAttributeMetadata.ATTR_USAGE_NORMAL);
			attributeMetadata.setReferencedLayers(null);
			attributeMetadata.setLazy(false);
			attributeMetadata.setPresentation(attributePresentation);
			attributeMetadata.setQueryCapable(true);
			attributeMetadata.setShow(true);
			attributesMetadata.put(attributeMetadata.getName(), attributeMetadata);
		}
		
		layerMetadata.setAttributesMetadata(attributesMetadata);
		
		return layerMetadata;
	}
	
	
	private FeatureType createFeatureType(LayerDefinition layerDefinition)
			throws SchemaException {
		
		LayerAttributeDefinition attributeDefinitions[] = layerDefinition.getOrderedLayerAttributeDefinitions();
		
		// Creo el AttributeType para cada atributo de la capa
		
		AttributeType atributos[] = new AttributeType[layerDefinition.getLayerAttributeDefinitions()
				.values().size()];

		LayerAttributeDefinition def_attr = null;
		Class<?> cl;
		
		for (int i = 0; i < attributeDefinitions.length; i++) {
			
			def_attr = attributeDefinitions[i];
			
			try {
				
				cl = Thread.currentThread().getContextClassLoader().loadClass(def_attr.getType());
				
				atributos[i] = MixtoAttributeTypeFactory.newAttributeType(
						def_attr.getName(),
						cl,
						Geometry.class.isAssignableFrom(cl) || def_attr.isNillable(),
						0,
						createDefaultValue(cl, def_attr));
			}
			catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("El tipo del atributo "	+ def_attr.getName() + " no es valido");
			}
		}

		// Creo el FeatureType para la capa
		FeatureType ft = FeatureTypeBuilder.newFeatureType(atributos, layerDefinition.getName());

		return ft;
	}

	public static Object createDefaultValue(Class<?> cl, LayerAttributeDefinition attributeDefinition) {
		
		if (Float.class.isAssignableFrom(cl))
			return (attributeDefinition.isNillable() ? null : new Float(0));
		else if (Number.class.isAssignableFrom(cl))
			return (attributeDefinition.isNillable() ? null : new Integer(0));
		else if (String.class.isAssignableFrom(cl))
			return (attributeDefinition.isNillable() ? null : "");
		else if (java.util.Date.class.isAssignableFrom(cl))
			return (attributeDefinition.isNillable() ? null : new java.util.Date());
		else if (ExternalAttribute.class.isAssignableFrom(cl))
			return new ExternalAttribute("","", attributeDefinition.getReferencedLayer());
		else if (FeatureReferenceAttribute.class.isAssignableFrom(cl))
			return new FeatureReferenceAttribute("", attributeDefinition.getReferencedLayer());
		else if (java.sql.Time.class.isAssignableFrom(cl))
			return (attributeDefinition.isNillable() ? null : 
					new java.sql.Time((new java.util.Date()).getTime()));
		else
			return null;
	}

	public DefinicionAplicacion getDef() {
		return def;
	}
}
