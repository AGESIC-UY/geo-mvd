package imm.gis.comm;

import java.io.IOException;
import java.io.StringReader;

import org.opengis.filter.Filter;
import org.geotools.filter.FilterFilter;
import org.geotools.gml.GMLFilterDocument;
import org.geotools.gml.GMLFilterGeometry;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class FilterDecoder {
    private SimpleFilterHandler simpleFilterHandler = 
        new SimpleFilterHandler();

    private FilterFilter filterFilter;

    private GMLFilterGeometry filterGeometry;

    private GMLFilterDocument filterDocument;

    private XMLReader reader;

    public FilterDecoder() {
        
    	simpleFilterHandler = new SimpleFilterHandler();
        filterFilter = new FilterFilter(simpleFilterHandler, null);
        filterGeometry = new GMLFilterGeometry(filterFilter);
        filterDocument = new GMLFilterDocument(filterGeometry);
        
        
        
//        SimpleFilterHandler simpleFilterHandler = new SimpleFilterHandler();
//        FilterFilter filterFilter = new FilterFilter(simpleFilterHandler, null);
//        GMLFilterGeometry filterGeometry = new GMLFilterGeometry(filterFilter);
//        GMLFilterDocument filterDocument = new GMLFilterDocument(filterGeometry);
//
//        // parse xml
//        XMLReader reader = XMLReaderFactory.createXMLReader();
//        reader.setContentHandler(filterDocument);
//        reader.parse(input);
//        
        
        
        try {
            reader = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public Filter parse(String xmlInput) throws IOException, SAXException {

        // setup ContentHandler

        // parse xml

        reader.setContentHandler(filterDocument);
        StringReader readerStr=new StringReader(xmlInput);
        org.xml.sax.InputSource ipSource = new InputSource(readerStr);
        reader.parse(ipSource);//parse(xmlInput);

        return simpleFilterHandler.getFilter();

    }
}
