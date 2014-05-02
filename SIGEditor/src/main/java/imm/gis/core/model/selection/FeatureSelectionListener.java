package imm.gis.core.model.selection;


import java.util.EventListener;

public interface FeatureSelectionListener extends EventListener {
	public void selectionPerformed(FeatureSelectionEvent se);
}
