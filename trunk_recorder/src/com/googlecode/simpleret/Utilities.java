package com.googlecode.simpleret;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utilities {

	public static BufferedReader getResurceReader(String resource) {
		InputStream is = Utilities.class.getResourceAsStream(resource);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		return br;
	}

	// Return base directory in APPDATA (OS Windows) or user.home (OS Linux).
	public static String getApplicationDataPath() {

		String os = System.getProperty("os.name");
		if (os != null) {
			os = os.toLowerCase();
		}

		String appdata = null;

		if (os.startsWith("windows")) {
			appdata = System.getenv("APPDATA");
		} else {
			// Probably, Linux OS;
			appdata = System.getProperty("user.home");
		}

		if (appdata == null) {
			throw new RuntimeException("Unknown error: APPDATA/user.home");
		}

		appdata = ( appdata + "/simpleret/" ) . replace('\\', '/');

		System.out.println("Looking in ... " + appdata + "\n");

		File simpleret = new File(appdata);
		if ( ! simpleret.exists() ) {
			if ( ! simpleret.mkdir() ) {
				throw new RuntimeException("Can't create a directory: " + appdata);
			}
			System.out.println("Created a new directory: " + appdata);
		}
		return appdata;
	}

}
