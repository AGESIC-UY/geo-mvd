package imm.gis.form.item;

import imm.gis.client.fidmapper.FidMapperUtil;
import imm.gis.core.layer.metadata.LayerAttributePresentation;

import org.geotools.feature.AttributeType;

public class FIDFormItem extends BasicFormItem {
	private String layerName;

	public FIDFormItem(String _layerName,AttributeType at, Object value, LayerAttributePresentation lap) {
		super(at, value, lap==null ? new LayerAttributePresentation():lap);
		this.layerName = _layerName;
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  public Object getValue() {
	  if(super.getValue() == null){
		  return null;
	  }
	  String value = super.getValue().toString();
	  if (value.equals("")){
		  return value;
	  }
	  
	  return FidMapperUtil.getInstance().getFID(this.layerName, value);
	  //return ilfid.getFID(super.getValue().toString());
}
}
