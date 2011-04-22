package com.googlecode.simpleret.recorder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.logging.Logger;

import com.googlecode.simpleret.Utilities;
import com.googlecode.simpleret.recorder.ThreadData;


public class Configuration {

	static Logger logger = Logger.getLogger(Configuration.class.getSimpleName());
	
	private boolean enabled = false;

	private boolean screen = false;
	
	// include only these signatures;
	private Set<String> initial;
	
	private Set<String> runtime;
	
	// signatures to exclude;
	private Set<String> signatures;
	
	// signatures to exclude;
	private Set<String> signaturesRe;

	/**
	 * For everything that starts like this '[some mode]' in configuration file.
	 */
	private Set<String> modes;

	private Map<Long, ThreadData> threads = new HashMap<Long, ThreadData>();

	private String base = null;
	private File inputFile;
	private BufferedReader reader;
	
	private String outputFileName = null;
	private File outputFile;

	private Writer fileWriter = null;

	// last modification time of configuration file.
	private long modificationTime = -1;

	public Configuration() {
		this.base = Utilities.getApplicationDataPath();
	}

	public void setInputFile(String path) {
		System.out.println("Input file: " + path + "\n");
		inputFile = new File(base + path);
	}

	/**
	 * Especially for tests. Use it instead of setInputFile(String)
	 */
	public void setInputReader(BufferedReader reader) {
		this.reader = reader;
	}

	private boolean testing = false;

	/**
	 * @return
	 * 		BufferedReader or null if we don't need an initialization.
	 * 
	 * @throws FileNotFoundException
	 */
	private BufferedReader getFileReader() throws FileNotFoundException {
		BufferedReader result = null;
		if (testing) {
			result = reader;
		} else {
			long lastModified = inputFile.lastModified();
			if (lastModified != modificationTime) {
				modificationTime = lastModified;
				result = new BufferedReader(new FileReader(inputFile));
			}
		}
		return result;
	}


	public void initialize() {

		synchronized (Configuration.class) {

			try {

				BufferedReader in = getFileReader();
				if (in == null) {
					return;
				}

				logger.info("Trace recorder initialization");

				String string;

				initial = new HashSet<String>();
				runtime = new HashSet<String>();
				signatures = new HashSet<String>();
				signaturesRe = new HashSet<String>();
				
				modes = new HashSet<String>();

				while ((string = in.readLine()) != null) {
					string = string.trim();
					
					//logger.debug("reading [" + string + "]");
					
					if (string.startsWith("#")) {
						// COMMENTS;
						continue; // go to the next string;
					} else if (string.startsWith("[")) {
						// A CONFIGURATION [key]:value
						String[] parameters = string.split(":");
						String value = "";
						if (parameters.length > 1) {
							string = parameters[0].trim();
							value = parameters[1].trim();
						}
						if ("".equals(value)) {
							// Some mode;
							modes.add(string);
						} else {
							// Some value;
							if ("[include]".equals(string)) {
								initial.add(value);
								logger.info("include [" + value + "]");
							} else if ("[runtime]".equals(string)) {
								runtime.add(value);
								logger.info("runtime [" + value + "]");
							} else if ("[file]".equals(string)) {
								
								if (! value.equals(outputFileName)) {
									logger.info("file [" + value + "]");
									this.closeFile();
									outputFileName = value;
									outputFile = new File(base + value);
									this.openFile();
								}
								
							}
						}

						if (string.equals("[stop]")) {
							break;
						}

					} else if (string.startsWith("^")) {
						// A REGULAR EXPRESSION;
						signaturesRe.add(string);
						logger.info("signature RE [" + string + "]");
					} else {
						if (!string.equals("")) {
							signatures.add(string);
							logger.info("signature [" + string + "]");
						}
					}
				}

				in.close();
				if (testing) {
					// initialize only once;
					reader = null;
				}

				if (signatures.size() == 0) {
					signatures = null;
				}
				
				if (signaturesRe.size() == 0) {
					signaturesRe = null;
				}
				
				if (modes.contains("[enabled]")) {
					if (! enabled) {
						logger.info("Trace recording has been enabled.");

						this.openFile();

						fileWriter.write("# {enabled}{"
								+ (new Date()).toString() + "}\n");
						fileWriter.flush();
						enabled = true;
					}
				} else {
					if (enabled) {
						logger.info("Trace recording has been disabled.");
						fileWriter.write("{# disabled}{"
								+ (new Date()).toString() + "}\n");
						fileWriter.flush();
						this.closeFile();
						enabled = false;
					}
					return;
				}
				
				if (modes.contains("[display]")) {
					screen = true;
					logger.info("Display tracing.");
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

	public boolean isScreen() {
		return screen;
	}

	public Set<String> getSignatures() {
		return signatures;
	}

	public Set<String> getSignaturesRe() {
		return signaturesRe;
	}

	public boolean containsRuntimeFilter(Signature signature) {
		String name = signature.getClassName();

		Iterator<String> iterator;
		String mask;

		int size = runtime.size();

		if (size == 0)
			return true; // allow all signatures;

		if (true) { 

			iterator = runtime.iterator();

			while (iterator.hasNext()) {
				mask = (String) iterator.next();
				if (name.startsWith(mask)) {				
					return true;
				}
			}
		}

		return false;
	}

	public boolean contains (Signature signature) {
		String name = signature.getStamp();
		
		Iterator<String> iterator;
		String mask;
		
		if (signatures != null) { 

			if ( signatures.contains(name)) {
				return true;
			}

			iterator = signatures.iterator();

			while (iterator.hasNext()) {
				mask = (String) iterator.next();
				if (name.startsWith(mask)) {				
					return true;
				}
			}

		}

		if (signaturesRe != null) {
			iterator = signaturesRe.iterator();
			while (iterator.hasNext()) {
				mask = iterator.next();
				if (name.matches(mask)) {
					return true;
				}
			}
		}

		return false;
	}

	private void openFile() {

		try {
			if (testing || (outputFile == null)) {
				fileWriter = new StringWriter();
			} else {
				// Create a new file, or use existing;
				if (!outputFile.exists()) {
					outputFile.createNewFile();
				}
				fileWriter = new BufferedWriter(new FileWriter(
					outputFile, true));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void closeFile() {
		try {
			if (fileWriter != null) {
				fileWriter.close();
				fileWriter = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	String[] arrayOfStrings = {};
	public String[] getIncludeSignatures() {
		return (String []) initial.toArray(arrayOfStrings);
	}

	public Writer getFileWriter() {
		return fileWriter;
	}

	public void setTesting(boolean testing) {
		this.testing = testing;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

}
