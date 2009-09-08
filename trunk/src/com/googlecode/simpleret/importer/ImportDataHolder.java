package com.googlecode.simpleret.importer;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.simpleret.database.VocabularyCache;

/**
 * @author (c) Igor O. Kovalchuk 2008, 2009.
 */
public class ImportDataHolder {

	VocabularyCache vocabularyCache = new VocabularyCache();
	
	Map<Integer, Integer> startID2endID = new HashMap<Integer, Integer>();
}
