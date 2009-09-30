package com.googlecode.simpleret.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.googlecode.simpleret.Constants;

abstract public class TraceFileReaderAbstract extends TraceImporterProgress {

	static Logger abstractLogger = Logger
			.getLogger(TraceFileReaderAbstract.class);

	abstract protected void beforeRead();

	/**
	 * @param values ,
	 *            where:<br>
	 *            values[0] - id,<br>
	 *            values[1] - level,<br>
	 *            values[2] - threadID,<br>
	 *            values[3] - (time),<br>
	 *            values[4] - call,<br>
	 * 
	 */
	abstract protected void processString(String[] values);

	abstract protected void afterRead(boolean errors);

	/**
	 * 1) Call child's class method 'before reading a file';<br>
	 * 2) Call child's class method 'process string';<br>
	 * 3) Call child's class method 'after reading a file';<br>
	 * 
	 * @param fileName
	 */
	protected void startProcessing(String fileName) {

		// abstractLogger.info("Reading file: " + fileName);

		File fileToRead = new File(fileName);

		if (!fileToRead.exists()) {
			abstractLogger.error("Can not find this file: " + fileName);
			return;
		}

		// BEFORE READ THE FILE
		this.beforeRead();

		boolean errors = false;

		try {
			long size = fileToRead.length();
			long bytes = 0;
			FileReader fr = new FileReader(fileToRead);
			BufferedReader in = new BufferedReader(fr);
			String str;
			while ((str = in.readLine()) != null) {
				bytes += str.length();
				str = str.trim();

				// Ignore empty lines.
				if (str.equals("")) {
					continue;
				}

				// Ignore comments.
				if (str.startsWith("#")) {
					continue;
				}

				String[] values = str.split("\t");
				if (values.length != 5) {
					abstractLogger.error("Incorrect record: [" + str
							+ "], length = " + str.length());
					System.exit(1);
				}
				// PROCESS STRING
				processString(values);
				if (progressBar != null) {
					progressBar.setString(progressBarDescription + bytes
							+ " / " + size);
					progressBar
					.setValue((int) (Constants.PROGRESS_MAX * bytes / size));
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			abstractLogger.error(e);
			errors = true;
		} catch (IOException e) {
			e.printStackTrace();
			abstractLogger.error(e);
			errors = true;
		}

		// AFTER READ
		this.afterRead(errors);
	}

}
