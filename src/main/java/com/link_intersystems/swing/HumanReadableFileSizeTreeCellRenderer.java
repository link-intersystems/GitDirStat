package com.link_intersystems.swing;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.commons.io.FileUtils;

public class HumanReadableFileSizeTreeCellRenderer extends
		DefaultTreeCellRenderer {

	private static final long serialVersionUID = 5381781656497174451L;
	/**
	 * {@value}
	 */
	public static final String DEFAULT_FORMAT = "%s [%s]";

	private String format = DEFAULT_FORMAT;
	private FileModelAdapterFactory fileModelAdapterFactory = DefaultMutableTreeNodeFileModelAdapterFactory.INSTANCE;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		String text = "";

		FileModel fileModel = fileModelAdapterFactory.createAdapter(value);
		if (fileModel != null) {
			String displaySize = FileUtils.byteCountToDisplaySize(fileModel
					.getSize());
			String name = fileModel.getName();
			text = String.format(format, name, displaySize);
		}

		setText(text);

		return this;
	}

	/**
	 * The format string for the {@link FileModel}. Default is
	 * {@link #DEFAULT_FORMAT}.
	 *
	 * @param format
	 */
	public void setFormat(String format) {
		if (format == null) {
			format = DEFAULT_FORMAT;
		}
		this.format = format;
	}

	public void setFileModelAdapterFactory(
			FileModelAdapterFactory fileModelAdapterFactory) {
		if (fileModelAdapterFactory == null) {
			fileModelAdapterFactory = DefaultMutableTreeNodeFileModelAdapterFactory.INSTANCE;
		}
		this.fileModelAdapterFactory = fileModelAdapterFactory;
	}

	private static class DefaultMutableTreeNodeFileModelAdapterFactory
			implements FileModelAdapterFactory {

		private static final DefaultMutableTreeNodeFileModelAdapterFactory INSTANCE = new DefaultMutableTreeNodeFileModelAdapterFactory();

		@Override
		public FileModel createAdapter(Object treeModelObject) {
			FileModel fileModel = null;

			if (treeModelObject instanceof FileModel) {
				fileModel = FileModel.class.cast(treeModelObject);
			} else if (treeModelObject instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode mutableTreeNode = DefaultMutableTreeNode.class
						.cast(treeModelObject);
				Object userObject = mutableTreeNode.getUserObject();
				if (userObject instanceof FileModel) {
					fileModel = FileModel.class.cast(userObject);
				}
			}

			return fileModel;
		}

	}
}
