package com.googlecode.simpleret.utilities;

import java.io.BufferedReader;

public class FileOrResource {

	private String nameOfFile = null;
	private BufferedReader reader = null;
	private boolean resource = false;

	public FileOrResource(String nameOfFile) {
		this.nameOfFile = nameOfFile;
	}

	public FileOrResource(BufferedReader reader) {
		this.reader = reader;
		this.resource = true;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public String getNameOfFile() {
		return nameOfFile;
	}

	public void setNameOfFile(String nameOfFile) {
		this.nameOfFile = nameOfFile;
	}

	public boolean isResource() {
		return resource;
	}

}
