package com.googlecode.simpleret.recorder;

public class ThreadData {
	
	private long id = -1;
	
	// Current depth/level of a trace. 
	private int callDepth = 0;
	
	// Filtering in progress.
	private boolean filtering = false;
	
	private int filteredCallDepth = -1;
	
	private String filteredSignature = "";
	
	ThreadData(long id) {
		this.id = id;
	}
	
	public void resetFiltering() {
		filtering = false;
		filteredSignature = "";
		filteredCallDepth = -1;
	}

	public int getCallDepth() {
		return callDepth;
	}

	public void setCallDepth(int callDepth) {
		this.callDepth = callDepth;
	}
	
	public void increaseCallDepth() {
		this.callDepth++;
	}
	
	public void decreaseCallDepth() {
		this.callDepth--;
	}

	public boolean isFiltering() {
		return filtering;
	}

	public int getFilteredCallDepth() {
		return filteredCallDepth;
	}

	public void setFilteredCallDepth(int filteredCallDepth) {
		this.filteredCallDepth = filteredCallDepth;
	}

	public String getFilteredSignature() {
		return filteredSignature;
	}

	public void setFilteredSignature(String filteredSignature) {
		this.filteredSignature = filteredSignature;
	}
	
	public boolean isEndOfFiltering(Signature signature) {
		if ( (filteredCallDepth == callDepth) &&
			filteredSignature.equals(signature.getStamp()) ) {
			return true;
		}
		return false;
	}
	
	public void setFiltering(Signature signature) {
		filtering = true;
		filteredSignature = signature.getStamp();
		filteredCallDepth = callDepth;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
