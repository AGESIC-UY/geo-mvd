package imm.gis.form.item;

import imm.gis.core.controller.IMapListener;
import imm.gis.core.controller.MapEvent;
import imm.gis.core.feature.FeatureReferenceAttribute;
import imm.gis.core.feature.FeatureReferenceAttributeType;
import imm.gis.core.gui.GuiUtils;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.form.item.interactor.ItemInteractor;
import imm.gis.form.item.interactor.OnlyOneNotNull;
import imm.gis.tool.ITool;
import imm.gis.tool.misc.ChooseFeatureTool;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.geotools.feature.AttributeType;

public class MapElementFormItem extends AbstractFormItem implements IMapListener {

	private static final long serialVersionUID = 1L;
	
	private JButton chooseMapElement = null;
	private JButton showMapElement = null;
	private JPanel container;
	
	private ICoreAccess coreAccess = null;
	private String layer;
	private Component parent;
	
	private ITool previousTool;
	
	
	
	public MapElementFormItem(AttributeType at, Object value, ICoreAccess ca, String layer) {
		this(at, value, ca, layer, null);
	}

	public MapElementFormItem(AttributeType at, Object value, ICoreAccess ca, String layer, Component parent) {
		super(at);
		
		this.coreAccess = ca;
		this.layer = layer;
		this.parent = parent;
		
		chooseMapElement = new JButton();
		chooseMapElement.setPreferredSize(new Dimension(80,16));
		chooseMapElement.setToolTipText("Seleccionar elemento");
		chooseMapElement.addActionListener(this);
		
		showMapElement = new JButton(GuiUtils.loadIcon("Paste16.gif"));
		showMapElement.setPreferredSize(new Dimension(16,16));
		showMapElement.setToolTipText("Ver elemento referenciado");
		showMapElement.addActionListener(this);
		
		updateButtons(value);
		
		container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		
		container.add(chooseMapElement);
		container.add(showMapElement);

		if (!at.getClass().isAssignableFrom(FeatureReferenceAttributeType.class))
			throw new IllegalArgumentException("AttributeType must be FeatureReferenceAttributeType for MapElementFormItem");
		
		ItemInteractor ii = new OnlyOneNotNull();
		ii.setMyValue(value);
		setItemInteractor(ii);
	}
	
	private void updateButtons(Object value) {
		if (value != null && value.toString() != null && !value.toString().equals("")) {
			String textParts[] = value.toString().split("\\.");
			
			if (textParts[textParts.length - 1].equals("null")) {
				chooseMapElement.setText(null);
				chooseMapElement.setIcon(GuiUtils.loadIcon("Arrow16.GIF"));
				showMapElement.setEnabled(false);
				showMapElement.setToolTipText(null);
			}
			else {
				chooseMapElement.setIcon(null);
				chooseMapElement.setText(value.toString());
				showMapElement.setEnabled(true);
				showMapElement.setToolTipText("Mostrar elemento seleccionado");
			}
		}
		else {
			chooseMapElement.setText(null);
			chooseMapElement.setIcon(GuiUtils.loadIcon("Arrow16.GIF"));
			showMapElement.setEnabled(false);
			showMapElement.setToolTipText(null);
		}
	}
	
	public Object getValue() {
		return getFeatureReferenceAttribute(chooseMapElement.getText());
	}

	public JComponent getValueComponent() {
		return container;
	}

	
	/*
	 * Requerido por IMapListener
	 */
	
	public String getListenedSchema() {
		return layer;
	}

	public void mapEvent(MapEvent me) {
		changed = true;
		
		updateButtons(me.getFeature().getID());

		coreAccess.setActiveTool(previousTool);
		
        fireEditingStopped();
		fireAttributeChanged(me.getFeature().getID());
		
		if (parent != null)
			parent.setVisible(true);
	}

	/*
	 * Requerido por ActionListener
	 */
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == chooseMapElement) {
			if (parent != null)
				parent.setVisible(false);
			
			previousTool = coreAccess.getActiveTool();
			
			coreAccess.setActiveTool(new ChooseFeatureTool(coreAccess, this));
		}
		else if (e.getSource() == showMapElement) {
			String referencedType = ((FeatureReferenceAttribute) getAttributeType().createDefaultValue()).getReferencedLayer();
			String FID = chooseMapElement.getText();
			
			try {
				coreAccess.getIForm().openShowFeatureForm(coreAccess.getIModel().getFeature(referencedType, FID, false));
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/*
	 * Requerido por AbstractCellEditor
	 */
	
	public Object getCellEditorValue() {
		return getFeatureReferenceAttribute(chooseMapElement.getText());
	}
	
	public Component getTableCellEditorComponent(JTable table, Object val, boolean sel, int row, int col) {
		
		updateButtons(val);

		return chooseMapElement;
	}
	
	private FeatureReferenceAttribute getFeatureReferenceAttribute(String FID) {
		
		if (FID == null || FID.equals(" Ninguno "))
			return null;
		else
			return new FeatureReferenceAttribute(FID, ((FeatureReferenceAttribute) getAttributeType().createDefaultValue()).getReferencedLayer());
	}

	public void setEnabled(boolean enabled) {
		chooseMapElement.setEnabled(enabled);
		
		if (!enabled)
			chooseMapElement.setToolTipText(null);
		else
			chooseMapElement.setToolTipText("Seleccionar elemento");
	}

	protected void setNewValues(Object newValues) {
		changed = true;
		
		updateButtons(newValues);
	}
	
	public boolean hasChanged() {
		return changed;
	}
}
