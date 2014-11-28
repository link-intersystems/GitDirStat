package com.link_intersystems.tools.git.ui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.commons.io.FileUtils;

import com.link_intersystems.tools.git.ui.SizeMetricsTreeModel.TreeObjectTreeNode;

public class HumanReadableFileSizeTreeCellRenderer extends
		DefaultTreeCellRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = 5381781656497174451L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		if (value instanceof TreeObjectTreeNode) {
			TreeObjectTreeNode treeObjectTreeNode = (TreeObjectTreeNode) value;
			TreeObjectModel treeObject = treeObjectTreeNode
					.getTreeObjectModel();
			String displaySize = FileUtils.byteCountToDisplaySize(treeObject
					.getTotalSize());
			String name = treeObject.getName();
			setText(name + " [" + displaySize + "]");
		}
		return this;
	}
}
