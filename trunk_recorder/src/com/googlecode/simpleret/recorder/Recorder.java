package com.googlecode.simpleret.recorder;

import java.io.IOException;

public class Recorder {

	private static Configuration configuration;

	private static final long REINITIALIZE = 10000L; // 60000 = 1 min

	private ThreadData threadData = null;

	private long time = 0;

	private static final long DELTHA_TIME = 1000;
	private static final long INITIAL_TIME = System.currentTimeMillis()
			/ DELTHA_TIME;

	// number of record;
	private long record = 0;

	public Recorder(Configuration configuration) {
		Recorder.configuration = configuration;
	}

	static long counter = 0;

	synchronized public void trace(Signature signature) {

		if (! configuration.containsRuntimeFilter(signature)) {
			//System.out.println("Ignored:    " + signature.getClassName());
			//System.out.println(signature.getStamp() + "		" + counter++);
			System.out.println(counter++);
			return;
		}

		System.out.print(counter++);
		System.out.print(" ");

		if (signature.isEntry()) {
			threadData = configuration.getThreadData(signature.getThreadID());
			threadData.increaseCallDepth();
			tracing(signature);
		} else {
			threadData = configuration.getThreadData(signature.getThreadID());
			tracing(signature);
			threadData.decreaseCallDepth();
		}
	}

	private void tracing(Signature signature) {

		long now = System.currentTimeMillis();
		if ((now - time) > REINITIALIZE) {
			// We periodically check the configuration file;
			time = now;
			configuration.initialize();
		}

		if ( ! configuration.isEnabled()) {
			return;
		}

		if (threadData.isFiltering()) {
			if (threadData.isEndOfFiltering(signature)) {
				threadData.resetFiltering();
				return;
			} else {
				// Filtering continues;
				return;
			}
		}

		if (configuration.contains(signature)) {
			threadData.setFiltering(signature);
			return;
		}

		// Create a trace record;

		StringBuffer result = new StringBuffer("");

		long currentTime = (System.currentTimeMillis() / DELTHA_TIME)
				- INITIAL_TIME;

		record++;

		int cd = threadData.getCallDepth();
		
		result.append(record).append('	').append(cd).append('	').append(
				threadData.getId()).append('	').append(currentTime).append('	');

		for (int i = 0; i < cd; i++)
			result.append("  ");

		boolean returning = false;

		if ( ! signature.isEntry()) {
			returning = true;
		}
		
		result.append(signature.getClassName()).append('.');
		if (returning) {
			result.append("<<return>>");
		} else {
			result.append(signature.getMethodName());
		}
		result.append("()").append("\n");
		
		if (configuration.isScreen()) {
			System.out.print(result);
		}

		try {
			configuration.getFileWriter().write(result.toString());
			configuration.getFileWriter().flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static Configuration getConfiguration() {
		return configuration;
	}

}
