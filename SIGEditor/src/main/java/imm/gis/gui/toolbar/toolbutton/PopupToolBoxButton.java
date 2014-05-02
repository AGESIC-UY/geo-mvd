/*
 * PopupToolBoxButton.java
 *
 * Created on 15 February 2003, 20:27
 */

package imm.gis.gui.toolbar.toolbutton;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonModel;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;

/** 
 * An extension of JButton to which alternative actions can be added.
 * The button to trigger these actions become available when a
 * dropdown icon is pressed on this button.
 *
 * @author Bob Tarling
 */
public class PopupToolBoxButton extends ToolButton {

	private static final long serialVersionUID = 1L;

	private PopupToolBox _popupToolBox;
    private DecoratedIcon _standardIcon;
    private String tooltip;
    private boolean _showSplitter;
    
    /** Creates a new instance of PopupToolboxButton
     * @param a The default action when pressing this button
     * @param rows The number of rows of buttons to display in the popup toolbox
     * @param cols The number of columns of buttons to display in the popup toolbox
     */
    public PopupToolBoxButton(Action a, int rows, int cols, boolean rollover) {
        super(a);
        setAction(a);
        
        _popupToolBox = new PopupToolBox(rows, cols, rollover);
        
        MyMouseListener myMouseListener = new MyMouseListener();
        addMouseMotionListener(myMouseListener);
        addMouseListener(myMouseListener);
    }

    /** Provide a new default action for this button
     * @param a The new default action
     */    
    public void setAction(Action a) {
        // Create an invisible button to contain the new action.
        // We can use this button to find out various info for the
        // current plaf (eg preferred button size) so we can emulate
        // whatever plaf the user is set to.
        _button = new JButton(a);
        Icon realIcon = _button.getIcon();
        if (a instanceof AbstractButtonAction) {
            realIcon = ((AbstractButtonAction)a).getIcon();
        }
        tooltip = _button.getToolTipText();
        if (tooltip == null || tooltip.trim().length() == 0) {
            tooltip = _button.getText();
        }
        _button.setText(null);
        
        _standardIcon = new DropDownIcon((ImageIcon)realIcon);
        
        // Remove any knowledge of the action to perform from the ancestor
        // we take control of performing the action and displaying the icon.
        super.setAction(new ToolButtonAction());
        setIcon(_standardIcon);
        setText(null);
        setToolTipText(tooltip);
        if (a instanceof ExtendedAction){
        	setDisabledIcon(((ExtendedAction)a).getDisabledIcon());
        }
    }
    
    private void popup() {
        final JPopupMenu popup = new JPopupMenu();
        
        MouseAdapter m = (new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Component c = e.getComponent();
                if (c instanceof ModalButton) {
                    Action a = ((ModalButton)c).getRealAction();
                    setAction(a);
                    a.putValue("popped", Boolean.valueOf(true));
                    setSelected(true);
                    ButtonModel bm = getModel();
                    bm.setRollover(true);
                    setBorderPainted(true);
                    bm.setArmed(true);
                }
                popup.setVisible(false);
            }
        });
        _popupToolBox.setButtonMouseListener(m);
        
        _popupToolBox.rebuild();
        popup.add(_popupToolBox);
        popup.show(this, 0, getHeight());
    }

    /** Add a new action to appear as a button on the
     * popup toolbox
     * @param a The action to be added
     * @return The button generated to trigger the action
     */    
    public JButton add(Action a) {
        if (a.getValue("isDefault") instanceof Boolean) {
            if(((Boolean)a.getValue("isDefault")).booleanValue()) {
                setAction(a);
            }
        }
        return _popupToolBox.add(a);
    }
    
    private int getSplitterPosn() {
        return getIconPosn() + _button.getIcon().getIconWidth() + 3;
    }
    
    /**
     * Get the xy position of the icon.
     * TODO
     * For the moment this assumes that the button has the icon centered.
     */
    private int getIconPosn() {
        int x = (this.getWidth() - _standardIcon.getIconWidth()) / 2;
        return x;
    }
    

    public void paint(Graphics g) {
        super.paint(g);
        Color[] colors = {
	    getBackground(),
	    UIManager.getColor("controlDkShadow"),
	    UIManager.getColor("controlText"),
	    UIManager.getColor("controlHighlight")};

        if (_showSplitter) {
            showSplitter(colors[1], g, getSplitterPosn(),     1, getHeight()-4);
            showSplitter(colors[3], g, getSplitterPosn() + 1, 1, getHeight()-4);
        }
    }
    
    public void showSplitter(Color c, Graphics g, int x, int y, int height) {
        g.setColor(c);
        g.drawLine(x, y + 0, x, y + height);
    }
    
    public void showSplitter(boolean show) {
        if (show && !_showSplitter) {
            _showSplitter = true;
            repaint();
            setToolTipText("Select Tool");
        } else if (!show && _showSplitter) {
            _showSplitter = false;
            repaint();
            setToolTipText(tooltip);
        }
    }

    protected void performAction(java.awt.event.ActionEvent actionEvent) {
        if (_showSplitter) {
            popup();
        } else {
            super.performAction(actionEvent);
        }
    }
    
    private class MyMouseListener extends MouseInputAdapter {

        /**
         * If the mouse movement occurs within the PopupToolBoxButton.
         * If the mouse moves in and out of the area of the button that has the dropdown
         * then change the icon.
         */
        public void mouseMoved(MouseEvent me) {
            showSplitter(me.getX() >= getSplitterPosn());
        }

        /**
         * Empty method to satisy interface only, there is no special
         * action to take place when the mouse first enters the
         * PopupToolBoxButton area
         */
        public void mouseEntered(MouseEvent me) {
            showSplitter(me.getX() >= getSplitterPosn());
        }

        /**
         * Be double sure the dropdowns rollover divider is removed when we leave the
         * button.
         */
        public void mouseExited(MouseEvent me) {
            showSplitter(false);
        }
    }
}
