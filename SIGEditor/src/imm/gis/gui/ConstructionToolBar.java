package imm.gis.gui;

import java.awt.Component;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import imm.gis.gui.toolbar.ToolBar;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;
import imm.gis.gui.toolbar.toolbutton.PopupToolBoxButton;

public abstract class ConstructionToolBar extends ToolBar {

	private static final long serialVersionUID = 1L;

	public ConstructionToolBar(boolean rollover) {
		if (rollover)
			putClientProperty("JToolBar.isRollover",  Boolean.TRUE);
		else
			putClientProperty("JToolBar.isRollover",  Boolean.FALSE);
	}
	
    protected void addItemsToToolBar(Object items[], boolean rollover) {
        for (int i=0; i < items.length; ++i) {
            addItemToToolBar(items[i], rollover);
        }
    }

	protected void addItemToToolBar(Object item, boolean rollover) {
        
        if (item == null) {
            addSeparator();
        } else if (item instanceof ExtendedAction){
            ExtendedAction a = (ExtendedAction)item;
            javax.swing.AbstractButton button;
            
            if (a.isToggle()){
            	button = new JToggleButton(a);
            } else {
            	button = new JButton(a);
            }
            
            //button.setBorderPainted(false);
            
            add(button);
            if (button.getToolTipText() == null || button.getToolTipText().trim().length() == 0) {
                button.setToolTipText((String)a.getValue(Action.NAME));
            }
            button.setDisabledIcon(a.getDisabledIcon());
            if (button.getIcon() != null) button.setText(null);
        } else if (item instanceof Action) {
            Action a = (Action)item;
            JButton button = add(a);
            button.setBorderPainted(false);
            if (button.getToolTipText() == null || button.getToolTipText().trim().length() == 0) {
                button.setToolTipText((String)a.getValue(Action.NAME));
            }
        } else if (item instanceof Object[]) {
            Object[] subActions = (Object[])item;
            JButton button = buildPopupToolBoxButton(subActions, rollover);
            button.setBorderPainted(false);
            add(button);
        } else if (item instanceof Component) {
            add((Component)item);
        }
    }

    private static PopupToolBoxButton buildPopupToolBoxButton(Object[] actions, boolean rollover) {
        PopupToolBoxButton toolBox = null;
        for (int i=0; i < actions.length; ++i) {
            if (actions[i] instanceof Action) {
                Action a = (Action)actions[i];
                if (toolBox == null) {
                    toolBox = new PopupToolBoxButton(a, 0, 1, rollover);
                }
                toolBox.add(a);
            }
            else if (actions[i] instanceof Object[]) {
                Object[] actionRow = (Object[])actions[i];
                for (int j=0; j < actionRow.length; ++j) {
                    Action a = (Action)actionRow[j];
                    if (toolBox == null) {
                        int cols = actionRow.length;
                        toolBox = new PopupToolBoxButton(a, 0, cols, rollover);
                    }
                    toolBox.add(a);
                }
            }
        }
        return toolBox;
    }

    public abstract Map getActions();
}
