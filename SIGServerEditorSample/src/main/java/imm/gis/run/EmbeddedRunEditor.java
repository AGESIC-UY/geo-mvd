package imm.gis.run;

import imm.gis.SigEditor;

import javax.swing.JFrame;

public class EmbeddedRunEditor extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmbeddedRunEditor(){
		super("Editor embebido");
		setSize(600, 400);
		SigEditor editor = new SigEditor("agenda", "Prueba");
		editor.getControlador().getIApplication().setExitOnClose(true);
		setContentPane(editor.getControlador().getIApplication().getMainGUI().getContentPane());
		setVisible(true);
	}

	public static void main(String[] args) {
		new EmbeddedRunEditor();
	}	
	
}
