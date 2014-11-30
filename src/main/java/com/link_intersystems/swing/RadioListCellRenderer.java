package com.link_intersystems.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;

public class RadioListCellRenderer extends JRadioButton implements ListCellRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = 7492071899615624010L;

	public RadioListCellRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setComponentOrientation(list.getComponentOrientation());
		setEnabled(list.isEnabled());
		setFont(list.getFont());

		Color bg = list.getBackground();
		Color fg = list.getForeground();

		setBackground(bg);
		setForeground(fg);

		setSelected(isSelected);

		if (value instanceof Icon) {
			setIcon((Icon) value);
			setText("");
		} else {
			setIcon(null);
			setText((value == null) ? "" : value.toString());
		}

		return this;
	}
}