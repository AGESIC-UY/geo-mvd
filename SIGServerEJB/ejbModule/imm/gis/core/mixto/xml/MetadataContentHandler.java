package imm.gis.core.mixto.xml;

import java.util.ArrayList;
import java.util.List;

import imm.gis.core.layer.metadata.ChildMetadata;
import imm.gis.core.layer.metadata.LayerAttributeMetadata;
import imm.gis.core.layer.metadata.LayerAttributePresentation;
import imm.gis.core.layer.metadata.LayerMetadata;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class MetadataContentHandler implements ContentHandler {

	private LayerMetadata layerMetadata;
	private List<ChildMetadata> childMetadata;
	
	public MetadataContentHandler(LayerMetadata layerMetadata) {
		this.layerMetadata = layerMetadata;
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
	}

	public void endDocument() throws SAXException {
		layerMetadata.setChildrenMetadata(childMetadata);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
	}

	public void processingInstruction(String target, String data) throws SAXException {
	}

	public void setDocumentLocator(Locator locator) {
	}

	public void skippedEntity(String name) throws SAXException {
	}

	public void startDocument() throws SAXException {
		childMetadata = new ArrayList<ChildMetadata>();
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		if (localName.equals("layer")){
			layerMetadata.setUserIdAttribute(atts.getValue("user_id"));
			if (atts.getValue("fid_query_capable") != null){
				layerMetadata.setFIDQueryCapable(Boolean.valueOf(atts.getValue("fid_query_capable")).booleanValue());
				if(atts.getValue("fid_query_att_name") != null){
					layerMetadata.setFidAttributeName(atts.getValue("fid_query_att_name"));
				}
			}
		}
		else if (localName.equals("attribute")) {
			
			LayerAttributeMetadata lam = layerMetadata.getAttributeMetadata(atts.getValue("name"));
			
			if (atts.getValue("label") != null){
				lam.setLabel(atts.getValue("label"));
			}
			
			if (atts.getValue("read_only") != null)
				lam.setReadOnly(Boolean.valueOf(atts.getValue("read_only")).booleanValue());
			
			if (atts.getValue("order_by_description") != null){
				lam.setOrderByDescription(Boolean.parseBoolean(atts.getValue("order_by_description")));
			}
			
			if (atts.getValue("usage") != null) {
				
				String usage = atts.getValue("usage");
				
				if (usage.equals("NORMAL"))
					lam.setUsage(LayerAttributeMetadata.ATTR_USAGE_NORMAL);
				else if (usage.equals("FEATURE_REFERENCE")) {
					lam.setUsage(LayerAttributeMetadata.ATTR_USAGE_FEATURE_REFERENCE);
					
					if (atts.getValue("referenced_layer") == null)
						throw new SAXException(new IllegalArgumentException("The FEATURE_REFERENCE usage requires a referenced_layer attribute"));
					else
						lam.setReferencedLayers(atts.getValue("referenced_layer"));
				}
				else if (usage.equals("LOV"))
					lam.setUsage(LayerAttributeMetadata.ATTR_USAGE_LOV);
				else if (usage.equals("LOV_PANEL"))
					lam.setUsage(LayerAttributeMetadata.ATTR_USAGE_LOV_PANEL);
				else
					throw new SAXException(new IllegalArgumentException("Unknown attribute usage: "+usage));
			}
			
			if (atts.getValue("depends_on") != null) {
				String dependsOn[] = atts.getValue("depends_on").split(",");
				
				for (int i = 0; i < dependsOn.length; i++)
					if (dependsOn[i] != null && !dependsOn[i].equals(""))
						lam.addDependence(dependsOn[i]);
			}
			
			if (atts.getValue("show") != null) {
				lam.setShow(Boolean.valueOf(atts.getValue("show")).booleanValue());
			}
			
			if (atts.getValue("unique") != null)
				lam.getPresentation().setUnique(Boolean.parseBoolean(atts.getValue("unique")));
			
			if (atts.getValue("depends_locked") != null)
				lam.setLockedDependence(Boolean.parseBoolean(atts.getValue("depends_locked")));
			
			if (atts.getValue("locking") != null)
				lam.getPresentation().setLocking(Boolean.parseBoolean(atts.getValue("locking")));
			
			if (atts.getValue("presentation") != null) {
				
				LayerAttributePresentation lap = lam.getPresentation();;
				
				String presentation = atts.getValue("presentation");
				
				if (presentation.equals("DEFAULT"))
					lap.setType(LayerAttributePresentation.ATTR_PRESENTATION_DEFAULT);
				else if (presentation.equals("TEXT_AREA"))
					lap.setType(LayerAttributePresentation.ATTR_PRESENTATION_TEXT_AREA);
				else if (presentation.equals("URL"))
					lap.setType(LayerAttributePresentation.ATTR_PRESENTATION_URL);
				else
					throw new SAXException(new IllegalArgumentException("Unknown attribute presentation: "+presentation));
				
				if (atts.getValue("columns") != null)
					lap.setColumns(Integer.parseInt(atts.getValue("columns")));
				
				if (atts.getValue("rows") != null)
					lap.setRows(Integer.parseInt(atts.getValue("rows")));
			}
			
			if (atts.getValue("is_lazy") != null)
				lam.setLazy(Boolean.valueOf(atts.getValue("is_lazy")).booleanValue());
			if (atts.getValue("query_capable") != null)
				lam.setQueryCapable(Boolean.valueOf(atts.getValue("query_capable")).booleanValue());
		}
		else if (localName.equals("child")) {
			
			String layerName = atts.getValue("layer");
			String parentIdAttribute = atts.getValue("parent_id_attribute");
			
			if (layerName == null || layerName.equals("") ||
					parentIdAttribute == null || parentIdAttribute.equals(""))
				throw new SAXException(new IllegalArgumentException("Must specify valid 'layer' and 'parent_id_attribute' attributes"));
			
			childMetadata.add(new ChildMetadata(layerName, parentIdAttribute));
		}
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {
	}

	public LayerMetadata getLayerMetadata() {
		return layerMetadata;
	}
}
