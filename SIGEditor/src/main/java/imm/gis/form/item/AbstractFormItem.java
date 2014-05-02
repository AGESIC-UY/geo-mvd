package imm.gis.form.item;

import imm.gis.form.item.interactor.ItemInteractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.table.TableCellEditor;

import org.geotools.feature.AttributeType;


public abstract class AbstractFormItem extends AbstractCellEditor
	implements TableCellEditor, IAttributeChangeNotifier, IAttributeChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AttributeType at;
	private Collection<IAttributeChangeListener> listeners;
	private ItemInteractor interactor;
	
	private boolean isLocking = false;
	private boolean isLockedDependence = false;
	protected boolean changed = false;

	public void setLocking(boolean locking) {
		this.isLocking = locking;
	}
	
	public boolean isLocking() {
		return isLocking;
	}
	
	public void setLockedDependence(boolean lockedDependence) {
		this.isLockedDependence = lockedDependence;
	}
	
	public boolean isLockedDependence() {
		return isLockedDependence;
	}
	

	public AbstractFormItem(AttributeType _at) {
		listeners = new ArrayList<IAttributeChangeListener>();
		at = _at;
	}
	
	public AttributeType getAttributeType() {
		return at;
	}
	
	public abstract JComponent getValueComponent();
	public abstract Object getValue();
	public abstract void setEnabled(boolean enabled);
	
	public void addAttributeChangeListener(IAttributeChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeAttributeChangeListener(IAttributeChangeListener listener) {
		listeners.remove(listener);
	}
	
	protected void fireAttributeChanged(Object newValue) {
		if (interactor != null)
			interactor.setMyValue(newValue);
		
		Iterator<IAttributeChangeListener> i = listeners.iterator();
		
		while (i.hasNext())
			i.next().attributeChanged(at.getLocalName(), newValue);
	}
	
	public void attributeChanged(String attributeName, Object newValue) {
		changed = true;
		if (interactor != null) {
			interactor.itemChanged(attributeName, newValue);
			setNewValues(interactor.getValue());
		}
		
		if (isLockedDependence)
			setEnabled(true);
	}

	protected abstract void setNewValues(Object newValues);
	
	protected void setItemInteractor(ItemInteractor interactor) {
		this.interactor = interactor;
	}
	
	protected ItemInteractor getItemInteractor() {
		return this.interactor;
	}

	/**
	 * Indica si ha cambiado el valor de este item.
	 * 
	 * @return Indica si ha cambiado el valor de este item
	 */
	public abstract boolean hasChanged();
}
