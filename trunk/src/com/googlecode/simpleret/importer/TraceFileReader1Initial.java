package com.googlecode.simpleret.importer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * First thing - we need to count number of calls
 * in each Thread (if there are several threads),
 * so user can later choose the only one Thread to analyze/visualize.
 * 
 * @author (c) Igor O. Kovalchuk 2008, 2009.
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
		call = call.substring(3);

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
		Set<Long> keySet = threads.keySet();
		Iterator<Long> threadIDS = keySet.iterator();
		StringBuffer sb = new StringBuffer();
		while (threadIDS.hasNext()) {
			Long threadID = threadIDS.next();
			sb.append("Thread, id = ");
			sb.append(threadID);
			sb.append(", operation types =  ");
			sb.append(threads.get(threadID).getOperations().size());
			sb.append(", operations = ");
			sb.append(threads.get(threadID).getTotalOperationsNumber());
			sb.append("\n");
		}
		result = sb.toString();
	}

	public String getResult() {
		return result;
	}

}
