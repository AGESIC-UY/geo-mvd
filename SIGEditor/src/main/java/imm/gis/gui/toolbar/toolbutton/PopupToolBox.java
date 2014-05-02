/*
 * PopupToolBox.java
 *
 * Created on 23 February 2003, 09:59
 */

package imm.gis.gui.toolbar.toolbutton;

import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.JButton;

/**
 * A toolbox which appears when the user click the drop down image on
 * a PopupToolBoxButton
 *
 * @author  Bob Tarling
 */
public class PopupToolBox extends ToolBox {

	private static final long serialVersionUID = 1L;

	ArrayList<Action> actions = new ArrayList<Action>();
    MouseListener mouseListener;

    /** Creates a new instance of PopupToolBox */
    public PopupToolBox(int rows, int cols) {
        super(rows, cols);
    }

    /** Creates a new instance of PopupToolBox */
    public PopupToolBox(int rows, int cols, boolean rollover) {
        super(rows, cols, rollover);
    }

    public JButton add(Action action) {
        JButton button = super.add(action);
        actions.add(action);
        
        return button;
    }

    public void setButtonMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    /**
     * Occasionally the ToolBox gets in a state where a button
     * shows rollover status at the wrong time.
     * The only way to get around this is to rebuild the ToolBox.
     */
    public void rebuild() {
        super.removeAll();
        Iterator it = actions.iterator();
        while(it.hasNext()) {
            Action a = (Action)it.next();
            JButton button = super.add(a);
            button.setBorderPainted(false);
            if (mouseListener != null) {
                button.addMouseListener(mouseListener);
            }
        }
    }
}
