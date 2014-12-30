package com.link_intersystems.swing;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class JLabelDocumentAdapter {

	private JLabel label;
	private Document document;

	private class DocumentUpdateAdapter implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateLabel();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateLabel();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateLabel();
		}

	}

	private DocumentUpdateAdapter documentUpdateAdapter = new DocumentUpdateAdapter();

	public JLabelDocumentAdapter(JLabel label) {
		this.label = label;
	}

	private void updateLabel() {
		try {
			String text = document.getText(0, document.getLength());
			label.setText(text);
		} catch (BadLocationException e) {
		}
	}

	public void setDocument(Document document) {
		if (this.document != null) {
			this.document.removeDocumentListener(documentUpdateAdapter);
		}
		this.document = document;
		if (this.document != null) {
			this.document.addDocumentListener(documentUpdateAdapter);
		}
	}

	public Document getDocument() {
		return document;
	}

}
