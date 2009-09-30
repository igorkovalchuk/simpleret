package com.googlecode.simpleret.importer;

import com.googlecode.simpleret.viewer.FrameProgressBar;

abstract public class TraceImporterProgress {

	protected FrameProgressBar progressBar;
	protected String progressBarDescription = "";

	public void setProgressBar(FrameProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public void setProgressBarDescription(String progressBarDescription) {
		this.progressBarDescription = progressBarDescription;
	}

}
