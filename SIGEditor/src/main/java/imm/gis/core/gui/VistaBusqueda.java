package imm.gis.core.gui;

import imm.gis.form.search.SearchCriteriaPanel;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;


import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;

import org.geotools.feature.FeatureCollection;
import org.geotools.gui.swing.table.FeatureTableModel;

public class VistaBusqueda extends JDialog {

	private static final long serialVersionUID = 1L;

	private JComboBox cmbCapa1;
	private JComboBox cmbCapa2;
	private JPanel pnlCapa1;
	private JPanel pnlCapa2;
	private JTable tblCapa1;
	private JTable tblCapa2;
	private JButton butBuscar1;
	private JButton butBuscar2;
	private JLabel lblCantCapa1;
	private JLabel lblCantCapa2;	
	private JButton butCentrar1;
	private JButton butCentrar2;
	private JButton butOk;
	private JRadioButton radOperIntersect;
	private JRadioButton radOperUnion;
	private JButton butCombinar;
	private JRadioButton radPorZona;
	private JRadioButton radPorElementos;
	private JScrollPane resultadosIzquierda;
	private JScrollPane resultadosDerecha;
	private ListSelectionListener leftResultListener = null;
	private ListSelectionListener rightResultListener = null;
	
	GuiUtils.ConstraintGroup cg;
	
