package imm.gis.core.data.mixto;

import org.geotools.feature.DefaultFeature;
import org.geotools.feature.DefaultFeatureType;
import org.geotools.feature.IllegalAttributeException;


public class MixtoFIDFeature extends DefaultFeature {

  protected MixtoFIDFeature(DefaultFeatureType ft, Object[] attributes, String id)
    throws IllegalAttributeException {
    super(ft, attributes, id);

  }

  public void setID(String id) {
    this.featureId = id;
  }
}
