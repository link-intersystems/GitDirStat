package com.link_intersystems.beans;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.text.MessageFormat;

public class ReflectivePropertyChangeListenerBinding implements
		PropertyChangeListenerBinding {

	private Object bean;
	private BeanInfo beanInfo;
	private EventSetDescriptor propertyChangeListenerDescriptor;

	public ReflectivePropertyChangeListenerBinding(Object bean) {
		this.bean = bean;
		try {
			beanInfo = Introspector.getBeanInfo(bean.getClass());
		} catch (IntrospectionException e) {
			String msg = MessageFormat.format(
					"Unable to introspect bean '{1}'", bean.getClass());
			throw new RuntimeException(msg);
		}
	}

	private EventSetDescriptor getPropertyChangeListenerDescriptor() {
		if (this.propertyChangeListenerDescriptor == null) {
			EventSetDescriptor[] eventSetDescriptors = beanInfo
					.getEventSetDescriptors();
			for (int i = 0; i < eventSetDescriptors.length; i++) {
				EventSetDescriptor eventSetDescriptor = eventSetDescriptors[i];
				String name = eventSetDescriptor.getName();
				if ("propertyChange".equals(name)) {
					this.propertyChangeListenerDescriptor = eventSetDescriptor;
					break;
				}
			}

			if (this.propertyChangeListenerDescriptor == null) {
				String msg = MessageFormat
						.format("Can't find PropertyChangeEvent binding support for bean '{1}'",
								bean.getClass());
				throw new RuntimeException(msg);
			}
		}
		return this.propertyChangeListenerDescriptor;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		try {
			EventSetDescriptor eventSetDescriptor = getPropertyChangeListenerDescriptor();
			Method addListenerMethod = eventSetDescriptor
					.getAddListenerMethod();
			addListenerMethod.invoke(bean, listener);
		} catch (Exception e) {
			String msg = MessageFormat.format(
					"Unable to add PropertyChangeListener to bean '{1}'",
					bean.getClass());
			new IllegalArgumentException(msg, e);
		}
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		try {
			EventSetDescriptor eventSetDescriptor = getPropertyChangeListenerDescriptor();
			Method removeListenerMethod = eventSetDescriptor
					.getRemoveListenerMethod();
			removeListenerMethod.invoke(bean, listener);
		} catch (Exception e) {
			String msg = MessageFormat.format(
					"Unable to remove PropertyChangeListener from bean '{1}'",
					bean.getClass());
			new IllegalArgumentException(msg, e);
		}
	}

}