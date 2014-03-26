package imm.gis.form.item;

import imm.gis.AppContext;
import imm.gis.consulta.ICargadorLOV;
import imm.gis.consulta.LOVLoader;
import imm.gis.core.feature.ExternalAttribute;
import imm.gis.core.gui.FeatureUserChooser;
import imm.gis.core.layer.metadata.LayerMetadata;
import imm.gis.form.item.interactor.FilteringLovLoader;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;


public class LovPanelFormItem extends AbstractFormItem implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JButton chooseElement;
	private FeatureUserChooser userChooser; 
	private String layer;
	private ExternalAttribute externalAttribute;
	private ICargadorLOV iclov;
	
	public LovPanelFormItem(AttributeType _at, Feature feat, Dialog dialog){
		super(_at);
		layer = ((ExternalAttribute)_at.createDefaultValue()).getOriginLayer();
		userChooser = new FeatureUserChooser(dialog);
		userChooser.setTitle(layer);
		chooseElement = new JButton();
		chooseElement.setHorizontalAlignment(SwingConstants.LEFT);
		chooseElement.setPreferredSize(new Dimension(80,16));
		chooseElement.setToolTipText("Seleccionar elemento");
		chooseElement.addActionListener(this);
		chooseElement.setText(feat == null ? null : feat.getAttribute(_at.getLocalName()).toString());
		iclov = new LOVLoader(_at);
		setItemInteractor(new FilteringLovLoader(iclov));
	}
	
	@Override
	public Object getValue() {
		return externalAttribute;
	}

	@Override
	public JComponent getValueComponent() {
		return chooseElement;
	}

	@Override
	public boolean hasChanged() {
		return changed;
	}

	@Override
	public void setEnabled(boolean enabled) {
		chooseElement.setEnabled(enabled);
		
		if (!enabled)
			chooseElement.setToolTipText(null);
		else
			chooseElement.setToolTipText("Seleccionar elemento");
	}

	@Override
	protected void setNewValues(Object value) {
		changed = true;
		
		if (value == null){
			chooseElement.setText(null);
		} else if (value instanceof Object[] && ((Object[])value).length > 0){
			chooseElement.setText(((Object[])value)[0].toString());			
		} else if (value instanceof Collection && !((Collection)value).isEmpty()){
			chooseElement.setText(((Collection)value).iterator().next().toString());						
		} else {
			chooseElement.setText(value.toString());			
		}
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		chooseElement.setText(value.toString());
		return chooseElement;
	}

	public Object getCellEditorValue() {
		return externalAttribute;
	}

	public void actionPerformed(ActionEvent e) {
		FeatureCollection fc = null;
		
		try {
			AppContext context = AppContext.getInstance();
			LayerMetadata metadata = context.getCapa(layer).getMetadata();

			Set<String> attsSet = metadata.getAttributesMetadata().keySet();
			String atts[] = (String[])attsSet.toArray(new String[attsSet.size()]);
			fc = iclov.getFeatures(atts);
			
			userChooser.choose(fc, metadata.getVisibleAttributes());
			if (userChooser.getSelectedOption() != null){
				Feature f = userChooser.getSelectedOption();
				externalAttribute = new ExternalAttribute(f.getID(), f.getAttribute(getAttributeType().getLocalName()), layer);
				chooseElement.setText(f.getID());				
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally{
			if (fc != null){
				fc.purge();
			}
		}
	}
}
