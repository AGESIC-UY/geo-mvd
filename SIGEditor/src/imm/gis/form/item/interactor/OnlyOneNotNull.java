package imm.gis.form.item.interactor;

public class OnlyOneNotNull implements ItemInteractor {

	private Object myValue = null;
	
	public Object getValue() {
		return myValue;
	}

	public void itemChanged(String attributeName, Object otherValue) {
		if (otherValue != null)
			myValue = null;
	}
	
	public void setMyValue(Object myValue) {
		this.myValue = myValue;
	}

}
