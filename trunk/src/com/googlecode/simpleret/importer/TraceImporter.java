package com.googlecode.simpleret.importer;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.googlecode.simpleret.Constants;
import com.googlecode.simpleret.Utilities;
import com.googlecode.simpleret.utilities.FileOrResource;
import com.googlecode.simpleret.viewer.FrameProgressBar;

/**
 * Import a trace from some file.<br>
 * 
 * 1. Select a file.<br>
 * 2. Show an information about threads stored in that file.<br>
 * 3. Select a thread (if there are several threads).<br>
 * 4. Store a vocabulary of calls to the database.<br>
 * 5. Store a trace to the database.<br>
 * 6. Create additional references in the database.
 */
public class TraceImporter {

	private static final long serialVersionUID = -1;

	private static Logger logger = Logger.getLogger(TraceImporter.class);

	final static int BATCH = 20;

	private final static String TITLE = "Program Trace Reader";
	private final static String STORE_TO_DATABASE = "A previous database data will be deleted. Proceed ?";
	private final static String SELECT_FILE = TITLE
			+ " - Please select a file to read";

	private GridBagConstraints constraints = new GridBagConstraints();

	private FileOrResource source = null;
	private Long threadID = null;

	public void process() {
		Insets insets = new Insets(3, 3, 3, 3);
		constraints.insets = insets;
		this.selectFile();
		this.seekThreads();

		int result = JOptionPane.showConfirmDialog(null, STORE_TO_DATABASE,
				TITLE, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			System.exit(0);
		}

		this.store();
	}

	public void processWebStart(FileOrResource object, Long threadID) {
		this.source = object;
		this.threadID = threadID;
		this.store();
	}

	/**
	 * Show a File Selection Dialog.
	 */
	private void selectFile() {

		String path = Utilities.getApplicationDataPath();
		File currentDirectory = new File(path);

		final JFileChooser fileChooser = new JFileChooser();
		TraceImporterFileFilter ff = new TraceImporterFileFilter();
		fileChooser.addChoosableFileFilter(ff);
		fileChooser.setCurrentDirectory(currentDirectory);

		fileChooser.setDialogTitle(SELECT_FILE);

		int value = fileChooser.showOpenDialog(null);
		if (value == JFileChooser.APPROVE_OPTION) {
			String fileName = fileChooser.getSelectedFile().getPath();
			source = new FileOrResource(fileName);
		} else {
			System.exit(0);
		}
	}

	/**
	 * Loading a text trace file and detect if there are several threads have been recorded.<br>
	 * 
	 * Chose one of them if there are several threads.
	 */
	private void seekThreads() {
		TraceFileReader1Initial reader = new TraceFileReader1Initial();
		reader.startProcessing(source);
		Map<Long, ThreadsData> threads = reader.getThreadsData();
		if (threads.size() == 0) {
			JOptionPane.showMessageDialog(null, "This is an incorrect file.");
			System.exit(0);
		}
		if (threads.size() == 1) {
			this.threadID = threads.values().iterator().next().getThreadId();
		} else {
			this.chooseThreadId(threads);
		}
	}

	/**
	 * Show a selection dialog to select one of threads.
	 */
	private void chooseThreadId(Map<Long, ThreadsData> threads) {

		Map<Long, String> selection = new LinkedHashMap<Long, String>();
		Iterator<ThreadsData> tds = threads.values().iterator();
		while (tds.hasNext()) {
			ThreadsData td = tds.next();
			selection.put(td.getThreadId(), td.getAsString());
		}

		Object[] b = selection.values().toArray();
		Object a = b[0];
		Object result = JOptionPane.showInputDialog(null,
				"Please select one of these threads", TITLE,
				JOptionPane.PLAIN_MESSAGE, null, b, a);
		if (result == null) {
			System.exit(0);
		}
		Iterator<Long> ids = selection.keySet().iterator();
		Long id;
		while (ids.hasNext()) {
			id = ids.next();
			if (result.equals(selection.get(id))) {
				this.threadID = id;
				logger.debug("Selected thread id = " + id);
				break;
			}
		}
		return;
	}

	/**
	 * Store a vocabulary of calls to the database.<br>
	 * Store a trace to the database.<br>
	 * Create additional references in the database.
	 */
	private void store() {

		FrameProgressBar progressBar = new FrameProgressBar(0,
				Constants.PROGRESS_MAX);

		logger.info("Create vocabulary ...");
		TraceFileReader2StoreVocabulary storeVocabulary = new TraceFileReader2StoreVocabulary(
				threadID);
		storeVocabulary.setProgressBar(progressBar);
		storeVocabulary
				.setProgressBarDescription("Stage 2 of 4. Create vocabulary: ");
		storeVocabulary.startProcessing(source);

		logger.info("Store trace ...");
		ImportDataHolder dataHolder = new ImportDataHolder();
		TraceFileReader3StoreTrace storeTrace = new TraceFileReader3StoreTrace(
				dataHolder, threadID);
		storeTrace.setProgressBar(progressBar);
		storeTrace
				.setProgressBarDescription("Stage 3 of 4. Store program trace: ");
		storeTrace.startProcessing(source);

		logger.info("Set additional references ...");
		TraceFileReader4SetAdditionalReferences references = new TraceFileReader4SetAdditionalReferences();
		references.setProgressBar(progressBar);
		references
				.setProgressBarDescription("Stage 4 of 4. Create additional references: ");
		references.storeReferences(dataHolder);
		logger.info("Done.");
		progressBar.dipose();
	}

}
