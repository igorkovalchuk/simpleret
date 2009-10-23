package com.googlecode.simpleret.viewer;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;

/**
 * SQL Where
 */
public class Where {

	public final static String PLACEHOLDER_LEVEL = "aLevel";
	public final static String PLACEHOLDER_START_ID = "aStartID";
	public final static String PLACEHOLDER_END_ID = "aEndID";
	public final static String PLACEHOLDER_FROM_ID = "aFromID";
	public final static String PLACEHOLDER_TO_ID = "aToID";
	public final static String PLACEHOLDER_COLOUR = "aColour";
	public final static String PLACEHOLDER_WORD_ID = "aWord";

	private StringBuffer where = new StringBuffer();
	private Map<String, Object> placeholders = new HashMap<String, Object>();

	public String createBaseClause(Data data) {
		
		if (data.isDisplayColouredOnly()) {
			this.addClause("colourMarker is not null");
		}
		
		if (data.getLevel() != 0) {
			this.addClause("level <= :" + PLACEHOLDER_LEVEL);
			this.addPlaceholder(PLACEHOLDER_LEVEL, data.getLevel());
		}

		if (data.isDisplayRange()) {
			if ((data.getRangeFrom() != null) && (data.getRangeFrom().intValue() != 0 )) {
				this.addClause("id >= :" + PLACEHOLDER_FROM_ID);
				this.addPlaceholder(PLACEHOLDER_FROM_ID, data.getRangeFrom());
			}
			if ((data.getRangeTo() != null) && (data.getRangeTo().intValue() != 0 )) {
				this.addClause("id <= :" + PLACEHOLDER_TO_ID);
				this.addPlaceholder(PLACEHOLDER_TO_ID, data.getRangeTo());
			}
		}

		return where.toString();
	}

	public void addClause(String string) {
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

	public void addClauseOR(String string) {
		if (string == null)
			return;
		if (string.equals(""))
			return;
		if (where.length() > 0) {
			where.append(" or ");
		}
		where.append(" ");
		where.append(string);
		where.append(" ");
	}

	public void addClauseNonFolded() {
		this.addClause("folding = false");
	}

	public void addClauseActiveRecords() {
		this.addClause("dis = false");
	}

	public void addPlaceholder(String name, Object object) {
		placeholders.put(name, object);
	}

	public void usePlaceholders(Query query) {
		if (placeholders.containsKey(PLACEHOLDER_LEVEL)) {
			Integer level = (Integer) placeholders.get(PLACEHOLDER_LEVEL);
			query.setInteger(PLACEHOLDER_LEVEL, level);
		}
		if (placeholders.containsKey(PLACEHOLDER_START_ID)) {
			Integer id = (Integer) placeholders.get(PLACEHOLDER_START_ID);
			query.setInteger(PLACEHOLDER_START_ID, id);
		}
		if (placeholders.containsKey(PLACEHOLDER_END_ID)) {
			Integer id = (Integer) placeholders.get(PLACEHOLDER_END_ID);
			query.setInteger(PLACEHOLDER_END_ID, id);
		}
		if (placeholders.containsKey(PLACEHOLDER_FROM_ID)) {
			Integer id = (Integer) placeholders.get(PLACEHOLDER_FROM_ID);
			query.setInteger(PLACEHOLDER_FROM_ID, id);
		}
		if (placeholders.containsKey(PLACEHOLDER_TO_ID)) {
			Integer id = (Integer) placeholders.get(PLACEHOLDER_TO_ID);
			query.setInteger(PLACEHOLDER_TO_ID, id);
		}
		if (placeholders.containsKey(PLACEHOLDER_COLOUR)) {
			Integer colour = (Integer) placeholders.get(PLACEHOLDER_COLOUR);
			query.setInteger(PLACEHOLDER_COLOUR, colour);
		}
		if (placeholders.containsKey(PLACEHOLDER_WORD_ID)) {
			Integer id = (Integer) placeholders.get(PLACEHOLDER_WORD_ID);
			query.setInteger(PLACEHOLDER_WORD_ID, id);
		}
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
	
	/*
	 * @return an SQL (HSQL) string like this " CONDITION1 and CONDITION2 and ... "
	 */
	//private String toSimpleString() {
		//return where.toString();
	//}
	
}
