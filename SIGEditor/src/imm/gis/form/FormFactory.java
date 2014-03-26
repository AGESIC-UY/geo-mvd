package imm.gis.form;

import java.awt.Component;
import java.awt.Dialog;
import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Geometry;

import imm.gis.AppContext;
import imm.gis.consulta.ICargadorLOV;
import imm.gis.consulta.LOVLoader;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.LayerContextManager;
import imm.gis.core.layer.metadata.LayerAttributeMetadata;
import imm.gis.core.layer.metadata.LayerAttributePresentation;
import imm.gis.form.item.AbstractFormItem;
import imm.gis.form.item.BasicFormItem;
import imm.gis.form.item.DateFormItem;
import imm.gis.form.item.FIDFormItem;
import imm.gis.form.item.GeometryFormItem;
import imm.gis.form.item.LovFormItem;
import imm.gis.form.item.LovPanelFormItem;
import imm.gis.form.item.MapElementFormItem;
import imm.gis.form.item.URLFormItem;

public class FormFactory {
	private Map<String,IFeatureForm> forms = new HashMap<String,IFeatureForm>();
	private static FormFactory instance = null;
	
	private FormFactory() {
	}

	public static FormFactory getInstance(){
		if (instance == null){
			instance = new FormFactory();
		}
		
		return instance;
	}
	
	public IFeatureForm createForm(String layer, int mode) {
		String key = layer + mode;
		IFeatureForm iff = null;
		
		if (forms.containsKey(key)){
			iff = (IFeatureForm)forms.get(key);
		} else {
			AppContext am = AppContext.getInstance();
			ICoreAccess coreAccess = am.getCoreAccess();
			
			if (mode == IFeatureForm.MODIFY_FEATURES){
				iff = new EditFeaturesForm(coreAccess);
				iff.setFeatureType(am.getCapa(layer).getFt());
			}
			else if (am.getUserForm(layer) != null){
				iff = am.getUserForm(layer);
			} else {
				iff = new DefaultForm(coreAccess, mode);
				iff.setFeatureType(am.getCapa(layer).getFt());
			}
			
			forms.put(key, iff);
		}
		
		return iff;
	}

	public AbstractFormItem createFormItem(int mode, Feature feature,
			AttributeType attributeType, LayerAttributeMetadata metadata,
			Component container) {

		AbstractFormItem formItem = null;
		LayerAttributePresentation itemLap,lapDef = new LayerAttributePresentation();
		if (mode != IFeatureForm.SEARCH_FEATURE || metadata.isQueryCapable()) {
			
			itemLap =  mode == IFeatureForm.SEARCH_FEATURE ? lapDef:metadata.getPresentation();
			if(mode == IFeatureForm.SEARCH_FEATURE){
				//Si estoy en modo de busqueda el layAttrbutePresentation es el por defecto.
			}
			
			// if (!Geometry.class.isAssignableFrom(attributeType.getType()) &&
			// (mode != AbstractForm.SEARCH_FEATURE ||
			// metadata.isQueryCapable())) {

			// En base al tipo del atributo diferencio que
			// implementacion usar para representar el atributo en el formulario
			if (metadata.getUsage() == LayerAttributeMetadata.ATTR_USAGE_LOV) {
				ICargadorLOV iclov = new LOVLoader(attributeType);
				
				iclov.setOrderByDescription(metadata.isOrderByDescription());
				if (metadata.getPresentation().isUnique())
					iclov.setUnique(true);
				
				if (feature != null)
					formItem = new LovFormItem(attributeType, feature
							.getAttribute(attributeType.getLocalName()), iclov,
							metadata);
				else
					formItem = new LovFormItem(attributeType, null, iclov,
							metadata);

				if (mode == IFeatureForm.SEARCH_FEATURE)
					((LovFormItem) formItem).setNillable(true, true);
			} else if (metadata.getUsage() == LayerAttributeMetadata.ATTR_USAGE_LOV_PANEL){
					formItem = new LovPanelFormItem(attributeType, feature, 
							container instanceof Dialog ? (Dialog)container :  null);					
			} else if (metadata.getUsage() == LayerAttributeMetadata.ATTR_USAGE_FEATURE_REFERENCE) {
				ICoreAccess coreAccess = AppContext.getInstance().getCoreAccess();
				if (feature != null)
					formItem = new MapElementFormItem(attributeType, feature
							.getAttribute(attributeType.getLocalName()), coreAccess,
							metadata.getReferencedLayers(), container);
				else
					formItem = new MapElementFormItem(attributeType, null,
							coreAccess, metadata.getReferencedLayers(),
							container);
			}
			else {
				if (java.util.Date.class.isAssignableFrom(attributeType.getBinding())) {

					if (feature != null)
						formItem = new DateFormItem(attributeType, feature
								.getAttribute(attributeType.getLocalName()));
					else
						formItem = new DateFormItem(attributeType, null);
				} else if (Geometry.class.isAssignableFrom(attributeType.getBinding())) {
					if (feature != null)
						formItem = new GeometryFormItem(attributeType, feature
								.getAttribute(attributeType.getLocalName()));
					else
						formItem = new GeometryFormItem(attributeType, null);
				} else {
					if (itemLap.getType() == LayerAttributePresentation.ATTR_PRESENTATION_URL){
						formItem = new URLFormItem(attributeType, feature == null ? null :
													feature.getAttribute(attributeType.getLocalName()), itemLap);
					} else{
						formItem = new BasicFormItem(attributeType, feature == null ? null :
													feature.getAttribute(attributeType.getLocalName()),
													itemLap);
						
					}
				}

			}

			formItem.setEnabled(mode == IFeatureForm.SEARCH_FEATURE ||isEditable(mode, metadata, feature));
			
			if (metadata.getPresentation().isLocking())
				formItem.setLocking(true);

		}

		
		return formItem;
	}

	public FIDFormItem createFIDFormItem(String layerName, AttributeType at,
			 LayerAttributePresentation lap) {
//		PreffixFIDMapper pfm = new PreffixFIDMapper(layerName);
		FIDFormItem formItem = new FIDFormItem(layerName, at, null, lap);
		return formItem;

	}
/*
	public AttributePanel createAttributePanel(int mode, Layer layer, Feature feature,
			Component topLevelContainer) {

		AttributePanel ap = new AttributePanel(
				mode, 
				AppContext.getInstance().getCoreAccess(), layer,
				topLevelContainer, feature);
		return ap;
	}
*/
	private boolean isEditable(int mode, LayerAttributeMetadata a, Feature f) {

		if (mode == IFeatureForm.SHOW_FEATURE)
			return false;

		//if (mode == AbstractForm.SEARCH_FEATURE)
		//	return true;

		if (f == null)
			return !a.isReadOnly();
		else {
			String layerName = f.getFeatureType().getTypeName();
			LayerContextManager lctxmgr = AppContext.getInstance().getCapa(
					layerName).getCtxAttrManager();
			Boolean boolVal = (Boolean) lctxmgr.getAttributeProperty(mode, f,
					a, "editable");
			return boolVal.booleanValue();
		}
	}
}
