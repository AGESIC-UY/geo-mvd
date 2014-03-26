package imm.gis.core.gui.test;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.ButtonGroup;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import java.awt.FlowLayout;
import javax.swing.JRadioButton;

public class Proto1 extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JToolBar jJToolBarBar = null;
	private JButton jMenuItem = null;
	private JPanel statusBar = null;
	private JSplitPane jSplitPane = null;
	private JTree jTree = null;
	private JPanel jPanel1 = null;
	private JLabel lblCalle = null;
	private JTextField txtCalle = null;
	private JLabel lblPuerta = null;
	private JTextField txtPuerta = null;
	private JLabel lblEsquina = null;
	private JComboBox cmbEsquina = null;
	private JButton jMenuItem1 = null;
	private JButton jMenuItem2 = null;
	private JButton jMenuItem3 = null;
	private JLabel lblImage = null;
	private JLabel lblPto = null;
	private JTextField txtPto = null;
	private JLabel lblLinea = null;
	private JTextField txtLinea = null;
	private JLabel lblPuesta = null;
	private JTextField txtPuesta = null;
	private JButton butBuscar = null;
	private JButton jMenuItem4 = null;
	private JButton jMenuItem5 = null;
	private JButton jMenuItem6 = null;
	private JButton jMenuItem7 = null;
	private JButton jMenuItem8 = null;
	private JButton jMenuItem9 = null;
	private JTabbedPane jTabbedPane = null;
	private JRadioButton rbDireccion = null;
	private JLabel lblDireccion = null;
	private JRadioButton rbPunto = null;
	private JLabel lblPunto = null;
	private JRadioButton rbLinea = null;
	private JLabel lblSearchLinea = null;
	private JRadioButton rbPuesta = null;
	private JLabel lblSearchPuesta = null;
	private ButtonGroup buttonGroup = null;
	private JLabel lblSeparador1 = null;
	private JLabel lblSeparador2 = null;
	private JLabel lblSeparador3 = null;

	/**
	 * This method initializes jJToolBarBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJJToolBarBar() {
		if (jJToolBarBar == null) {
			jJToolBarBar = new JToolBar();
			jJToolBarBar.add(getJMenuItem());
			jJToolBarBar.add(getJMenuItem1());
			jJToolBarBar.add(getJMenuItem2());
			jJToolBarBar.add(getJMenuItem3());
			jJToolBarBar.add(getJMenuItem8());
			jJToolBarBar.add(getJMenuItem9());
			jJToolBarBar.addSeparator();
			jJToolBarBar.add(getJMenuItem4());
			jJToolBarBar.add(getJMenuItem5());
			jJToolBarBar.add(getJMenuItem6());
			jJToolBarBar.add(getJMenuItem7());
		}
		return jJToolBarBar;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JButton getJMenuItem() {
		if (jMenuItem == null) {
			jMenuItem = new JButton("Editar");
			jMenuItem.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/Refresh16.gif"));
		}
		return jMenuItem;
	}

	/**
	 * This method initializes statusBar	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getStatusBar() {
		if (statusBar == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
			statusBar = new JPanel();
			statusBar.setLayout(flowLayout);
			statusBar.add(new JLabel("Listo"), null);
		}
		return statusBar;
	}

	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			lblImage = new JLabel();
			lblImage.setText("");
			lblImage.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/luminarias.JPG"));
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getJTabbedPane());
			jSplitPane.setRightComponent(lblImage);
		}
		return jSplitPane;
	}

	private JTabbedPane getJTabbedPane(){
		if (jTabbedPane == null){
			jTabbedPane = new JTabbedPane();
			jTabbedPane.add("Red Utap", getJTree());
			jTabbedPane.add("Busqueda", getJPanel1());
		}
		
		return jTabbedPane;
	}

	/**
	 * This method initializes jTree	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private JTree getJTree() {
		if (jTree == null) {
			jTree = new JTree(new TreeNode());
			jTree.setCellRenderer(new TreeRenderer());
		}
		return jTree;
	}

	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			lblDireccion = new JLabel("x Direccion");
			lblPunto = new JLabel("x Punto alimentacion");
			lblSearchLinea = new JLabel("x Linea");
			lblSearchPuesta = new JLabel("x Puesta");
			lblPto = new JLabel("Punto");
			lblLinea = new JLabel("Linea");
			lblPuesta = new JLabel("Puesta");
			lblEsquina = new JLabel("Esquina");
			lblPuerta = new JLabel("Puerta");
			lblCalle = new JLabel("Calle");
			lblSeparador1 = new MySeparator();
			lblSeparador2 = new MySeparator();
			lblSeparador3 = new MySeparator();
			jPanel1 = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.weightx = 1.0;
			c.weighty = 0.0;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(2, 2, 2, 2);
			
			c.gridwidth = GridBagConstraints.RELATIVE;
			jPanel1.add(getRbDireccion(), c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			jPanel1.add(lblDireccion, c);

			c.gridwidth = GridBagConstraints.RELATIVE;
			jPanel1.add(lblCalle, c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			jPanel1.add(getTxtCalle(), c);

			c.gridwidth = GridBagConstraints.RELATIVE;
			jPanel1.add(lblPuerta, c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(2, 2, 2, 60);
			jPanel1.add(getTxtPuerta(), c);
			c.insets = new Insets(2, 2, 2, 2);

			c.gridwidth = GridBagConstraints.RELATIVE;
			jPanel1.add(lblEsquina, c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			jPanel1.add(getCmbEsquina(), c);

			c.gridwidth = GridBagConstraints.REMAINDER;
			jPanel1.add(lblSeparador1, c);
			
			c.gridwidth = GridBagConstraints.RELATIVE;
			jPanel1.add(getRbPunto(), c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			jPanel1.add(lblPunto, c);

			c.gridwidth = GridBagConstraints.RELATIVE;
			jPanel1.add(lblPto, c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(2, 2, 2, 60);
			jPanel1.add(getTxtPto(), c);
			c.insets = new Insets(2, 2, 2, 2);

			c.gridwidth = GridBagConstraints.REMAINDER;
			jPanel1.add(lblSeparador2, c);

			c.gridwidth = GridBagConstraints.RELATIVE;
			jPanel1.add(getRbLinea(), c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			jPanel1.add(lblSearchLinea, c);

			c.gridwidth = GridBagConstraints.RELATIVE;
			jPanel1.add(lblLinea, c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(2, 2, 2, 60);
			jPanel1.add(getTxtLinea(), c);
			c.insets = new Insets(2, 2, 2, 2);

			c.gridwidth = GridBagConstraints.REMAINDER;
			jPanel1.add(lblSeparador3, c);

			c.gridwidth = GridBagConstraints.RELATIVE;
			jPanel1.add(getRbPuesta(), c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			jPanel1.add(lblSearchPuesta, c);

			c.gridwidth = GridBagConstraints.RELATIVE;
			jPanel1.add(lblPuesta, c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(2, 2, 2, 60);
			jPanel1.add(getTxtPuesta(), c);
			c.insets = new Insets(2, 2, 2, 2);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridheight = GridBagConstraints.REMAINDER;
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;
			c.weighty = 1.0;
			jPanel1.add(getButBuscar(), c);

			buttonGroup = new ButtonGroup();
			buttonGroup.add(getRbDireccion());
			buttonGroup.add(getRbPunto());
			buttonGroup.add(getRbLinea());
			buttonGroup.add(getRbPuesta());
		}
		return jPanel1;
	}

	/**
	 * This method initializes txtCalle	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtCalle() {
		if (txtCalle == null) {
			txtCalle = new JTextField();
		}
		return txtCalle;
	}

	/**
	 * This method initializes txtPuerta	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtPuerta() {
		if (txtPuerta == null) {
			txtPuerta = new JTextField("", 4);
		}
		return txtPuerta;
	}

	/**
	 * This method initializes cmbEsquina	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCmbEsquina() {
		if (cmbEsquina == null) {
			cmbEsquina = new JComboBox();
		}
		return cmbEsquina;
	}

	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JButton getJMenuItem1() {
		if (jMenuItem1 == null) {
			jMenuItem1 = new JButton();
//			jMenuItem1.setText("Puesta");
			jMenuItem1.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/Puesta16+.gif"));
		}
		return jMenuItem1;
	}

	/**
	 * This method initializes jMenuItem2	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JButton getJMenuItem2() {
		if (jMenuItem2 == null) {
			jMenuItem2 = new JButton();
//			jMenuItem2.setText("Pto alimentacion");
			jMenuItem2.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/PtoAlim16+.gif"));
		}
		return jMenuItem2;
	}

	/**
	 * This method initializes jMenuItem3	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JButton getJMenuItem3() {
		if (jMenuItem3 == null) {
			jMenuItem3 = new JButton();
//			jMenuItem3.setText("Linea");
			jMenuItem3.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/Linea16+.gif"));
		}
		return jMenuItem3;
	}

	/**
	 * This method initializes txtPto	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtPto() {
		if (txtPto == null) {
			txtPto = new JTextField();
		}
		return txtPto;
	}

	/**
	 * This method initializes txtLinea	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtLinea() {
		if (txtLinea == null) {
			txtLinea = new JTextField();
		}
		return txtLinea;
	}

	/**
	 * This method initializes txtPuesta	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtPuesta() {
		if (txtPuesta == null) {
			txtPuesta = new JTextField();
		}
		return txtPuesta;
	}

	/**
	 * This method initializes butBuscar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButBuscar() {
		if (butBuscar == null) {
			butBuscar = new JButton();
			butBuscar.setText("Buscar");
		}
		return butBuscar;
	}

	/**
	 * This method initializes jMenuItem4	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JButton getJMenuItem4() {
		if (jMenuItem4 == null) {
			jMenuItem4 = new JButton();
			jMenuItem4.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/Back16.gif"));
		}
		return jMenuItem4;
	}

	/**
	 * This method initializes jMenuItem5	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JButton getJMenuItem5() {
		if (jMenuItem5 == null) {
			jMenuItem5 = new JButton();
			jMenuItem5.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/Up16.gif"));
		}
		return jMenuItem5;
	}

	/**
	 * This method initializes jMenuItem6	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JButton getJMenuItem6() {
		if (jMenuItem6 == null) {
			jMenuItem6 = new JButton();
			jMenuItem6.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/Down16.gif"));
		}
		return jMenuItem6;
	}

	/**
	 * This method initializes jMenuItem7	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JButton getJMenuItem7() {
		if (jMenuItem7 == null) {
			jMenuItem7 = new JButton();
			jMenuItem7.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/Forward16.gif"));
		}
		return jMenuItem7;
	}

	/**
	 * This method initializes jMenuItem8	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JButton getJMenuItem8() {
		if (jMenuItem8 == null) {
			jMenuItem8 = new JButton();
			jMenuItem8.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/Save16.gif"));
		}
		return jMenuItem8;
	}

	/**
	 * This method initializes jMenuItem9	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JButton getJMenuItem9() {
		if (jMenuItem9 == null) {
			jMenuItem9 = new JButton();
			jMenuItem9.setIcon(new ImageIcon("C:/Documents and Settings/jbarone/workspace/utap/images/Stop16.gif"));
		}
		return jMenuItem9;
	}

	/**
	 * This method initializes rbDireccion	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getRbDireccion() {
		if (rbDireccion == null) {
			rbDireccion = new JRadioButton();
		}
		return rbDireccion;
	}

	/**
	 * This method initializes rbPunto	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getRbPunto() {
		if (rbPunto == null) {
			rbPunto = new JRadioButton();
		}
		return rbPunto;
	}

	/**
	 * This method initializes rbLinea	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getRbLinea() {
		if (rbLinea == null) {
			rbLinea = new JRadioButton();
		}
		return rbLinea;
	}

	/**
	 * This method initializes rbPuesta	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getRbPuesta() {
		if (rbPuesta == null) {
			rbPuesta = new JRadioButton();
		}
		return rbPuesta;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Proto1 application = new Proto1();
		application.setVisible(true);
	}

	/**
	 * This is the default constructor
	 */
	public Proto1() {
		super();
		initialize();
		ItemListener listener = new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				if (e.getStateChange() == ItemEvent.SELECTED){
					setSearchOptions(e.getSource());
				}
			}
		};
		getRbDireccion().addItemListener(listener);
		getRbLinea().addItemListener(listener);
		getRbPuesta().addItemListener(listener);
		getRbPunto().addItemListener(listener);
		getRbDireccion().setSelected(true);		
	}

	private void setSearchOptions(Object selected){
		if (selected == rbDireccion){
			lblCalle.setEnabled(true);
			lblPuerta.setEnabled(true);
			lblEsquina.setEnabled(true);
			txtCalle.setEnabled(true);
			txtPuerta.setEnabled(true);
			cmbEsquina.setEnabled(true);
			lblPunto.setEnabled(false);
			lblPto.setEnabled(false);
			txtPto.setEnabled(false);
			lblSearchLinea.setEnabled(false);
			lblLinea.setEnabled(false);
			txtLinea.setEnabled(false);
			lblSearchPuesta.setEnabled(false);
			lblPuesta.setEnabled(false);
			txtPuesta.setEnabled(false);
		} else if (selected == rbLinea){
			lblCalle.setEnabled(false);
			lblPuerta.setEnabled(false);
			lblEsquina.setEnabled(false);
			txtCalle.setEnabled(false);
			txtPuerta.setEnabled(false);
			cmbEsquina.setEnabled(false);
			lblPunto.setEnabled(false);
			lblPto.setEnabled(false);
			txtPto.setEnabled(false);
			lblSearchLinea.setEnabled(true);
			lblLinea.setEnabled(true);
			txtLinea.setEnabled(true);
			lblSearchPuesta.setEnabled(false);
			lblPuesta.setEnabled(false);
			txtPuesta.setEnabled(false);
		} else if (selected == rbPuesta){
			txtCalle.setEnabled(false);
			txtPuerta.setEnabled(false);
			cmbEsquina.setEnabled(false);
			lblPunto.setEnabled(false);
			lblPto.setEnabled(false);
			txtPto.setEnabled(false);
			lblSearchLinea.setEnabled(false);
			lblLinea.setEnabled(false);
			txtLinea.setEnabled(false);
			lblSearchPuesta.setEnabled(true);
			lblPuesta.setEnabled(true);
			txtPuesta.setEnabled(true);
		} else if (selected == rbPunto){
			lblCalle.setEnabled(false);
			lblPuerta.setEnabled(false);
			lblEsquina.setEnabled(false);
			txtCalle.setEnabled(false);
			txtPuerta.setEnabled(false);
			cmbEsquina.setEnabled(false);
			lblPunto.setEnabled(true);
			lblPto.setEnabled(true);
			txtPto.setEnabled(true);
			lblSearchLinea.setEnabled(false);
			lblLinea.setEnabled(false);
			txtLinea.setEnabled(false);
			lblSearchPuesta.setEnabled(false);
			lblPuesta.setEnabled(false);
			txtPuesta.setEnabled(false);
		}		
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getJJMenuBar());
		this.setSize(800, 600);
		this.setLocation((int)(Math.abs(Math.round(dim.getWidth()) - 800) / 2), (int)(Math.abs(Math.round(dim.getHeight()) - 600) / 2));
		this.setContentPane(getJContentPane());
		this.setTitle("Aplicacion SIG Utap");
		for (int i = 0; i < jTree.getRowCount(); i++) this.jTree.expandRow(i);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJJToolBarBar(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getStatusBar(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJSplitPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("Archivo");
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Ayuda");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new JDialog(Proto1.this, "About", true).setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save");
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK, true));
		}
		return saveMenuItem;
	}

	private class MySeparator extends JLabel{
		
		private static final long serialVersionUID = 1L;

		public Dimension getPreferredSize(){
			return new Dimension(getParent().getWidth(), 20);
		}
		
		public Dimension getMinimumSize(){
			return getPreferredSize();
		}
		
		public Dimension getMaximumSize(){
			return getPreferredSize();
		}

		public void paintComponent(java.awt.Graphics g){
			super.paintComponent(g);
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			Dimension d = getSize();
			int y = (d.height-3)/2;
			g.setColor(Color.white);
			g.drawLine(1, y, d.width-1, y);
			y++;
			g.drawLine(0, y, 1, y);
			g.setColor(Color.gray);
			g.drawLine(d.width-1, y, d.width, y);
			y++;
			g.drawLine(1, y, d.width-1, y);
/*			String text = getText();
			if (text.length()==0)
			return;
			g.setFont(getFont());
			FontMetrics fm = g.getFontMetrics();
			y = (d.height + fm.getAscent())/2;
			int l = fm.stringWidth(text);
			g.setColor(getBackground());
			g.fillRect(OFFSET-5, 0, OFFSET+l, d.height);
			g.setColor(getForeground());
			g.drawString(text, OFFSET, y);
*/		}
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
