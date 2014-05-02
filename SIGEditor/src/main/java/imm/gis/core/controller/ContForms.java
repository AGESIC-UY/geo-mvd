package imm.gis.core.controller;

import imm.gis.AppContext;
import imm.gis.core.interfaces.IForm;
import imm.gis.core.layer.metadata.ChildMetadata;
import imm.gis.core.model.FeatureHierarchyWrapper;
import imm.gis.core.model.ModelData;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureEventManager;
import imm.gis.form.FormFactory;
import imm.gis.form.IFeatureForm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.geotools.data.DefaultQuery;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.Feature;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

public class ContForms implements IForm {
	private ModelData model;
	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
	private boolean editFormEnabled = true;
	private Collection<Feature> uneditableFeatures = new ArrayList<Feature>();
	private Map<String,Map<String,Object>> calculatedAttributes = new TreeMap<String, Map<String,Object>>();
	private boolean insDelMultipleChild = true;
	

	public void setInsDelMultipleChild(boolean insDelMultipleChild) {
		this.insDelMultipleChild = insDelMultipleChild;
	}

	public ContForms(ContPpal mainController) {
		model = mainController.getModel();
	}
	
	public void setUneditableFeatures(Collection col){
		uneditableFeatures = col;
	}
	
	public void setCalculatedAttribute(String featureID, String nameAtt, Object value){
		TreeMap<String, Object> tm = (TreeMap<String, Object>)calculatedAttributes.get(featureID);
		if (tm == null)
			tm = new TreeMap<String, Object>();
		tm.put(nameAtt, value);
		calculatedAttributes.put(featureID, tm);		
	}

	private Map<String, Map> getData(String layer, String id) throws Exception {
		Map<String, Map> data;
		data = model.getFormData(layer, id);
		
		if (data.size() == 0) {
			Set<Identifier> set = new HashSet<Identifier>();
			set.add(ff.featureId(id));
			
			Id fidFilter = ff.id(set);
			
			data = model.getFormData(new DefaultQuery(layer, fidFilter), true);
		}		
		
		return data;
	}
	
	public void openShowFeatureForm(Feature f) throws Exception {
		Map data = getData(f.getFeatureType().getTypeName(), f.getID());
		IFeatureForm af = FormFactory.getInstance().createForm(f.getFeatureType().getTypeName(), IFeatureForm.SHOW_FEATURE);
		
		af.show(FeatureHierarchyWrapper.getInstance(f.getFeatureType().getTypeName(), data), calculatedAttributes, insDelMultipleChild);
		calculatedAttributes.clear();
		insDelMultipleChild = true;
	}
	
	public void openShowFeaturesForm(Collection col) throws Exception {
		Map<String, Map> data = null;
		Feature f = null;
		
		for (Iterator it = col.iterator(); it.hasNext();){
			f = (Feature)it.next();
			if (data == null){
				data = getData(f.getFeatureType().getTypeName(), f.getID());
			} else {
				Map tmp = getData(f.getFeatureType().getTypeName(), f.getID());
				data.put(f.getID(), (Map)tmp.get(f.getID()));
			}
		}
		
		IFeatureForm af = FormFactory.getInstance().createForm(f.getFeatureType().getTypeName(), IFeatureForm.SHOW_FEATURE);
		af.show(FeatureHierarchyWrapper.getInstance(f.getFeatureType().getTypeName(), data), calculatedAttributes, insDelMultipleChild);
		calculatedAttributes.clear();
		insDelMultipleChild = true;
	}

	
	public void openEditFeatureForm(Feature f) throws Exception {
		Map data = model.getFormData(f.getFeatureType().getTypeName(), f.getID());
		IFeatureForm af = FormFactory.getInstance().createForm(f.getFeatureType().getTypeName(), IFeatureForm.MODIFY_FEATURE);
		af.show(FeatureHierarchyWrapper.getInstance(f.getFeatureType().getTypeName(), data), calculatedAttributes, insDelMultipleChild);
		calculatedAttributes.clear();
		insDelMultipleChild = true;
	}

	public void openEditCollectionFeatureForm(Collection col) throws Exception {
		if (editFormEnabled){
			Map<String, Map> data = null;
			Feature f = null;
			
			for (Iterator it = col.iterator(); it.hasNext();){
				f = (Feature)it.next();

				if (!uneditableFeatures.contains(f)){				
					if (data == null){
						data = model.getFormData(f.getFeatureType().getTypeName(), f.getID());
					} else {
						Map tmp = model.getFormData(f.getFeatureType().getTypeName(), f.getID());
						data.put(f.getID(), (Map)tmp.get(f.getID()));
					}
				}
			}
			
			IFeatureForm af = FormFactory.getInstance().createForm(f.getFeatureType().getTypeName(), IFeatureForm.MODIFY_FEATURE);
			af.show(FeatureHierarchyWrapper.getInstance(f.getFeatureType().getTypeName(), data), calculatedAttributes, insDelMultipleChild);
			calculatedAttributes.clear();
			insDelMultipleChild = true;
		}		
	}

	
	public void disableEditFeatureForm() throws Exception{
		this.editFormEnabled = false;
	}
	
	public void enableEditFeatureForm() throws Exception{
		this.editFormEnabled = true;
	}

	public void openEditFeaturesForm(Collection features) throws Exception {
		
		if(features.isEmpty()) {
			throw new Exception("No hay elementos a editar!!!");
		}
		
		String fT = ((Feature)features.iterator().next()).getFeatureType().getTypeName();
		
		IFeatureForm iff = FormFactory.getInstance().createForm(fT, IFeatureForm.MODIFY_FEATURES);
		iff.show(null);
	}

	public void openNewFeatureForm(Feature f) throws Exception {
		Map<String, Map> info = new HashMap<String, Map>();
		Map<String, Map> m = new HashMap<String, Map>();
		Map<String, Feature> item = new HashMap<String, Feature>();

		ChildMetadata child;
		
		String editedType = f.getFeatureType().getTypeName();
		
		model.notifyChangeListeners(new FeatureChangeEvent(f,
				f.getFeatureType().getTypeName(), null, null,
				FeatureChangeEvent.OPEN_FORM_NEW_FEATURE),
				FeatureEventManager.BEFORE_MODIFY);
		
		item.put(f.getID(), f);
		m.put(editedType, item);
		java.util.List children = AppContext.getInstance().getCapa(editedType)
				.getMetadata().getChildrenMetadata();
		
		for (Iterator it = children.iterator(); it.hasNext();) {
			child = (ChildMetadata) it.next();
			m.put(child.getLayerName(), new HashMap());
		}
		
		info.put(f.getID(), m);
		
		IFeatureForm af = FormFactory.getInstance().createForm(f.getFeatureType().getTypeName(), IFeatureForm.NEW_FEATURE);
		af.show(FeatureHierarchyWrapper.getInstance(f.getFeatureType().getTypeName(), info), calculatedAttributes, insDelMultipleChild);
		calculatedAttributes.clear();
		insDelMultipleChild = true;
	}
}
