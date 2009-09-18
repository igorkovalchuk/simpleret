package com.googlecode.simpleret.viewer;

public class Range {

	Integer from = null;
	Integer to = null;

	public Range(Integer from, Integer to) {
		this.from = from;
		this.to = to;
		correct();
	}
	
	public Integer getFrom() {
		return from;
	}
	
	public void setFrom(Integer from) {
		this.from = from;
	}
	
	public Integer getTo() {
		return to;
	}
	
	public void setTo(Integer to) {
		this.to = to;
	}

	public boolean isDefined() {
		if ( (from != null) || (to != null) ) {
			return true;
		} else {
			return false;
		}
	}

	public void correct() {
		if ( (from != null) && (to != null) ) {
			Integer tmp;
			if (from > to) {
				tmp = to;
				to = from;
				from = tmp;
			}
		}
	}

	public String toString() {
		return "(" + this.from + ":" + this.to + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Range other = (Range) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

}
