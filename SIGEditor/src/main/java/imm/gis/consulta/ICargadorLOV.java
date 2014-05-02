package imm.gis.consulta;

import org.geotools.feature.FeatureCollection;

import imm.gis.core.feature.ExternalAttribute;


/**
 * Se utiliza por otras clases para cargar valores de atributos LOV sin conocer los detalles de este
 * atributo.
 *  
 * @author agrassi
 *
 */
public interface ICargadorLOV {
	
	public final int SEARCH_EQUALS = 1;
	public final int SEARCH_LIKE = 2;
	
	public ExternalAttribute[] getValores() throws Exception;
	public FeatureCollection getFeatures() throws Exception;
	public FeatureCollection getFeatures(String[] atts) throws Exception;
	
	public void setUnique(boolean unique);
	public boolean isUnique();
	
	public void setOrderByDescription(boolean orderByDescription);
	
	public void restrictValues(Object restriction);
	public void addRestriction(String attributeName, Object value);
	public void removeRestriction(String attributeName);
	public void clearRestrictions();
	public void setSearchMethod(int method);
}
