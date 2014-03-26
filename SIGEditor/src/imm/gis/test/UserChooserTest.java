package imm.gis.test;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import imm.gis.core.gui.UserChooser;

public class UserChooserTest extends JFrame {
	private static final long serialVersionUID = 1L;
	private UserChooser userChooser = new UserChooser("Prueba a ver que sale", JOptionPane.getRootFrame());
	
	public UserChooserTest(){
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		Object[] values = new Object[]{"Opcion1", "Opcion2"};
		userChooser.choose(values);
		
		if (userChooser.getSelectedOptions() != null){
			for (Object o : userChooser.getSelectedOptions()){
				System.out.println(o);			
			}			
		}		
	}
	
	public static void main(String[] args){
		new UserChooserTest();
	}
}
