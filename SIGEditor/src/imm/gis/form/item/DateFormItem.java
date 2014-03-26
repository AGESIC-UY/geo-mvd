package imm.gis.form.item;

import java.awt.Component;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.geotools.feature.AttributeType;

import com.toedter.calendar.JDateChooser;

public class DateFormItem extends AbstractFormItem implements TableCellRenderer {
	private JDateChooser dateChooser;

	
	
	public DateFormItem(AttributeType _at,Object value) {
		super(_at);
		dateChooser = new JDateChooser("dd/MM/yyyy", "##/##/####", '_');
		if(value!=null){
			dateChooser.setDate((Date)value);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public Object getValue() {
		return dateChooser.getDate();
	}


	public JComponent getValueComponent() {
		return dateChooser;
	}


	public void setEnabled(boolean enabled) {
		dateChooser.setEnabled(enabled);
	}


	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		dateChooser.setEnabled(true);
		if(value!=null){
			this.dateChooser.setDate((Date)value);
		}
		return this.dateChooser;
	}


	public Object getCellEditorValue() {
		return dateChooser.getDate();
	}


	protected void setNewValues(Object newValues) {
		changed = true;
		
		if (newValues == null)
			dateChooser.setDate(null);
		else {
			if (!(newValues instanceof Date))
				throw new RuntimeException("Expected Date");
			
			dateChooser.setDate((Date) newValues);
		}
	}


	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		dateChooser.setEnabled(false);
		if(value!=null){
			this.dateChooser.setDate((Date)value);
		}
		
		return this.dateChooser;
	}

	public boolean hasChanged() {
		return changed;
	}
}
