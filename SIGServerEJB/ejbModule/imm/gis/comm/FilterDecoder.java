package imm.gis.comm;

import java.io.IOException;

import java.io.StringReader;

import org.geotools.filter.FilterFilter;
import org.geotools.gml.GMLFilterDocument;
import org.geotools.gml.GMLFilterGeometry;
import org.opengis.filter.Filter;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class FilterDecoder {
    private SimpleFilterHandler simpleFilterHandler = null; 
        

    private FilterFilter filterFilter;

    private GMLFilterGeometry filterGeometry;

    private GMLFilterDocument filterDocument;

    private XMLReader reader;
    
    private StringReader readerStr;
    
    private org.xml.sax.InputSource inputSource;

    public FilterDecoder() {
      
    }

    public Filter parse(String xmlInput) throws IOException, SAXException {

        // setup ContentHandler

        // parse xml
        simpleFilterHandler = new SimpleFilterHandler();
        filterFilter = new FilterFilter(simpleFilterHandler, null);
        filterGeometry = new GMLFilterGeometry(filterFilter);
        filterDocument = new GMLFilterDocument(filterGeometry);
        try {
            reader = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    	readerStr=new StringReader(xmlInput);
        inputSource = new InputSource(readerStr);
        reader.setContentHandler(filterDocument);
        reader.parse(inputSource);
      //  readerStr.close();

        return simpleFilterHandler.getFilter();

    }
}
