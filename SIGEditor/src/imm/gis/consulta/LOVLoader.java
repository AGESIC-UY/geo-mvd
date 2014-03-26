package imm.gis.consulta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotools.data.FeatureReader;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.geotools.filter.IllegalFilterException;

import imm.gis.AppContext;
import imm.gis.comm.IServerServices;
import imm.gis.comm.ServerServicesFactory;
import imm.gis.core.feature.ExternalAttribute;


/**
 * Se utiliza por otras clases para cargar valores de codigueras.
 *  
 * @author agrassi
 *
 */
public class LOVLoader implements ICargadorLOV {
	private Map<String, Object> restrictions = new HashMap<String, Object>();
	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
	private AttributeType attributeType;
	private boolean unique = false;
	private String originLayer;
	private boolean orderByDescription;
	private int searchMethod = SEARCH_EQUALS;
	
	public LOVLoader() {
	}

	public LOVLoader(AttributeType at) {
		setAttributeType(at);
	}
	
	public ExternalAttribute[] getValores() throws Exception   {
		FeatureReader fr = getFeatures(null, orderByDescription);
		if (!fr.hasNext()){
			fr.close();
			return null;
		}
		
		ArrayList<ExternalAttribute> res = new ArrayList<ExternalAttribute>();
		Feature feature = null;
		ExternalAttribute ea = null;
		
		while (fr.hasNext()) {
			feature = (Feature) fr.next();
			
			if (unique){
				ea = new ExternalAttribute("", feature.getAttribute(attributeType.getLocalName()), originLayer);
				if (!res.contains(ea)){
					res.add(ea);
				}
			} else {
				ea = new ExternalAttribute(feature.getID(), feature.getAttribute(attributeType.getLocalName()), originLayer);
				res.add(ea);				
			}
		}
				
		fr.close();
		
		return res.toArray(new ExternalAttribute[res.size()]);
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	private Filter constructFilter(String attributeName, Object restriction) throws IllegalFilterException {
		Filter f = null;
		
		if (restriction != null) {
			if (CharSequence.class.isAssignableFrom(restriction.getClass()) && searchMethod == SEARCH_LIKE) {
				f = ff.like(
						ff.property(attributeName), 
						"*"+((CharSequence) restriction).toString()+"*",
						"*",
						"?",
						"!");
//				lf.setPattern("*"+((CharSequence) restriction).toString()+"*","*","?","!");
			}else {
				f = ff.equals(
						ff.property(attributeName),
						ff.literal(restriction));
			}
		}
		
		return f;
	}
	
	public void setAttributeType(AttributeType at) {
		this.attributeType = at;
		originLayer = ((ExternalAttribute) attributeType.createDefaultValue()).getOriginLayer();
	}
	
	public AttributeType getAttributeType() {
		return this.attributeType;
	}

	public void addRestriction(String attributeName, Object value) {
		restrictions.put(attributeName, value);
	}

	public void clearRestrictions() {
		restrictions.clear();
	}

	public void restrictValues(Object restriction) {
		restrictions.put(attributeType.getLocalName(), restriction);
	}
	
	public void removeRestriction(String attributeName) {
		if (restrictions.containsKey(attributeName))
			restrictions.remove(attributeName);
	}

	public void setSearchMethod(int method) {
		searchMethod = method;
	}

	public boolean isUnique() {
		return this.unique;
	}

	
	public FeatureCollection getFeatures() throws Exception {
		return getFeaturesInCollection(null, orderByDescription);
	}

	public FeatureCollection getFeatures(String[] atts) throws Exception {
		return getFeaturesInCollection(atts, orderByDescription);
	}
	
	private FeatureCollection getFeaturesInCollection(String[] atts, boolean orderByDescription) throws Exception {
		FeatureReader fr = getFeatures(atts, orderByDescription);
		FeatureCollection fc = FeatureCollections.newCollection();
		
		while (fr.hasNext()){
			fc.add(fr.next());
		}
		
		fr.close();
		
		return fc;
	}
	
	private FeatureReader getFeatures(String[] atts, boolean orderByDescription) throws Exception {
		Filter mainFilter;
		Object restriction;	
		ArrayList<Filter> filters = new ArrayList<Filter>();
		String key;
		List<String> attNames = new ArrayList<String>(); // Atributos que pido en el resultado
		Iterator it = restrictions.keySet().iterator(); 
		
		attNames.add(attributeType.getLocalName()); // El atributo de la LOV
		
		if (atts != null){ // Agrego atributos pedidos
			for (int i = 0; i < atts.length; i++){
				if (!attNames.contains(atts[i])){
					attNames.add(atts[i]);
				}
			}
		}
		
		while (it.hasNext()) { // Agrego atributos pedidos en el filtro
			key = (String) it.next();
			restriction = restrictions.get(key);
			
			if (restriction != null) {
				filters.add(constructFilter(key, restriction));
				if (!attNames.contains(key)){ 
					attNames.add(key); 
				}
			}
		}

		if (filters.isEmpty()){
			mainFilter = Filter.EXCLUDE;
		} else {
			mainFilter = ff.and(filters);
		}
		
		
		// Obtengo los posibles valores para el atributo
		
		IServerServices iss = ServerServicesFactory.getServerServices();
		FeatureType ft = AppContext.getInstance().getSchema(originLayer);
		FeatureReader fr = (orderByDescription) ? iss.loadLov(ft, mainFilter, attNames.toArray(new String[]{}), attributeType.getLocalName()):
												  iss.loadLov(ft, mainFilter, attNames.toArray(new String[]{}));
		
		return fr;
	}

	public void setOrderByDescription(boolean b) {
		this.orderByDescription = b;
	}
}
