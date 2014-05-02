package imm.gis.form.search;
import imm.gis.core.feature.ExternalAttribute;
import imm.gis.core.feature.FeatureReferenceAttribute;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.layer.Layer;
import imm.gis.form.AttributePanel;
import imm.gis.form.IFeatureForm;
import imm.gis.form.item.AbstractFormItem;
import imm.gis.form.item.FIDFormItem;

import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.identity.FeatureId;

public class SearchCriteriaPanel extends AttributePanel {

	private static final long serialVersionUID = 1L;
	private final String DATE_FORMAT = "dd-MM-yyyy";
	private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());


	public SearchCriteriaPanel(ICoreAccess coreAccess, Layer layer, Component topLevelContainer) {
		super(IFeatureForm.SEARCH_FEATURE, coreAccess, layer, topLevelContainer);
	}
	
	public SearchCriteriaPanel(ICoreAccess coreAccess, Layer layer) {
		super(IFeatureForm.SEARCH_FEATURE, coreAccess, layer);
	}
	
	public Filter getFilter(){
		Filter finalFilter = null;
		BinaryComparisonOperator cf = null;
		PropertyIsLike lf = null;
		Filter tmpFilter = null;
		AbstractFormItem formItem;
		Iterator i = getFormItems().values().iterator();
		
		while (i.hasNext()) {
			formItem = (AbstractFormItem) i.next();
			
			if (formItem.getValue() == null || "".equals(formItem.getValue()))
				continue;
			if(formItem instanceof FIDFormItem){
				FeatureId fId = ff.featureId((String)formItem.getValue());
				HashSet<FeatureId> set = new HashSet<FeatureId>();
				set.add(fId);
				tmpFilter = ff.id(set);
			}
			else if (Number.class.isAssignableFrom(formItem.getValue().getClass())) {
				cf = ff.equals(
						ff.property(formItem.getAttributeType().getLocalName()),
						ff.literal(formItem.getValue())
				);
				tmpFilter = cf;
			}else if (CharSequence.class.isAssignableFrom(formItem.getValue().getClass())) {
				lf = ff.like(
						ff.property(formItem.getAttributeType().getLocalName()),
						"*"+formItem.getValue()+"*","*","!","."
				);
				tmpFilter = lf;
			}
			else if (FeatureReferenceAttribute.class.isAssignableFrom(formItem.getValue().getClass())) {
				cf = ff.equals(
						ff.property(formItem.getAttributeType().getLocalName()),
						ff.literal(((FeatureReferenceAttribute) formItem.getValue()).getReferencedFeatureID())
				);
				tmpFilter = cf;
			}
			else if (ExternalAttribute.class.isAssignableFrom(formItem.getValue().getClass())) {
				Object tmp = ((ExternalAttribute) formItem.getValue()).getValue();
				cf = ff.equals(
						ff.property(formItem.getAttributeType().getLocalName()),
						ff.literal(
								Number.class.isAssignableFrom(tmp.getClass()) ?
										((Number) tmp).longValue() : tmp)
				);
				tmpFilter = cf;
			}
			else if (Date.class.isAssignableFrom(formItem.getValue().getClass())) {
				
				/**
				 * Creo un filtro compuesto que acota al dia
				 * 
				 */
				try {
					
					Date today = dateFormat.parse(dateFormat.format(formItem.getValue()));
					GregorianCalendar g = new GregorianCalendar();
					g.setTime(today);
					g.add(GregorianCalendar.DAY_OF_MONTH,1 );
					Date tomorrow = g.getTime();
					cf = ff.greaterOrEqual(
							ff.property(formItem.getAttributeType().getLocalName()),
							ff.literal(dateFormat.format(today))
					);
					PropertyIsLessThan cf2 = ff.less(
							ff.property(formItem.getAttributeType().getLocalName()),
							ff.literal(dateFormat.format(tomorrow))
					);
					tmpFilter = ff.and(cf, cf2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
			}
			else
				throw new RuntimeException("Clase de valor no implementada: " + formItem.getValue().getClass());

			if (finalFilter == null)
				finalFilter = tmpFilter;
			else
				finalFilter = ff.and(finalFilter, tmpFilter);
		}
		
		return finalFilter == null ? Filter.EXCLUDE : finalFilter;
	}
}
