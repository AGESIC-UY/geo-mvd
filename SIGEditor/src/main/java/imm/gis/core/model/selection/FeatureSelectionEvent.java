package imm.gis.core.model.selection;

import java.util.Collection;
import java.util.EventObject;

import org.geotools.feature.Feature;

public class FeatureSelectionEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private int command;
	private String layer;
	private Feature lastSelChanged;
	static public final int SELECTED_FEATURE = 0;
	static public final int UNSELECTED_FEATURE = 1;
	static public final int UNSELECTED_ALL_FEATURES = 2;
	static public final int SELECTED_LAYER = 3;
	static public final int UNSELECTED_LAYER = 4;
	
	public FeatureSelectionEvent(SelectionModel md, int com, String layer,Feature selChanged){
		super(md);
		this.layer = layer;
		command = com;	
		lastSelChanged = selChanged;
	}
	
	public int getCommand(){
		return command;
	}

	public String getLayerName() {
		return layer;
	}

	public Feature getLastSelChanged() {
		return lastSelChanged;
	}
	
	public Collection getAllSelected(){
		return ((SelectionModel)getSource()).getSelected(getLayerName());
	}
}
