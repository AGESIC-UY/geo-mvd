package imm.gis.form;

import imm.gis.AppContext;
import imm.gis.GisException;
import imm.gis.client.fidmapper.FidMapperUtil;
import imm.gis.core.controller.ContPpal;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.interfaces.IDrawPanel;
import imm.gis.core.layer.Layer;
import imm.gis.core.layer.metadata.ChildMetadata;
import imm.gis.core.layer.metadata.LayerAttributeMetadata;
import imm.gis.core.layer.metadata.LayerAttributePresentation;
import imm.gis.core.layer.metadata.LayerMetadata;
import imm.gis.core.model.IFeatureHierarchyWrapper;
import imm.gis.form.item.AbstractFormItem;
import imm.gis.form.item.DateCellRenderer;
import imm.gis.gui.table.TextAreaEditor;
import imm.gis.gui.table.TextAreaRenderer;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.IllegalAttributeException;

import com.toedter.calendar.JDateChooserCellEditor;
import com.vividsolutions.jts.geom.Point;

public class DefaultForm extends AbstractForm {
	private static final long serialVersionUID = 1L;
	private JTabbedPane mainPanel = new JTabbedPane();
	private Map<String, Map> formData = null;
	private Map editedData = null;
	private ICoreAccess coreAccess = null;

	public DefaultForm(ICoreAccess ca, int formMode) {
		super(ca, formMode);
		this.coreAccess = ca;
	}

	public Map getEditedData() throws IllegalAttributeException {
		String editedLayer = getFeatureType().getTypeName();
		Iterator ei = editedData.keySet().iterator();

		Map em;
		Map ec;
		Map fm;
		Feature original;
		AbstractFormItem afi;
		Iterator values;

		while (ei.hasNext()) {
			em = (Map) editedData.get(ei.next());
			ec = (Map) em.get(editedLayer);

			original = (Feature) ec.values().iterator().next();
			fm = (Map) formData.get(original.getID());

			values = fm.values().iterator();

			while (values.hasNext()) {
				afi = (AbstractFormItem) values.next();
				original.setAttribute(afi.getAttributeType().getLocalName(), afi
						.getValue());
			}
		}

		return editedData;
	}

