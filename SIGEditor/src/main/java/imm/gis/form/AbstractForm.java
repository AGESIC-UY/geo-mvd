package imm.gis.form;

import imm.gis.core.controller.ContPpal;
import imm.gis.core.feature.Util;
import imm.gis.core.interfaces.AbstractNonUILogic;
import imm.gis.core.interfaces.ICoreAccess;
import imm.gis.core.interfaces.IModel;
import imm.gis.core.model.IFeatureHierarchyWrapper;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import java.util.Map;

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

class OkListener implements ActionListener {

	private IFeatureForm af;

	public OkListener(IFeatureForm af) {
		this.af = af;
	}

	public void actionPerformed(ActionEvent e) {
		af.saveChanges();
	}
}

class CancelListener extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private AbstractForm af;

	public CancelListener(AbstractForm af) {
		this.af = af;
	}

	public void actionPerformed(ActionEvent e) {
		af.setVisible(false);
	}
}

class CenterListener implements ActionListener {
	
	private ICoreAccess coreAccess;
	private IFeatureForm form;
	
	public CenterListener(ICoreAccess coreAccess, IFeatureForm form) {
		this.coreAccess = coreAccess;
		this.form = form;
	}
	
	public void actionPerformed(ActionEvent e) {
		coreAccess.getIMap().center(form.getFeature().getDefaultGeometry().getCentroid().getCoordinate());
		coreAccess.getISelection().selectFeature(form.getFeature());
	}
}

