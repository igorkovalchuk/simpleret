package com.googlecode.simpleret.database;

import com.googlecode.simpleret.viewer.Data;

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

	/**
	 * Reference to the Vocabulary of calls.
	 */
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

	/**
	 * Especially for the SimpleRET Viewer.
	 * 
	 * @return
	 * 		a string that represent a call.
	 */
	public String getTextView(Data data) {

		VocabularyCache vc = data.getVocabularyCache();
		StringBuffer sb = new StringBuffer();

		sb.append("<a href=\"http://localhost?id=" + this.id + "\">");

		String thisIDstr = String.valueOf(id);
		while (thisIDstr.length() < 9) {
			thisIDstr = " " + thisIDstr;
		}

		sb.append(thisIDstr).append("</a>").

		append("&nbsp;").append(level);
		for (int i = 0; i <= level; i++) {
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
			while (c.length() < 6) {
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

	/**
	 * Especially for HTML export.
	 * 
	 * @return
	 * 		a string that represent a call.
	 */
	public String getHTML(Data data) {
		
		VocabularyCache vc = data.getVocabularyCache();
		
		StringBuffer sb = new StringBuffer();

		String idString = String.valueOf(id);
		
		int len = idString.length();
		while (len < 9) {
			sb.append(" ");
			len++;
		}
		
		Integer otherID;
		if (this.ret) {
			otherID = data.getEndId2currentId().get(this.id);
		} else {
			otherID = data.getCurrentId2endId().get(this.id);
		}
		
		sb.append("<a id=\"" + idString + "\"" + " href=\"#" + otherID + "\" " + ">");
		sb.append(idString);
		sb.append("</a>");
		
		sb.append("&nbsp;");

		for (int i = 0; i <= level; i++) {
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
			while (c.length() < 6) {
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
	
	/**
	 * @return
	 * 		a string that represent this call,
	 * 		like this "package.class.method".
	 */
	public String getSignature(Data data) {
		VocabularyCache vc = data.getVocabularyCache();
		String call = vc.getCall(this.vocabularyId);
		if (call == null) 
			call = "UnknownClass.unknownMethod"+id;
		return call;
	}
	
	/*
	public String getClassName(String signature) {
		int index = signature.lastIndexOf((int)'.');
		if (index > 0) {
			return signature.substring(0, index);
		}
		throw new RuntimeException("Can't find a class name for [" + signature + "]");
	}
	*/
	
	/**
	 * Especially to create a name of class like this "Class :: package.Class"
	 * for the AmaterasUML diagram.
	 * 
	 * If we will show only "package.Class" a user will
	 * not see that caption, because an icon of class
	 * have a limited width.
	 * 
	 * @return
	 * 		a string like this 
	 * 		"Class :: package.Class"
	 * 		or this
	 * 		"Class" .
	 */
	public String getClassNameAmaterasUML(String signature) {
		StringBuffer result = new StringBuffer();
		int index1 = signature.lastIndexOf((int)'.'); // point before a methods name;	
		int index2;
		if (index1 > 0) {
			index2 = signature.lastIndexOf((int)'.', index1 - 1);// point before a class' name;
			if (index2 >= 0) {
				// "package.Class.method()" => "Class :: package.Class"
				result.append(signature.substring(index2 + 1, index1));
				result.append(" :: ");
				result.append(signature.substring(0, index1));
			} else {
				// "Class.method()" => "Class"
				result.append(signature.substring(0, index1));
			}
			return result.toString();
		}
		throw new RuntimeException("Can't find a class name for [" + signature + "]");
	}

	/**
	 * Especially to create a description of a call
	 * for the AmaterasUML diagram.
	 * 
	 * @return
	 * 		a string like this "method" .
	 */
	public String getMethodName(String signature) {
		int index1 = signature.lastIndexOf((int)'.') + 1;
		int index2 = signature.lastIndexOf((int)'(');
		if (index1 >= 0) {
			if (index2 >= 0) {
				return signature.substring(index1, index2);
			} else {
				return signature.substring(index1);
			}
		}
		throw new RuntimeException("Can't find a class name for [" +  signature + "]");
	}
	
}
