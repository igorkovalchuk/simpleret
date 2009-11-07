package com.googlecode.simpleret;

import com.googlecode.simpleret.importer.TraceImporter;

public class RunImport {

	public static void main(String[] args) {
		TraceImporter importer = new TraceImporter();
		importer.process();
	}

}
