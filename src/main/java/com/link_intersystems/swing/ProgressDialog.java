/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.link_intersystems.swing;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import com.link_intersystems.math.IncrementalAverage;

public class ProgressDialog {

	private ProgressDialogState dialogState;

	/**
	 * Constructs a graphic object that shows progress, typically by filling in
	 * a rectangular bar as the process nears completion.
	 *
	 * @param parentComponent
	 *            the parent component for the dialog box
	 * @param message
	 *            a descriptive message that will be shown to the user to
	 *            indicate what operation is being monitored. This does not
	 *            change as the operation progresses. See the message parameters
	 *            to methods in {@link JOptionPane#message} for the range of
	 *            values.
	 * @param note
	 *            a short note describing the state of the operation. As the
	 *            operation progresses, you can call setNote to change the note
	 *            displayed. This is used, for example, in operations that
	 *            iterate through a list of files to show the name of the file
	 *            being processes. If note is initially null, there will be no
	 *            note line in the dialog box and setNote will be ineffective
	 * @param min
	 *            the lower bound of the range
	 * @param max
	 *            the upper bound of the range
	 * @see JDialog
	 * @see JOptionPane
	 */
	public ProgressDialog(Component parentComponent, Object message, int min,
			int max) {
		this.dialogState = new ProgressDialogHidden();

		this.dialogState.setMinimum(min);
		this.dialogState.setMaximum(max);
		this.dialogState
				.setParentComponent(getWindowForComponent(parentComponent));
		this.dialogState.setMessage(message);

		this.dialogState = dialogState.nextState();
	}

	static Window getWindowForComponent(Component parentComponent)
			throws HeadlessException {
		if (parentComponent == null)
			return JOptionPane.getRootFrame();
		if (parentComponent instanceof Frame
				|| parentComponent instanceof Dialog)
			return (Window) parentComponent;
		return getWindowForComponent(parentComponent.getParent());
	}

	/**
	 * Indicate the progress of the operation being monitored. If the specified
	 * value is >= the maximum, the progress monitor is closed.
	 *
	 * @param nv
	 *            an int specifying the current value, between the maximum and
	 *            minimum specified for this component
	 * @see #setMinimum
	 * @see #setMaximum
	 * @see #close
	 */
	public void setProgress(int nv) {
		dialogState.setProgress(nv);
		dialogState = dialogState.nextState();
	}

	/**
	 * Indicate that the operation is complete. This happens automatically when
	 * the value set by setProgress is >= max, but it may be called earlier if
	 * the operation ends early.
	 */
	public void close() {
		dialogState.close();
	}

	/**
	 * Returns the minimum value -- the lower end of the progress value.
	 *
	 * @return an int representing the minimum value
	 * @see #setMinimum
	 */
	public int getMinimum() {
		return dialogState.getMinimum();
	}

	/**
	 * Specifies the minimum value.
	 *
	 * @param m
	 *            an int specifying the minimum value
	 * @see #getMinimum
	 */
	public void setMinimum(int m) {
		dialogState.setMinimum(m);
	}

	/**
	 * Returns the maximum value -- the higher end of the progress value.
	 *
	 * @return an int representing the maximum value
	 * @see #setMaximum
	 */
	public int getMaximum() {
		return dialogState.getMaximum();
	}

	/**
	 * Specifies the maximum value.
	 *
	 * @param m
	 *            an int specifying the maximum value
	 * @see #getMaximum
	 */
	public void setMaximum(int m) {
		dialogState.setMaximum(m);
	}

	/**
	 * Returns true if the user hits the Cancel button in the progress dialog.
	 */
	public boolean isCanceled() {
		return dialogState.isCanceled();
	}

	/**
	 * Specifies the amount of time to wait before deciding whether or not to
	 * popup a progress monitor.
	 *
	 * @param millisToDecideToPopup
	 *            an int specifying the time to wait, in milliseconds
	 * @see #getMillisToDecideToPopup
	 */
	public void setMillisToDecideToPopup(int millisToDecideToPopup) {
		dialogState.setMillisToDecideToPopup(millisToDecideToPopup);
	}

	/**
	 * Returns the amount of time this object waits before deciding whether or
	 * not to popup a progress monitor.
	 *
	 * @see #setMillisToDecideToPopup
	 */
	public int getMillisToDecideToPopup() {
		return dialogState.getMillisToDecideToPopup();
	}

