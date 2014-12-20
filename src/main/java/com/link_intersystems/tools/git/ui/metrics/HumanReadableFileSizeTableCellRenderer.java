package com.link_intersystems.tools.git.ui.metrics;

import java.awt.Component;
import java.math.BigInteger;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.io.FileUtils;

public class HumanReadableFileSizeTableCellRenderer extends
		DefaultTableCellRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = 9218037116141144226L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		BigInteger bytes = (BigInteger) value;
		String displaySize = FileUtils.byteCountToDisplaySize(bytes);
		setText(displaySize);
		return this;
	}

}
