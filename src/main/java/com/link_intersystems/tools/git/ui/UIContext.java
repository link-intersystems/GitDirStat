package com.link_intersystems.tools.git.ui;

import java.awt.Window;

import javax.swing.ImageIcon;

public interface UIContext {

	public enum IconType {
		OPEN("document-open.png"), UPDATE("view-refresh.png"), CLEAN(
				"edit-clear.png"), GIT_LOGO("logo-git.png");

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
}
