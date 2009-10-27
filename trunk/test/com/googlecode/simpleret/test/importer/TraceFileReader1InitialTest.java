package com.googlecode.simpleret.test.importer;

import java.io.BufferedReader;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.googlecode.simpleret.importer.ThreadsData;
import com.googlecode.simpleret.importer.TraceFileReader1Initial;
import com.googlecode.simpleret.test.TestUtilities;

public class TraceFileReader1InitialTest {

	private BufferedReader br;

	@Before
	public void before() {
		br = TestUtilities.getResurceReader("/files/test-trace-2.txt");
	}

	@After
	public void after() throws Exception {
		br.close();
	}

	@Test
	public void test() {
		TraceFileReader1Initial reader = new TraceFileReader1Initial();
		reader.startProcessing(br);

		Map <Long, ThreadsData> td = reader.getThreadsData();
		td = reader.getThreadsData();

		assertTrue(td.size() == 1);

		ThreadsData data = td.get(15L);
		assertTrue(data != null);

		assertEquals("Thread id = 15 ; call types: 2 ; calls: 4", data.getAsString());
	}

}
