package com.googlecode.simpleret.importer;

import java.util.Set;
import java.util.TreeSet;

/**
 * For collect information about threads in program trace and about operations
 * (i.e. calls) in each thread.
 */
public class ThreadsData {

	/**
	 * A thread's identifier.
	 */
	private Long id = null;
	
	/**
	 * Total number of trace calls.
	 */
	private long operations = 0;
	
	/**
	 * Names of trace calls in alphabetic order.
	 */
	private Set<String> data = new TreeSet<String>();

	void addToTotalOperationsNumber() {
		operations++;
	}

	long getTotalOperationsNumber() {
		return operations;
	}

	/**
	 * @return false if this element is already added
	 */
	boolean addOperation(String call) {
		return this.data.add(call);
	}

	/** 
	 * @return
	 * 		names of trace calls.
	 */
	Set<String> getOperations() {
		return data;
	}

	Long getThreadId() {
		return id;
	}

	void setThreadId(Long id) {
		this.id = id;
	}

	/**
	 * @return
	 * 		a string like this: Thread id = 1 ; call types: 5 ; calls: 1000
	 */
	public String getAsString() {
		return String.format("Thread id = %s ; call types: %s ; calls: %s", 
			this.getThreadId(),
			this.getOperations().size(),
			this.getTotalOperationsNumber()
		);		
	}

}
