package com.googlecode.simpleret.recorder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.googlecode.simpleret.recorder.ThreadData;

public class Configuration {

	private boolean enabled = false;

	private boolean screen = false;

	private boolean filtering = false;
	
	private Set<String> signatures;
	private Set<String> signaturesRe;

	/**
	 * For everything that starts like this '[some mode]' in configuration file.
	 */
	private Set<String> modes;

	private Map<Long, ThreadData> threads = new HashMap<Long, ThreadData>();

	private String base = "c:/Projects/";
	private File inputFile = new File(base + "trace.cfg.test.txt");
	private File outputFile = new File(base + "trace.out.test.txt");
	private BufferedWriter fileWriter = null;

	// last modification time of configuration file.
	private long modificationTime = 0;

	public Configuration() {

	}

	public void initialize() {

		synchronized (Configuration.class) {

			try {
				long lastModified = inputFile.lastModified();
				if (lastModified == modificationTime) {
					return;
				}
				modificationTime = lastModified;

				BufferedReader in = new BufferedReader(
						new FileReader(inputFile));

				String string;

				signatures = new HashSet<String>();
				signaturesRe = new HashSet<String>();
				modes = new HashSet<String>();

				while ((string = in.readLine()) != null) {
					string = string.trim();
					if (string.startsWith("#")) {
						// COMMENTS;
						continue; // go to the next string;
					} else if (string.startsWith("[")) {
						// A CONFIGURATION [key]:value
						String[] parameters = string.split(":");
						//String value = "";
						if (parameters.length > 1) {
							string = parameters[0].trim();
							//value = parameters[1].trim();
						}
						modes.add(string);

						if (string.equals("[disabled]")) {
							break;
						}

					} else if (string.startsWith("^")) {
						// A REGULAR EXPRESSION;
						signaturesRe.add(string);
					} else {
						if (!string.equals("")) {
							signatures.add(string);
						}
					}
				}

				in.close();

				if (modes.contains("[disabled]")) {
					if (enabled) {
						System.out.println("Trace recording has been enabled.");
						fileWriter.write("# {disabled}{"
								+ (new Date()).toString() + "}");
						fileWriter.newLine();
						fileWriter.flush();
						fileWriter.close();
						fileWriter = null;
					}
					enabled = false;
					return;
				} else {
					if (!enabled) {
						System.out
								.println("Trace recording has been disabled.");
						try {
							// Create a new file, or use existing;
							if (!outputFile.exists()) {
								outputFile.createNewFile();
							}
							fileWriter = new BufferedWriter(new FileWriter(
									outputFile, true));
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						fileWriter.write("{# enabled}{"
								+ (new Date()).toString() + "}");
						fileWriter.newLine();
						fileWriter.flush();
					}
					enabled = true;
				}
				
				if (modes.contains("[filter]")) {
					filtering = true;
				} else {
					filtering = false; // default
					Collection<ThreadData> set = threads.values();
					Iterator<ThreadData> it = set.iterator();
					while (it.hasNext()) {
						ThreadData td = (ThreadData) it.next();
						// ignore the filtering, if configuration has been changed;
						td.resetFiltering();
					}
				}
				
				if (modes.contains("[screen]")) {
					screen = true;
				} else {
					screen = false;
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

	}

	public ThreadData getThreadData(long currentThreadID) {
		ThreadData data = (ThreadData) threads.get(currentThreadID);
		if (data == null) {
			data = new ThreadData(currentThreadID);
			threads.put(currentThreadID, data);
		}
		return data;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isFiltering() {
		return filtering;
	}

	public boolean isScreen() {
		return screen;
	}

	public Set<String> getSignatures() {
		return signatures;
	}

	public Set<String> getSignaturesRe() {
		return signaturesRe;
	}

	public boolean contains (Signature signature) {
		String name = signature.getStamp();
		
		if (signatures.contains(name)) {
			return true;
		}
		
		Iterator<String> iterator = signatures.iterator();
		
		String mask;

		while (iterator.hasNext()) {
			mask = (String) iterator.next();
			if (name.startsWith(mask)) {				
				return true;
			}
		}

		iterator = signaturesRe.iterator();
		while (iterator.hasNext()) {
			mask = iterator.next();
			if (name.matches(mask)) {
				return true;
			}
		}
		
		return false;
	}
	
}
