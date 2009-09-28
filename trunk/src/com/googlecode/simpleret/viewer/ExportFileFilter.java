package com.googlecode.simpleret.viewer;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ExportFileFilter extends FileFilter {

	public final static int MODE_TXT = 0;
	public final static int MODE_HTML = 1;
	public final static int MODE_AMATERAS_UML = 2;
	
	private int mode = MODE_TXT;
	
	public ExportFileFilter(int mode) {
		this.mode = mode;
	}
	
	public String getDescription() {
		if (mode == MODE_HTML) {
			return "HTML files";
		} else if (mode == MODE_AMATERAS_UML) {
			return "Amateras SQD files";
		} else {
			return "Text files";
		}
	}

	public boolean accept(File f) {
		if (f.isFile()) {
			if (mode == MODE_HTML) {
				if (f.getName().endsWith(".html")) {
					return true;
				}
			} else if (mode == MODE_AMATERAS_UML) {
				if (f.getName().endsWith(".sqd")) {
					return true;
				}
			} else {
				if (f.getName().endsWith(".txt")) {
					return true;
				}
			}
		}
		
		if (f.isDirectory()) {
			return true;
		}
		
		return false;
	}

}