public abstract class AbstractForm extends JDialog implements IFeatureForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton okButton;
	private JButton cancelButton;
	private JButton centerButton;
	private JPanel buttons;
	private Map originalData;
	private FeatureType ft;
	private ICoreAccess ica;
	private Dimension viewSize = null;
	private JComponent userView = null;
	int mode;
	protected IFeatureHierarchyWrapper currentFhw;
	
	
	public AbstractForm(ICoreAccess ica) {
		this(ica, SHOW_FEATURE);
	}
	
	public AbstractForm(final ICoreAccess ica, int formMode) {
		super(ica.getIApplication().getMainGUI());
		this.setModal(true);
		this.ica = ica;
		mode = formMode;

		buttons = new JPanel(new java.awt.FlowLayout());
//		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

		if(mode== SHOW_FEATURE) {
			centerButton = new JButton("Centrar mapa");
			centerButton.addActionListener(new CenterListener(ica, this));
			
			buttons.add(centerButton);
			buttons.add(Box.createHorizontalGlue());
		}
		
		okButton = new JButton("   Ok   ");
		okButton.addActionListener(new OkListener(this));
		buttons.add(okButton);
		okButton.setHorizontalAlignment(SwingConstants.RIGHT);

		if (formMode != SHOW_FEATURE) {
			cancelButton = new JButton("Cancelar");
			cancelButton.addActionListener(new CancelListener(this));
			buttons.add(cancelButton);
			cancelButton.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		
		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
		getRootPane().getActionMap().put("ESCAPE", new CancelListener(this));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		addWindowListener(new WindowListener() 
		{		  
			@Override
			public void windowOpened(WindowEvent e) {}
	
			@Override
			public void windowClosing(WindowEvent e) {
				((ContPpal)ica).getSelectionModel().unselectAll();
			}
	
			@Override
			public void windowClosed(WindowEvent e) {				
			}
	
			@Override
			public void windowIconified(WindowEvent e) {}
	
			@Override
			public void windowDeiconified(WindowEvent e) {}
	
			@Override
			public void windowActivated(WindowEvent e) {}
	
			@Override
			public void windowDeactivated(WindowEvent e) {}
		});
	}

	/* (non-Javadoc)
	 * @see imm.gis.form.IFeatureForm#getFeature()
	 */
	public Feature getFeature() {
		return currentFhw.getRoots(this.ft.getTypeName())[0];
	}
	
	
	private void saveMapChanges(IModel ima, Map nuevo, Map viejo){
		Iterator it = nuevo.keySet().iterator();
		
		String llave;
		Object val;
		
		while (it.hasNext()){
			llave = (String) it.next();
			val = nuevo.get(llave);
			if (Feature.class.isAssignableFrom(val.getClass())){ // Obtuve datos del padre
				Feature fNuevo = (Feature)val;
				Feature fViejo = (Feature)viejo.get(llave);
				saveFeatureChanges(ima, fNuevo, fViejo);
			}
			else if (viejo == null || viejo.get(llave) == null) { // Obtuve un mapa de hijos
				insertMapFeatures(ima, (Map) nuevo.get(llave));
			}
			else {
				saveMapChanges(ima, (Map) nuevo.get(llave), (Map) viejo.get(llave));
			}
		}
	
		if (viejo != null){
			it = viejo.keySet().iterator();
			
			while (it.hasNext()) {
				llave = (String) it.next();
				val = viejo.get(llave);
				
				if (!Feature.class.isAssignableFrom(val.getClass())) {
					if (nuevo.get(llave) == null)
						deleteMapFeatures(ima, (Map) viejo.get(llave));
				}
			}			
		}
	}
	
	private void insertMapFeatures(IModel ima, Map mapa) {
	
		Iterator it = mapa.values().iterator();
		
		Object val;
		
		while (it.hasNext()) {
			val = it.next();
			
			if (Feature.class.isAssignableFrom(val.getClass()))
				saveFeatureChanges(ima, (Feature) val, null);
			else
				insertMapFeatures(ima, (Map) val);
		}
	}
	
	private void deleteMapFeatures(IModel ima, Map mapa) {
		
		Iterator it = mapa.values().iterator();
		
		Object val;
		
		while (it.hasNext()) {
			val = it.next();
			
			if (Feature.class.isAssignableFrom(val.getClass()))
				saveFeatureChanges(ima, null, (Feature) val);
			else
				deleteMapFeatures(ima, (Map) val);
		}
	}
	
	
	private void saveFeatureChanges(final IModel ima, final Feature newFeature,
			final Feature oldFeature) {
		
			
			AbstractNonUILogic nonui = new AbstractNonUILogic() {

				public void logic() {
			
					try {
											
						if (mode == NEW_FEATURE){
							ima.addFeature(newFeature);
							ica.getISelection().selectFeature(newFeature);
						}
							
						
						else if (mode == MODIFY_FEATURE) {
							if (oldFeature == null) {
								ima.addFeature(newFeature);
							}
							else if (newFeature == null){
								ima.delFeature(oldFeature);
							}
							else if (!oldFeature.equals(newFeature)) {
								String attNames[] = new String[oldFeature.getNumberOfAttributes()];
								
								for (int i = 0; i < attNames.length; i++)
									attNames[i] = oldFeature.getFeatureType().getAttributeType(i).getLocalName();
								
								ima.modifyFeature(oldFeature, attNames, newFeature
										.getAttributes(new Object[newFeature
												.getNumberOfAttributes()]));
							}
						}
						
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				}
			};
			
			ica.getIUserInterface().doNonUILogic(nonui);
								
	}

	/* (non-Javadoc)
	 * @see imm.gis.form.IFeatureForm#saveChanges()
	 */
	public void saveChanges() {
		Map editedData;
		IModel ima = ica.getIModel();

		try {
			editedData = getEditedData();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (editedData != null && !editedData.isEmpty()) {
			saveMapChanges(ima, editedData, originalData);
		}

		close();
	}

	public void show(IFeatureHierarchyWrapper data) throws IllegalAttributeException{
		show(data, null, true);		
	}
	
	
	/* (non-Javadoc)
	 * @see imm.gis.form.IFeatureForm#show(imm.gis.core.model.IFeatureHierarchyWrapper)
	 */
	public void show(IFeatureHierarchyWrapper data, Map<String,Map<String,Object>> calculatedAttributes, final boolean insertDeleteMultipleChild) throws IllegalAttributeException {

		if (mode == MODIFY_FEATURE)
			originalData = Util.cloneFormData(data.getOriginalMap());

		userView = constructUI();
		getContentPane().removeAll();
		getContentPane().add(userView);
		getContentPane().add(Box.createVerticalGlue());
		getContentPane().add(buttons);
		currentFhw = data;
		showForm(data, calculatedAttributes, insertDeleteMultipleChild);

		//java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		// Este setSize no encaraba, dejaba los aceptar y cancelar afuera para formularios pequenios,
		// y tampoco encaraba con los atributos cuya entrada es un text area con dimensiones prefijadas
		if (viewSize == null){
			pack();			
		} else {
			setSize(viewSize);
		}
		//setSize((int)Math.round(screen.getWidth()*0.8), (int)Math.round(screen.getHeight()*0.7));
		imm.gis.core.gui.GuiUtils.centerWindowOnScreen(this);
		setVisible(true);
	}

	public void setFeatureType(FeatureType ft) {
		this.ft = ft;
	}

	public FeatureType getFeatureType() {
		return ft;
	}

	public void close(){
		if (getSize() != viewSize){
			viewSize = getSize();
			invalidate();
		}
//		viewSize = userView.getSize();
		setVisible(false);
		((ContPpal)ica).getSelectionModel().unselectAll();
	}
	
	protected abstract Map getEditedData() throws IllegalAttributeException;

	protected abstract void showForm(IFeatureHierarchyWrapper data, Map<String,Map<String,Object>> calculatedAttributes, final boolean insertDeleteMultiple);

	protected abstract JComponent constructUI();
}
