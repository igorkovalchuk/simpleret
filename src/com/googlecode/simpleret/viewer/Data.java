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
	
	private long count = 0;
	
	/**
	 * Re-initialize flag (get SQL count()).
	 */
	private boolean reinitialize = true;
	
	/**
	 * Re-loading data flag (show a new page).
	 */
	private boolean changed = true;
	
	/**
	 * Current page.
	 */
	private int page = 1;
	private int pagePrevious = 1;
	
	/**
	 * Number of pages (in database).
	 */
	private int pages = 1; 
	
	private boolean displayColouredOnly = false;
	private boolean displayColouredOnlyPrevious = true; // for 1-st init.!!!;

	
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

	/**
	 * We shell know about 
	 * isReinitialize and
	 * isChanged.
	 */
	public void defineState() {
		reinitialize = false;
		changed = false;
		if (page != pagePrevious) {
			changed = true;
		}
		if (displayColouredOnly != displayColouredOnlyPrevious) {
			reinitialize = true;
			changed = true;
		}
	}

	public void resetState() {
		pagePrevious = page;
		displayColouredOnlyPrevious = displayColouredOnly;
		reinitialize = false;
		changed = false;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isReinitialize() {
		return reinitialize;
	}

	public void setReinitialize(boolean reinitialize) {
		this.reinitialize = reinitialize;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		if (page > pages) {
			page = pages;
		} else if (page < 1) {
			page = 1;
		}
		this.page = page;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}
	
}
