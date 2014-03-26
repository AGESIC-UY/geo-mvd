package imm.gis.core.interfaces;

import javax.swing.JToolBar;

public interface IUserInterface {

	public void addToolBar(JToolBar bar);
	public void removeToolBar(JToolBar bar);
	
	public void doNonUILogic(final NonUILogic l);
	
	public void showError(String title, Exception e);
	public void showError(Exception e);
	public void showError(String title, String msg);
	public void showError(String msg);
}
