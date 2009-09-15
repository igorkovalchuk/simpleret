package com.googlecode.simpleret.viewer;

public class DataFilter {


	private boolean itself = false;
	private boolean subelements = false;
	private boolean parents = false;
	
	
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

}
