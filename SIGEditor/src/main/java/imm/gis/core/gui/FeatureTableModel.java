package imm.gis.core.gui;

import javax.swing.table.AbstractTableModel;

import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;

public class FeatureTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private FeatureCollection featureTable;
	private transient Feature[] featureArray;
	private String[] columns;
	
	public FeatureTableModel() {
	}

	public FeatureTableModel(final FeatureCollection features) {
		setFeatureCollection(features, null);
	}

	public FeatureTableModel(final FeatureCollection features, String[] fields) {
		setFeatureCollection(features, fields);
	}

	public void setFeatureCollection(final FeatureCollection features) {
		setFeatureCollection(features, null);
	}

	public void setFeatureCollection(final FeatureCollection features, String[] fields) {
		featureArray = null;
		featureTable = features;
		
		if (fields == null || fields.length == 0){
			Feature firstFeature = featureTable.features().next();
			FeatureType firstType = firstFeature.getFeatureType();
			columns = new String[firstType.getAttributeCount()];
			
			for (int count = 0; count < firstType.getAttributeCount(); count++){
				columns[count] = firstType.getAttributeType(count).getLocalName();
			}
		} else {
			columns = fields;
		}
		
		fireTableStructureChanged();		
	}

	public int getColumnCount() {
		if (featureTable == null || featureTable.isEmpty()) {
			return 0;
		}
		return columns == null ? featureTable.features().next().getNumberOfAttributes() :  columns.length;
	}

	public int getRowCount() {
		if (featureTable == null) {
			return 0;
		}
		return featureTable.size();
	}

	public String getColumnName(int col) {
		if (featureTable == null || featureTable.isEmpty()) {
			return null;
		}

		return columns[col];
	}

	public Object getValueAt(final int row, final int col) {
		if (featureArray == null) {
			featureArray = (Feature[]) featureTable
					.toArray(new Feature[featureTable.size()]);
		}
		return featureArray[row].getAttribute(columns[col]);
	}
}
