package com.googlecode.simpleret.importer;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class TraceImporterFileFilter extends FileFilter {

	public String getDescription() {
		return "Text files";
	}

	public boolean accept(File f) {
		if (f.isFile()) {
			if (f.getName().endsWith(".txt")) {
				return true;
			}
		}
		if (f.isDirectory()) {
			return true;
		}
		return false;
	}
}
