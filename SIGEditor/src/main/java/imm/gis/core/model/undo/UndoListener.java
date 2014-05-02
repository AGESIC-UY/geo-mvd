package imm.gis.core.model.undo;


import java.util.EventListener;

public interface UndoListener extends EventListener {
	public void changedPerformed(UndoableOperation uo);
}
