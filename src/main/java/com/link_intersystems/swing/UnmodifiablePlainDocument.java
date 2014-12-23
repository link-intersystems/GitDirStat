package com.link_intersystems.swing;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;

public class UnmodifiablePlainDocument extends PlainDocument {

	private static final long serialVersionUID = 3306245088334490679L;

	private ModifiableAdapter modifiableAdapter = new ModifiableAdapter();

	public SimpleDocument getModifiable() {
		return modifiableAdapter;
	}

	@Override
	protected void removeUpdate(DefaultDocumentEvent chng) {
	}

	@Override
	protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
	}

	@Override
	public void remove(int offs, int len) throws BadLocationException {
	}

	private class ModifiableAdapter implements SimpleDocument {

		public int getLength() {
			return UnmodifiablePlainDocument.super.getLength();
		}

		public void addDocumentListener(DocumentListener listener) {
			UnmodifiablePlainDocument.super.addDocumentListener(listener);
		}

		public void removeDocumentListener(DocumentListener listener) {
			UnmodifiablePlainDocument.super.removeDocumentListener(listener);
		}

		public void addUndoableEditListener(UndoableEditListener listener) {
			UnmodifiablePlainDocument.super.addUndoableEditListener(listener);
		}

		public void removeUndoableEditListener(UndoableEditListener listener) {
			UnmodifiablePlainDocument.super
					.removeUndoableEditListener(listener);
		}

		public Object getProperty(Object key) {
			return UnmodifiablePlainDocument.super.getProperty(key);
		}

		public void putProperty(Object key, Object value) {
			UnmodifiablePlainDocument.super.putProperty(key, value);
		}

		public void remove(int offs, int len) throws BadLocationException {
			UnmodifiablePlainDocument.super.remove(offs, len);
		}

		public void insertString(int offset, String str, AttributeSet a)
				throws BadLocationException {
			UnmodifiablePlainDocument.super.insertString(offset, str, a);
		}

		public String getText(int offset, int length)
				throws BadLocationException {
			return UnmodifiablePlainDocument.super.getText(offset, length);
		}

		public void getText(int offset, int length, Segment txt)
				throws BadLocationException {
			UnmodifiablePlainDocument.super.getText(offset, length, txt);
		}

		public Position getStartPosition() {
			return UnmodifiablePlainDocument.super.getStartPosition();
		}

		public Position getEndPosition() {
			return UnmodifiablePlainDocument.super.getEndPosition();
		}

		public Position createPosition(int offs) throws BadLocationException {
			return UnmodifiablePlainDocument.super.createPosition(offs);
		}

		public Element[] getRootElements() {
			return UnmodifiablePlainDocument.super.getRootElements();
		}

		public Element getDefaultRootElement() {
			return UnmodifiablePlainDocument.super.getDefaultRootElement();
		}

		public void render(Runnable r) {
			UnmodifiablePlainDocument.super.render(r);
		}

		@Override
		public void setText(String text) {
			try {
				remove(0, getLength());
				insertString(0, text, null);
			} catch (BadLocationException e) {
			}

		}

	}

}
