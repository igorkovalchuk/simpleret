package com.googlecode.simpleret.importer;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.simpleret.database.VocabularyCache;

public class ImportDataHolder {

	VocabularyCache vocabularyCache = new VocabularyCache();
	
	Map<Integer, Integer> startID2endID = new HashMap<Integer, Integer>();
}
