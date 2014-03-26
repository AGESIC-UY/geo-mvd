package imm.gis.core.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class StyleDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JTextField txtTamanioVertice;
	public JTextField txtTransparencia;
	public JTextField txtGrosorLinea;
	public JComboBox cmbPatronLinea;
	public LineColorRenderer lblLinea;
	public JComboBox cmbPatronRelleno;
	public LineColorRenderer txtRelleno;
	public JButton butRelleno;
	public JCheckBox chkRelleno;
	public JCheckBox chkPatronRelleno;
	public JCheckBox chkLinea;
	public JButton butLinea;
	public JCheckBox chkPatronLinea;
	public JCheckBox chkSincroLineaRelleno;
	public JLabel lblGrosorLinea;
	public JSlider sldGrosorLinea;
	public JLabel lblTransparencia;
	public JSlider sldTransparencia;
	public JLabel lblVisionPreliminar;
	public JCheckBox chkTamanioVertice;
	public JSlider sldTamanioVertice;
	public JButton butOk;	
	public JButton butAplicar;	
	public JButton butRevertir;	

	/**
	 * Create the dialog
	 */
	public StyleDialog() {
		super();
		setModal(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("Cambiar estilo");
		getContentPane().setLayout(new GridBagLayout());
		setBounds(100, 100, 495, 384);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		chkRelleno = new JCheckBox();
		chkRelleno.setHorizontalAlignment(SwingConstants.LEFT);
		chkRelleno.setText("Relleno");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 0.1;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		getContentPane().add(chkRelleno, gridBagConstraints);

		txtRelleno = new LineColorRenderer();
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.weightx = 0;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 1;
		getContentPane().add(txtRelleno, gridBagConstraints_1);

		butRelleno = new JButton();
		butRelleno.setText("...");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_2.weightx = 0;
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 2;
		getContentPane().add(butRelleno, gridBagConstraints_2);

		chkPatronRelleno = new JCheckBox();
		chkPatronRelleno.setText("Patron de relleno");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_3.weighty = 0.1;
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		getContentPane().add(chkPatronRelleno, gridBagConstraints_3);

		cmbPatronRelleno = new JComboBox();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.weightx = 0;
		gridBagConstraints_4.gridwidth = 2;
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridx = 1;
		getContentPane().add(cmbPatronRelleno, gridBagConstraints_4);

		chkLinea = new JCheckBox();
		chkLinea.setText("Linea");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_5.weighty = 0.1;
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.gridy = 2;
		gridBagConstraints_5.gridx = 0;
		getContentPane().add(chkLinea, gridBagConstraints_5);

		lblLinea = new LineColorRenderer();
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.gridy = 2;
		gridBagConstraints_6.gridx = 1;
		getContentPane().add(lblLinea, gridBagConstraints_6);

		butLinea = new JButton();
		butLinea.setText("...");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 2;
		getContentPane().add(butLinea, gridBagConstraints_7);

		chkPatronLinea = new JCheckBox();
		chkPatronLinea.setText("Patron de linea");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_8.weighty = 0.1;
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 3;
		gridBagConstraints_8.gridx = 0;
		getContentPane().add(chkPatronLinea, gridBagConstraints_8);

		cmbPatronLinea = new JComboBox();
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_9.weightx = 0;
		gridBagConstraints_9.gridwidth = 2;
		gridBagConstraints_9.anchor = GridBagConstraints.WEST;
		gridBagConstraints_9.gridy = 3;
		gridBagConstraints_9.gridx = 1;
		getContentPane().add(cmbPatronLinea, gridBagConstraints_9);

		chkSincroLineaRelleno = new JCheckBox();
		chkSincroLineaRelleno.setText("Sincronizar color de linea con color de relleno");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_10.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_10.weightx = 0;
		gridBagConstraints_10.weighty = 0.1;
		gridBagConstraints_10.gridwidth = 3;
		gridBagConstraints_10.anchor = GridBagConstraints.WEST;
		gridBagConstraints_10.gridy = 4;
		gridBagConstraints_10.gridx = 0;
		getContentPane().add(chkSincroLineaRelleno, gridBagConstraints_10);

		txtGrosorLinea = new JTextField();
		txtGrosorLinea.setEditable(false);
		txtGrosorLinea.setText("1");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_11.insets = new Insets(0, 10, 0, 70);
		gridBagConstraints_11.anchor = GridBagConstraints.WEST;
		gridBagConstraints_11.weightx = 0;
		gridBagConstraints_11.gridy = 4;
		gridBagConstraints_11.gridx = 3;
		getContentPane().add(txtGrosorLinea, gridBagConstraints_11);

		lblGrosorLinea = new JLabel();
		lblGrosorLinea.setText("Grosor de linea");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_12.weighty = 0.1;
		gridBagConstraints_12.anchor = GridBagConstraints.WEST;
		gridBagConstraints_12.gridy = 5;
		gridBagConstraints_12.gridx = 0;
		getContentPane().add(lblGrosorLinea, gridBagConstraints_12);

		sldGrosorLinea = new JSlider();
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_13.anchor = GridBagConstraints.WEST;
		gridBagConstraints_13.gridwidth = 2;
		gridBagConstraints_13.gridy = 5;
		gridBagConstraints_13.gridx = 1;
		getContentPane().add(sldGrosorLinea, gridBagConstraints_13);

		lblTransparencia = new JLabel();
		lblTransparencia.setText("Transparencia");
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_14.weighty = 0.1;
		gridBagConstraints_14.anchor = GridBagConstraints.WEST;
		gridBagConstraints_14.gridy = 6;
		gridBagConstraints_14.gridx = 0;
		getContentPane().add(lblTransparencia, gridBagConstraints_14);

		sldTransparencia = new JSlider();
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_16.anchor = GridBagConstraints.WEST;
		gridBagConstraints_16.gridwidth = 2;
		gridBagConstraints_16.gridy = 6;
		gridBagConstraints_16.gridx = 1;
		getContentPane().add(sldTransparencia, gridBagConstraints_16);

		txtTransparencia = new JTextField();
		txtTransparencia.setEditable(false);
		txtTransparencia.setText("105");
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_18.weightx = 0;
		gridBagConstraints_18.insets = new Insets(0, 10, 0, 70);
		gridBagConstraints_18.anchor = GridBagConstraints.WEST;
		gridBagConstraints_18.gridy = 6;
		gridBagConstraints_18.gridx = 3;
		getContentPane().add(txtTransparencia, gridBagConstraints_18);

		chkTamanioVertice = new JCheckBox();
		chkTamanioVertice.setText("Tamaño de vertices");
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_19.weightx = 0;
		gridBagConstraints_19.weighty = 0.1;
		gridBagConstraints_19.anchor = GridBagConstraints.WEST;
		gridBagConstraints_19.gridy = 7;
		gridBagConstraints_19.gridx = 0;
		getContentPane().add(chkTamanioVertice, gridBagConstraints_19);

		sldTamanioVertice = new JSlider();
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_20.anchor = GridBagConstraints.WEST;
		gridBagConstraints_20.weightx = 0;
		gridBagConstraints_20.gridwidth = 2;
		gridBagConstraints_20.gridy = 7;
		gridBagConstraints_20.gridx = 1;
		getContentPane().add(sldTamanioVertice, gridBagConstraints_20);

		txtTamanioVertice = new JTextField();
		txtTamanioVertice.setEditable(false);
		txtTamanioVertice.setText("4");
		final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_21.insets = new Insets(0, 10, 0, 70);
		gridBagConstraints_21.anchor = GridBagConstraints.WEST;
		gridBagConstraints_21.weightx = 0;
		gridBagConstraints_21.gridy = 7;
		gridBagConstraints_21.gridx = 3;
		getContentPane().add(txtTamanioVertice, gridBagConstraints_21);

		final JLabel label1 = new JLabel();
		label1.setText("Vision preliminar");
		final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_22.anchor = GridBagConstraints.WEST;
		gridBagConstraints_22.gridy = 8;
		gridBagConstraints_22.gridx = 0;
		getContentPane().add(label1, gridBagConstraints_22);

		lblVisionPreliminar = new JLabel();
		lblVisionPreliminar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_15.gridwidth = 2;
		gridBagConstraints_15.weighty = 1;
		gridBagConstraints_15.fill = GridBagConstraints.BOTH;
		gridBagConstraints_15.gridy = 9;
		gridBagConstraints_15.gridx = 1;
		getContentPane().add(lblVisionPreliminar, gridBagConstraints_15);

		butAplicar = new JButton();
		butAplicar.setText("Aplicar");
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.anchor = GridBagConstraints.EAST;
		gridBagConstraints_17.weightx = 1;
		gridBagConstraints_17.insets = new Insets(10, 0, 5, 0);
		gridBagConstraints_17.gridy = 10;
		gridBagConstraints_17.gridx = 1;
		getContentPane().add(butAplicar, gridBagConstraints_17);

		butRevertir = new JButton();
		butRevertir.setText("Revertir");
		final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
		gridBagConstraints_23.insets = new Insets(10, 0, 5, 0);
		gridBagConstraints_23.gridy = 10;
		gridBagConstraints_23.gridx = 2;
		getContentPane().add(butRevertir, gridBagConstraints_23);

		butOk = new JButton();
		butOk.setText("Confirmar");
		final GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
		gridBagConstraints_24.insets = new Insets(10, 0, 5, 0);
		gridBagConstraints_24.gridy = 10;
		gridBagConstraints_24.gridx = 3;
		getContentPane().add(butOk, gridBagConstraints_24);
		//
	}

}
