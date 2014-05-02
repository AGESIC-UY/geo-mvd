package imm.gis.core.data.mixto;

import java.io.IOException;
import java.util.ArrayList;

import org.geotools.data.FeatureReader;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

public class MixtoFeatureReader implements FeatureReader {
	private FilasResultado datos = null;
	private Object atributos[] = null;
	private ArrayList<String> properties = new ArrayList<String>();
	private AttributeType[] tipos = null;

	public MixtoFeatureReader(FilasResultado fr){
		datos = fr;	
		atributos = new Object[fr.getFeatureType().getAttributeCount()];

		String[] props = fr.getProperties();
		
		tipos = fr.getFeatureType().getAttributeTypes();
		
		for (int i=0;i<props.length;i++)
			properties.add(i,props[i]);
	}
	
	
	
	public FeatureType getFeatureType() {
		return datos.getFeatureType();
	}

	
	
	public Feature next() throws IOException, IllegalAttributeException {
		datos.next();
		
		for (int i = 0; i < tipos.length; i++)
			atributos[i] = tipos[i].createDefaultValue();

		Feature f;
		
		f = datos.getFeatureType().create(atributos,datos.readFidColumn());
		
		for (int i = 0; i < tipos.length; i++)
            if (properties.contains(tipos[i].getLocalName()) || properties.contains(tipos[i].getLocalName().concat("_id")))
    			f.setAttribute(i,datos.read(tipos[i].getLocalName()));

		return f;
	}

	
	
	public boolean hasNext() throws IOException {
		try{
			return datos.hasNext();			
		} catch (Exception e){
			e.printStackTrace();
			throw new IOException(e.getLocalizedMessage());
		}
	}

	
	
	public void close() throws IOException {
		datos.close();
	}
}
