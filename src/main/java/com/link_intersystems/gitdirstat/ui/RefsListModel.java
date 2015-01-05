package com.link_intersystems.gitdirstat.ui;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public void setList(List<? extends Ref> list) {
		List<Ref> oldList = new ArrayList<Ref>(getList());
		super.setList(list);

		if (oldList != null && !oldList.equals(list)) {
			setAllRefSelected();
		}

	}

	private void setAllRefSelected() {
		int size = getSize();
		refsSelectionModel.setValueIsAdjusting(true);
		refsSelectionModel.clearSelection();
		if (size > 0) {
			refsSelectionModel.addSelectionInterval(0, size - 1);
		}
		refsSelectionModel.setValueIsAdjusting(false);
	}

	public ListSelectionModel getListSelectionModel() {
		return refsSelectionModel;
	}

	public ListModelSelection<? extends Ref> getSelectionModel() {
		return refsListModelSelection;
	}

}
