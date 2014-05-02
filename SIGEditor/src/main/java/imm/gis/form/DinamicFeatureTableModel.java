package imm.gis.form;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;

public class DinamicFeatureTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Feature> data = new ArrayList<Feature>();
	private String attNames[];
	
	public DinamicFeatureTableModel(String att[]){
		setAttributeNames(att);
	}
	
	public void setAttributeNames(String att[]){
		attNames = att;
		fireTableStructureChanged();				
	}
	
	public void setFeatureCollection(FeatureCollection fc){
		data.clear();
		addFeatureCollection(fc);
	}
	
	public void addFeatureCollection(FeatureCollection fc){
		for (Iterator it = fc.iterator(); it.hasNext();){
			data.add((Feature)it.next());
		}
		
		fireTableStructureChanged();		
	}
	
	public int getColumnCount() {
		return (attNames == null ? 0 : attNames.length);
	}

	public String getColumnName(int colIndex){
		return attNames[colIndex];
	}
	
	public Class<?> getColumnClass(int colIndex){
		return (data.isEmpty()) ? null : getFeatureAt(0).getAttribute(getColumnName(colIndex)).getClass(); 
	}
	
	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex >= attNames.length){
			throw new RuntimeException("Columna invalida (" + columnIndex + ")");
		}
		
		Feature f = data.get(rowIndex);
		Object res = f.getAttribute(attNames[columnIndex]);
		
		return res;
	}

	public Feature getFeatureAt(int rowIndex){
		return data.get(rowIndex);
	}
}