	/**
	 * Specifies the amount of time it will take for the popup to appear. (If
	 * the predicted time remaining is less than this time, the popup won't be
	 * displayed.)
	 *
	 * @param millisToPopup
	 *            an int specifying the time in milliseconds
	 * @see #getMillisToPopup
	 */
	public void setMillisToPopup(int millisToPopup) {
		dialogState.setMillisToPopup(millisToPopup);
	}

	/**
	 * Returns the amount of time it will take for the popup to appear.
	 *
	 * @see #setMillisToPopup
	 */
	public int getMillisToPopup() {
		return dialogState.getMillisToPopup();
	}

	public void setMessage(Object message) {
		dialogState.setMessage(message);
	}

	public void setRemainingTimeEnabled(boolean remainingTimeEnabled) {
		this.dialogState.setRemainingTimeEnabled(remainingTimeEnabled);
	}

	private static abstract class ProgressDialogState {

		public abstract void setMessage(Object message);

		public abstract void setRemainingTimeEnabled(
				boolean remainingTimeEnabled);

		public abstract int getMillisToPopup();

		public abstract void setMillisToPopup(int millisToPopup);

		public abstract int getMillisToDecideToPopup();

		public abstract void setMillisToDecideToPopup(int millisToDecideToPopup);

		public abstract boolean isCanceled();

		public abstract int getMaximum();

		public abstract void setMaximum(int max);

		public abstract int getMinimum();

		public abstract void setMinimum(int min);

		public void close() {
		}

		public ProgressDialogState nextState() {
			return this;
		}

		public abstract void setParentComponent(Component parentComponent);

		public abstract void setProgress(int nv);
	}

	private class ProgressDialogHidden extends ProgressDialogState {

		private IncrementalAverage workAverage = new IncrementalAverage();
		private Object message;
		private Component parentComponent;
		private int progress;
		private int min;
		private int max;
		private int millisToDecideToPopup = 500;
		private int millisToPopup;
		private long T0 = System.currentTimeMillis();
		boolean remainingTimeEnabled;

		@Override
		public void setMessage(Object message) {
			this.message = message;

		}

		@Override
		public void setParentComponent(Component parentComponent) {
			this.parentComponent = parentComponent;
		}

		@Override
		public void setProgress(int nv) {
			this.progress = nv;
		}

		@Override
		public ProgressDialogState nextState() {
			ProgressDialogState nextState = this;

			if (max < 0) {
				nextState = new ProgressDialogVisible(this);
			} else {
				nextState = timeToPopup(nextState);
			}

			return nextState;
		}

		private ProgressDialogState timeToPopup(ProgressDialogState nextState) {
			long T = System.currentTimeMillis();
			long dT = (int) (T - T0);
			if (dT >= millisToDecideToPopup) {
				int predictedCompletionTime;
				if (progress > min) {
					predictedCompletionTime = (int) ((long) dT * (max - min) / (progress - min));
				} else {
					predictedCompletionTime = millisToPopup;
				}
				if (predictedCompletionTime >= millisToPopup) {
					nextState = new ProgressDialogVisible(this);
				}
			}
			return nextState;
		}

		@Override
		public void setMinimum(int min) {
			this.min = min;
		}

		@Override
		public int getMinimum() {
			return min;
		}

		@Override
		public void setMaximum(int max) {
			this.max = max;
		}

		@Override
		public int getMaximum() {
			return max;
		}

		@Override
		public boolean isCanceled() {
			return false;
		}

		@Override
		public void setMillisToDecideToPopup(int millisToDecideToPopup) {
			this.millisToDecideToPopup = millisToDecideToPopup;
		}

		@Override
		public int getMillisToDecideToPopup() {
			return millisToDecideToPopup;
		}

		@Override
		public void setMillisToPopup(int millisToPopup) {
			this.millisToPopup = millisToPopup;
		}

		@Override
		public int getMillisToPopup() {
			return millisToPopup;
		}

		@Override
		public void setRemainingTimeEnabled(boolean remainingTimeEnabled) {
			this.remainingTimeEnabled = remainingTimeEnabled;
		}
	}

	private class ProgressDialogVisible extends ProgressDialogState {

		private static final String UNKNOWN_ETA = "ETA --:--:--";
		private static final String UNKNOWN_PROGRESS = "--/--";
		private JOptionPane pane;
		private JProgressBar myBar = new JProgressBar();
		private ProgressTextPanel progressTextPanel = new ProgressTextPanel();
		private JDialog dialog;
		private ProgressDialogHidden hiddenState;

