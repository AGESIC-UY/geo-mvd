package imm.gis.core.controller;

import java.util.Iterator;

import org.geotools.feature.Feature;

import imm.gis.core.model.FeatureModifyOperation;
import imm.gis.core.model.FeatureOperation;
import imm.gis.core.model.ModelData;
import imm.gis.core.model.undo.UndoModel;
import imm.gis.core.model.undo.UndoableOperation;

import org.apache.log4j.Logger;

public class ContUndo {
	private UndoModel model;
	private ModelData modelData;
	private static Logger logger = Logger.getLogger(ContUndo.class);
	
	public ContUndo(ContPpal c){
		this(c, 10);
	}
	
	public ContUndo(ContPpal c, int lenOperations){
		logger.debug("ContUndo constructor");
		model = new UndoModel(this, lenOperations);
		
		modelData = c.getModel();
	}
	
	public UndoModel getModel(){
		return model;
	}
	
	public void undo() throws Exception {
		
		if (model.isEmpty())
			return;
		
		UndoableOperation uo = model.getLastOperation();
		Iterator it = uo.getOperations().iterator();
		FeatureOperation fo = null;
		Feature f = null;
		
		while (it.hasNext()){
			fo = (FeatureOperation)it.next();
			f = fo.getFeature();
			
			switch (fo.getActualStatus()){
			case ModelData.CREATED_ATTRIBUTE:
				modelData.delFeature(f, false, false);
				break;
			case ModelData.DELETED_ATTRIBUTE:
				modelData.setStatus(f, fo.getOriginalStatus());
				break;
			case ModelData.UPDATED_ATTRIBUTE:
				if(fo instanceof FeatureModifyOperation){
					FeatureModifyOperation fmo = (FeatureModifyOperation)fo;
					f = modelData.modifyFeature(f, fmo.getOldFeature().getAttributes(new Object[fmo.getOldFeature().getNumberOfAttributes()]),true,false);//modifyFeature(f,);
					modelData.setStatus(f, fo.getOriginalStatus());
				}
				
				break;
			default:
				break;
			}
		}
		
		modelData.refreshViews();
		modelChanged();
	}
	
	public void clean(){
		model.removeOperations();
		//accionesEdicion.getUndoAction().setEnabled(false);
	}
	
	public void modelChanged(){
		/*
		boolean b = !model.isEmpty();
		
		accionesEdicion.getUndoAction().setEnabled(b);			
		accionesEdicion.getSaveAction().setEnabled(b);			
		accionesEdicion.getCancelAction().setEnabled(b);
		*/
	}
}
