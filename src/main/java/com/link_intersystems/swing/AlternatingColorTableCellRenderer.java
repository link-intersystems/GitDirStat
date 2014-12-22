package com.link_intersystems.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class AlternatingColorTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 2701013950867204696L;

	private TableCellRenderer cellRenderer;

	private Color oddForeground = Color.BLACK;
	private Color oddBackground = UIManager.getColor("Panel.background");
	private Color evenForeground = Color.BLACK;
	private Color evevBackground = Color.WHITE;

	public AlternatingColorTableCellRenderer() {
		this.cellRenderer = new TableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				return AlternatingColorTableCellRenderer.super
						.getTableCellRendererComponent(table, value,
								isSelected, hasFocus, row, column);
			}
		};
	}

	public AlternatingColorTableCellRenderer(TableCellRenderer cellRenderer) {
		this.cellRenderer = cellRenderer;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component renderComponent = cellRenderer.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);

		if (!isSelected) {
			if (row % 2 == 0) {
				renderComponent.setForeground(oddForeground);
				renderComponent.setBackground(oddBackground);
			} else {
				renderComponent.setForeground(evenForeground);
				renderComponent.setBackground(evevBackground);
			}
		}

		return renderComponent;
	}

	public void setOddColor(Color foreground, Color background) {
		this.oddForeground = foreground;
		this.oddBackground = background;
	}

	public void setEvenColor(Color foreground, Color background) {
		this.evenForeground = foreground;
		this.evevBackground = background;
	}

}
