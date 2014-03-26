package imm.gis.form;

import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.metadata.LayerAttributeMetadata;
import imm.gis.core.layer.metadata.LayerMetadata;
import imm.gis.form.item.AbstractFormItem;
import imm.gis.form.item.FIDFormItem;
import imm.gis.form.item.IAttributeChangeListener;
import imm.gis.form.item.IAttributeChangeNotifier;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.JTextComponent;

import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;

import com.vividsolutions.jts.geom.Geometry;

public class AttributePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private FeatureType featureType = null;

	private Map<String, AbstractFormItem> formItems = new HashMap<String, AbstractFormItem>();
	
	int mode;

	public AttributePanel(int mode, ICoreAccess coreAccess, Layer layer) {
		this(mode, coreAccess, layer, null, null, null);
	}

	public AttributePanel(int mode, ICoreAccess coreAccess, Layer layer,
			Component parent) {
		this(mode, coreAccess, layer, parent, null, null);
	}

	public AttributePanel(int mode, ICoreAccess coreAccess, Layer layer,
			Component parent, Feature feature, Map<String,Object> calculatedAttribute) {

		this.mode = mode;

		this.setLayout(new GridBagLayout());
		int attIndex = 0;

		LayerAttributeMetadata attributeMetadata;
		AttributeType attributeType;
		AbstractFormItem formItem = null;

		LayerMetadata layerMetadata = layer.getMetadata();
		featureType = layer.getFt();

		Component topLevelContainer = parent == null ? this : parent;

		int geometryIndex = -1;
		
		if (mode == IFeatureForm.SEARCH_FEATURE) {
			if (layerMetadata.isFIDQueryCapable()) {
				AttributeType attType = AttributeTypeFactory.newAttributeType(
						layerMetadata.getFidAttributeName()/*"FID"*/, String.class);
				formItem = FormFactory.getInstance().createFIDFormItem(layerMetadata
						.getName(), attType, null);
				addFormItem(attIndex, layerMetadata.getFidAttributeName(), formItem);

				attIndex++;

				addFormItem(formItem, attType);
			}
		}
		
		for (int i = 0; i < featureType.getAttributeCount(); i++) {

			attributeType = featureType.getAttributeType(i);
			attributeMetadata = layerMetadata
					.getAttributeMetadata(attributeType.getLocalName());

			if (!Geometry.class.isAssignableFrom(attributeType.getBinding())) {
				if (!attributeMetadata.getShow()) continue; 
				formItem = FormFactory.getInstance().createFormItem(mode, feature, attributeType,
						attributeMetadata, topLevelContainer);

				if (formItem != null) {

					addFormItem(attIndex, attributeMetadata.getLabel(), formItem);

					attIndex++;

					addFormItem(formItem, attributeType);
				}
			}
			else {
				if (attributeMetadata.getShow()){
					geometryIndex = i;
				}
			}
		}

		if (mode == IFeatureForm.SHOW_FEATURE && geometryIndex != -1) {
			attributeType = featureType.getAttributeType(geometryIndex);
			attributeMetadata = layerMetadata
					.getAttributeMetadata(attributeType.getLocalName());

			formItem = FormFactory.getInstance().createFormItem(mode, feature, attributeType,
					attributeMetadata, topLevelContainer);

			if (formItem != null) {

				this.add(new JLabel(attributeType.getLocalName()),
						new GridBagConstraints(0, attIndex, 1, 1, 0, 0,
								GridBagConstraints.WEST,
								GridBagConstraints.HORIZONTAL, new Insets(2, 2,
										2, 2), 0, 0));

				this.add(formItem.getValueComponent(), new GridBagConstraints(
						1, attIndex, 1, 1, 0, JScrollPane.class
								.isAssignableFrom(formItem.getValueComponent()
										.getClass()) ? 1.0 : 0,
						GridBagConstraints.EAST, GridBagConstraints.BOTH,
						new Insets(2, 2, 2, 2), 0, 0));

				attIndex++;

				addFormItem(formItem, attributeType);
			}
		}

		// Relaciono dentro de los consultables

		Iterator i = formItems.keySet().iterator();

		while (i.hasNext()) {

			String currentKey = (String) i.next();
			if (formItems.get(currentKey) instanceof FIDFormItem) {
				// Si el formITem es un FIDFORM item no me interesa procesar la
				// metadata

				continue;
			}

			attributeType = featureType.getAttributeType(currentKey);

			attributeMetadata = layerMetadata
					.getAttributeMetadata(attributeType.getLocalName());

			Iterator<String> dependsOn = attributeMetadata.getDependences();

			IAttributeChangeListener dependant = (IAttributeChangeListener) formItems
					.get(attributeType.getLocalName());

			while (dependsOn.hasNext())
				((IAttributeChangeNotifier) formItems.get(dependsOn.next()))
						.addAttributeChangeListener(dependant);
			
			if (attributeMetadata.isLockedDependence()) {
				((AbstractFormItem) formItems.get(attributeType.getLocalName())).setLockedDependence(true);
				((AbstractFormItem) formItems.get(attributeType.getLocalName())).setEnabled(false);
			}
		}
		
		//Agrego los atributos calculados (no mapeados a campos de tablas o vistas)
		if (calculatedAttribute != null){
			Iterator it = calculatedAttribute.keySet().iterator();
			while(it.hasNext()){
		      String name = (String) it.next();
		      this.add(new JLabel(name),
						new GridBagConstraints(0, attIndex, 1, 1, 0, 0,
								GridBagConstraints.WEST,
								GridBagConstraints.HORIZONTAL, new Insets(2, 2,
										2, 2), 0, 0));

		      String value = (String)calculatedAttribute.get(name);
		      
		      JTextComponent attValue = new JFormattedTextField();
		      
		      if (value != null) {
					((JFormattedTextField) attValue).setValue(value);
		      }
		      ((JFormattedTextField) attValue).setHorizontalAlignment(JFormattedTextField.LEFT);
		      attValue.setEditable(false);
			      
		      this.add(attValue, new GridBagConstraints(
							1, attIndex, 1, 1, 0, JScrollPane.class
									.isAssignableFrom(formItem.getValueComponent()
											.getClass()) ? 1.0 : 0,
							GridBagConstraints.EAST, GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));

				attIndex++;
			}
			
		}
		
	}

	private void addFormItem(AbstractFormItem formItem, AttributeType attType) {
		formItems.put(attType.getLocalName(), formItem);
	}

	private void addFormItem(int attIndex, String attName,
			AbstractFormItem formItem) {
		this.add(new JLabel(attName), new GridBagConstraints(0, attIndex, 1, 1,
				0, 0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

		this.add(formItem.getValueComponent(), new GridBagConstraints(1,
				attIndex, 1, 1, 0, JScrollPane.class.isAssignableFrom(formItem
						.getValueComponent().getClass()) ? 1.0 : 0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2,
						2, 2, 2), 0, 0));
	}

	public FeatureType getFeatureType() {
		return featureType;
	}

	public Map<String, AbstractFormItem> getFormItems() {
		return formItems;
	}

	public Map<String,Object> getChangedItems() {
		Map<String,Object>  toReturn = new HashMap<String,Object>();
		
		for (Iterator<String> it = formItems.keySet().iterator(); it.hasNext();) {
			String attName = it.next();
			
			if (formItems.get(attName).hasChanged())
				toReturn.put(attName, formItems.get(attName).getValue());
		}
		
		return toReturn;
	}
}
