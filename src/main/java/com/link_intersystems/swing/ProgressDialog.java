/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.link_intersystems.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.link_intersystems.math.IncrementalAverage;

public class ProgressDialog {
	private ProgressDialog root;
	private JDialog dialog;
	private JOptionPane pane;
	private JProgressBar myBar;
	private JLabel noteLabel;
	private Component parentComponent;
	private String note;
	private Object[] cancelOption = null;
	private Object message;
	private long T0;
	private int millisToDecideToPopup = 500;
	private int millisToPopup = 2000;
	private int min;
	private int max;
	private boolean etaEnabled;
	private IncrementalAverage workAverage;
	private long last;

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
	public ProgressDialog(Component parentComponent, Object message,
			String note, int min, int max) {
		this(parentComponent, message, note, min, max, null);
	}

	private ProgressDialog(Component parentComponent, Object message,
			String note, int min, int max, ProgressDialog group) {
		this.min = min;
		this.max = max;
		this.parentComponent = parentComponent;

		cancelOption = new Object[1];
		cancelOption[0] = UIManager.getString("OptionPane.cancelButtonText");

		this.message = message;
		this.note = note;
		if (group != null) {
			root = (group.root != null) ? group.root : group;
			T0 = root.T0;
			dialog = root.dialog;
		} else {
			T0 = System.currentTimeMillis();
		}
		if (max < 0) {
			showProgressDialog(max);
		}
		workAverage = new IncrementalAverage();
		last = Long.MIN_VALUE;
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

	private class ProgressOptionPane extends JOptionPane {
		private static final long serialVersionUID = 6465617362433967869L;

		ProgressOptionPane(Object messageList) {
			super(messageList, JOptionPane.INFORMATION_MESSAGE,
					JOptionPane.DEFAULT_OPTION, null,
					ProgressDialog.this.cancelOption, null);
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
							&& (event.getPropertyName().equals(VALUE_PROPERTY) || event
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
		if (nv >= max) {
			close();
		} else {
			if (myBar != null) {
				setProgressBarProgress(nv);
			} else {
				long T = System.currentTimeMillis();
				long dT = (int) (T - T0);
				if (dT >= millisToDecideToPopup) {
					int predictedCompletionTime;
					if (nv > min) {
						predictedCompletionTime = (int) ((long) dT
								* (max - min) / (nv - min));
					} else {
						predictedCompletionTime = millisToPopup;
					}
					if (predictedCompletionTime >= millisToPopup) {
						showProgressDialog(nv);
					}
				}
			}
		}
	}

	private void showProgressDialog(int nv) {
		myBar = new JProgressBar();
		if (etaEnabled) {
			myBar.setStringPainted(true);
		}
		setProgressBarProgress(nv);
		if (note != null)
			noteLabel = new JLabel(note);
		pane = new ProgressOptionPane(
				new Object[] { message, noteLabel, myBar });
		dialog = pane.createDialog(parentComponent,
				UIManager.getString("ProgressMonitor.progressText"));
		dialog.setVisible(true);
	}

	private void setProgressBarProgress(int nv) {
		if (nv >= 0) {
			int oldValue = myBar.getValue();
			String paintedString = "";
			if (etaEnabled) {
				String eta = calculateETA(oldValue, nv);
				paintedString = nv + " / " + getMaximum() + "   " + eta;

			}
			myBar.setString(paintedString);
			myBar.setIndeterminate(false);
			myBar.setMinimum(min);
			myBar.setMaximum(max);
			myBar.setValue(nv);
		} else {
			myBar.setIndeterminate(true);
		}
	}

	private String calculateETA(int oldValue, int newValue) {
		long actual = System.currentTimeMillis();
		if (last == Long.MIN_VALUE) {
			last = actual;
		}
		long diff = actual - last;

		if (diff > 0) {
			double averagePerWork = (double) diff
					/ (double) (newValue - oldValue);
			workAverage.addValue(averagePerWork);
		}
		last = actual;

		Double value = workAverage.getValue();
		String eta = "ETA: --:--:--";
		if (value != 0.0) {
			int maximum = getMaximum();
			int remaining = maximum - newValue;

			long etaTimeMs = (long) (value * remaining);
			eta = DurationFormatUtils
					.formatDuration(etaTimeMs, "ETA: HH:mm:ss");
		}
		return eta;
	}

	/**
	 * Indicate that the operation is complete. This happens automatically when
	 * the value set by setProgress is >= max, but it may be called earlier if
	 * the operation ends early.
	 */
	public void close() {
		if (dialog != null) {
			dialog.setVisible(false);
			dialog.dispose();
			dialog = null;
			pane = null;
			myBar = null;
		}
	}

	/**
	 * Returns the minimum value -- the lower end of the progress value.
	 *
	 * @return an int representing the minimum value
	 * @see #setMinimum
	 */
	public int getMinimum() {
		return min;
	}

	/**
	 * Specifies the minimum value.
	 *
	 * @param m
	 *            an int specifying the minimum value
	 * @see #getMinimum
	 */
	public void setMinimum(int m) {
		if (myBar != null) {
			myBar.setMinimum(m);
		}
		min = m;
	}

	/**
	 * Returns the maximum value -- the higher end of the progress value.
	 *
	 * @return an int representing the maximum value
	 * @see #setMaximum
	 */
	public int getMaximum() {
		return max;
	}

	/**
	 * Specifies the maximum value.
	 *
	 * @param m
	 *            an int specifying the maximum value
	 * @see #getMaximum
	 */
	public void setMaximum(int m) {
		if (myBar != null) {
			myBar.setMaximum(m);
		}
		max = m;
	}

	/**
	 * Returns true if the user hits the Cancel button in the progress dialog.
	 */
	public boolean isCanceled() {
		if (pane == null)
			return false;
		Object v = pane.getValue();
		return ((v != null) && (cancelOption.length == 1) && (v
				.equals(cancelOption[0])));
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
		this.millisToDecideToPopup = millisToDecideToPopup;
	}

	/**
	 * Returns the amount of time this object waits before deciding whether or
	 * not to popup a progress monitor.
	 *
	 * @see #setMillisToDecideToPopup
	 */
	public int getMillisToDecideToPopup() {
		return millisToDecideToPopup;
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
		this.millisToPopup = millisToPopup;
	}

	/**
	 * Returns the amount of time it will take for the popup to appear.
	 *
	 * @see #setMillisToPopup
	 */
	public int getMillisToPopup() {
		return millisToPopup;
	}

	/**
	 * Specifies the additional note that is displayed along with the progress
	 * message. Used, for example, to show which file the is currently being
	 * copied during a multiple-file copy.
	 *
	 * @param note
	 *            a String specifying the note to display
	 * @see #getNote
	 */
	public void setNote(String note) {
		this.note = note;
		if (noteLabel != null) {
			noteLabel.setText(note);
		}
	}

	public void setMessage(Object message) {
		if (pane != null) {
			pane.setMessage(new Object[] { message, noteLabel, myBar });
		} else {
			this.message = message;
		}
	}

	/**
	 * Specifies the additional note that is displayed along with the progress
	 * message.
	 *
	 * @return a String specifying the note to display
	 * @see #setNote
	 */
	public String getNote() {
		return note;
	}

	public void setETAEnabled(boolean etaEnabled) {
		this.etaEnabled = etaEnabled;
		if (myBar != null) {
			myBar.setStringPainted(etaEnabled);
		}
	}
}
