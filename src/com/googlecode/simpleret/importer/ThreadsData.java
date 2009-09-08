package com.googlecode.simpleret.importer;

import java.util.Set;
import java.util.TreeSet;

/**
 * For collect information about threads in program trace and about operations
 * (i.e. calls) in each thread.
 * 
 * @author (c) Igor O. Kovalchuk 2008, 2009.
 */
public class ThreadsData {

	private Long id = null;
	private long operations = 0;
	private Set<String> data = new TreeSet<String>(); // TreeSet - sort calls in alphabetic order;

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

	Set<String> getOperations() {
		return data;
	}

	Long getThreadId() {
		return id;
	}

	void setThreadId(Long id) {
		this.id = id;
	}

}
