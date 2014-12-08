package com.link_intersystems.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

public class CheckboxRenderer extends JCheckBox implements ListCellRenderer,
		TableCellRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = 7492071899615624010L;

	public CheckboxRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		applyComponentRenderComponent(list);
		applyAbstractButtonRenderComponent(value, isSelected);
		return this;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		applyComponentRenderComponent(table);
		applyAbstractButtonRenderComponent(value, isSelected);
		return null;
	}

	private void applyAbstractButtonRenderComponent(Object value,
			boolean isSelected) {
		setSelected(isSelected);

		if (value instanceof Icon) {
			setIcon((Icon) value);
			setText("");
		} else {
			setIcon(null);
			setText((value == null) ? "" : value.toString());
		}
	}

	private void applyComponentRenderComponent(JComponent component) {
		setComponentOrientation(component.getComponentOrientation());
		setEnabled(component.isEnabled());
		setFont(component.getFont());

		Color bg = component.getBackground();
		Color fg = component.getForeground();

		setBackground(bg);
		setForeground(fg);
	}
}