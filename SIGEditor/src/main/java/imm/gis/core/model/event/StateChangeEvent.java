package imm.gis.core.model.event;

import java.util.EventObject;

public class StateChangeEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int oldState;
	private int newState;
	
	public StateChangeEvent(Object s, int o, int n){
		super(s);
		oldState = o;
		newState = n;
	}

	public int getNewState() {
		return newState;
	}

	public int getOldState() {
		return oldState;
	}
}
