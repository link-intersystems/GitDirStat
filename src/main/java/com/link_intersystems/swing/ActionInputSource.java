package com.link_intersystems.swing;

import java.awt.event.ActionEvent;

public interface ActionInputSource<I> {

	public I getActionInput(ActionEvent e);
}
