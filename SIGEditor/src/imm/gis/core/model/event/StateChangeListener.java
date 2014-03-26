package imm.gis.core.model.event;

import java.util.EventListener;

public interface StateChangeListener extends EventListener {
	public void stateChanged(StateChangeEvent e);
}
