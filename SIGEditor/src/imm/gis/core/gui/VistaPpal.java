package imm.gis.core.gui;

import java.awt.Toolkit;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import imm.gis.AppContext;
import imm.gis.comm.ServerServicesFactory;
import imm.gis.core.controller.ContAccionesAplicacion;
import imm.gis.core.controller.ContPpal;
import imm.gis.core.gui.editedfeatures.EditedFeaturesPanel;
import imm.gis.core.gui.worker.BlockingSwingWorker;
import imm.gis.core.interfaces.IMap;
import imm.gis.core.interfaces.IUserInterface;
import imm.gis.core.interfaces.NonUILogic;
import imm.gis.gui.EditionToolBar;
import imm.gis.gui.GeneralToolBar;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import imm.gis.gui.toolbar.layouts.DockLayout;
import imm.gis.navigation.NavigationController;

public class VistaPpal extends JFrame implements IUserInterface {

	private static final long serialVersionUID = 1L;

	private ContPpal contPpal;
	private PanelDibujo panelDibujo;
	//private TreePanel treePanel;
	private LeftPanel leftPanel;
	private GeneralToolBar generalToolBar;
	private EditionToolBar editionToolBar;
	private JSplitPane splitPane;
	private CoordinateBox coordinateBox;
	
	public ButtonGroup buttonGroup;
	
