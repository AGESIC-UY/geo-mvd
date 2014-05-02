package imm.gis.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import imm.gis.core.gui.FilterLayerDialog;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class TestFilterLayerDialog extends JFrame {
	private static final long serialVersionUID = 1L;
	private JButton butTest = new JButton("Dialogo");
	private FilterLayerDialog dialog = new FilterLayerDialog(this);
	
	public TestFilterLayerDialog(){
		super();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		butTest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(!dialog.isVisible());
			}
		});
		
		addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				System.exit(0);
			}
		});
		add(butTest);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new TestFilterLayerDialog();
	}

}