	public VistaBusqueda(javax.swing.JFrame parent){
		super(parent, "Busqueda", false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		cmbCapa1 = new JComboBox();
		cmbCapa2 = new JComboBox();
		pnlCapa1 = new JPanel();
		pnlCapa2 = new JPanel();
		tblCapa1 = new JTable();
		tblCapa2 = new JTable();
		lblCantCapa1 = new JLabel("0");
		lblCantCapa2 = new JLabel("0");
		butBuscar1 = new JButton("Buscar");
		butBuscar2 = new JButton("Buscar");
		butCentrar1 = new JButton("Centrar mapa");
		butCentrar2 = new JButton("Centrar mapa");
		butOk = 	  new JButton(" Cerrar ");
		radOperIntersect = new JRadioButton("Interseccion", true);
		radOperUnion = new JRadioButton("Union");
		butCombinar = new JButton("Combinar");
		radPorZona = new JRadioButton("Zona");
		radPorElementos = new JRadioButton(" Elementos   ", true);

		ButtonGroup group = new ButtonGroup();
		group.add(radOperIntersect);
		group.add(radOperUnion);

		group = new ButtonGroup();
		group.add(radPorZona);
		group.add(radPorElementos);
		
//		lblCantCapa1.setEnabled(false);
//		lblCantCapa2.setEnabled(false);
		init();
		pack();
	}
	
	private void init(){
		java.awt.Container cnt = getContentPane();
		
		cnt.setLayout(new java.awt.GridBagLayout());

		GUIConstraints cons = new GUIConstraints();
		cg = new GuiUtils.ConstraintGroup(cons.gMain, cons.wMain);
		
		cnt.add(new JLabel("Capa"), cg.getConstraints(0));
		cnt.add(cmbCapa1, cg.getConstraints(1));
		cnt.add(new JLabel("Capa"), cg.getConstraints(2));
		cnt.add(cmbCapa2, cg.getConstraints(3));
		
		cnt.add(pnlCapa1, cg.getConstraints(4));
		cnt.add(pnlCapa2, cg.getConstraints(5));

		cnt.add(butBuscar1, cg.getConstraints(6));
		cnt.add(butBuscar2, cg.getConstraints(7));

		resultadosIzquierda = new JScrollPane(tblCapa1);
		resultadosDerecha = new JScrollPane(tblCapa2);
		cnt.add(resultadosIzquierda, cg.getConstraints(8));
		cnt.add(resultadosDerecha, cg.getConstraints(9));
		
//		tmp1.setEnabled(false);
//		tmp2.setEnabled(false);
		cnt.add(lblCantCapa1, cg.getConstraints(10));
//		cnt.add(tmp1, cg.getConstraints(11));
		cnt.add(lblCantCapa2, cg.getConstraints(11));
//		cnt.add(tmp2, cg.getConstraints(13));

		cnt.add(butCentrar1, cg.getConstraints(12));
		cnt.add(butCentrar2, cg.getConstraints(13));

		cnt.add(new DialogSeparator("Combinar capas"), cg.getConstraints(14));
		cnt.add(new JLabel("Operaciones de combinacion"), cg.getConstraints(15));
		cnt.add(new JLabel("Aplicar esta combinacion a"), cg.getConstraints(16));
		cnt.add(radOperIntersect, cg.getConstraints(17));
		cnt.add(radPorElementos, cg.getConstraints(18));
		cnt.add(radOperUnion, cg.getConstraints(19));
		cnt.add(radPorZona, cg.getConstraints(20));

		cnt.add(new DialogSeparator(), cg.getConstraints(21));

		cnt.add(butCombinar, cg.getConstraints(22));
		cnt.add(butOk, cg.getConstraints(23));
	}

	private final class GUIConstraints {
		private final int NONE = GridBagConstraints.NONE;
		private final int BOTH = GridBagConstraints.BOTH;
//		private final int SOUT = GridBagConstraints.SOUTH;
		private final int CENT = GridBagConstraints.CENTER;
		private final int EAST = GridBagConstraints.EAST;
		private final int WEST = GridBagConstraints.WEST;
//		private final int SOEA = GridBagConstraints.SOUTHEAST;
		private final int HORI = GridBagConstraints.HORIZONTAL;
//		private final int RELA = GridBagConstraints.RELATIVE;
		private final int REMA = GridBagConstraints.REMAINDER;

//		{gridx, gridy}, {gridwidth, gridheight}, {fill, anchor}, {top, left, bottom, right}(insets)
		final int[][] gMain = new int[][] {
//			Combos para elegir capas			
			{0, 0}, {1, 1 }, {NONE, EAST}, {2, 10, 0, 2},
			{1, 0}, {1, 1 }, {HORI, WEST}, {2, 2, 0, 50},
			{2, 0}, {1, 1 }, {NONE, EAST}, {2, 10, 0, 2},
			{3, 0}, {REMA, 1 }, {HORI, WEST}, {2, 2, 0, 40},
			
//			Paneles de datos
			{0, 1}, {2, 1 }, {HORI, CENT}, {0, 0, 0, 0},
			{2, 1}, {REMA, 1 }, {HORI, CENT}, {0, 0, 0, 0},

//			Botones buscar
			{0, 2}, {2, 1}, {NONE, CENT}, {0, 0, 2, 0},
			{2, 2}, {REMA, 1}, {NONE, CENT}, {0, 0, 2, 0},
			
//			Tablas de resultados
			{0, 3}, {2, 1 }, {BOTH, CENT}, {2, 0, 0, 0},
			{2, 3}, {REMA, 1 }, {BOTH, CENT}, {2, 0, 0, 0},
			
//			Etiquetas de cantidad de resultados
			{0, 4}, {2, 1 }, {NONE, CENT}, {0, 0, 2, 0},
			{2, 4}, {2, 1 }, {NONE, CENT}, {0, 0, 2, 0},
//			{2, 4}, {1, 1 }, {NONE, EAST}, {0, 0, 2, 0},
//			{3, 4}, {1, 1 }, {NONE, WEST}, {0, 0, 2, 0},

//			Botones de centrar mapa
			{0, 5}, {2, 1}, {NONE, CENT}, {4, 0, 0, 0},
			{2, 5}, {REMA, 1}, {NONE, CENT}, {4, 0, 0, 0},
			
//			Opciones de combinacion de resultados			
			{0, 6}, {REMA, 1}, {HORI, CENT}, {0, 0, 0, 0},

			{0, 7}, {2, 1}, {NONE, CENT}, {0, 0, 0, 0},
			{2, 7}, {2, 1}, {NONE, CENT}, {0, 0, 0, 0},
			
			{1, 8}, {1, 1}, {NONE, WEST}, {0, 0, 0, 0},
			{3, 8}, {1, 1}, {NONE, WEST}, {0, 0, 0, 0},

			{1, 9}, {1, 1}, {NONE, WEST}, {0, 0, 0, 0},
			{3, 9}, {1, 1}, {NONE, WEST}, {0, 0, 0, 0},

			
//			Separador
			{0, 10}, {REMA, 1}, {HORI, CENT}, {0, 0, 0, 0},

//			Boton combinar
			{0, 11}, {2, REMA}, {NONE, EAST}, {0, 0, 2, 2},
//			Boton ok
			{2, 11}, {REMA, REMA}, {NONE, WEST}, {0, 2, 2, 0}
		};

//		{weightx, weighty}
		final double[][] wMain = new double[][] {
			{0.0, 0.0},
			{0.0, 0.0},
			{0.0, 0.0},
			{0.0, 0.0},

			{0.0, 0.0},
			{0.0, 0.0},

			{0.0, 0.0},
			{0.0, 0.0},

			{1.0, 1.0},
			{1.0, 1.0},

			{0.0, 0.0},
//			{0.0, 0.0},
//			{0.0, 0.0},
			{0.0, 0.0},

			{0.0, 0.0},
			{0.0, 0.0},

			{0.0, 0.0},
			{0.0, 0.0},
			{0.0, 0.0},
			{0.0, 0.0},
			{0.0, 0.0},
			{0.0, 0.0},
			{0.0, 0.0},

			{0.0, 0.0},
			
			{0.0, 0.1},
			{0.0, 0.1}
		};
	}

	public void habilitarCentradoIzquierdo(boolean b) {
		butCentrar1.setEnabled(b);
	}	

	public void habilitarCentradoDerecho(boolean b) {
		butCentrar2.setEnabled(b);
	}


	
	/**
	 * Habilita o deshabilita las opciones de combinacion de resultados.
	 * 
	 * @param b True si se quiere habilitar, false en caso contrario
	 */
	public void habilitarCombinacion(boolean b){
		butCombinar.setEnabled(b);
		radOperIntersect.setEnabled(b);
		radOperUnion.setEnabled(b);
		radPorElementos.setEnabled(b);
		radPorZona.setEnabled(b);
	}

	public void setCantidadIzquierda(int n) {
		lblCantCapa1.setText(n+" elemento(s)");
	}

	public void setCantidadDerecha(int n) {
		lblCantCapa2.setText(n+" elemento(s)");
	}

	public void addOkActionListener(ActionListener action) {
		butOk.addActionListener(action);
	}

	public void addComboIzquierdoListener(ActionListener listener) {
		cmbCapa1.addActionListener(listener);
	}

	public void addComboDerechoListener(ActionListener listener) {
		cmbCapa2.addActionListener(listener);
	}

	public void addBuscarIzquierdoListener(ActionListener listener) {
		butBuscar1.addActionListener(listener);
	}

	public void addBuscarDerechoListener(ActionListener listener) {
		butBuscar2.addActionListener(listener);
	}

	public void addCentrarIzquierdoListener(ActionListener listener) {
		butCentrar1.addActionListener(listener);
	}

	public void addCentrarDerechoListener(ActionListener listener) {
		butCentrar2.addActionListener(listener);
	}
	
	public void addCombinarListener(ActionListener listener) {
		butCombinar.addActionListener(listener);
	}

	public void habilitarBusquedaIzquierda(boolean b) {
		butBuscar1.setEnabled(b);
	}

	public void habilitarBusquedaDerecha(boolean b) {
		butBuscar2.setEnabled(b);
	}

	public void limpiarBusquedaIzquierda() {
		pnlCapa1.removeAll();
		habilitarBusquedaIzquierda(false);
		habilitarCentradoIzquierdo(false);
		limpiarResultadosIzquierda();
	}

	public void limpiarBusquedaDerecha() {
		pnlCapa2.removeAll();
		habilitarBusquedaDerecha(false);
		habilitarCentradoDerecho(false);
		limpiarResultadosDerecha();
	}

	public void setBusquedaIzquierda(SearchCriteriaPanel presentacion) {
		pnlCapa1.add(presentacion);
		pnlCapa1.validate();
	}

	public void setBusquedaDerecha(SearchCriteriaPanel presentacion) {
		pnlCapa2.add(presentacion);
		pnlCapa1.validate();
	}

	public void limpiarResultadosIzquierda() {
		getContentPane().remove(resultadosIzquierda);
		tblCapa1 = new JTable();
		tblCapa1.getSelectionModel().addListSelectionListener(this.leftResultListener);
		
		resultadosIzquierda = new JScrollPane(tblCapa1);
		getContentPane().add(resultadosIzquierda, cg.getConstraints(8));
	}
	
	public void limpiarResultadosDerecha() {
		getContentPane().remove(resultadosDerecha);
		tblCapa2 = new JTable();
		tblCapa2.getSelectionModel().addListSelectionListener(this.rightResultListener);

		resultadosDerecha = new JScrollPane(tblCapa2);
		getContentPane().add(resultadosDerecha, cg.getConstraints(9));
	}
	
	public void setCapasIzquierda(String types[]) {
		for (int i = 0; i < types.length; i++)
			cmbCapa1.addItem(types[i]);
		
		cmbCapa1.setSelectedItem(null);
	}

	public void setCapasDerecha(String types[]) {
		for (int i = 0; i < types.length; i++)
			cmbCapa2.addItem(types[i]);
		
		cmbCapa2.setSelectedItem(null);
	}

	public JButton botonBuscarIzquierdo() {
		return butBuscar1;
	}

	public JButton botonBuscarDerecho() {
		return butBuscar2;
	}
	
	public void setResultadosIzquierda(FeatureCollection fc) {
		FeatureTableModel ftm = new FeatureTableModel(fc);
		tblCapa1.setModel(ftm);
	}

	public void setResultadosDerecha(FeatureCollection fc) {
		FeatureTableModel ftm = new FeatureTableModel(fc);
		tblCapa2.setModel(ftm);
	}

	public JComboBox comboIzquierdo() {
		return cmbCapa1;
	}

	public JComboBox comboDerecho() {
		return cmbCapa2;
	}

	public JButton botonCentrarIzquierdo() {
		return butCentrar1;
	}

	public JButton botonCentrarDerecho() {
		return butCentrar2;
	}

	public JTable resultadosIzquierda() {
		return tblCapa1;
	}

	public JTable resultadosDerecha() {
		return tblCapa2;
	}

	public boolean combinarElementos() {
		return radPorElementos.isSelected();
	}

	public boolean combinarIntersectando() {
		return radOperIntersect.isSelected();
	}

	public void setLeftResultListener(ListSelectionListener listener) {
		this.leftResultListener = listener;
	}

	public void setRightResultListener(ListSelectionListener listener) {
		this.rightResultListener = listener;
	}
}
