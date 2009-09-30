package com.googlecode.simpleret.importer;

import java.util.HashMap;
import java.util.Map;

/**
 * First thing - we need to count number of calls in each Thread (if there are
 * several threads), so user can later choose the only one Thread to
 * analyze/visualize.
 */
public class TraceFileReader1Initial extends TraceFileReaderAbstract {

	Map<Long, ThreadsData> threads = new HashMap<Long, ThreadsData>();

	String result = "";

	@Override
	protected void beforeRead() {
		// does nothing;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processString(String[] values) {

		Long threadID = Long.parseLong(values[2]);

		String call = values[4].trim();
		// call = call.substring(3);

		boolean ret = false;
		if (call.endsWith(".<<return>>()")) {
			ret = true;
		}

		ThreadsData td = threads.get(threadID);
		if (td == null) {
			td = new ThreadsData();
			td.setThreadId(threadID);
			threads.put(threadID, td);
		}

		if (!ret) {
			if (td.addOperation(call)) {
				System.out.println(call);
			}
		}
		td.addToTotalOperationsNumber();
	}

	@Override
	protected void afterRead(boolean errors) {
	}

	public String getResult() {
		return result;
	}

	public Map<Long, ThreadsData> getThreadsData() {
		return threads;
	}

}
