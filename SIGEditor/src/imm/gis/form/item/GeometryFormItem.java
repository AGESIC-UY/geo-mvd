package imm.gis.form.item;

import imm.gis.form.GeometryPropertiesPanel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;

import org.geotools.feature.AttributeType;

import com.vividsolutions.jts.geom.Geometry;

public class GeometryFormItem extends AbstractFormItem implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JButton showProperties;
	private Geometry geom;
	
	
	
	public GeometryFormItem(AttributeType at, Object value) {
		super(at);
		
		if (value!=null && !Geometry.class.isAssignableFrom(value.getClass()))
			throw new RuntimeException("Unknown geometry");
		
		geom = (Geometry) value;
		
		if (value != null)
			showProperties = new JButton("Mostrar propiedades");
		else {
			showProperties = new JButton("Nulo");
			showProperties.setEnabled(false);
		}
		
		showProperties.addActionListener(this);
	}
	
	public Object getValue() {
		return geom;
	}

	public JComponent getValueComponent() {
		return showProperties;
	}

	public void setEnabled(boolean enabled) {
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		
		if (value != null)
			showProperties = new JButton("Mostrar propiedades");
		else {
			showProperties = new JButton("Nulo");
			showProperties.setEnabled(false);
		}
		
		return showProperties;
	}

	public Object getCellEditorValue() {
		return null;
	}

	protected void setNewValues(Object newValues) {
		changed = true;
		
		if (!(newValues instanceof Geometry))
			throw new RuntimeException("Expected Geometry");
		
		geom = (Geometry) newValues;
	}

	public void actionPerformed(ActionEvent e) {
		new GeometryPropertiesPanel(geom);
	}
	
	public boolean hasChanged() {
		return changed;
	}
}