		private long last = Long.MIN_VALUE;
		private long elapsedTimeStart = 0;
		private long etaTime = -1;

		private Timer timer;
		private TimerTask updateProgressTextTask = new TimerTask() {

			@Override
			public void run() {
				updateProgressStatus();
			}
		};

		private Object[] cancelOption = new Object[] { UIManager
				.getString("OptionPane.cancelButtonText") };

		public ProgressDialogVisible(ProgressDialogHidden hiddenState) {
			this.hiddenState = hiddenState;
			int max = hiddenState.getMaximum();
			myBar.setMinimum(hiddenState.getMinimum());
			myBar.setMaximum(max);
			myBar.setIndeterminate(max < 0);

			pane = new ProgressOptionPane(getOptionPaneMessage());
			dialog = pane.createDialog(hiddenState.parentComponent,
					UIManager.getString("ProgressMonitor.progressText"));
			dialog.setVisible(true);
		}

		@Override
		public void setMessage(Object message) {
			hiddenState.setMessage(message);
			pane.setMessage(getOptionPaneMessage());

		}

		private Object[] getOptionPaneMessage() {
			List<Object> messageElements = new ArrayList<Object>();

			messageElements.add(hiddenState.message);
			messageElements.add(myBar);
			if (hiddenState.remainingTimeEnabled) {
				updateProgressStatus();
				messageElements.add(progressTextPanel);
			}

			return (Object[]) messageElements
					.toArray(new Object[messageElements.size()]);
		}

		@Override
		public void setParentComponent(Component parentComponent) {
			dialog.dispose();
			dialog = pane.createDialog(parentComponent,
					UIManager.getString("ProgressMonitor.progressText"));
			dialog.setVisible(true);
		}

		@Override
		public void setProgress(int nv) {
			if (nv >= 0) {
				int oldValue = myBar.getValue();
				etaTime = calculateRemainingTime(oldValue, nv);
				if (hiddenState.remainingTimeEnabled) {
					updateProgressStatus();
				}
				myBar.setValue(nv);
			} else {
				myBar.setIndeterminate(true);
			}
		}

		private void updateProgressStatus() {
			String timeString = null;
			String progressString = null;

			int nv = myBar.getValue();
			if (myBar.isIndeterminate()) {
				long elapsedTime = calculateElapsedTime();
				timeString = "ELAPS " + formatDuration(elapsedTime, "HH:mm:ss");
				progressString = "---/---";
			} else {
				timeString = UNKNOWN_ETA;
				progressString = UNKNOWN_PROGRESS;

				if (etaTime >= 0) {
					timeString = "ETA " + formatDuration(etaTime, "HH:mm:ss");
				}

				progressString = String.format("%s/%s", nv, getMaximum());
			}

			progressTextPanel.getTimeDocument().setText(timeString);
			progressTextPanel.getProgressDocument().setText(progressString);
		}

		private long calculateElapsedTime() {
			return System.currentTimeMillis() - elapsedTimeStart;
		}

		private long calculateRemainingTime(int oldValue, int newValue) {
			long actual = System.currentTimeMillis();
			if (last == Long.MIN_VALUE) {
				last = actual;
			}
			long diff = actual - last;

			if (diff > 0) {
				double averagePerWork = (double) diff
						/ (double) (newValue - oldValue);
				hiddenState.workAverage.addValue(averagePerWork);
			}
			last = actual;
			long etaTimeMs = -1;

			Double value = hiddenState.workAverage.getValue();
			if (value != 0.0) {
				int maximum = getMaximum();
				int remaining = maximum - newValue;
				etaTimeMs = (long) (value * remaining);
			}
			return etaTimeMs;
		}

		@Override
		public void close() {
			dialog.setVisible(false);
			dialog.dispose();
			dialog = null;
			pane = null;
			myBar = null;
			if (timer != null) {
				timer.cancel();
			}
		}

		@Override
		public void setMinimum(int min) {
			myBar.setMinimum(min);
		}

		@Override
		public int getMinimum() {
			return myBar.getMinimum();
		}

		@Override
		public void setMaximum(int max) {
			myBar.setMaximum(max);
			if (max < 0) {
				elapsedTimeStart = System.currentTimeMillis();
				myBar.setIndeterminate(true);
				timer = new Timer(true);
				timer.schedule(updateProgressTextTask, 0, 1000);
			} else {
				timer.cancel();
				myBar.setIndeterminate(false);
				hiddenState.workAverage = new IncrementalAverage();
				etaTime = -1;
			}
			updateProgressStatus();

		}

