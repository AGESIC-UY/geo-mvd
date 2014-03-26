package imm.gis.core.data.mixto.attributeio;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.data.jdbc.fidmapper.FIDMapper;

public class LayerIO {

	private FIDMapper layerFIDMapper;
	
	private Map<String, LayerAttributeIO> layerAttributeIOs = new HashMap<String, LayerAttributeIO>();
	
	private Map<String, LayerIO> layerIOs = new HashMap<String, LayerIO>();
	
	private Map<String, LayerIO> externalAttributeIOs = new HashMap<String, LayerIO>();
	
	private String actualFID;
	
	private String layerName;
	
	private LayerIO parentLayerIO = null;
	
	public LayerIO(String layerName, FIDMapper layerFIDMapper) {
		this.layerName = layerName;
		this.layerFIDMapper = layerFIDMapper;
	}
	
	public LayerIO(String layerName, FIDMapper layerFIDMapper, LayerIO parentLayerIO) {
		this(layerName, layerFIDMapper);
		this.parentLayerIO = parentLayerIO;
	}
	
	
	public void addChildLayerIO(LayerIO layerReader) {
		
		if (parentLayerIO != null)
			parentLayerIO.addChildLayerIO(layerReader);
		
		layerIOs.put(layerReader.getLayerName(), layerReader); 
	}
	
	
	public LayerIO getChildLayerIO(String layerName) {
		return (LayerIO) layerIOs.get(layerName);
	}

	
	public void putLayerAttributeIO(String attributeName, LayerAttributeIO layerAttributeIO) {
		layerAttributeIOs.put(attributeName, layerAttributeIO);
		
		if (parentLayerIO != null)
			parentLayerIO.associateExternalAttribute(attributeName, this);
	}
	
	public void associateExternalAttribute(String attributeName, LayerIO layerAttributeIO) {
		externalAttributeIOs.put(attributeName, layerAttributeIO);
		
		if (parentLayerIO != null)
			parentLayerIO.associateExternalAttribute(attributeName, this);
	}
	
	public Object read(ResultSet rs, String attributeName) throws IOException {
		
		// Si el atributo es originiario de esta capa, lo leo, sino le
		// pido al lector de la capa origen que me lo de.
		
		if (layerAttributeIOs.containsKey(attributeName))
			return layerAttributeIOs.get(attributeName).read(rs);
		else if (externalAttributeIOs.containsKey(attributeName))
			return externalAttributeIOs.get(attributeName).read(rs, attributeName);
		else
			throw new IOException("Unknown attribute: "+attributeName);
	}
	
	
	public void write(ResultSet rs, String attributeName, Object value) throws IOException {

		// Si el atributo es originiario de esta capa, lo escribo, sino le
		// pido al escritor de la capa origen que lo escriba
		
		if (layerAttributeIOs.containsKey(attributeName))
			((LayerAttributeIO) layerAttributeIOs.get(attributeName)).write(rs, value);
		else if (externalAttributeIOs.containsKey(attributeName))
			((LayerIO) externalAttributeIOs.get(attributeName)).write(rs, attributeName, value);
		else
			throw new IOException("Unknown attribute: "+attributeName);		
	}
	
	
	public void next(ResultSet rs) throws SQLException {
		
		// Tengo que leer el FID desde el ResultSet (aca se llega inmediatamente del
		// next() en el FilasResultado)
    	Object pkAttributes[] = new Object[layerFIDMapper.getColumnCount()];
    	
    	for (int i = 0; i < layerFIDMapper.getColumnCount(); i++)
    		pkAttributes[i] = rs.getObject(layerFIDMapper.getColumnName(i));
    	
    	actualFID = layerFIDMapper.getID(pkAttributes);
	
    	Iterator<LayerAttributeIO> i = layerAttributeIOs.values().iterator();
    	
    	while (i.hasNext()){
    		i.next().setActualFID(actualFID);
    	}
    	
    	// Le aviso a los encargados de las otras capas (es decir, las otras
    	// capas que componen la que este LayerReader representa) de que deben
    	// actualizar su FID

    	// El if es para asegurarnos de que solo se haga un next() por LayerIO
    	if (parentLayerIO == null) {
	    	Iterator<LayerIO> j = layerIOs.values().iterator();
	    	
	    	while (j.hasNext()){ 
	    		LayerIO io = j.next();
	    		io.next(rs);
	    	}
    	}
	}
	
	public String getActualFID() {
		return actualFID;
	}
	
	public String getLayerName() {
		return this.layerName;
	}
	
	public LayerAttributeIO getLayerAttributeIO(String attributeName) {
		if (layerAttributeIOs.containsKey(attributeName))
			return (LayerAttributeIO) layerAttributeIOs.get(attributeName);
		else if (externalAttributeIOs.containsKey(attributeName))
			return ((LayerIO) externalAttributeIOs.get(attributeName)).getLayerAttributeIO(attributeName);
		else
			return null;		
	}

	public void close() {
		Iterator<LayerIO> i = this.layerIOs.values().iterator();
		while (i.hasNext()) i.next().close();		
	}
}
