package com.link_intersystems.gitdirstat.ui;

import java.awt.Window;

import javax.swing.ImageIcon;

import com.link_intersystems.swing.ProgressMonitor;

public interface UIContext {

	public enum IconType {
		OPEN("document-open.png"), UPDATE("view-refresh.png"), CLEAN(
				"edit-clear.png");

		private String name;

		private IconType(String name) {
			this.name = name;

		}

		public String getName() {
			return name;
		}
	}

	Window getMainFrame();

	public ImageIcon getIcon(IconType iconType);

	ProgressMonitor getProgressMonitor();

	ImageIcon getIcon(String classpath);
}
