package imm.gis.form.item.interactor;

import imm.gis.consulta.ICargadorLOV;
import imm.gis.core.feature.ExternalAttribute;

public class FilteringLovLoader implements ItemInteractor {

	private ICargadorLOV iclov;
	
	public FilteringLovLoader(ICargadorLOV iclov) {
		this.iclov = iclov;
	}
	
	public Object getValue() {
		iclov.setSearchMethod(ICargadorLOV.SEARCH_EQUALS);

		ExternalAttribute[] newValues = null;
		
		try {
			newValues = iclov.getValores();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return newValues;
	}

	public void itemChanged(String attributeName, Object newValue) {
		iclov.addRestriction(attributeName, newValue);		
	}

	public void setMyValue(Object myValue) {
	}
	
}
