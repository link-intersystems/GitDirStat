package com.link_intersystems.swing;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import com.link_intersystems.beans.BeanPropertySync;

public class ActionPropertySync extends BeanPropertySync<Action> {

	private static List<String> ACTION_PROPS = Arrays.asList(Action.NAME,
			Action.ACCELERATOR_KEY, Action.ACTION_COMMAND_KEY,
			Action.DISPLAYED_MNEMONIC_INDEX_KEY, Action.LARGE_ICON_KEY,
			Action.LONG_DESCRIPTION, Action.MNEMONIC_KEY, Action.SELECTED_KEY,
			Action.SHORT_DESCRIPTION, Action.SMALL_ICON);

	public ActionPropertySync(Action actionSync) {
		super(actionSync);
	}

	@Override
	protected void applyBeanProperties(Action baseBean) {
		super.applyBeanProperties(baseBean);

		Action beanSync = getBeanSync();
		for (String actionProperty : ACTION_PROPS) {
			Object value = baseBean.getValue(actionProperty);
			beanSync.putValue(actionProperty, value);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (ACTION_PROPS.contains(propertyName)) {
			Action baseBean = (Action) evt.getSource();
			Action beanSync = getBeanSync();
			Object value = baseBean.getValue(propertyName);
			beanSync.putValue(propertyName, value);
		} else {
			super.propertyChange(evt);
		}
	}

}
