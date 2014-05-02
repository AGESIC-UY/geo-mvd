package imm.gis.form;

import imm.gis.AppContext;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.interfaces.IModel;
import imm.gis.core.model.IFeatureHierarchyWrapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

class MultipleOkListener implements ActionListener {

	private ICoreAccess ica;
	private AttributePanel attributePanel;
	private IFeatureForm iform;
	
	public MultipleOkListener(ICoreAccess ica, IFeatureForm iform) {
		this.ica = ica;
		this.iform = iform;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Collection selected = ica.getISelection().getSelected(iform.getFeatureType().getTypeName());
			
			IModel model = ica.getIModel();
			Map<String,Object> changedItems = attributePanel.getChangedItems();
			
			String[] attributes = changedItems.keySet()
					.toArray(new String[] {});
			
			Object[] values = changedItems.values().toArray();
			
			if (attributes.length > 0) {
				for (Iterator iter = selected.iterator(); iter.hasNext();) {
					Feature element = (Feature) iter.next();
					model.modifyFeature(element, attributes, values);
				}
			}

			iform.close();
		}
		catch (NoSuchElementException e1) {
			e1.printStackTrace();
			ica.getIUserInterface().showError("Ocurrio un error al salvar los cambios", e1);
		}
		catch (IOException e1) {
			e1.printStackTrace();
			ica.getIUserInterface().showError("Ocurrio un error al salvar los cambios", e1);
		}
		catch (IllegalAttributeException e1) {
			e1.printStackTrace();
			ica.getIUserInterface().showError("Ocurrio un error al salvar los cambios", e1);
		}
		catch (Exception e1) {
			e1.printStackTrace();
			ica.getIUserInterface().showError("Ocurrio un error al salvar los cambios", e1);
		}
	}

	public void setAttributePanel(AttributePanel attributePanel) {
		this.attributePanel = attributePanel;
	}
}



class MultipleCancelListener extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private IFeatureForm iform;
	
	public MultipleCancelListener(IFeatureForm iform) {
		this.iform = iform;
	}
	
	public void actionPerformed(ActionEvent e) {
		iform.close();
	}

}

public class EditFeaturesForm extends JDialog implements IFeatureForm {

	private static final long serialVersionUID = 1L;

	private ICoreAccess ica;

	private JPanel buttons;

	private JPanel contentPanel;

	private JButton okButton;

	private JButton cancelButton;
	
	private FeatureType ft;

	private AttributePanel attributePanel;

	private MultipleOkListener okListener;
	
	public EditFeaturesForm(ICoreAccess ica) {
		super();
		this.setModal(true);
		this.ica = ica;
		
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		buttons = new JPanel();
		
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

		okButton = new JButton("   Ok   ");
		buttons.add(okButton);
		okButton.setHorizontalAlignment(SwingConstants.RIGHT);

		cancelButton = new JButton("Cancelar");
		buttons.add(cancelButton);
		cancelButton.setHorizontalAlignment(SwingConstants.RIGHT);

		okListener = new MultipleOkListener(ica, this);
		okButton.addActionListener(okListener);
		
		MultipleCancelListener cancelListener = new MultipleCancelListener(this);
		
		cancelButton.addActionListener(cancelListener);
		
		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
		getRootPane().getActionMap().put("ESCAPE", cancelListener);

	}

	public Feature getFeature() {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveChanges() {
		// TODO Auto-generated method stub

	}

	public FeatureType getFeatureType() {
		return ft;
	}

	public void setFeatureType(FeatureType ft) {
		this.ft = ft;

	}

	public void show(IFeatureHierarchyWrapper data) throws IllegalAttributeException{
		show(data, null, true);
	}
	
	public void show(IFeatureHierarchyWrapper data, Map<String, Map<String,Object>> calculatedAttributes, final boolean insertDeleteMultipleChild)
			throws IllegalAttributeException {

		setTitle("Capa " + getFeatureType().getTypeName());
		
		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		// AttributePanel(int mode, ICoreAccess coreAccess, Layer layer)
		AppContext am = AppContext.getInstance();
		attributePanel = new AttributePanel(IFeatureForm.MODIFY_FEATURES,
				this.ica, am.getCapa(this.ft.getTypeName()), this);

		okListener.setAttributePanel(attributePanel);
		
		contentPanel.add(attributePanel);

		getContentPane().add(contentPanel);
		getContentPane().add(Box.createVerticalGlue());
		getContentPane().add(buttons);
		pack();
		
		// Este setSize no encaraba, dejaba los aceptar y cancelar afuera para
		// formularios pequenios,
		// y tampoco encaraba con los atributos cuya entrada es un text area con
		// dimensiones prefijadas
//		if (getHeight() > java.awt.Toolkit.getDefaultToolkit().getScreenSize()
//				.getHeight() * 0.7) {
//			this.setSize(getWidth(), (int) Math.round(getHeight() * 0.8));
//		}
		
		imm.gis.core.gui.GuiUtils.centerWindowOnScreen(this);
		setVisible(true);

	}

	public void close() {
		setVisible(false);
	}
}
