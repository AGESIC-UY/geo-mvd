package imm.gis.core.controller;

import java.awt.event.ActionListener;

public interface IMapListener extends ActionListener {
	
	public String getListenedSchema();
	public void mapEvent(MapEvent me) throws InvalidStateException;
}
