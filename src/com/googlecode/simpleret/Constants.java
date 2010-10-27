package com.googlecode.simpleret;

public class Constants {

	public final static String NBSP = "&nbsp;";

	public final static int PAGE_LENGTH = 30;
	
	public final static int VIEWER_WIDTH = 950;
	public final static int VIEWER_HEIGHT = 700;

	public final static String TITLE_LEVEL = "Level";
	public final static String BUTTON_LEVEL = "OK";
	
	public final static String TITLE_VIEWER = "Simple Reverse Engineering Tool - Viewer";
	public final static String TITLE_INSTRUMENTS = "Simple Reverse Engineering Tool - Instruments";
	public final static String TITLE_RANGE = "Input a range of identifiers to display.";
	
	public final static int SQL_CAPACITY = 500;
	
	public final static int PROGRESS_MAX = 1000; 

	private static boolean webStartMode = false;

	public static boolean isWebStartMode() {
		return webStartMode;
	}

	public static void setWebStartMode(boolean value) {
		Constants.webStartMode = value;
	}

}
