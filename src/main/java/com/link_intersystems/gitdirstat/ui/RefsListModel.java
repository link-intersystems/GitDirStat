package com.link_intersystems.gitdirstat.ui;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;

import com.link_intersystems.gitdirstat.domain.Ref;
import com.link_intersystems.swing.ListAdapterListModel;
import com.link_intersystems.swing.ListModelSelection;

public class RefsListModel extends ListAdapterListModel<Ref> {

	private static final long serialVersionUID = -1991729532536558847L;

	private ListSelectionModel refsSelectionModel = new DefaultListSelectionModel();
	private ListModelSelection<? extends Ref> refsListModelSelection = new ListModelSelection<Ref>(
			this, refsSelectionModel);

	public ListSelectionModel getListSelectionModel() {
		return refsSelectionModel;
	}

	public ListModelSelection<? extends Ref> getSelectionModel() {
		return refsListModelSelection;

	}
}
