package imm.gis.form.item.interactor;

public interface ItemInteractor {

	public void itemChanged(String attributeName, Object newValue);
	public Object getValue();
	public void setMyValue(Object myValue);
}
