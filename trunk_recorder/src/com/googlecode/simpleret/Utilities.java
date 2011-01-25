package com.googlecode.simpleret;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.hibernate.cfg.Configuration;

public class Utilities {

	public static BufferedReader getResurceReader(String resource) {
		InputStream is = Utilities.class.getResourceAsStream(resource);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		return br;
	}

	/**
	 * 
	 * @param prefix
	 * 			a prefix for file, example &amp;/hibernate-test&amp;
	 * @return
	 * 		Configuration for HibernateUtility
	 */
	public static Configuration getConfiguration(String prefix) throws IOException {

		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		Configuration cnf = new Configuration();

		InputStream inStream = Utilities.class.getResourceAsStream(prefix
				+ ".properties");
		Properties properties = new Properties();
		properties.load(inStream);
		cnf.setProperties(properties);

		cnf.configure(prefix + ".cfg.xml");

		return cnf;
	}

	// Check - is it a supported OS ?
	// Return base directory in APPDATA.
	public static String getApplicationDataPath() {
		int system = 0;
		
		String os = System.getenv("OS");
		if (os != null) {
			os = os.toLowerCase();
			if (os.startsWith("windows")) {
				system = 1;
			}
		}
		
		if (system != 1) {
			throw new RuntimeException("Unsupported OS. " + System.getenv("OS"));
		}
		
		String appdata = System.getenv("APPDATA");
		
		if (appdata == null) {
			throw new RuntimeException("Can't find an environment variable APPDATA.");
		}
		
		appdata = ( appdata + "/simpleret/" ) . replace('\\', '/');
		File simpleret = new File(appdata);
		if ( ! simpleret.exists() ) {
			if ( ! simpleret.mkdir() ) {
				throw new RuntimeException("Can't create a directory: " + appdata);
			}
			System.out.println("Created a new directory: " + appdata);
		}
		return appdata;
	}

	public static void sleep(long millis) {
		try {
			System.out.println("Sleep: " + millis/1000 + " sec.");
			Thread.sleep(millis);
			System.out.println("Sleep - done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