	public VistaPpal(ContPpal c){
		super("Editor SIG");
		contPpal = c;
		setJMenuBar(createMenuBar());
		getContentPane().setLayout(new DockLayout(this, DockLayout.STACKING_STYLE));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		panelDibujo = new PanelDibujo(contPpal.getModel(), contPpal.getSelectionModel());
		
		coordinateBox = new CoordinateBox(c.getContMapa(), this);
		
		leftPanel = new LeftPanel(c.getEditionContext(), c.getModel(), c.getContMapa(), this);
		//treePanel = new TreePanel(c);
		buttonGroup = new ButtonGroup();
		JPanel toolBarPanel = new JPanel();
		createToolBars(toolBarPanel);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, panelDibujo);
		splitPane.setOneTouchExpandable(true);
		getContentPane().add(DockLayout.center, splitPane);
		pack();
		if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH)){
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			GuiUtils.fullScreen(this);			
		}
	}
	
	public void setUpListeners() {
		((IMap) contPpal.getContMapa()).addCoordinateListener(coordinateBox);		
	}
	

	private void createToolBars(java.awt.Container cnt){
		NavigationController navigationController = contPpal.getNavigationController();
		
		generalToolBar = new GeneralToolBar(contPpal, navigationController, contPpal.getContBusqueda(), contPpal.getEditionContext());
		
		
		editionToolBar = new EditionToolBar(contPpal, contPpal.getEditionContext()); 
		contPpal.getEditionContext().addContextListener(editionToolBar);	
		contPpal.getSelectionModel().addFeatureSelectionListener(editionToolBar);
		
		addToButtonGroup(buttonGroup, generalToolBar);
		addToButtonGroup(buttonGroup, editionToolBar);
		
		buttonGroup.setSelected(((AbstractButton)generalToolBar.getComponent(0)).getModel(), true);
		buttonGroup.remove((AbstractButton)editionToolBar.getComponent(0));
		buttonGroup.remove((AbstractButton)editionToolBar.getComponent(8));
		buttonGroup.remove((AbstractButton)generalToolBar.getComponent(3));

		
		
		getContentPane().add(DockLayout.north, generalToolBar);
		getContentPane().add(DockLayout.north, editionToolBar);
		getContentPane().add(DockLayout.north, coordinateBox.getToolBar());
	}
	
	private void addToButtonGroup(ButtonGroup bg, JToolBar tb){
		AbstractButton ab;
		ExtendedAction ea;
		java.awt.Component comp;
		
		for (int i = 0; i < tb.getComponentCount(); i++){
			comp = tb.getComponent(i); 
			if (comp instanceof AbstractButton){
				ab = (AbstractButton)comp;
				if (ab.getAction() instanceof ExtendedAction){
					ea = (ExtendedAction)ab.getAction();
					if (ea.isToggle()){
						bg.add(ab);
					}
				}				
			}
		}
	}
	
	private JMenuBar createMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		ContAccionesAplicacion cont = contPpal.getContAccionesAplicacion();

		menu = new JMenu("Archivo");
		menu.setMnemonic('A');
		menuBar.add(menu);
		menu.add(createMenuItem(cont.getExitAction()));
		
		menu = new JMenu("Ver");
		menu.setMnemonic('V');
		menuBar.add(menu);
		menu.add(createMenuItem(cont.getFullScreenAction()));

		menu = new JMenu("Herramientas");
		menu.setMnemonic('H');
		menuBar.add(menu);
		menu.add(createMenuItem(cont.getExportImageAction()));
		menu.add(createMenuItem(cont.getExportShapeAction()));
		menu.add(createMenuItem(cont.getExportExcelAction()));
		menu.add(createMenuItem(cont.getExportKMLAction()));

		menu = new JMenu("Configuracion");
		menu.setMnemonic('C');
		menuBar.add(menu);

		menu = new JMenu("Ayuda");
		menu.setMnemonic('Y');
		menuBar.add(menu);
	
		AppContext appContext = AppContext.getInstance();
		String codebase = System.getProperty(ServerServicesFactory.SERVICES_CODEBASE);		
		menu.add(createMenuItem(cont.getManualUsuarioAction(codebase + "/" + appContext.getId() + "/ayuda.doc")));
						
		return menuBar;
	}

	public void createMenu (String name){
		JMenu menu = new JMenu(name);		
		getJMenuBar().add(menu);
	}
	
	public void addMenuItem (int menuPosition, ExtendedAction ea){
		JMenu menu = getJMenuBar().getMenu(menuPosition);
		menu.add(createMenuItem(ea));
	}
	
	private JMenuItem createMenuItem(ExtendedAction ea){
		JMenuItem but = new JMenuItem(ea);
		but.setDisabledIcon(ea.getDisabledIcon());
		
		return but;
	}

	public PanelDibujo getPanelDibujo(){
		return panelDibujo;
	}
	
	public EditedFeaturesPanel getEditionHistoryPanel(){
		return leftPanel.getTreePanel();
	}


	public void addToolBar(JToolBar bar) {
		getContentPane().add(DockLayout.south, bar);
		repaint();		
	}

	public void removeToolBar(JToolBar bar) {
		getContentPane().remove(bar);
		repaint();
	}


	public ScalePanel getScalePanel() {
		return leftPanel.getScalePanel();
	}


	public LeftPanel getLeftPanel() {
		return leftPanel;
	}
	
	public void doNonUILogic(final NonUILogic l) {
		new BlockingSwingWorker(this) {
			    protected void doNonUILogic() throws RuntimeException {
				    try {
				    	l.logic();
				    }
				    catch (Exception e) {
				    	e.printStackTrace();
				    	throw new RuntimeException(e);
				    }
			    }
		    }.start();
	}
	
	public void showError(String title, Exception e) {
		showError(title, e.getMessage());
	}

	public void showError(Exception e) {
		showError(e.getMessage());
	}

	public void showError(String title, String msg) {
		javax.swing.JOptionPane.showMessageDialog(this, msg, title,
				javax.swing.JOptionPane.ERROR_MESSAGE);
	}

	public void showError(String msg) {
		javax.swing.JOptionPane.showMessageDialog(this, msg, "Error",
				javax.swing.JOptionPane.ERROR_MESSAGE);
	}

	public GeneralToolBar getGeneralToolBar() {
		return generalToolBar;
	}
	
	public EditionToolBar getEditionToolBar() {
		return editionToolBar;
	}
	
	public void setEditable(boolean editable) {
		if (!editable){
			getContentPane().remove(editionToolBar);
		} else {
			boolean esta = false;
			java.awt.Component[] comps = getContentPane().getComponents();
			
			for (int i = 0; i < comps.length && !esta; i++){
				esta = (comps[i] == editionToolBar);
			}
			if (!esta) getContentPane().add(editionToolBar);
		}
	}
		
}