	protected void showForm(IFeatureHierarchyWrapper data, Map<String,Map<String,Object>> calculatedAttributes, final boolean insertDeleteMultipleChild) {
		AppContext ap = AppContext.getInstance();
		final String editedLayer = getFeatureType().getTypeName();
		Layer rootLayerDef = ap.getCapa(editedLayer);
		Layer layerDef;


		setTitle("Capa " + editedLayer);
		// Por ahora lo unico que hago es mostrar la info del padre
		mainPanel.removeAll();
		formData = new HashMap<String, Map>();
		editedData = data.getOriginalMap();

		Feature mainFeature = null;
		Map item;

		JPanel contentPanel;

		Iterator keyIterator;
		String currentKey;

		Iterator iterator = editedData.keySet().iterator();
		// Para cada item de la coleccion, meto toda su info en un tab
		while (iterator.hasNext()) {

			// Construyo un panel a ser agregado como otro tab dentro del
			// JTabbedPane principal
			contentPanel = new JPanel();
			contentPanel
					.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

			// Primero construyo la info del feature principal

			item = (Map) editedData.get(iterator.next());

			mainFeature = (Feature) ((Map) item.get(editedLayer)).values()
					.iterator().next();

			AttributePanel attributePanel = null;
			if (calculatedAttributes != null)
				attributePanel = new AttributePanel(this.mode, coreAccess, rootLayerDef, this, mainFeature, 
											calculatedAttributes.get(mainFeature.getID()));
			else
				attributePanel = new AttributePanel(this.mode, coreAccess, rootLayerDef, this, mainFeature, 
						null);
			
			contentPanel.add(attributePanel);

			formData.put(mainFeature.getID(), attributePanel.getFormItems());

			// Ahora agrego una tabla para cada coleccion de hijos
			keyIterator = item.keySet().iterator();
			while (keyIterator.hasNext()) {
				currentKey = (String) keyIterator.next();
				if (!currentKey.equals(editedLayer)) {
					contentPanel.add(Box.createVerticalStrut(10));
					JTabbedPane tabPanel = new JTabbedPane();

					Map childItem = (Map) item.get(currentKey);
					AppContext.getInstance().getCapa(currentKey)
					.getCtxAttrManager().setIfhw(this.currentFhw);

					boolean bool_val = mode == NEW_FEATURE
					|| (mode != SHOW_FEATURE && isEditableChild(getChild(
							currentKey, childItem)));
					final JTable childTable = getChildTable(
							mainFeature.getID(), editedLayer, currentKey,
							childItem, bool_val);
					// childPanel2.fi
					tabPanel.addTab(currentKey, getChildPanel(currentKey,
							childTable, bool_val,insertDeleteMultipleChild)); // Tab
					// del
					// hijo
//					updateChildPanelButtons(currentKey, bool_val);

					layerDef = ap.getCapa(currentKey); // Creo tab de hijos del
					// hijo
					final Map<String, JTable> tabTables = new HashMap<String,JTable>();
					for (Iterator it = layerDef.getMetadata()
							.getChildrenMetadata().iterator(); it.hasNext();) {
						ChildMetadata cd = (ChildMetadata) it.next();
						final JTable childChildTable = getChildTable(null,
								currentKey, cd.getLayerName(), null, bool_val);
						tabTables.put(cd.getLayerName(), childChildTable);
						tabPanel.addTab(cd.getLayerName(), getChildPanel(cd
								.getLayerName(), childChildTable, bool_val, insertDeleteMultipleChild)); // Tab del
						// hijo
					}
					tabPanel.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							JTabbedPane tabs = (JTabbedPane) e.getSource();
							int index = tabs.getSelectedIndex();
							int row = childTable.convertRowIndexToModel(childTable.getSelectedRow());

							if (index > 0) { // Me eligieron un tab hijo del
								// hijo
								EditableFeatureTableModel ftmPadre = (EditableFeatureTableModel) childTable
										.getModel();
								JTable table = (JTable) tabTables.get(tabs
										.getTitleAt(index));
								EditableFeatureTableModel ftmHijo = (EditableFeatureTableModel) table
										.getModel();
								if (row >= 0) { // Hay una fila elegida en la
									// tabla del hijo

									//Feature selectedValue = (Feature) ftmPadre
									//		.getFeature(row);
									//boolean bool_val = mode == NEW_FEATURE
									//		|| (mode != SHOW_FEATURE && isEditableChild(selectedValue));
/*									updateChildPanelButtons(ftmHijo
											.getLayerName(), bool_val);
*/									ftmHijo.setParentID(ftmPadre
											.getFeatureID(row));
									ftmHijo.setFeatureCollection(ftmPadre
											.getChildData(row, ftmHijo
													.getLayerName()));
									AppContext.getInstance().getCapa(
											ftmHijo.getLayerName())
											.getCtxAttrManager().setIfhw(
													currentFhw);
								} else {
/*									updateChildPanelButtons(ftmHijo
											.getLayerName(), false);
*/									ftmHijo.setParentID(null);
									ftmHijo.setFeatureCollection(null);
								}
							}
						}
					});
					contentPanel.add(tabPanel);
				}
			}			
			mainPanel.addTab(FidMapperUtil.getInstance().getID(mainFeature.getFeatureType().getTypeName(), mainFeature.getID()).toString(), contentPanel);
		}
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
						
  	    //Resalto la geometria del tab seleccionado	
  	    mainPanel.addChangeListener(new ChangeListener()
        {
           // @Override
          public void stateChanged(ChangeEvent e)
            {
        	  String fid;
        	  JTabbedPane jt = (JTabbedPane) e.getSource();
        	  if (jt.getSelectedIndex() == -1)
        		  fid = jt.getTitleAt(0);
        	  else
        		  fid = jt.getTitleAt(jt.getSelectedIndex());
        	  
        	 
        	  if ((coreAccess.getIModel().getFeature(editedLayer, fid, true) != null) && 
        			  !(coreAccess.getIModel().getFeature(editedLayer, fid, true).getDefaultGeometry() instanceof Point)){
        		  ((ContPpal)coreAccess).getSelectionModel().unselectAll();
        		  ((ContPpal)coreAccess).getSelectionModel().selectFeature(coreAccess.getIModel().getFeature(editedLayer, fid, true));
        	  }
            };
         });
	}

