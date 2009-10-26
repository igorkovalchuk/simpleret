package com.googlecode.simpleret.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

import com.googlecode.simpleret.importer.TraceFileReaderAbstract;

public class TraceFileReaderAbstractTest {

	@Test
	public void testAbstract() {

		final List<String[]> list = new ArrayList<String[]>();

		TraceFileReaderAbstract reader = new TraceFileReaderAbstract() {
			protected void beforeRead() {
				list.add(new String[] { "before" });
			}

			protected void processString(String[] values) {
				list.add(values);
			}

			protected void afterRead(boolean errors) {
				list.add(new String[] { "after" });
			}

		};

		String resource = "/files/test-trace-1.txt";

		URL url = TraceFileReaderAbstract.class.getResource(resource);
		String path = url.getPath();
		File file = new File(path);
		long size = file.length();
		size = size != 0 ? size : 1; // a dummy value;

		InputStream is = TraceFileReaderAbstract.class
				.getResourceAsStream(resource);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		reader.startProcessing(br, size);

		assertEquals(26, list.size());

		String actual;

		actual = ( list.get(0) )[0];
		assertEquals("before", actual);

		List<String> expectedList = Arrays.asList( new String[] {"4","2","1","1",
			"    com.googlecode.simpleret.example.Alpha.start()"} );
		List<String> actualList = Arrays.asList(list.get(4));
		assertEquals(expectedList, actualList);

		actual = ( list.get(25) )[0];
		assertEquals("after", actual);

		// printArray(list.toArray());
	}



	/*
	// Prints an array of arrays.
	private void printArray(Object[] data) {
		int len1 = data.length - 1;
		String[] current;
		int len2;
		for (int i = 0; i <= len1; i++) {
			current = (String[]) data[i];
			len2 = current.length - 1;
			System.out.print("{");
			for (int j = 0; j <= len2; j++) {
				System.out.print("\"" + current[j] + "\"");
				if (j < len2) {
					System.out.print(",");
				}
			}
			System.out.println("}");
			if (i < len1) {
				System.out.print(",");
			}
		}
	}
	*/

}
