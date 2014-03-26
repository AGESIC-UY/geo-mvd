package imm.gis.form;

import imm.gis.AppContext;
import imm.gis.GisException;
import imm.gis.core.feature.FeatureReferenceAttribute;
import imm.gis.core.interfaces.IModel;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.LayerContextManager;
import imm.gis.core.layer.metadata.ChildMetadata;
import imm.gis.core.layer.metadata.LayerAttributeMetadata;
import imm.gis.core.model.FeatureHierarchyWrapper;
import imm.gis.core.model.event.FeatureChangeEvent;
import imm.gis.core.model.event.FeatureEventManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.geotools.feature.DefaultFeature;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

public class EditableFeatureTableModel extends AbstractTableModel {
	private static Logger log = Logger.getLogger(EditableFeatureTableModel.class);
	private static final long serialVersionUID = 1L;
	private Map<String, Map> featureTable;
	private transient List<Feature> featureList;
	private Map atributos;
	private String[] columns;
	private FeatureType featureType;
	private String parentId = null;
	private String joinKey;
	private Layer layerDefinition;
	private int mode;
	private IModel modelAccess;

	public EditableFeatureTableModel(String id, String joinAtt, Map<String, Map> fc,
			Layer layerInfo, Map atts, String[] cols, int _mode, IModel ima) {
		atributos = atts;
		featureType = layerInfo.getFt();
		layerDefinition = layerInfo;
		parentId = id;
		joinKey = joinAtt;
		columns = cols;
		setFeatureCollection(fc);
		mode = _mode;
		modelAccess = ima;
	}
	
	public void setParentID(String id) {
		parentId = id;
	}

	public void setFeatureCollection(Map<String, Map> features) {
		featureTable = features;
		featureList = new ArrayList<Feature>(); // featureTable.values());

		if (features != null) {
			Iterator i = features.values().iterator();
			Map item;
			Map value;
			while (i.hasNext()) {
				item = (Map) i.next();
				value = (Map) item.get(featureType.getTypeName());
				Feature currFeature = (Feature) value.values().iterator().next();
				featureList.add(currFeature);
			}
		}

		fireTableStructureChanged();
	}

    public int getColumnCount() {
        return columns.length;
    }

    public int getRowCount() {
        if (featureTable == null || featureList == null || featureList.isEmpty()) {
            return 0;
        }
        
        return featureTable.size();
    }

    public String getColumnName(int col) {
        return columns[col];
    }
    
   public Class<?> getColumnClass(int columnIndex) {
	   String columnName = getColumnName(columnIndex);
	   return layerDefinition.getFt().getAttributeType(columnName).getBinding();
}

    public Object getValueAt(final int row, final int col) {
		String column = getColumnName(col);

		return ((Feature)featureList.get(row)).getAttribute(column);
    }
    
	public boolean isCellEditable(int row, int col){
		String columnName = getColumnName(col);
		boolean boolVal =  ( mode != DefaultForm.SHOW_FEATURE && !((LayerAttributeMetadata) atributos.get(columnName)).isReadOnly());
		LayerContextManager lCtx = AppContext.getInstance().getCapa(featureType.getTypeName()).getCtxAttrManager();
		return /*mode == DefaultForm.NEW_FEATURE || */(boolVal && ((Boolean) lCtx.getAttributeProperty((Feature) featureList.get(0),columnName,"editable")).booleanValue());
	}
	
	
	public void setValueAt(Object value, int row, int col){
		String att = getColumnName(col);
		DefaultFeature newFeature = null;
		DefaultFeature oldFeature = null;
		
		try {
			newFeature = (DefaultFeature)featureList.get(row);
			oldFeature = (DefaultFeature)newFeature.getFeatureType().create(
					newFeature.getAttributes(new Object[newFeature.getNumberOfAttributes()]), 
					newFeature.getID()
			);
			log.info("Modificando atributo " + att + " : " + value);
			newFeature.setAttribute(att, value);
			modelAccess.notifyChangeListeners(new FeatureChangeEvent(modelAccess.getModelContext(),
					oldFeature.getFeatureType().getTypeName(), newFeature, oldFeature,
					FeatureChangeEvent.MODIFIED_ATTRIBUTE_FEATURE_CHILD_LAYER),
					FeatureEventManager.AFTER_MODIFY);
			fireTableDataChanged();
			fireTableStructureChanged();
//			fireTableCellUpdated(row, col);
		} catch (IllegalAttributeException e) {
			e.printStackTrace();
		}
	}
	
	public void removeRow(int row){
		log.info("Borrando fila " + row);
		Feature f = (Feature)featureList.get(row);
		
		featureList.remove(row);
		try {
			FeatureHierarchyWrapper.getInstance().removeChild(f, parentId);
			featureTable.remove(f.getID());
		} catch (GisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fireTableRowsDeleted(row, row);
	}
	
	public void addRow() throws IllegalAttributeException, IOException,GisException{
		if(this.parentId== null){
			//Chequeo que hayan seleccionado un elemento de la capa padre
			throw new GisException("Debe Seleccionar un elemento del tab anterior");
						
		}
		Feature f = modelAccess.createEmptyFeature(layerDefinition.getNombre(), null);
		//Feature f = featureType.create(null);
		
		// El atributo del feature hijo que hace referencia al id del padre, no es un string. Es un FeatureReferenceAttribute.
		// Utilizando dicho tipo de atributo, el valor de parentID es convertido a los valores reales de la clave en la base
		// mediante el FIDMapper de la capa padre.
		
		String referencedLayer = ((FeatureReferenceAttribute) featureType.getAttributeType(joinKey).createDefaultValue()).getReferencedLayer();
		f.setAttribute(joinKey, new FeatureReferenceAttribute(parentId, referencedLayer));
		
		modelAccess.notifyChangeListeners(new FeatureChangeEvent(f,
				f.getFeatureType().getTypeName(), null, null,
				FeatureChangeEvent.NEW_FEATURE_CHILD_LAYER),
				FeatureEventManager.BEFORE_MODIFY);
		
		// Creo un nuevo mapa, con las claves: nombre de capa padre, y una por cada capa hija
		Map<String, Map<String, Feature>> itemMap = new HashMap<String, Map<String, Feature>>();
		Map<String, Feature> unaryFeatureMap = new HashMap<String, Feature>();
		
		unaryFeatureMap.put(f.getID(), f);
		itemMap.put(featureType.getTypeName(), unaryFeatureMap);
		
		Iterator<ChildMetadata> childDefinitionIterator = layerDefinition.getMetadata().getChildrenMetadata().iterator();
		
		while (childDefinitionIterator.hasNext()) {
			itemMap.put(childDefinitionIterator.next().getLayerName(), new HashMap<String, Feature>());
		}
		
		featureTable.put(f.getID(), itemMap);
		try {
			FeatureHierarchyWrapper.getInstance().addChild(f, itemMap, parentId);
		} catch (GisException e) {
			e.printStackTrace();
		}
		featureList.add(0,f);
		fireTableRowsInserted(0, 0);
	}
	
	public String getFeatureID(int row){
		return ((Feature)featureList.get(row)).getID();
	}
	
	public Map getChildData(int row, String child){
		return (Map)((Map)featureTable.get(getFeatureID(row))).get(child);
	}
	
	public String getLayerName(){
		return layerDefinition.getNombre();
	}
	
	public Feature getFeature(int row) {
		return ((Feature) featureList.get(row));
	}
	

}
