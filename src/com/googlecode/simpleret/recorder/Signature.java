package com.googlecode.simpleret.recorder;

public class Signature {

	private String signature;
	private String className;
	private String methodName;
	
	private Long threadID;
	
	// method entry/exit;
	private boolean isEntry = true;
	
	Signature(String signature, boolean isEntry) {
		this.signature = signature;
		this.isEntry = isEntry;
	}
	
	boolean isEntry() {
		return isEntry;
	}
	
	String getSignature() {
		return signature;
	}

	// We need this to compare different/equal signatures;
	String getStamp() {
		return signature;
	}

	public Long getThreadID() {
		return threadID;
	}

	public void setThreadID(Long threadID) {
		this.threadID = threadID;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
}
