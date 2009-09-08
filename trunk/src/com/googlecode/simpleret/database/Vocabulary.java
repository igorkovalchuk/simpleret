package com.googlecode.simpleret.database;

/**
 * Represent an unique signature that taken from a program trace.
 * 
 * @author (c) Igor O. Kovalchuk 2008, 2009.
 */
public class Vocabulary {

	private Integer id = null;
	private String word = "";

	public Integer getId() {
		return id;
	}

	private void setId(Integer id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
