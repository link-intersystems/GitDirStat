package com.link_intersystems.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class ShowPopupMouseAdapter extends MouseAdapter {

	private JPopupMenu popupMenu;

	public ShowPopupMouseAdapter(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
