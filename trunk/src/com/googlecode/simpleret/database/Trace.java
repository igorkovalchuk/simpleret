package com.googlecode.simpleret.database;

import com.googlecode.simpleret.viewer.Data;

//import com.googlecode.simpleret.Constants;

/**
 * Represent a line (a string) from the program trace.
 */
public class Trace {

	private int id = 0;

	/**
	 * For the enclosing element of trace it is a reference to the id of start
	 * element of trace, otherwise 0.
	 */
	private int startId = 0;

	/**
	 * For the start element of trace it is a reference to the id of enclosing
	 * element of trace, otherwise 0.
	 */
	private int endId = 0;

	/**
	 * Reference to the parent's trace element id in hierarchy.
	 * 
	 * For a start element of trace it is a reference to the id of start element
	 * of parent.
	 * 
	 * For an enclosing element of trace it is a reference to the id of
	 * enclosing element of parent.
	 */
	private int parentId = 0;

	/**
	 * If this Trace represents "return".
	 */
	private boolean ret = false; // return

	private int vocabularyId = 0;

	/**
	 * Current trace depth.
	 */
	private int level = 0;

	private Integer colourMarker = null;

	// private final static String NBSP1 = Constants.NBSP;
	// private final static String NBSP2 = Constants.NBSP + Constants.NBSP;;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStartId() {
		return startId;
	}

	public void setStartId(int startId) {
		this.startId = startId;
	}

	public boolean isReturn() {
		return ret;
	}

	public void setReturn(boolean returns) {
		this.ret = returns;
	}

	public int getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(int vocabularyId) {
		this.vocabularyId = vocabularyId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Integer getColourMarker() {
		return colourMarker;
	}

	public void setColourMarker(Integer colourMarker) {
		this.colourMarker = colourMarker;
	}

	public int getEndId() {
		return endId;
	}

	public void setEndId(int endId) {
		this.endId = endId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getTextView(Data data) {

		VocabularyCache vc = data.getVocabularyCache();
		StringBuffer sb = new StringBuffer();

		sb.append("<a href=\"http://localhost?id="+this.id+"\">");

		String thisIDstr = String.valueOf(id);
		while(thisIDstr.length() < 9) {
			thisIDstr = " " + thisIDstr;
		}

		sb.append(thisIDstr).
		append("</a>").

		append("&nbsp;").append(level);
		for(int i = 0; i  <= level; i++) {
			sb.append("&nbsp;&nbsp;");
		}
		if (this.ret) {
			sb.append("&nbsp;&nbsp;");
		} else {
			sb.append("&gt;&nbsp;");
		}
		String call = vc.getCall(this.vocabularyId);
		call = call.replace(">", "&gt;");
		call = call.replace("<", "&lt;");

		String c = null;
		if (this.colourMarker != null) {
			c = Integer.toHexString(this.colourMarker.intValue());
			if (c.length() == 8) {
				c = c.substring(2, 8);
			}
			while(c.length() < 6) {
				c = '0' + c;
			}
		}

		if (c != null) {
			sb.append("<span style=\"color:#" + c + ";\">");
		}

		sb.append(call);

		if (c != null) {
			sb.append("</span>");
		}

		return sb.toString();
	}
	
}
