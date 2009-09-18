package com.googlecode.simpleret.viewer;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.classic.Session;

import com.googlecode.simpleret.database.Trace;
import com.googlecode.simpleret.database.VocabularyCache;

public class Data {

	private static Logger logger = Logger.getLogger(Data.class);
	
	private Session session = null;
	
	private VocabularyCache vocabularyCache = null;

	// Maps of identifiers.
	
	private Map<Integer, Integer> currentId2endId = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> endId2currentId = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> currentId2parentId = new HashMap<Integer, Integer>();
	private Set<Integer> currentIdIsReturn = new HashSet<Integer>();
	
	/**
	 * Re-initialize flag (get SQL count()).
	 */
	private boolean reinitialize = true;
	
	/**
	 * Re-loading data flag (show a new page).
	 */
	private boolean changed = true;
	
	/**
	 * Current pointer - number (not an id) of a first record on the screen.
	 */
	private int pointer = 0;
	private int pointerPrevious = 0;
	
	/**
	 * Number of selected records (from database).
	 */
	private int count = 0;
	
	private boolean displayColouredOnly = false;
	private boolean displayColouredOnlyPrevious = true; // for 1-st init.!!!;
	
	private boolean displayRange = false;
	private boolean displayRangePrevious = false;	
	
	private int level = 0;
	private int levelPrevious = 0;
	
	private ListOfSignatures signatures = new ListOfSignatures();
	
	private Integer takenColour = null;
	
	private List<Trace> loadedList = null;
	
	private Integer rangeTo = null;
	private Integer rangeFrom = null;
	
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
	
	public void changeDisplayColouredOnly() {
		if (displayColouredOnly == false) {
			displayColouredOnly = true;
		} else {
			displayColouredOnly = false;
		}
	}
	
	/**
	 * We shell know about 
	 * isReinitialize and
	 * isChanged.
	 */
	public void defineState() {
		reinitialize = false;
		changed = false;
		if (pointer != pointerPrevious) {
			changed = true;
		}
		if ( (displayColouredOnly != displayColouredOnlyPrevious) ||
			(level != levelPrevious) ||
			(displayRange != displayRangePrevious)
		) {
			reinitialize = true;
			changed = true;
		}
	}

	public void resetState() {
		pointerPrevious = pointer;
		displayColouredOnlyPrevious = displayColouredOnly;
		displayRangePrevious = displayRange;
		levelPrevious = level;
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

	public int getPointer() {
		return pointer;
	}

	public void setPointer(int pointer) {
		if (pointer >= count) {
			pointer = count - 1; // incorrect, if count = 0, pointer = -1;
		}
		if (pointer < 0) {
			pointer = 0;
		}
		this.pointer = pointer;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		if (level < 0) {
			level = 0;
		}
		if (level > 100) {
			level = 0;
		}
		this.level = level;
	}
	
	private int lastIdentifier = 0;
	private int lastPointer = 0;
	private int lastListSize = 0;
	
	public void setLastIdentifier(List<Integer> list) {
		// Don't reset last identifiers, if list.size() == 0;
		//logger.debug("[PREVIOUS] LastPointer = " + lastPointer + "; Last Identifier = " + lastIdentifier);
		Integer idObject = null;
		if (lastListSize == 0) {
			lastListSize = list.size();
			return;
		}
		lastListSize = list.size();
		
		if ( list.size() > 0 ) {
			idObject = list.get(0);
		} else {
			return;
		}
		lastIdentifier = idObject.intValue();
		lastPointer = pointer;
		//logger.debug("[NOW]      LastPointer = " + lastPointer + "; Last Identifier = " + lastIdentifier);
	}

	public int getLastPointer() {
		return lastPointer;
	}

	public int getLastIdentifier() {
		return lastIdentifier;
	}

	public String getPercent() {
		float result = 100f * (float) getPointer() / ( (float) (getCount() - 1) );
		return String.format("%.3f", result);
	}

	public int getLastListSize() {
		return lastListSize;
	}

	public ListOfSignatures getSignatures() {
		return signatures;
	}

	public Integer getTakenColour() {
		return takenColour;
	}

	public void setTakenColour(Integer takenColour) {
		this.takenColour = takenColour;
	}

	public List<Trace> getLoadedList() {
		return loadedList;
	}

	public void setLoadedList(List<Trace> loadedList) {
		this.loadedList = loadedList;
	}

	public Trace findTraceById(int id) {
		if (this.loadedList == null)
			return null;
		Iterator<Trace> i = this.loadedList.iterator();
		while(i.hasNext()) {
			Trace trace = i.next();
			if (trace.getId() ==  id) {
				return trace;
			}
		}
		return null;
	}

	public Color getTakenColourAsColour() {
		if (this.takenColour == null) {
			logger.debug("Taken Colour = null");
			return null;
		}
		Color result = new Color( this.takenColour );
		logger.debug("Taken Colour = " + this.takenColour);
		return result;
	}
	
	public Set<Integer> findParents(Integer id) {
		Set<Integer> result = new HashSet<Integer>();
		Integer parentID = this.currentId2parentId.get(id);
		if (parentID == null) {
			return result;
		}
		result.add(parentID);
		result.addAll(findParents(parentID));
		return result;
	}

	public void correctRange() {
		Integer tmp;
		if ( (rangeFrom != null) && (rangeTo != null) ) {
			if (rangeFrom > rangeTo) {
				tmp = rangeTo;
				rangeTo = rangeFrom;
				rangeFrom = tmp;
			}
		}
	}

	public boolean isRangeDefined() {
		if ( (rangeFrom != null) || (rangeTo != null) ) {
			return true;
		} else {
			return false;
		}
	}

	public Integer getRangeTo() {
		return rangeTo;
	}

	public void setRangeTo(Integer rangeTo) {
		this.rangeTo = rangeTo;
	}

	public Integer getRangeFrom() {
		return rangeFrom;
	}

	public void setRangeFrom(Integer rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public boolean isDisplayRange() {
		return displayRange;
	}

	public void setDisplayRange(boolean displayRange) {
		this.displayRange = displayRange;
	}
	
	public void changeDisplayRange() {
		if (displayRange == false) {
			if (isRangeDefined()) {
				displayRange = true;
			}
		} else {
			displayRange = false;
		}
	}

}
