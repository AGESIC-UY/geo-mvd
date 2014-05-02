package imm.gis.gui.actions.misc;

import imm.gis.core.controller.ContBusqueda;
import imm.gis.core.gui.GuiUtils;
import imm.gis.gui.toolbar.toolbutton.ExtendedAction;

import java.awt.event.ActionEvent;

public class SearchAction extends ExtendedAction{

	private static final long serialVersionUID = 1L;
	private ContBusqueda searchController;

	public SearchAction(ContBusqueda searchController){
		super("Buscar (F3)", GuiUtils.loadIcon("Search16.gif"));
		
		this.searchController = searchController;
	}
	
	public void actionPerformed(ActionEvent e){
		searchController.show();
	}
}
