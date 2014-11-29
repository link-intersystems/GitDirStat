package com.link_intersystems.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JRadioButtonMenuItem;

public class RadioButtonGroupModel {

	private class ButtonActionAdapter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			propertyChangeSupport.firePropertyChange("selectedValue", null,
					getSelectionValue());

		}

	}

	private ButtonGroup buttonGroup = new ButtonGroup();
	private Map<Object, Object> selectionValueMap = new HashMap<Object, Object>();
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	private ActionListener buttonActionAdapter = new ButtonActionAdapter();

	public void add(JRadioButtonMenuItem radioButtonMenuItem,
			Object selectionValue) {
		buttonGroup.add(radioButtonMenuItem);
		selectionValueMap.put(radioButtonMenuItem, selectionValue);
		radioButtonMenuItem.addActionListener(buttonActionAdapter);
	}

	public void remove(JRadioButtonMenuItem radioButtonMenuItem) {
		buttonGroup.remove(radioButtonMenuItem);
		selectionValueMap.remove(radioButtonMenuItem);
		radioButtonMenuItem.removeActionListener(buttonActionAdapter);
	}

	public Object getSelectionValue() {
		Object selectionValue = null;

		ButtonModel selection = buttonGroup.getSelection();

		if (selection != null) {
			Enumeration<AbstractButton> elements = buttonGroup.getElements();
			AbstractButton selectedButton = null;
			while (elements.hasMoreElements()) {
				AbstractButton abstractButton = elements.nextElement();
				if (selection.equals(abstractButton.getModel())) {
					selectedButton = abstractButton;
					break;
				}

			}

			selectionValue = selectionValueMap.get(selectedButton);
		}

		return selectionValue;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	public void setSelected(AbstractButton abstractButton, boolean selected) {
		buttonGroup.setSelected(abstractButton.getModel(), selected);
	}
}
