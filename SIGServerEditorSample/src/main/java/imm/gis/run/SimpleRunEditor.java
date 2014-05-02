package imm.gis.run;

import imm.gis.SigEditor;

public class SimpleRunEditor{
	
	// Para ejecutar por RMI (como desktop, no por web) pasarle el parametro "services.rmi" a la jvm...
	// Esto se hace poniendo -Dservices.rmi en la linea de comandos de la jvm
	
	public static void main(String[] args) {
		SigEditor editor = new SigEditor("gnn");
		editor.getControlador().getIApplication().setExitOnClose(true);
		editor.getControlador().getIApplication().getMainGUI().setVisible(true);	
	}	
}
