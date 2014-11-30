package com.link_intersystems.tools.git.ui.config;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;

import com.link_intersystems.swing.CheckboxListCellRenderer;
import com.link_intersystems.swing.ListSelectionModelMemento;
import com.link_intersystems.tools.git.domain.Ref;

class SelectRefsAction extends AbstractAction {

	/**
	 *
	 */
	private final RepositoryConfigParams configParams;
	private static final long serialVersionUID = -6024006313949630749L;
	private ListModel listModel;
	private ListSelectionModel listSelectionModel;

	public SelectRefsAction(RepositoryConfigParams configParams,
			ListModel listModel, ListSelectionModel listSelectionModel) {
		this.configParams = configParams;
		this.listModel = listModel;
		this.listSelectionModel = listSelectionModel;
		listSelectionModel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				firePropertyChange(NAME, null, getValue(Action.NAME));
				firePropertyChange(SHORT_DESCRIPTION, null, getValue(Action.SHORT_DESCRIPTION));
			}
		});
	}

	@Override
	public Object getValue(String key) {
		if (Action.NAME.equals(key) || Action.SHORT_DESCRIPTION.equals(key)) {
			String selectedRefs = getSelectedRefs();
			if (Action.NAME.equals(key)) {
				String abbreviated = StringUtils.abbreviate(selectedRefs, 35);
				return abbreviated;
			} else {
				return selectedRefs;
			}
		}
		return super.getValue(key);
	}

	private String getSelectedRefs() {
		if (listSelectionModel.isSelectionEmpty()) {
			return "All branches";
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			int minIndex = listSelectionModel.getMinSelectionIndex();
			int maxIndex = listSelectionModel.getMaxSelectionIndex();

			for (int i = minIndex; i <= maxIndex; i++) {
				if(listSelectionModel.isSelectedIndex(i)){
					Ref ref = (Ref) listModel.getElementAt(i);
					stringBuilder.append(ref.getSimpleName());
					stringBuilder.append(",");
				}
			}

			return StringUtils.removeEnd(stringBuilder.toString(), ",");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ListSelectionModelMemento listSelectionModelMemento = new ListSelectionModelMemento();
		listSelectionModelMemento.save(listSelectionModel);

		JList jList = new JList(listModel);
		jList.setCellRenderer(new CheckboxListCellRenderer());

		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		DefaultListSelectionModel defaultListSelectionModel = new DefaultListSelectionModel();
		listSelectionModelMemento.restore(defaultListSelectionModel);
		jList.setSelectionModel(defaultListSelectionModel);

		JScrollPane jScrollPane = new JScrollPane(jList);

		Window mainFrame = this.configParams.getMainFrame();
		int showOptionDialog = JOptionPane.showOptionDialog(mainFrame,
				jScrollPane, "Select branches", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (showOptionDialog == JOptionPane.OK_OPTION) {
			listSelectionModelMemento.save(defaultListSelectionModel);
			listSelectionModelMemento.restore(listSelectionModel);
		}
	}

}