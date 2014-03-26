package imm.gis.core.mixto.xml;

import imm.gis.core.layer.Layer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class AppContentHandler implements ContentHandler {
	private DefinicionAplicacion def = null;
	private Layer capa = null;
	private String tmp;
	
	public void startDocument() throws SAXException {
		def = new DefinicionAplicacion();
	}

	public void endDocument() throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		if (localName.equals("aplicacion")){
			def.setName(atts.getValue("nombre"));
			def.setUserFiltersProvider(atts.getValue("user_filters_provider"));
		} else if (localName.equals("capa")){
			capa = new Layer();
			tmp = atts.getValue("editable");
			capa.setEditable(Boolean.valueOf(tmp).booleanValue());
			capa.setDataFile(atts.getValue("datos"));
			capa.setSldFile(atts.getValue("estilo"));
			capa.setNombre(atts.getValue("nombre"));
			
			if (atts.getValue("visible")!=null)
				capa.setVisible(Boolean.valueOf(atts.getValue("visible")).booleanValue());
			
			capa.setMetadataFileName(atts.getValue("metadata"));
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("capa")){
			def.addLayer(capa);
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	public void skippedEntity(String name) throws SAXException {
	}

	public void setDocumentLocator(Locator locator) {
	}

	public DefinicionAplicacion getDef() {
		return def;
	}
}
