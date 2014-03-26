package imm.gis.core.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;

import imm.gis.core.interfaces.AbstractNonUILogic;
import imm.gis.core.interfaces.CoordinateListener;
import imm.gis.core.interfaces.IMap;
import imm.gis.core.interfaces.IUserInterface;
import imm.gis.gui.toolbar.ToolBarFactory;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinateBox implements CoordinateListener {

	private JToolBar toolBarPos;
	private JTextField mapCoordinateX;
	private JTextField mapCoordinateY;

	private NumberFormat nf;
	
	private IMap map;
	private IUserInterface userInterface;

	public CoordinateBox(IMap map, IUserInterface userInterface) {
		this.map = map;
		this.userInterface = userInterface;
		
		mapCoordinateX = new JTextField(10);
		mapCoordinateY = new JTextField(10);

		mapCoordinateX.addKeyListener(new ConfirmationListener());
		mapCoordinateY.addKeyListener(new ConfirmationListener());
		
		mapCoordinateX.setMaximumSize(new Dimension(100,20));
		mapCoordinateY.setMaximumSize(new Dimension(100,20));
		
		Object tmp[] = new Object[] {
				new JLabel("E(X): "),
				mapCoordinateX,
				null,
				new JLabel("N(Y): "),
				mapCoordinateY
		};
		
		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		
		toolBarPos = ToolBarFactory.createToolBar(true, tmp);
		
		
	}

	public Component getToolBar() {
		return toolBarPos;
	}
	
	private class ConfirmationListener extends KeyAdapter {
		
		public void keyPressed(KeyEvent e) {
			
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				try {
					
					
					final double coordinateX = nf.parse(mapCoordinateX.getText()).doubleValue();
					final double coordinateY = nf.parse(mapCoordinateY.getText()).doubleValue();
					
					if (coordinateX > 0 && coordinateY > 0) {
						AbstractNonUILogic l = new AbstractNonUILogic() {
							public void logic() {
								try {
									map.center(new Coordinate(coordinateX, coordinateY));
								}
								catch (Exception e) {
									userInterface.showError("Error en la navegacion",e);
									e.printStackTrace();
								}
							}
						};
						
						userInterface.doNonUILogic(l);
					}
					else
						JOptionPane.showMessageDialog(null,"Las coordenadas introducidas no son validas","",JOptionPane.WARNING_MESSAGE);
				}
				catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null,"Las coordenadas introducidas no son validas","",JOptionPane.WARNING_MESSAGE);					
				}
				catch (ParseException ex) {
					JOptionPane.showMessageDialog(null,"Las coordenadas introducidas no son validas","",JOptionPane.WARNING_MESSAGE);					
				}
			}
		}
	}

	public void coordinateChanged(Coordinate c) {
		mapCoordinateX.setText(nf.format(c.x));
		mapCoordinateY.setText(nf.format(c.y));
	}

}