/*	private void updateChildPanelButtons(String currentKey, boolean bool_val) {
		JButton[] tmp = (JButton[]) this.childPanelButtons.get(currentKey);
		for (int i = 0; i < tmp.length; i++) {
			tmp[i].setEnabled(bool_val);
		}
	}
*/
	private boolean isEditableChild(Feature child) {
		if (child == null)
			return true;

		String layerName = child.getFeatureType().getTypeName();
		AppContext.getInstance().getCapa(layerName).getCtxAttrManager()
				.setIfhw(this.currentFhw);
		return ((Boolean) AppContext.getInstance().getCapa(layerName)
				.getCtxAttrManager()
				.getAttributeProperty(child, "", "editable")).booleanValue();
	}

	private Feature getChild(String childLayerName, Map childItem) {
		if (childItem != null) {
			Iterator i = childItem.values().iterator();
			Map item;
			Map value;
			if (i.hasNext()) {
				item = (Map) i.next();
				value = (Map) item.get(childLayerName);
				return ((Feature) value.values().iterator().next());
			}
		}
		return null;
	}

	private JPanel getChildPanel(String childLayer, final JTable childTable, boolean editable, final boolean insertDeleteMultiple) {
		JPanel childPanel = new JPanel();
		childPanel.setLayout(new BoxLayout(childPanel, BoxLayout.Y_AXIS));


		JScrollPane childScrollPane = new JScrollPane(childTable);
		childScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		if (mode != SHOW_FEATURE && (mode != NEW_FEATURE) && editable){
			JPanel insDeletePanel = new JPanel(new FlowLayout());
			final JButton butIns = new JButton("Agregar fila");
			final JButton butDelete = new JButton("Borrar fila");

			final EditableFeatureTableModel ftm = (EditableFeatureTableModel) childTable
					.getModel();

			butIns.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						ftm.addRow();
						if (!insertDeleteMultiple){
							butIns.setEnabled(false);
							butDelete.setEnabled(false);
						}
					} catch (IllegalAttributeException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					} catch (GisException e2) {
						javax.swing.JOptionPane.showMessageDialog(childTable, "Debe Seleccionar un elemento del tab anterior", "ERROR",javax.swing.JOptionPane.ERROR_MESSAGE);
						
					}

				}
			});
//			butIns.setEnabled(mode != SHOW_FEATURE);

			
			butDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int row = childTable.getSelectedRow();
					if (row >= 0) {						
						ftm.removeRow(childTable.convertRowIndexToModel(row));
						if (!insertDeleteMultiple){
							butIns.setEnabled(false);
							butDelete.setEnabled(false);
						}
					}
				}
			});
//			butDelete.setEnabled(mode != SHOW_FEATURE);

			insDeletePanel.add(butIns);
			insDeletePanel.add(butDelete);
			childPanel.add(insDeletePanel);			
		}
		
		childPanel.add(childScrollPane);
