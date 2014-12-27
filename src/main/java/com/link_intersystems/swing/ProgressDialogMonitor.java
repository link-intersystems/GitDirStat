package com.link_intersystems.swing;

import java.awt.Component;

public class ProgressDialogMonitor implements ProgressMonitor {

	private Component dialogParent;

	public ProgressDialogMonitor(Component dialogParent) {
		this.dialogParent = dialogParent;
	}

	private com.link_intersystems.swing.ProgressDialog progressMonitor;
	private int worked = 0;
	private int millisToDecideToPopup = 250;
	private int millisToPopup = 250;
	private boolean etaEnabled;

	@Override
	public void update(int completed) {
		if (this.progressMonitor != null) {
			worked += completed;
			this.progressMonitor.setProgress(worked);
		}
	}

	@Override
	public void start(String taskName, int totalWork) {
		this.worked = 0;
		if (this.progressMonitor == null) {
			this.progressMonitor = new com.link_intersystems.swing.ProgressDialog(
					dialogParent, taskName, "", 0, totalWork);
			progressMonitor.setMillisToDecideToPopup(millisToDecideToPopup);
			progressMonitor.setMillisToPopup(millisToPopup);
			progressMonitor.setRemainingTimeEnabled(etaEnabled);
		} else {
			this.progressMonitor.setMessage(taskName);
			this.progressMonitor.setMaximum(totalWork);
		}

	}

	public void setETAEnabled(boolean etaEnabled){
		this.etaEnabled = etaEnabled;
		if(progressMonitor != null){
			progressMonitor.setRemainingTimeEnabled(etaEnabled);
		}
	}

	@Override
	public void end() {
		if (this.progressMonitor != null) {
			this.progressMonitor.close();
			this.progressMonitor = null;
		}
	}

	public boolean isCanceled() {
		if (this.progressMonitor != null) {
			return this.progressMonitor.isCanceled();
		}
		return true;
	}

	public int getMillisToDecideToPopup() {
		return millisToDecideToPopup;
	}

	public void setMillisToDecideToPopup(int millisToDecideToPopup) {
		this.millisToDecideToPopup = millisToDecideToPopup;
		if (progressMonitor != null) {
			progressMonitor.setMillisToDecideToPopup(millisToDecideToPopup);
		}
	}

	public int getMillisToPopup() {
		return millisToPopup;
	}

	public void setMillisToPopup(int millisToPopup) {
		this.millisToPopup = millisToPopup;
	}

}
