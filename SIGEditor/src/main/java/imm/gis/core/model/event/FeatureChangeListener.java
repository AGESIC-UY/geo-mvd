package imm.gis.core.model.event;

import java.util.EventListener;

public interface FeatureChangeListener extends EventListener {
	public void isChanging(FeatureChangeEvent fc);
	public void changePerformed(FeatureChangeEvent fc);
	public String getLayer();
}
