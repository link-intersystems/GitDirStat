package com.link_intersystems.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author rene.link
 *
 * @param <T>
 */
public class BeanPropertySync<T> implements PropertyChangeListener {

	private T targetBean;
	private boolean skipMissingPropertiesEnabled;
	private ReflectivePropertyChangeListenerBinding sourceBeanPropertyChangeListenerBinding;

	public BeanPropertySync(T targetBean) {
		this.targetBean = targetBean;
	}

	protected T getBeanSync() {
		return targetBean;
	}

	public void setSkipMissingPropertiesEnabled(
			boolean skipMissingPropertiesEnabled) {
		this.skipMissingPropertiesEnabled = skipMissingPropertiesEnabled;
	}

	public boolean isSkipMissingPropertiesEnabled() {
		return skipMissingPropertiesEnabled;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			trySetProperty(evt);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			catchSetPropertyException(e);
		}
	}

	private void trySetProperty(PropertyChangeEvent evt)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		String propertyName = evt.getPropertyName();
		PropertyDescriptor propertyDescriptor = PropertyUtils
				.getPropertyDescriptor(targetBean, propertyName);
		if (propertyDescriptor == null) {
			if (!skipMissingPropertiesEnabled) {
				String msg = MessageFormat
						.format("Can't find write method for property '{0}' of bean '{1}'",
								propertyName, targetBean.getClass());
				throw new RuntimeException(msg);
			}
		} else {
			Method writeMethod = propertyDescriptor.getWriteMethod();
			writeMethod.invoke(targetBean, evt.getNewValue());
		}
	}

	private void catchSetPropertyException(Exception e) {
		throw new RuntimeException("Unable to sync bean", e);
	}

	public void setSynchronization(T sourceBean) {
		if (this.sourceBeanPropertyChangeListenerBinding != null) {
			sourceBeanPropertyChangeListenerBinding
					.removePropertyChangeListener(this);
		}
		if (sourceBean == null) {
			sourceBeanPropertyChangeListenerBinding = null;
		} else {
			sourceBeanPropertyChangeListenerBinding = new ReflectivePropertyChangeListenerBinding(
					sourceBean);
			applyBeanProperties(sourceBean);
			sourceBeanPropertyChangeListenerBinding
					.addPropertyChangeListener(this);
		}
	}

	protected void applyBeanProperties(T baseBean) {
		try {
			BeanUtils.copyProperties(this, baseBean);
		} catch (Exception e) {
			catchSetPropertyException(e);
		}
	}
}
