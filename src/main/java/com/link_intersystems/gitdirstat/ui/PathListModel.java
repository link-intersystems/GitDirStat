package com.link_intersystems.gitdirstat.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;

import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.swing.ListAdapterListModel;
import com.link_intersystems.swing.ListModelSelection;

public class PathListModel extends ListAdapterListModel<TreeObject> {

	private static final long serialVersionUID = -8951389167945564437L;

	private ListSelectionModel pathSelectionModel = new DefaultListSelectionModel();
	private ListModelSelection<TreeObject> pathListModelSelection = new ListModelSelection<TreeObject>(
			this, pathSelectionModel);

	@Override
	public void setList(List<? extends TreeObject> list) {
		Comparator<Object> reverseOrder = Collections.reverseOrder();
		Collections.sort(list, reverseOrder);
		super.setList(list);
	}

	public ListSelectionModel getListSelectionModel() {
		return pathSelectionModel;
	}

	public ListModelSelection<TreeObject> getSelectionModel() {
		return pathListModelSelection;

	}
}
