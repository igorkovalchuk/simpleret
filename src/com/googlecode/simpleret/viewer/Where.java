package com.googlecode.simpleret.viewer;

/**
 * SQL Where
 */
public class Where {

	private StringBuffer where = new StringBuffer();
	
	public void append(String string) {
		if (string == null)
			return;
		if (string.equals(""))
			return;
		if (where.length() > 0) {
			where.append(" and ");
		}
		where.append(" ");
		where.append(string);
		where.append(" ");
	}
	
	/**
	 * @return an SQL (HSQL) string like this " where CONDITION1 and CONDITION2 and ... "
	 */
	public String toWhere() {
		if (where.length() > 0) {
			return " where " + where.toString();
		} else {
			return "";
		}
	}
	
	/**
	 * @return an SQL (HSQL) string like this " CONDITION1 and CONDITION2 and ... "
	 */
	public String toString() {
		return where.toString();
	}
	
}
