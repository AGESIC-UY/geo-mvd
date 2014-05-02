package imm.gis.core.mixto.xml;

import imm.gis.core.layer.definition.DataOriginDefinition;
import imm.gis.core.layer.definition.DataSourceDefinition;
import imm.gis.core.layer.definition.LayerAttributeDefinition;
import imm.gis.core.layer.definition.LayerDefinition;
import imm.gis.core.layer.definition.TableAttributeDefinition;
import imm.gis.core.layer.definition.TableDefinition;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class MixtoContentHandler implements ContentHandler {
	private LayerDefinition layerDefinition = null;
	private DataOriginDefinition dataOriginDefinition = null;
	private TableDefinition tableDefinition = null;

	public void setDocumentLocator(Locator locator) {
	}
	
	public void startDocument() throws SAXException {
		this.layerDefinition = new LayerDefinition();
	}
	
	public void endDocument() throws SAXException {
	}
	
	public void processingInstruction(String target, String data) throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri) {
	}
	
	public void endPrefixMapping(String prefix) {
	}
	
	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes atts) throws SAXException {
		
		
		if (localName.equals("capa")) {
			this.layerDefinition.setName(atts.getValue("nombre"));
			
			if (atts.getValue("read_only") != null)
				this.layerDefinition.setReadOnly(Boolean.valueOf(atts.getValue("read_only")).booleanValue());
		}
		else if (localName.equals("atributo")) {

			// Obtengo el nombre y el tipo del atributo
			
			String name = atts.getValue("nombre");
			String type = atts.getValue("tipo");
			String referencedLayer = atts.getValue("capa_referenciada");
			boolean isNillable = Boolean.valueOf(atts.getValue("nillable")).booleanValue();			
			layerDefinition.addLayerAttributeDefinition(new LayerAttributeDefinition(name,type,referencedLayer,isNillable));
		}
		else if (localName.equals("origen_datos")) {
			this.dataOriginDefinition = new DataOriginDefinition();
		}
		else if (localName.equals("fuente"))
		{
			this.dataOriginDefinition.setDataSourceDefinition(
					new DataSourceDefinition(
							atts.getValue("nombre"),
							atts.getValue("tipo"),
							atts.getValue("proxy") == null ? false : new Boolean((String)atts.getValue("proxy"))
					)
			);
		}
		else if (localName.equals("tabla")){
			tableDefinition = new TableDefinition();
			tableDefinition.setSchema(atts.getValue("esquema") == null ? null :( String)atts.getValue("esquema"));
			tableDefinition.setName(atts.getValue("nombre"));
			tableDefinition.setAttributes(new HashMap<String, TableAttributeDefinition>());
			tableDefinition.setSequence(atts.getValue("secuencia") == null ? null :( String)atts.getValue("secuencia"));
			tableDefinition.setFid(atts.getValue("id") == null ? null :( String)atts.getValue("id"));
			tableDefinition.setAlias(atts.getValue("alias") == null ? null :( String)atts.getValue("alias"));
		} else if (localName.equals("atributo_tabla"))
			this.tableDefinition.addAttribute(new TableAttributeDefinition(atts.getValue("nombre_capa"), atts.getValue("nombre_bd")));
	}
	
	public void endElement(String namespaceURI, String localName,
							String rawName)	throws SAXException {

		if (localName.equals("origen_datos")) {
			this.layerDefinition.setDataOriginDefinition(this.dataOriginDefinition);
			this.dataOriginDefinition = null;
		}
		else if (localName.equals("tabla")) {
			this.dataOriginDefinition.setTableDefinition(this.tableDefinition);
			this.tableDefinition = null;
		}
	}
	
	public void characters(char[] ch, int start, int end) throws SAXException {
	}
	
	public void ignorableWhitespace(char[] ch, int start, int end) throws SAXException {
	}
	
	public void skippedEntity(String name) throws SAXException {
	}
		
	public LayerDefinition getLayerDefinition() {
		return this.layerDefinition;
	}
}