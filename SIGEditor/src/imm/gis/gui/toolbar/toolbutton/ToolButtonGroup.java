/*
 * StickyButtonGroup.java
 *
 * Created on 16 March 2003, 17:22
 */

package imm.gis.gui.toolbar.toolbutton;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 *
 * @author  Bob Tarling
 */
public class ToolButtonGroup {
    
    ArrayList<ToolButton> buttons = new ArrayList<ToolButton>();
    ToolButton defaultButton;
    
    /** Creates a new instance of StickyButtonGroup */
    public ToolButtonGroup() {
    }
    
    public ToolButton add(ToolButton toolButton) {
        buttons.add(toolButton);
        toolButton.setInGroup(this);
        return toolButton;
    }
    
    public void buttonSelected(ToolButton toolButton) {
        Iterator it = buttons.iterator();
        while (it.hasNext()) {
            ToolButton button = (ToolButton)it.next();
            if (button != toolButton) {
                button.setSelected(false);
                button.setBorderPainted(false);
            } else {
                button.setBorderPainted(true);
            }
        }
    }
    
    public ToolButton setDefaultButton(ToolButton toolButton) {
        defaultButton = toolButton;
        return toolButton;
    }
    
    public ToolButton getDefaultButton() {
        return defaultButton;
    }
}
