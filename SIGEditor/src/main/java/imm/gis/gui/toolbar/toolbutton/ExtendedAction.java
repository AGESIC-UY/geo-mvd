package imm.gis.gui.toolbar.toolbutton;

import javax.swing.AbstractAction;
import javax.swing.Icon;

public abstract class ExtendedAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Icon disabledIcon = null;
	private boolean isToggle = false;
	
	public boolean isToggle() {
		return isToggle;
	}

	public void setToggle(boolean isToggle) {
		this.isToggle = isToggle;
	}

	public ExtendedAction(String text){
		super(text);
	}

	public ExtendedAction(String text, Icon icon){
		super(text, icon);
	}

	public ExtendedAction(String text, Icon icon, Icon dis){
		super(text, icon);
		disabledIcon = dis;
	}

	public Icon getDisabledIcon() {
		return disabledIcon;
	}

	public void setDisabledIcon(Icon disabledIcon) {
		this.disabledIcon = disabledIcon;
	}	
}
