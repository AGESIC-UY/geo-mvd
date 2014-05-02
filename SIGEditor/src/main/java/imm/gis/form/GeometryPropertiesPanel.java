package imm.gis.form;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.vividsolutions.jts.geom.Geometry;

public class GeometryPropertiesPanel extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel propertiesPanel;
	
	public GeometryPropertiesPanel(Geometry g) {
		super();
		setModal(true);
		setTitle("Propiedades");
		
		propertiesPanel = new JPanel();
		propertiesPanel.setLayout(new GridBagLayout());
		propertiesPanel.setBorder(new EtchedBorder());
		// Tipo de geometria
		
		String geometryType = g.getGeometryType().equals("Point") ? "Punto" :
								g.getGeometryType().equals("LineString") ? "Linea" : "Poligono";
				
		int propIndex = 0;
		
		propertiesPanel.add(new JLabel("Tipo"),
				new GridBagConstraints(0, propIndex, 1, 1, 0, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(
								2, 2, 2, 2), 0, 0));
		
		JTextField geometryTypeField = new JTextField(geometryType);
		
		geometryTypeField.setEditable(false);

		propertiesPanel.add(geometryTypeField,
						new GridBagConstraints(
								1,
								propIndex,
								1,
								1,
								0,
								0,
								GridBagConstraints.EAST,
								GridBagConstraints.BOTH,
								new Insets(2, 2, 2, 2), 0, 0));

		propIndex++;
		
		if (geometryType.equals("Linea")) {
			propertiesPanel.add(new JLabel("Largo"),
					new GridBagConstraints(0, propIndex, 1, 1, 0, 0,
							GridBagConstraints.WEST,
							GridBagConstraints.HORIZONTAL, new Insets(
									2, 2, 2, 2), 0, 0));
			
			JFormattedTextField lengthTextField = new JFormattedTextField(new Double(g.getLength()));
			lengthTextField.setEditable(false);
			
			propertiesPanel.add(lengthTextField,
					new GridBagConstraints(
							1,
							propIndex,
							1,
							1,
							0,
							0,
							GridBagConstraints.EAST,
							GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));
			
			propIndex++;
			
			propertiesPanel.add(new JLabel("Cant. puntos"),
					new GridBagConstraints(0, propIndex, 1, 1, 0, 0,
							GridBagConstraints.WEST,
							GridBagConstraints.HORIZONTAL, new Insets(
									2, 2, 2, 2), 0, 0));
			
			JFormattedTextField puntosTextField = new JFormattedTextField(new Integer(g.getNumPoints()));
			puntosTextField.setEditable(false);
			
			propertiesPanel.add(puntosTextField,
					new GridBagConstraints(
							1,
							propIndex,
							1,
							1,
							0,
							0,
							GridBagConstraints.EAST,
							GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));
		}
		else if (geometryType.equals("Poligono")) {
			propertiesPanel.add(new JLabel("Area"),
					new GridBagConstraints(0, propIndex, 1, 1, 0, 0,
							GridBagConstraints.WEST,
							GridBagConstraints.HORIZONTAL, new Insets(
									2, 2, 2, 2), 0, 0));
			
			JFormattedTextField lengthTextField = new JFormattedTextField(new Double(g.getArea()));
			lengthTextField.setEditable(false);
			
			propertiesPanel.add(lengthTextField,
					new GridBagConstraints(
							1,
							propIndex,
							1,
							1,
							0,
							0,
							GridBagConstraints.EAST,
							GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));
			
			propIndex++;
			
			propertiesPanel.add(new JLabel("Cant. puntos"),
					new GridBagConstraints(0, propIndex, 1, 1, 0, 0,
							GridBagConstraints.WEST,
							GridBagConstraints.HORIZONTAL, new Insets(
									2, 2, 2, 2), 0, 0));
			
			JFormattedTextField puntosTextField = new JFormattedTextField(new Integer(g.getNumPoints()-1));
			puntosTextField.setEditable(false);
			
			propertiesPanel.add(puntosTextField,
					new GridBagConstraints(
							1,
							propIndex,
							1,
							1,
							0,
							0,
							GridBagConstraints.EAST,
							GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));
		}
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JButton aceptar = new JButton("Aceptar");
		aceptar.addActionListener(this);
		buttonPanel.add(aceptar);
		
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		add(propertiesPanel);
		add(Box.createVerticalStrut(10));
		add(buttonPanel);
		
		if (geometryType.equals("Punto"))
			setPreferredSize(new Dimension(220,100));
		else
			setPreferredSize(new Dimension(220,150));

		pack();
		
		imm.gis.core.gui.GuiUtils.centerWindowOnScreen(this);

		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		dispose();
	}
}
