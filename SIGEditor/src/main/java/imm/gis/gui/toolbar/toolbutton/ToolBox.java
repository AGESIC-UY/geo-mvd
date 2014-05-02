/*
 * Toolbox.java
 *
 * Created on 23 February 2003, 09:59
 */

package imm.gis.gui.toolbar.toolbutton;

import java.awt.GridLayout;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * A toolbar where buttons are shown in a grid instead of a row.
 * @author  Bob Tarling
 */
public class ToolBox extends JToolBar {

	private static final long serialVersionUID = 1L;

	private int _rows;
    private int _cols;

    /** Creates a new instance of ToolBox
     * @param rows the number of rows to display in the toolbox
     * @param cols the number of columns to display in the toolbox
     */
    public ToolBox(int rows, int cols) {
        this(rows, cols, false);
    }

    /** Creates a new instance of ToolBox
     * @param rows the number of rows to display in the toolbox
     * @param cols the number of columns to display in the toolbox
     */
    public ToolBox(int rows, int cols, boolean rollover) {
        super();
        setRollover(rollover);
        _rows = rows;
        _cols = cols;
        setLayout(new GridLayout(_rows,_cols));
        setFloatable(false);
    }

    /**
     * Turn on/off the rollover effect.
     * @param rollover true to turn rollover effects on
     */
    public void setRollover(boolean rollover) {
        // TODO Check for JDK1.4 before calling super class setRollover
        //super.setRollover(rollover);
        //this._rollover = rollover;
        // TODO Check for JDK1.4 before using Boolean.valueOf(rollover)
        //this.putClientProperty("JToolBar.isRollover", Boolean.valueOf(rollover));
        Boolean showRollover = Boolean.FALSE;
        if (rollover) showRollover = Boolean.TRUE;
        this.putClientProperty("JToolBar.isRollover",  showRollover);
    }
    
    public JButton add(Action action) {
        JButton button;

        if (action instanceof ModalAction) {
            button = new ModalButton(action);
            add(button);
        } else {
            button = super.add(action);
        }
        return button;
    }
}
