package imm.gis.run;

import imm.gis.SigEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;

public class PopupRunEditor extends JFrame implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton butLanza = new JButton("Va editor...");
	
	public PopupRunEditor(){
		super("Lanzador");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		butLanza.addActionListener(new MyAction(this));
		add(butLanza);
		pack();
		setVisible(true);
	}
	
	
	public static void main(String[] args) {
		new PopupRunEditor();
	}


	public void update(Observable arg0, Object arg1) {
		butLanza.setEnabled(true);
	}
	
	private class MyAction implements ActionListener{
		private Observer obs;
		
		public MyAction(Observer o){
			obs = o;
		}
		public void actionPerformed(ActionEvent evt){
			SigEditor editor = new SigEditor("limpieza", obs, "Prueba");
			editor.getControlador().getIApplication().getMainGUI().setVisible(true);
			butLanza.setEnabled(false);
		}
	}
}
