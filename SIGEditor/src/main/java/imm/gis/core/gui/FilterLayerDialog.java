package imm.gis.core.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class FilterLayerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public JList lstAttr = new JList();
	public JList lstValues = new JList();
	public JButton butEquals = new JButton("=");
	public JButton butMajor = new JButton(">");
	public JButton butMinor = new JButton("<");
	public JButton butMajorEq = new JButton(">=");
	public JButton butMinorEq = new JButton("<=");
	public JButton butAnd = new JButton("AND");
	public JButton butOr = new JButton("OR");
	
	public JButton butOk = new JButton("    Ok    ");
	public JButton butCancel = new JButton("Cancelar");
	public JTextArea txtFilter = new JTextArea();
	public JScrollPane scrollFilter = new JScrollPane();
	public JLabel lblFilter = new JLabel("Filtro: ");
//	private static Logger logger = Logger.getLogger(FilterLayerDialog.class);
	public FilterLayerDialog(java.awt.Component c){
		super();
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		createGui2();
		pack();
		setLocationRelativeTo(c);
	}
	
	private void createGui2(){
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		// 1er fila
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.insets = new Insets(3, 3, 3, 3);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		getContentPane().add(lblFilter, c);

		// 2da fila
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		lstAttr.setFixedCellWidth(200);
		
		getContentPane().add(new JScrollPane(lstAttr), c);

		c.gridwidth = GridBagConstraints.RELATIVE;
		getContentPane().add(createCenterPanel(), c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		//lstValues.setFixedCellWidth(200);
		getContentPane().add(new JScrollPane(lstValues), c);
		
		// 3a fila
		txtFilter.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK));
		txtFilter.setRows(3);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.0;
		scrollFilter.setViewportView(txtFilter);
		getContentPane().add(/*txtFilter*/scrollFilter, c);
		// 4ta fila
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JPanel south = new JPanel(new FlowLayout());
		south.add(butOk);
		south.add(butCancel);
		getContentPane().add(south, c);
	}

	private JPanel createCenterPanel(){
		JPanel res = new JPanel(new GridLayout(3, 3));
		res.add(butEquals);
		res.add(butMajor);
		res.add(butMinor);
		
		res.add(butMajorEq);
		res.add(butMinorEq);
		
		res.add(butAnd);
		res.add(butOr);
		
		return res;
	}
}
