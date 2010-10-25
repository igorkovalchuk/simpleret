package com.googlecode.simpleret.utilities;

import java.io.BufferedReader;

import com.googlecode.simpleret.Utilities;

public class FileOrResource {

	private String nameOfFile = null;
	private String pathToResource = null;
	private BufferedReader reader = null;
	private long sizeOfFile = 0;
	private boolean resource = false;

	public FileOrResource(String nameOfFile) {
		this.nameOfFile = nameOfFile;
	}

	public FileOrResource(String pathToResource, long sizeOfFile) {
		this.pathToResource = pathToResource;
		this.sizeOfFile = sizeOfFile;
		this.resource = true;
	}

	public BufferedReader getReader() {
		BufferedReader reader = Utilities.getResurceReader(pathToResource);
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

	public long getSizeOfFile() {
		return sizeOfFile;
	}

	public void setSizeOfFile(long sizeOfFile) {
		this.sizeOfFile = sizeOfFile;
	}

	public void setResource(boolean resource) {
		this.resource = resource;
	}

}
