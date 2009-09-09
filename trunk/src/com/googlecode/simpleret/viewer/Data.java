package com.googlecode.simpleret.viewer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.classic.Session;
import com.googlecode.simpleret.database.VocabularyCache;

public class Data {

	private Session session = null;
	
	private VocabularyCache vocabularyCache = null;

	// Maps of identifiers.
	
	private Map<Integer, Integer> currentId2endId = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> endId2currentId = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> currentId2parentId = new HashMap<Integer, Integer>();
	private Set<Integer> currentIdIsReturn = new HashSet<Integer>();
	
	private boolean displayColouredOnly = false;
	
	/**
	 * Number of selected records (in database).
	 */
	private long count = 0;
	
	/**
	 * Usually we:<br>
	 * 1) initialize (get SQL count());<br>
	 * 2) loading data;<br>
	 * So, if we need to reinitialize - set refresh = true.
	 */
	private boolean refresh = true;
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public VocabularyCache getVocabularyCache() {
		return vocabularyCache;
	}

	public void setVocabularyCache(VocabularyCache vocabularyCache) {
		this.vocabularyCache = vocabularyCache;
	}

	public Map<Integer, Integer> getCurrentId2endId() {
		return currentId2endId;
	}

	public Map<Integer, Integer> getEndId2currentId() {
		return endId2currentId;
	}

	public Map<Integer, Integer> getCurrentId2parentId() {
		return currentId2parentId;
	}

	public Set<Integer> getCurrentIdIsReturn() {
		return currentIdIsReturn;
	}

	public boolean isDisplayColouredOnly() {
		return displayColouredOnly;
	}

	public void setDisplayColouredOnly(boolean displayColouredOnly) {
		this.displayColouredOnly = displayColouredOnly;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public boolean isRefresh() {
		return refresh;
	}

	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}
	
}
