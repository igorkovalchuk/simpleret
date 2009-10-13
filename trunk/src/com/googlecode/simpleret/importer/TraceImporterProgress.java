package com.googlecode.simpleret.importer;

import com.googlecode.simpleret.viewer.FrameProgressBar;

/**
 * {@link TraceFileReaderAbstract} classes and
 * {@link TraceFileReader4SetAdditionalReferences}
 * need a progress bar,
 * because of long term database operations.
 */
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
