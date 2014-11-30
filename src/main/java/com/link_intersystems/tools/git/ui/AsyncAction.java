package com.link_intersystems.tools.git.ui;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public abstract class AsyncAction<T, V> extends AbstractAction {

	private static final long serialVersionUID = 7131523498822927047L;

	@Override
	public final void actionPerformed(ActionEvent e) {
		SwingWorkerAdapter swingWorkerAdapter = new SwingWorkerAdapter();
		swingWorkerAdapter.execute();
	}

	protected abstract T doInBackground() throws Exception;

	protected void process(List<V> chunks) {
	}

	protected void processResult(ResultRef<T> resultRef) {
		try {
			T t = resultRef.get();
			done(t);
		} catch (InterruptedException ignore) {
		} catch (ExecutionException executionException) {
			Throwable cause = executionException.getCause();
			String msg = String.format("Unexpected exception: %s",
					cause.toString());
			JOptionPane.showMessageDialog(null, msg, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void done(T result) {
	}

	private class SwingWorkerAdapter extends SwingWorker<T, V> {

		@Override
		protected T doInBackground() throws Exception {
			return AsyncAction.this.doInBackground();
		}

		@Override
		protected void process(List<V> chunks) {
			AsyncAction.this.process(chunks);
		}

		@Override
		protected void done() {
			AsyncAction.this.processResult(new ResultRef<T>() {

				@Override
				public T get() throws InterruptedException, ExecutionException {
					return SwingWorkerAdapter.this.get();
				}

			});
		}

	}

	protected interface ResultRef<T> {
		public T get() throws InterruptedException, ExecutionException;
	}

}
