package imm.gis.form.item;

import imm.gis.core.layer.metadata.LayerAttributePresentation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.JTextComponent;

import org.geotools.feature.AttributeType;

public class BasicFormItem extends AbstractFormItem implements ActionListener, FocusListener {

	private static final long serialVersionUID = 1L;

	private JTextComponent attValue;

	private boolean need_scrollPane = false;

	private JComponent componentValue;

	
	
	public BasicFormItem(AttributeType at, Object value,
			LayerAttributePresentation lap) {

		super(at);

		if (lap.getType() == LayerAttributePresentation.ATTR_PRESENTATION_TEXT_AREA) {
			need_scrollPane = true;
			if ((lap.getColumns() != 0) && (lap.getRows() != 0))
				attValue = new JTextArea(lap.getRows(), lap.getColumns());
			else
				attValue = new JTextArea();
			componentValue = new JScrollPane(attValue);

		} else {
			Format f;

			if (Date.class.isAssignableFrom(at.getBinding())) {
				f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				attValue = new JFormattedTextField(f);
			} else if (Integer.class.isAssignableFrom(at.getBinding())) {
				f = NumberFormat.getIntegerInstance();
				attValue = new JFormattedTextField(f);
			} else if (Long.class.isAssignableFrom(at.getBinding())) {
				f = NumberFormat.getIntegerInstance();
				attValue = new JFormattedTextField(f);
			} else if (Double.class.isAssignableFrom(at.getBinding())) {
				f = DecimalFormat.getIntegerInstance();
				attValue = new JFormattedTextField(f);
			} else if (CharSequence.class.isAssignableFrom(at.getBinding())){
				DefaultFormatter df = new DefaultFormatter();
				df.setOverwriteMode(false);
				attValue = new JFormattedTextField(df);
			} else
				attValue = new JFormattedTextField();

			((JFormattedTextField) attValue).addActionListener(this);
		}

		
		if (attValue instanceof JFormattedTextField) {

			
				((JFormattedTextField) attValue).setColumns(lap.getColumns()==0 ? 15:lap.getColumns());

			((JFormattedTextField) attValue)
					.setFocusLostBehavior(JFormattedTextField.COMMIT);
			if (value != null) {

				((JFormattedTextField) attValue).setValue(value);
			}
			((JFormattedTextField) attValue).setHorizontalAlignment(JFormattedTextField.LEFT);
		}
		else {
			attValue.setText(value!=null ? value.toString():null);
		}
	attValue.addFocusListener(this);
	}

	public JComponent getValueComponent() {
		if (need_scrollPane) {
			return componentValue;
		}
		return attValue;
	}

	public Object getValue() {

		if (attValue instanceof JFormattedTextField)
			return adaptValue(((JFormattedTextField) attValue).getValue());
		else
			return attValue.getText();
	}

	private Object adaptValue(Object value) {
		if (value instanceof String && ((String) value).equals(""))
			return null;
		else
			return value;
	}
	
	
	/*
	 * Requeridos por AbstractCellEditor
	 */

	public Object getCellEditorValue() {
		return attValue.getText();
	}

	public Component getTableCellEditorComponent(JTable table, Object val,
			boolean sel, int row, int col) {
		if (val != null)
			attValue.setText(val.toString());
		else
			attValue.setText(null);

		return attValue;
	}

	/*
	 * Requerido por ActionListener
	 */

	public void actionPerformed(ActionEvent e) {
		changed = true;
		fireEditingStopped();
		fireAttributeChanged(attValue.getText());
	}

	private JTextComponent getTextComponent() {
		return attValue;
	}

	public void setEnabled(boolean enabled) {
		getTextComponent().setEditable(enabled);

	}

	
	protected void setNewValues(Object newValues) {
		changed = true;
		
		if (attValue instanceof JFormattedTextField){
			((JFormattedTextField)attValue).setValue(newValues);
		} else if (newValues != null){
			attValue.setText(newValues.toString());
		} else {
			attValue.setText(null);
		}
	}

	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * El focuslost no marca el atributo como cambiado. El tema es que sino,
	 * en el estado actual de cosas, al construir la gui se dispara
	 * el focuslost y lo marca siempre como cambiado.
	 * 
	 * (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		//changed = true;
		
		fireEditingStopped();
		fireAttributeChanged(attValue.getText());
	}
	
	public boolean hasChanged() {
		return changed;
	}
}
