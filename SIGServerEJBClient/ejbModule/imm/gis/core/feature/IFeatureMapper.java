package imm.gis.core.feature;

import java.io.Serializable;

public interface IFeatureMapper extends Serializable {
void setFeatureId(String fID);
String getFeatureId();
Object getOriginalId();
}
