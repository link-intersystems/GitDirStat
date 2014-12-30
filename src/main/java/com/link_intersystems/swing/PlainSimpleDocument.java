package com.link_intersystems.swing;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class PlainSimpleDocument extends PlainDocument implements
		SimpleDocument {

	private static final long serialVersionUID = -8701400464984055441L;

	@Override
	public void setText(String text) {
		try {
			remove(0, getLength());
			insertString(0, text, null);
		} catch (BadLocationException e) {
		}
	}

}
