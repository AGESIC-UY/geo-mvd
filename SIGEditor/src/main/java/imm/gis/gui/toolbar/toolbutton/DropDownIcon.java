/*
 * DropDownIcon.java
 *
 * Created on 25 February 2003, 22:12
 */

package imm.gis.gui.toolbar.toolbutton;

import javax.swing.ImageIcon;

/**
 * An implementation of a decorated icon which adds a drop down decoration
 * to the given icon
 *
 * @author Bob Tarling
 */
public class DropDownIcon extends DecoratedIcon {

	private static final long serialVersionUID = 1L;

	private static final int[][] standardBuffer = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0},
        {0, 0, 1, 1, 1, 1, 1, 1, 3, 3, 0},
        {0, 0, 0, 1, 1, 1, 1, 3, 3, 0, 0},
        {0, 0, 0, 0, 1, 1, 3, 3, 0, 0, 0},
        {0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };


    /** Creates a new instance of DropDownIcon */
    DropDownIcon(ImageIcon imageIcon) {
        super(imageIcon);
        init(standardBuffer);
    }
}
