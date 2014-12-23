package com.link_intersystems.swing;

import java.awt.event.ActionEvent;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public abstract class AsyncProgressAction<I, V, O> extends ProgressAction {

	private static final int TYPE_PARAM_ACTION_INPUT = 0;
	private static final long serialVersionUID = 7131523498822927047L;

	private ActionInputSource<I> actionInputSource = NullActionInputSource.getInstance();

	@Override
	public final void doActionPerformed(ActionEvent e,
			ProgressMonitor progressMonitor) {
		I actionInput = getInput(e);
		if (isActionInputValid(actionInput)) {
			SwingWorkerAdapter swingWorkerAdapter = new SwingWorkerAdapter(
					actionInput, progressMonitor);
			swingWorkerAdapter.execute();
		}
	}

	protected boolean isActionInputValid(I actionInput) {
		Class<?> clazz = getClass();
		ParameterizedType parameterizedType = (ParameterizedType) clazz
				.getGenericSuperclass();
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		Type actionInputType = actualTypeArguments[TYPE_PARAM_ACTION_INPUT];
		if (Void.class.equals(actionInputType) && actionInput == null) {
			return true;
		}
		return actionInput != null;
	}

	public void setActionInputSource(ActionInputSource<I> actionInputSource) {
		if (actionInputSource == null) {
			actionInputSource = NullActionInputSource.getInstance();
		}
		this.actionInputSource = actionInputSource;
	}

	protected I getInput(ActionEvent e) {
		return actionInputSource.getActionInput(e);
	}

	protected abstract O doInBackground(I actionInput,
			ProgressMonitor progressMonitor) throws Exception;

	protected void process(List<V> chunks) {
	}

	protected void processResult(ResultRef<O> resultRef) {
		try {
			O t = resultRef.get();
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

	protected void done(O result) {
	}

	private class SwingWorkerAdapter extends SwingWorker<O, V> {

		private I actionInput;
		private ProgressMonitor progressMonitor;

		public SwingWorkerAdapter(I actionInput, ProgressMonitor progressMonitor) {
			this.actionInput = actionInput;
			this.progressMonitor = progressMonitor;
		}

		@Override
		protected O doInBackground() throws Exception {
			return AsyncProgressAction.this.doInBackground(actionInput,
					progressMonitor);
		}

		@Override
		protected void process(List<V> chunks) {
			AsyncProgressAction.this.process(chunks);
		}

		@Override
		protected void done() {
			AsyncProgressAction.this.processResult(new ResultRef<O>() {

				@Override
				public O get() throws InterruptedException, ExecutionException {
					return SwingWorkerAdapter.this.get();
				}

			});
		}

	}

	protected interface ResultRef<T> {
		public T get() throws InterruptedException, ExecutionException;
	}

	private static class NullActionInputSource<I> implements
			ActionInputSource<I> {

		@SuppressWarnings("rawtypes")
		private static final NullActionInputSource INSTANCE = new NullActionInputSource();

		@SuppressWarnings("unchecked")
		private static <I> NullActionInputSource<I> getInstance() {
			return INSTANCE;
		}

		@Override
		public I getActionInput(ActionEvent e) {
			return null;
		}

	}

}
