package imm.gis.form.item;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import imm.gis.consulta.ICargadorLOV;
import imm.gis.core.feature.ExternalAttribute;
import imm.gis.core.layer.metadata.LayerAttributeMetadata;
import imm.gis.form.item.interactor.FilteringLovLoader;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;

import org.geotools.feature.AttributeType;


/**
 * Se utiliza para cargar un lov cuando el usuario deja de teclear en el combo
 * 
 * @author agrassi
 *
 */
class LOVKeyListener extends KeyAdapter {
	
	private Timer t;
	
	public LOVKeyListener(ICargadorLOV iclov, JComboBox combo) {
		
		RefreshLOV cargar = new RefreshLOV(iclov, combo);
		this.t = new Timer(2000,cargar);
		cargar.setTimer(t);
	}
		
	public void keyReleased(KeyEvent e) {
		if (t.isRunning())
			t.restart();
		else
			t.start();
	}
	
	/**
	 * 	Este ActionListener es el que se encarga de cargar el lov luego de dos segundos de
	 *	que se haya soltado la ultima tecla
 	 */
	private class RefreshLOV implements ActionListener {
		
		private ICargadorLOV iclov;
		private JComboBox combo;
		private Timer t;
		
		public RefreshLOV(ICargadorLOV iclov, JComboBox combo) {
			this.iclov = iclov;
			this.combo = combo;
		}
		
		public void setTimer(Timer t) {
			this.t = t;
		}
		
		public void actionPerformed(ActionEvent e) {
			// Paro el timer
			t.stop();
			
			try {
				// Cargo los valores para el lov en caso de que se haya introducido algun valor
				if (combo.getEditor().getItem() !=null &&
					!combo.getEditor().getItem().toString().equals("")) {
					
					iclov.restrictValues(combo.getEditor().getItem().toString().toUpperCase());
					iclov.setSearchMethod(ICargadorLOV.SEARCH_LIKE);
					
					ExternalAttribute valores[] = iclov.getValores();
	
					// Si encuentro valores similares cargo el combo y sino aviso que no encontro nada
					if (valores!=null) {
					
						combo.removeAllItems();
						
						for (int i = 0; i < valores.length; i++)
							combo.addItem(valores[i]);
					}
					else  {
						combo.getEditor().selectAll();
						JOptionPane.showMessageDialog(null,"No se encontro ningun valor similar al introducido","Aviso",JOptionPane.INFORMATION_MESSAGE);
					}
						
				}
			}
			catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}




public class LovFormItem extends AbstractFormItem implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private boolean isNillable = false;
	private LOVKeyListener lazyKeyListener = null;
	
	private JComboBox attValue;
	
	
	
	public LovFormItem(AttributeType _at, Object value, ICargadorLOV iclov, LayerAttributeMetadata metadata) {
		super(_at);

		try {
			// Si la lista se carga bajo demanda, entonces la cargo cuando el usuario ingrese un substring
			if (metadata.isLazy()) {
				attValue = new JComboBox();
				attValue.setEditable(true);
				
				lazyKeyListener = new LOVKeyListener(iclov, attValue);
				
				attValue.getEditor().getEditorComponent().addKeyListener(lazyKeyListener);
			}
			else
				attValue = new JComboBox(iclov.getValores()==null? new Object[]{}:iclov.getValores() );
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		
		if (value != null) {
			if (iclov.isUnique()) {
				for (int i = 0; i < attValue.getItemCount(); i++) {
					ExternalAttribute ea = (ExternalAttribute) attValue.getItemAt(i);
					
					if (ea.getValue().equals(((ExternalAttribute) value).getValue())) {
						attValue.setSelectedIndex(i);
						break;
					}
				}
			}
			else
				attValue.setSelectedItem(value);
		}
		
		attValue.addActionListener(this);
		
		setItemInteractor(new FilteringLovLoader(iclov));
	}

	public void setNillable(boolean nillable, boolean select) {
		isNillable = nillable;
		
		
		if (nillable && lazyKeyListener == null) {
			attValue.addItem(null);
			
			if (select)
				attValue.setSelectedItem(null);
		}
	}
	
	public Object getValue() {
		return attValue.getSelectedItem();
	}

	public JComponent getValueComponent() {
		return attValue;
	}

	/*
	 * Requeridos por AbstractCellEditor
	 */
	public Object getCellEditorValue() {
		return attValue.getSelectedItem();
	}

	public Component getTableCellEditorComponent(JTable table, Object val,
			boolean sel, int row, int col) {

		if (val != null)
			attValue.setSelectedItem(val);

		return attValue;
	}

	/*
	 * Requerido por ActionListener
	 */
	public void actionPerformed(ActionEvent e) {
		changed = true;
		
		fireEditingStopped();
		
		ExternalAttribute selectedItem = (ExternalAttribute) attValue.getSelectedItem();
		
		if (selectedItem != null)
			fireAttributeChanged(selectedItem.getValue());
		else
			fireAttributeChanged(null);
		
		if (isLocking())
			setEnabled(false);
	}

	public void setEnabled(boolean enabled) {
		attValue.setEnabled(enabled);
	}

	protected void setNewValues(Object newValues) {
		//changed = true;
		
		attValue.removeActionListener(this);
		
		attValue.removeAllItems();
		
		if (newValues != null && !(newValues instanceof ExternalAttribute[]))
			throw new RuntimeException("Expected ExternalAttribute[]");
		
		ExternalAttribute[] optionList = (ExternalAttribute[]) newValues;
		
		if (optionList != null)
			for (int i = 0; i < optionList.length; i++)
				attValue.addItem(optionList[i]);
		
		if (isNillable)
			attValue.addItem(null);
		
		attValue.addActionListener(this);
		
		if (attValue.getSelectedItem() != null){ 
			fireAttributeChanged(((ExternalAttribute)attValue.getSelectedItem()).getValue());
		} else {
			fireAttributeChanged(null);
		}
	}
	
	public boolean hasChanged() {
		return changed;
	}
}
