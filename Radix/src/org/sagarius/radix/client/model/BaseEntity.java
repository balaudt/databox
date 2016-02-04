package org.sagarius.radix.client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import org.gwt.beansbinding.core.client.util.HasPropertyChangeSupport;
import org.sagarius.radix.client.model.util.BaseEntry;

public class BaseEntity implements HasPropertyChangeSupport, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	protected BaseEntry entry;

	public BaseEntity(BaseEntry entry) {
		this.entry = entry;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	public String render() {
		return entry.render();
	}

}