		@Override
		public int getMaximum() {
			return myBar.getMaximum();
		}

		@Override
		public boolean isCanceled() {
			Object v = pane.getValue();
			return ((v != null) && (cancelOption.length == 1) && (v
					.equals(cancelOption[0])));
		}

		@Override
		public void setMillisToDecideToPopup(int millisToDecideToPopup) {
			hiddenState.setMillisToDecideToPopup(millisToDecideToPopup);
		}

		@Override
		public int getMillisToDecideToPopup() {
			return hiddenState.getMillisToDecideToPopup();
		}

		@Override
		public int getMillisToPopup() {
			return hiddenState.getMillisToPopup();
		}

		@Override
		public void setMillisToPopup(int millisToPopup) {
			hiddenState.setMillisToPopup(millisToPopup);
		}

		@Override
		public void setRemainingTimeEnabled(boolean remainingTimeEnabled) {
			this.hiddenState.setRemainingTimeEnabled(remainingTimeEnabled);
			pane.setMessage(getOptionPaneMessage());
		}

		private class ProgressOptionPane extends JOptionPane {
			private static final long serialVersionUID = 6465617362433967869L;

			ProgressOptionPane(Object messageList) {
				super(messageList, JOptionPane.PLAIN_MESSAGE,
						JOptionPane.DEFAULT_OPTION, null,
						ProgressDialogVisible.this.cancelOption, null);
			}

			public int getMaxCharactersPerLineCount() {
				return 60;
			}

			// Equivalent to JOptionPane.createDialog,
			// but create a modeless dialog.
			// This is necessary because the Solaris implementation doesn't
			// support Dialog.setModal yet.
			public JDialog createDialog(Component parentComponent, String title) {
				final JDialog dialog;

				Window window = getWindowForComponent(parentComponent);
				if (window instanceof Frame) {
					dialog = new JDialog((Frame) window, title, false);
				} else {
					dialog = new JDialog((Dialog) window, title, false);
				}
				Container contentPane = dialog.getContentPane();

				contentPane.setLayout(new BorderLayout());
				contentPane.add(this, BorderLayout.CENTER);
				dialog.pack();
				dialog.setLocationRelativeTo(parentComponent);
				dialog.addWindowListener(new WindowAdapter() {
					boolean gotFocus = false;

					public void windowClosing(WindowEvent we) {
						setValue(cancelOption[0]);
					}

					public void windowActivated(WindowEvent we) {
						// Once window gets focus, set initial focus
						if (!gotFocus) {
							selectInitialValue();
							gotFocus = true;
						}
					}
				});

				addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent event) {
						if (dialog.isVisible()
								&& event.getSource() == ProgressOptionPane.this
								&& (event.getPropertyName().equals(
										VALUE_PROPERTY) || event
										.getPropertyName().equals(
												INPUT_VALUE_PROPERTY))) {
							dialog.setVisible(false);
							dialog.dispose();
						}
					}
				});

				return dialog;
			}

		}

	}

	private static class ProgressTextPanel extends JPanel {

		private static final long serialVersionUID = 3905254906266079708L;

		private JLabel progressLabel = new JLabel();
		private JLabelDocumentAdapter progressLabelDocumentAdapter = new JLabelDocumentAdapter(
				progressLabel);
		private PlainSimpleDocument progressDocument = new PlainSimpleDocument();

		private JLabel etaLabel = new JLabel();
		private JLabelDocumentAdapter etaLabelDocumentAdapter = new JLabelDocumentAdapter(
				etaLabel);
		private PlainSimpleDocument etaDocument = new PlainSimpleDocument();

		public ProgressTextPanel() {
			setLayout(new SpringLayout());
			progressLabelDocumentAdapter.setDocument(progressDocument);
			etaLabelDocumentAdapter.setDocument(etaDocument);
			Font oldFont = etaLabel.getFont();
			Font newFont = new Font("monospaced", Font.PLAIN, oldFont.getSize());
			etaLabel.setFont(newFont);
			progressLabel.setFont(newFont);
			add(etaLabel);
			add(progressLabel);

			SpringUtilities.makeCompactGrid(this, 1, 2, 0, 0, 15, 3);
		}

		public PlainSimpleDocument getProgressDocument() {
			return progressDocument;
		}

		public PlainSimpleDocument getTimeDocument() {
			return etaDocument;
		}
	}
}
