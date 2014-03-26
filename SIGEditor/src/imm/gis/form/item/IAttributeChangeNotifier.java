package imm.gis.form.item;

public interface IAttributeChangeNotifier {

	public void addAttributeChangeListener(IAttributeChangeListener listener);
	public void removeAttributeChangeListener(IAttributeChangeListener listener);
}