/*		this.childPanelButtons.put(childLayer, new JButton[] { butIns,
				butDelete });
*/		return childPanel;
	}

	private JTable getChildTable(String parentFID, String parentLayer,
			String childLayer, Map<String, Map> childData, boolean editable) {

		AppContext ap = AppContext.getInstance();
		Layer childLayerDefinition = ap.getCapa(childLayer);

		ChildMetadata childDefinition = null;

		Iterator childDefIterator = ap.getCapa(parentLayer).getMetadata()
				.getChildrenMetadata().iterator();

		while (childDefIterator.hasNext()) {
			childDefinition = (ChildMetadata) childDefIterator.next();

			if (childDefinition.getLayerName().equals(childLayer))
				break;
		}

		Map<String, LayerAttributeMetadata> attributeDefinitions = new LinkedHashMap<String, LayerAttributeMetadata>(childLayerDefinition
				.getMetadata().getAttributesMetadata());

		attributeDefinitions.remove(childDefinition.getParentIdAttribute());

		final JTable childTable = new JTable();
		childTable.setAutoCreateRowSorter(true);
		childTable.setDefaultEditor(Date.class, new JDateChooserCellEditor());
		childTable.setDefaultRenderer(Date.class, new DateCellRenderer());
		childTable.setRowHeight((int)Math.round(childTable.getRowHeight() * 1.3));
		if (editable){
			InputMap inputMap = childTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			ActionMap actionMap = childTable.getActionMap();
			KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
			Object key  = inputMap.get(keyStroke);
			final Action oldAction = actionMap.get(key);
			
			Action newAction = new AbstractAction(){
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e){
					int row = childTable.getSelectedRow();
					int rows = childTable.getRowCount();
					
					if (row == rows - 1){
						try {
							try {
								((EditableFeatureTableModel)childTable.getModel()).addRow();
							} catch (GisException e1) {
								javax.swing.JOptionPane.showMessageDialog(childTable, "Debe Seleccionar un elemento del tab anterior", "ERROR",javax.swing.JOptionPane.ERROR_MESSAGE);
								
							}
						} catch (IllegalAttributeException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}					
					oldAction.actionPerformed(e);
				}
			};
			actionMap.put(key, newAction);			
		}
		
		ArrayList<String> columnsList = new ArrayList<String>();

		// Obtengo la metadata de la capa hija
		LayerMetadata layerMetadata = ap.getCapa(childLayer).getMetadata();
		String columnName;
		Iterator it;
		for (it = attributeDefinitions.keySet().iterator(); it
				.hasNext();) {
			columnName = (String) it.next();
			if (layerMetadata.getAttributeMetadata(columnName).getShow()){
				columnsList.add(columnName);
			}
		}
		
		String[] columns = new String[columnsList.size()];
		for (int i = 0; i < columnsList.size(); i++){
			columns[i] = (String)columnsList.get(i);
			if (layerMetadata.getAttributeMetadata(columns[i]).getPresentation().getType() == LayerAttributePresentation.ATTR_PRESENTATION_TEXT_AREA){
				childTable.addColumn(new TableColumn(i, 40));				
			} else {
				childTable.addColumn(new TableColumn(i, 20));				
			}
		}

		final EditableFeatureTableModel ftm = new EditableFeatureTableModel(
				parentFID, childDefinition.getParentIdAttribute(), childData,
				childLayerDefinition, attributeDefinitions, columns, mode, coreAccess
						.getIModel());

		childTable.setModel(ftm);

		FormFactory formFactory = FormFactory.getInstance();
		
		for (int j = 0; j < columns.length; j++) {

			AttributeType type = childLayerDefinition.getFt().getAttributeType(
					columns[j]);

			// Las fechas y las text areas ya estan resueltas			
			if (type.getBinding().getClass().isAssignableFrom(Date.class)){
				continue;
			} else if (layerMetadata.getAttributeMetadata(columns[j])
				.getPresentation().getType()
				== LayerAttributePresentation.ATTR_PRESENTATION_TEXT_AREA){
				childTable.getColumnModel().getColumn(j).setCellEditor(new TextAreaEditor());
				childTable.getColumnModel().getColumn(j).setCellRenderer(new TextAreaRenderer());
				continue;
			}
			
			LayerAttributeMetadata attributeMetadata = layerMetadata
					.getAttributeMetadata(type.getLocalName());

			AbstractFormItem afi = formFactory.createFormItem(mode, null, type, attributeMetadata, childTable);
			childTable.getColumn(ftm.getColumnName(j)).setCellEditor(afi);
		}

		ftm.addTableModelListener(new FeatureTableModelListener());

		return childTable;
	}

	protected JComponent constructUI() {
		return new JScrollPane(mainPanel);
	}

//	public void close() {
//		super.close();
//		setVisible(false);
//	}
}
