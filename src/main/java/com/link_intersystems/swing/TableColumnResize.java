package com.link_intersystems.swing;

import javax.swing.table.TableColumn;

public class TableColumnResize implements ComponentResize<TableColumn> {

	@Override
	public void setWidth(TableColumn component, int width) {
		component.setPreferredWidth(width);
	}

}
