package com.link_intersystems.gitdirstat.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.swing.ListAdapterListModel;
import com.link_intersystems.swing.ListModelSelection;

public class PathModel {

	private ListAdapterListModel<TreeObject> listAdapterListModel = new ListAdapterListModel<TreeObject>();
	private ListSelectionModel pathSelectionModel = new DefaultListSelectionModel();
	private ListModelSelection<TreeObject> pathListModelSelection = new ListModelSelection<TreeObject>(
			listAdapterListModel, pathSelectionModel);

	public void setRootTreeObject(TreeObject treeObject) {
		if (treeObject == null) {
			clear();
			return;
		}
		List<TreeObject> fileList = treeObject.toFileList();
		Comparator<Object> reverseOrder = Collections.reverseOrder();
		Collections.sort(fileList, reverseOrder);
		listAdapterListModel.setList(fileList);
	}

	public ListModel getListModel() {
		return listAdapterListModel;
	}

	public ListSelectionModel getListSelectionModel() {
		return pathSelectionModel;
	}

	public ListModelSelection<TreeObject> getSelectionModel() {
		return pathListModelSelection;

	}

	public void clear() {
		List<? extends TreeObject> emptyList = Collections.emptyList();
		listAdapterListModel.setList(emptyList);
	}
}
