package com.link_intersystems.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;

public class WeakReferencePropertyChangeSupport extends PropertyChangeSupport
		implements PropertyChangeListener {

	private static final long serialVersionUID = 767896317743769551L;

	private Collection<PropertyChangeListener> removeQueue = new HashSet<PropertyChangeListener>();

	public WeakReferencePropertyChangeSupport(Object sourceBean) {
		super(sourceBean);
	}

	public void addWeakReferencePropertyChangeListener(
			PropertyChangeListener propertyChangeListener) {
		WeakReferencePropertyChangeListener weakReferencePropertyChangeListener = new WeakReferencePropertyChangeListener(
				propertyChangeListener, removeQueue);
		addPropertyChangeListener(weakReferencePropertyChangeListener);
	}

	public void firePropertyChange(PropertyChangeEvent evt) {
		super.firePropertyChange(evt);

		if (!removeQueue.isEmpty()) {
			for (PropertyChangeListener listener : removeQueue) {
				this.removePropertyChangeListener(listener);
			}
		}
	}

	private static class WeakReferencePropertyChangeListener implements
			PropertyChangeListener {

		private WeakReference<PropertyChangeListener> weakReferent;
		private Collection<PropertyChangeListener> removeQueue;

		public WeakReferencePropertyChangeListener(
				PropertyChangeListener weakReferent,
				Collection<PropertyChangeListener> removeQueue) {
			this.removeQueue = removeQueue;
			this.weakReferent = new WeakReference<PropertyChangeListener>(
					weakReferent);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			PropertyChangeListener propertyChangeListener = weakReferent.get();
			if (propertyChangeListener == null) {
				removeQueue.add(this);
			} else {
				propertyChangeListener.propertyChange(evt);
			}
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		firePropertyChange(evt);
	}

}
