package com.googlecode.simpleret.database;

/**
 * Represent an unique signature that taken from a program trace.
 */
public class Vocabulary {

	/**
	 * An id of word in the database table.
	 */
	private Integer id = null;

	/**
	 * A string like this: "package.class.method()".
	 */
	private String word = "";

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
