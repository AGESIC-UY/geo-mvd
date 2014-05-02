package imm.gis.core.interfaces;

import javax.swing.JFrame;

public interface IApplication {

	public JFrame getMainGUI();
	public void setExitOnClose(boolean exitOnClose);
	public void cerrar();
	public void cerrar(boolean confirm);
}
