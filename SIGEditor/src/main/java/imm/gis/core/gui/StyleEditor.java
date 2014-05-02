package imm.gis.core.gui;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import java.awt.Insets;
import java.awt.FlowLayout;

public class StyleEditor extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	public JRadioButton radPorDefecto = null;

	public JRadioButton radPersonalizado = null;

	public JComboBox cmbPersonalizados = null;

	public JCheckBox chkRelleno = null;

	public LineColorRenderer txtRelleno = null;

	public JButton butRelleno = null;

	public JCheckBox chkPatronRelleno = null;

	public JComboBox cmbPatronRelleno = null;

	public JCheckBox chkLinea = null;

	public LineColorRenderer txtLinea = null;

	public JButton butLinea = null;

	public JCheckBox chkPatronLinea = null;

	public JComboBox cmbPatronLinea = null;

	public JCheckBox chkSincroLineaRelleno = null;

	public JLabel lblGrosorLinea = null;

	public JSlider sldGrosorLinea = null;

	public JTextField txtGrosorLinea = null;

	public JLabel lblTransparencia = null;

	public JSlider sldTransparencia = null;

	public JTextField txtTransparencia = null;

	public JCheckBox chkTamanioVertice = null;

	public JSlider sldTamanioVertice = null;

	public JTextField txtTamanioVertice = null;

	private JPanel pnlBotones = null;

	public JButton butAplicar = null;

	public JButton butGuardar = null;

	public JButton butCancelar = null;

	//public JButton butSetPorDefecto = null;

	/**
	 * @param owner
	 */
	public StyleEditor(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(429, 465);
		this.setTitle("Edicion de estilos");
		this.setContentPane(getJContentPane());
//		this.setUndecorated(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(radPorDefecto);
		bg.add(radPersonalizado);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
			gridBagConstraints110.gridx = 0;
			gridBagConstraints110.gridwidth = 3;
			gridBagConstraints110.anchor = GridBagConstraints.SOUTHEAST;
			gridBagConstraints110.weighty = 1.0;
			gridBagConstraints110.insets = new Insets(0, 0, 10, 10);
			gridBagConstraints110.gridy = 10;
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = GridBagConstraints.NONE;
			gridBagConstraints22.gridy = 9;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints22.gridx = 2;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints21.gridy = 9;
			gridBagConstraints21.weightx = 0.0;
			gridBagConstraints21.anchor = GridBagConstraints.CENTER;
			gridBagConstraints21.weighty = 0.0;
			gridBagConstraints21.ipady = 0;
			gridBagConstraints21.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.gridx = 0;
			gridBagConstraints20.anchor = GridBagConstraints.NORTH;
			gridBagConstraints20.insets = new Insets(10, 10, 0, 0);
			gridBagConstraints20.weighty = 0.0;
			gridBagConstraints20.gridy = 9;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = GridBagConstraints.NONE;
			gridBagConstraints19.gridy = 8;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints19.gridx = 2;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints18.gridy = 8;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints18.gridx = 1;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.insets = new Insets(10, 10, 0, 0);
			gridBagConstraints17.gridy = 8;
			lblTransparencia = new JLabel();
			lblTransparencia.setText("Transparencia");
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = GridBagConstraints.NONE;
			gridBagConstraints16.gridy = 7;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints16.gridx = 2;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints15.gridy = 7;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints15.gridx = 1;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.insets = new Insets(10, 10, 0, 0);
			gridBagConstraints14.gridy = 7;
			lblGrosorLinea = new JLabel();
			lblGrosorLinea.setText("Grosor de linea");
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.gridwidth = 3;
			gridBagConstraints13.insets = new Insets(10, 10, 0, 0);
			gridBagConstraints13.gridy = 6;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 5;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.insets = new Insets(10, 0, 0, 10);
			gridBagConstraints12.gridwidth = 2;
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(10, 10, 0, 0);
			gridBagConstraints11.gridy = 5;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 2;
			gridBagConstraints10.fill = GridBagConstraints.NONE;
			gridBagConstraints10.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints10.gridy = 4;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 4;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.insets = new Insets(10, 10, 0, 0);
			gridBagConstraints8.gridy = 4;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 3;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new Insets(10, 0, 0, 10);
			gridBagConstraints7.gridwidth = 2;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.insets = new Insets(10, 10, 0, 0);
			gridBagConstraints6.gridy = 3;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 2;
			gridBagConstraints5.insets = new Insets(35, 0, 0, 0);
			gridBagConstraints5.fill = GridBagConstraints.NONE;
			gridBagConstraints5.gridy = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new Insets(35, 0, 0, 0);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.insets = new Insets(35, 10, 0, 0);
			gridBagConstraints3.gridy = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 0.0;
			gridBagConstraints2.insets = new Insets(3, 5, 0, 0);
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.weightx = 0.0;
			gridBagConstraints1.insets = new Insets(3, 35, 0, 0);
			gridBagConstraints1.weighty = 0.0;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.insets = new Insets(10, 35, 0, 0);
			gridBagConstraints.weighty = 0.0;
			gridBagConstraints.gridy = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getRadPorDefecto(), gridBagConstraints);
			jContentPane.add(getRadPersonalizado(), gridBagConstraints1);
			jContentPane.add(getCmbPersonalizados(), gridBagConstraints2);
			jContentPane.add(getChkRelleno(), gridBagConstraints3);
			jContentPane.add(getTxtRelleno(), gridBagConstraints4);
			jContentPane.add(getButRelleno(), gridBagConstraints5);
			jContentPane.add(getChkPatronRelleno(), gridBagConstraints6);
			jContentPane.add(getCmbPatronRelleno(), gridBagConstraints7);
			jContentPane.add(getChkLinea(), gridBagConstraints8);
			jContentPane.add(getTxtLinea(), gridBagConstraints9);
			jContentPane.add(getButLinea(), gridBagConstraints10);
			jContentPane.add(getChkPatronLinea(), gridBagConstraints11);
			jContentPane.add(getCmbPatronLinea(), gridBagConstraints12);
			jContentPane.add(getChkSincroLineaRelleno(), gridBagConstraints13);
			jContentPane.add(lblGrosorLinea, gridBagConstraints14);
			jContentPane.add(getSldGrosorLinea(), gridBagConstraints15);
			jContentPane.add(getTxtGrosorLinea(), gridBagConstraints16);
			jContentPane.add(lblTransparencia, gridBagConstraints17);
			jContentPane.add(getSldTransparencia(), gridBagConstraints18);
			jContentPane.add(getTxtTransparencia(), gridBagConstraints19);
			jContentPane.add(getChkTamanioVertice(), gridBagConstraints20);
			jContentPane.add(getSldTamanioVertice(), gridBagConstraints21);
			jContentPane.add(getTxtTamanioVertice(), gridBagConstraints22);
			jContentPane.add(getPnlBotones(), gridBagConstraints110);
		}
		return jContentPane;
	}

	/**
	 * This method initializes radPorDefecto	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getRadPorDefecto() {
		if (radPorDefecto == null) {
			radPorDefecto = new JRadioButton();
			radPorDefecto.setText("Por Defecto");
		}
		return radPorDefecto;
	}

	/**
	 * This method initializes radPersonalizado	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getRadPersonalizado() {
		if (radPersonalizado == null) {
			radPersonalizado = new JRadioButton();
			radPersonalizado.setText("Personalizado");
		}
		return radPersonalizado;
	}

	/**
	 * This method initializes cmbPersonalizados	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCmbPersonalizados() {
		if (cmbPersonalizados == null) {
			cmbPersonalizados = new JComboBox();
			cmbPersonalizados.setEditable(true);
		}
		return cmbPersonalizados;
	}

	/**
	 * This method initializes chkRelleno	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getChkRelleno() {
		if (chkRelleno == null) {
			chkRelleno = new JCheckBox();
			chkRelleno.setText("Relleno");
		}
		return chkRelleno;
	}

	/**
	 * This method initializes txtRelleno	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtRelleno() {
		if (txtRelleno == null) {
			txtRelleno = new LineColorRenderer();
		}
		return txtRelleno;
	}

	/**
	 * This method initializes butRelleno	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButRelleno() {
		if (butRelleno == null) {
			butRelleno = new JButton();
			butRelleno.setText("...");
		}
		return butRelleno;
	}

	/**
	 * This method initializes chkPatronRelleno	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getChkPatronRelleno() {
		if (chkPatronRelleno == null) {
			chkPatronRelleno = new JCheckBox();
			chkPatronRelleno.setText("Patron de relleno");
		}
		return chkPatronRelleno;
	}

	/**
	 * This method initializes cmbPatronRelleno	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCmbPatronRelleno() {
		if (cmbPatronRelleno == null) {
			cmbPatronRelleno = new JComboBox();
		}
		return cmbPatronRelleno;
	}

	/**
	 * This method initializes chkLinea	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getChkLinea() {
		if (chkLinea == null) {
			chkLinea = new JCheckBox();
			chkLinea.setText("Linea");
		}
		return chkLinea;
	}

	/**
	 * This method initializes txtLinea	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtLinea() {
		if (txtLinea == null) {
			txtLinea = new LineColorRenderer();
		}
		return txtLinea;
	}

	/**
	 * This method initializes butLinea	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButLinea() {
		if (butLinea == null) {
			butLinea = new JButton();
			butLinea.setText("...");
		}
		return butLinea;
	}

	/**
	 * This method initializes chkPatronLinea	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getChkPatronLinea() {
		if (chkPatronLinea == null) {
			chkPatronLinea = new JCheckBox();
			chkPatronLinea.setText("Patron de linea");
		}
		return chkPatronLinea;
	}

	/**
	 * This method initializes cmbPatronLinea	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCmbPatronLinea() {
		if (cmbPatronLinea == null) {
			cmbPatronLinea = new JComboBox();
		}
		return cmbPatronLinea;
	}

	/**
	 * This method initializes chkSincroLineaRelleno	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getChkSincroLineaRelleno() {
		if (chkSincroLineaRelleno == null) {
			chkSincroLineaRelleno = new JCheckBox();
			chkSincroLineaRelleno.setText("Sincronizar color de linea con color de relleno");
		}
		return chkSincroLineaRelleno;
	}

	/**
	 * This method initializes sldGrosorLinea	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSldGrosorLinea() {
		if (sldGrosorLinea == null) {
			sldGrosorLinea = new JSlider();
		}
		return sldGrosorLinea;
	}

	/**
	 * This method initializes txtGrosorLinea	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtGrosorLinea() {
		if (txtGrosorLinea == null) {
			txtGrosorLinea = new JTextField(3);
		}
		return txtGrosorLinea;
	}

	/**
	 * This method initializes sldTransparencia	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSldTransparencia() {
		if (sldTransparencia == null) {
			sldTransparencia = new JSlider();
		}
		return sldTransparencia;
	}

	/**
	 * This method initializes txtTransparencia	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtTransparencia() {
		if (txtTransparencia == null) {
			txtTransparencia = new JTextField(3);
		}
		return txtTransparencia;
	}

	/**
	 * This method initializes chkTamanioVertice	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getChkTamanioVertice() {
		if (chkTamanioVertice == null) {
			chkTamanioVertice = new JCheckBox();
			chkTamanioVertice.setText("Tamano de vertice");
		}
		return chkTamanioVertice;
	}

	/**
	 * This method initializes sldTamanioVertice	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSldTamanioVertice() {
		if (sldTamanioVertice == null) {
			sldTamanioVertice = new JSlider();
		}
		return sldTamanioVertice;
	}

	/**
	 * This method initializes txtTamanioVertice	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtTamanioVertice() {
		if (txtTamanioVertice == null) {
			txtTamanioVertice = new JTextField(3);
		}
		return txtTamanioVertice;
	}

	/**
	 * This method initializes pnlBotones	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPnlBotones() {
		if (pnlBotones == null) {
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.gridx = -1;
			gridBagConstraints23.gridy = -1;
			pnlBotones = new JPanel();
			pnlBotones.setLayout(new FlowLayout());
			pnlBotones.add(getButCancelar(), null);
			pnlBotones.add(getButAplicar(), null);
			pnlBotones.add(getButGuardar(), null);
			/*
			pnlBotones.add(getButSetPorDefecto(), null);
			*/
		}
		return pnlBotones;
	}

	/**
	 * This method initializes butAplicar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButAplicar() {
		if (butAplicar == null) {
			butAplicar = new JButton();
			butAplicar.setText("Aplicar");
		}
		return butAplicar;
	}

	/**
	 * This method initializes butGuardar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButGuardar() {
		if (butGuardar == null) {
			butGuardar = new JButton();
			butGuardar.setText("Guardar");
		}
		return butGuardar;
	}

	/**
	 * This method initializes butCancelar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButCancelar() {
		if (butCancelar == null) {
			butCancelar = new JButton();
			butCancelar.setText("Cancelar");
		}
		return butCancelar;
	}

	/**
	 * This method initializes butSetPorDefecto	
	 * 	
	 * @return javax.swing.JButton	
	 */
	/*
	private JButton getButSetPorDefecto() {
		if (butSetPorDefecto == null) {
			butSetPorDefecto = new JButton();
			butSetPorDefecto.setText("Por defecto");
		}
		return butSetPorDefecto;
	}
	*/
}  //  @jve:decl-index=0:visual-constraint="10,10"
