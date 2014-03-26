/*
 * PopupToolBoxButton.java
 *
 * Created on 15 February 2003, 20:27
 */

package imm.gis.gui.toolbar.toolbutton;

import javax.swing.Action;
import javax.swing.JButton;

/** An extension of JButton to which alternative actions can be added.
 * The button to trigger these actions become available when a
 * dropdown icon is pressed on this button.
 * @author Bob Tarling
 */
public class ModalButton extends ToolButton {

	private static final long serialVersionUID = 1L;

	private String tooltip;
    //private ToolButtonAction stickyAction;
    
    /** Creates a new instance of PopupToolboxButton
     * @param a The default action when pressing this button
     * @param rows The number of rows of buttons to display in the popup toolbox
     * @param cols The number of columns of buttons to display in the popup toolbox
     */
    public ModalButton(Action a) {
        super(a);
        setAction(a);
    }

    /** Provide a new default action for this button
     * @param a The new default action
     */
    public void setAction(Action a) {
        super.setAction(a);
        // Create an invisible button to contain the new action.
        // We can use this button to find out various info for the
        // current plaf (eg preferred button size) so we can emulate
        // whatever plaf the user is set to.
        _button = new JButton(a);
        tooltip = _button.getToolTipText();
        if (tooltip == null || tooltip.trim().length() == 0) {
            tooltip = _button.getText();
        }
        setText(null);
        if (a instanceof ModalAction) {
            // Remove any knowledge of the action to perform from the ancestor
            // we take control of performing the action and displaying the icon.
            super.setAction(new ToolButtonAction());
            setIcon(_button.getIcon());
            setToolTipText(tooltip);
        }
    }
    
    public Action getRealAction() {
        if (_button == null) return null;
        return _button.getAction();
    }
}
