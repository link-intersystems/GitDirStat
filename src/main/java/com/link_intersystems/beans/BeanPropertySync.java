package com.link_intersystems.beans;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

public class BeanPropertySync<T> implements PropertyChangeListener {

	private T beanSync;
	private BeanInfo baseBeanInfo;
	private T baseBean;

	public BeanPropertySync(T beanSync) {
		this.beanSync = beanSync;
	}

	protected T getBeanSync() {
		return beanSync;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		PropertyDescriptor propertyDescriptor;
		try {
			propertyDescriptor = PropertyUtils.getPropertyDescriptor(beanSync,
					propertyName);
			Method writeMethod = propertyDescriptor.getWriteMethod();
			writeMethod.invoke(beanSync, evt.getNewValue());
		} catch (Exception e) {
			throw new RuntimeException("Unable to sync bean", e);
		}
	}

	public void setSynchronization(T baseBean) {
		if (this.baseBean != null) {
			removePropertyChangeListener(baseBean);
		}
		this.baseBean = baseBean;
		if (this.baseBean != null) {
			applyBeanProperties(baseBean);
			addPropertyChangeListener(baseBean);
		}
	}

	protected void applyBeanProperties(T baseBean) {
		try {
			BeanUtils.copyProperties(this, baseBean);
		} catch (Exception e) {
			throw new RuntimeException("Unable to sync bean", e);
		}
	}

	private void addPropertyChangeListener(T baseBean) {
		try {
			baseBeanInfo = Introspector.getBeanInfo(baseBean.getClass());
			EventSetDescriptor[] eventSetDescriptors = baseBeanInfo
					.getEventSetDescriptors();
			for (int i = 0; i < eventSetDescriptors.length; i++) {
				Method addListenerMethod = eventSetDescriptors[i]
						.getAddListenerMethod();
				addListenerMethod.invoke(baseBean, this);
			}
		} catch (Exception e) {
			new IllegalArgumentException(
					"Unable to register PropertyChangeListener on bean", e);
		}
	}

	private void removePropertyChangeListener(T baseBean) {
		try {
			baseBeanInfo = Introspector.getBeanInfo(baseBean.getClass());
			EventSetDescriptor[] eventSetDescriptors = baseBeanInfo
					.getEventSetDescriptors();
			for (int i = 0; i < eventSetDescriptors.length; i++) {
				Method removeListenerMethod = eventSetDescriptors[i]
						.getRemoveListenerMethod();
				removeListenerMethod.invoke(baseBean, this);
			}
		} catch (Exception e) {
			new IllegalArgumentException(
					"Unable to register PropertyChangeListener on bean", e);
		}
	}

}
