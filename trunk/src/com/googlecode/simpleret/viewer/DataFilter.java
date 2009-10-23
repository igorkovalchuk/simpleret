package com.googlecode.simpleret.viewer;

public class DataFilter {


	private boolean itself = false;
	private boolean subelements = false;
	private boolean parents = false;

	/**
	 * Mode, one of the following:<br>
	 * - mark;<br>
	 * - delete;<br>
	 * - undelete.
	 */
	private int mode = 0; 

	private static final int MARK = 0;
	private static final int DELETE = 1;
	private static final int UNDELETE = 2;

	public boolean isItself() {
		return itself;
	}
	
	public void setItself(boolean itself) {
		this.itself = itself;
	}
	
	
	public boolean isSubelements() {
		return subelements;
	}
	
	public void setSubelements(boolean subelements) {
		this.subelements = subelements;
	}
	
	
	public boolean isParents() {
		return parents;
	}
	
	public void setParents(boolean parents) {
		this.parents = parents;
	}
	
	public boolean isDefined() {
		if ( this.itself || this.parents || this.subelements ) {
			return true;
		}
		return false;
	}

	/**
	 * Mode, one of the following: mark/delete/undelete.
	 */
	public void setMode(boolean delete, boolean undelete) {
		if (delete) {
			this.mode = DELETE;
		} else if (undelete) {
			this.mode = UNDELETE;
		} else {
			this.mode = MARK;
		}
	}

	public String getMode(String defaultStr) {
		if (this.mode == DELETE) {
			return "Delete";
		} else if (this.mode == UNDELETE) {
			return "Undelete";
		} else {
			return defaultStr;
		}
	}

	public boolean isDelete() {
		return (mode == DELETE)?true:false;
	}

	public boolean isUndelete() {
		return (mode == UNDELETE)?true:false;
	}

	public boolean isMark() {
		return (mode == MARK)?true:false;
	}

}
