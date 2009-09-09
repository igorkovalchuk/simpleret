package com.googlecode.simpleret.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the vocabulary of the whole trace.<br>
 * This is a list of all types of calls (and identifiers)
 * of the whole trace.<br>
 * For example:<br>
 * id => call<br> 
 * 1 => com.somepackage.SomeClass.someMethod()<br> 
 * 2 => com.somepackage.SomeClass.someOtherMethod()<br>
 *  ... => ...<br>
 */
public class VocabularyCache {

	/**
	 * call => identifier
	 */
	Map<String, Integer> call2id = new HashMap<String, Integer>();

	/**
	 * identifier => call 
	 */
	Map<Integer, String> id2call = new HashMap<Integer, String>();

	/**
	 * identifier => object of Vocabulary
	 */
	Map<Integer, Vocabulary> id2object = new HashMap<Integer, Vocabulary>();

	public void setValue(Vocabulary object) {
		Integer id = object.getId();
		String call = object.getWord();
		if (!id2call.containsKey(id)) {
			id2call.put(id, call);
			id2object.put(id, object);
			call2id.put(call, id);
		}
	}

	boolean contains(Vocabulary object) {
		Integer id = object.getId();
		return id2call.containsKey(id);
	}

	Vocabulary getObject(Integer id) {
		return id2object.get(id);
	}

	public Integer getID(String call) {
		Integer id = call2id.get(call);
		return id;
	}

	public String getCall(Integer id) {
		String call = id2call.get(id);
		return call;
	}

	public void setList(List<Vocabulary> list) {
		Iterator<Vocabulary> i = list.iterator();
		while (i.hasNext()) {
			this.setValue(i.next());
		}
	}

	/**
	 * <pre>
	 * Search calls either by regular expression... or by start string.<br>
	 * Examples:<br>
	 *    &quot;&circ;.*someWord.*$&quot; - a regular expression;
	 *    every regular expression must start with &quot;&circ;&quot;;
	 *    
	 *    &quot;com.somepackage&quot; - a start of string;
	 *    
	 *    &quot;com.somepackage.SomeClass&quot; - a start of string;
	 *    
	 *    &quot;com.somepackage.SomeClass.someMethod()&quot; -
	 *    the whole string;
	 * </pre>
	 * 
	 * @return list of matched strings
	 */
	public List<String> searchByPattern(String pattern) {
		List<String> result = new ArrayList<String>();
		Set<String> already = new HashSet<String>();

		boolean re = false;
		if (pattern.startsWith("^")) {
			re = true;
		}

		Set<String> calls = call2id.keySet();
		Iterator<String> i = calls.iterator();
		String call;
		while (i.hasNext()) {
			call = i.next();
			boolean matches = false;
			if (re) {
				if (call.matches(pattern)) {
					matches = true;
				}
			} else if (call.startsWith(pattern)) {
				matches = true;
			}

			if (matches) {
				if (!already.contains(call)) {
					result.add(call);
					already.add(call);
				}
			}
		}
		return result;
	}

	/**
	 * Get list of vocabulary identifiers from the list of strings/calls.
	 */
	private List<Integer> getVocabularyIdentifierList(Set<String> list) {

		List<Integer> identifiers = new ArrayList<Integer>();

		if (list == null || list.size() == 0) {
			return identifiers;
		}

		Iterator<String> i = list.iterator();

		Set<Integer> already = new HashSet<Integer>();

		while (i.hasNext()) {
			String call = i.next();
			Integer id = this.getID(call);
			if (already.contains(id)) {
				continue;
			}
			identifiers.add(id);
		}
		return identifiers;
	}

}
