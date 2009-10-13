package com.googlecode.simpleret.importer;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.simpleret.database.VocabularyCache;

/**
 * Just a complex parameter.
 */
class ImportDataHolder {

	VocabularyCache vocabularyCache = new VocabularyCache();
	
	/**
	 * Every trace call have either a start or an end point.
	 * <pre>
	 * ID    CALL
	 *  1    Alpha.one()           endID = 4
	 *  2      Betha.two()         endID = 3
	 *  3      Betha.<<return>>()  startID = 2
	 *  4    Alpha.<<return>>()    startID = 1
	 *  
	 *  A result:
	 *  
	 *  1 => 4
	 *  2 => 3
	 *  3 => 0
	 *  4 => 0
	 *  
a	 *  We need this data here, especially to store endID values to the database:
	 *  {@link TraceFileReader4SetAdditionalReferences}
	 *  
	 * </pre>
	 */
	Map<Integer, Integer> startID2endID = new HashMap<Integer, Integer>();
}
