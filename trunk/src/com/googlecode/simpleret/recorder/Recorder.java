package com.googlecode.simpleret.recorder;

public class Recorder {

	private static Configuration configuration = new Configuration();

	private static final long REINITIALIZE = 10000L; // 60000 = 1 min

	private ThreadData threadData = null;

	private long time = 0;

	private static final long DELTHA_TIME = 1000;
	private static final long INITIAL_TIME = System.currentTimeMillis()
			/ DELTHA_TIME;

	// number of record;
	private long record = 0;

	synchronized public void traceEntry(Signature signature) {
		threadData = configuration.getThreadData(signature.getThreadID());
		threadData.increaseCallDepth();
		trace(signature);
	}

	synchronized public void traceExit(Signature signature) {
		threadData = configuration.getThreadData(signature.getThreadID());
		trace(signature);
		threadData.decreaseCallDepth();
	}

	private void trace(Signature signature) {

		long now = System.currentTimeMillis();
		if ((now - time) > REINITIALIZE) {
			// We periodically check the configuration file;
			time = now;
			configuration.initialize();
		}

		if (configuration.isEnabled()) {
			return;
		}

		if (configuration.isFiltering() && threadData.isFiltering()) {
			if (threadData.isEndOfFiltering(signature)) {
				threadData.resetFiltering();
				return;
			} else {
				// Filtering continues;
				return;
			}
		}

		StringBuffer result = new StringBuffer("");

		if (configuration.isFiltering()) {
			if (configuration.contains(signature)) {
				threadData.setFiltering(signature);
			}
			return;
		}

		// Create a trace record;

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
		result.append("()");
		
	}

}
