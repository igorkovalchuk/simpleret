package com.googlecode.simpleret.recorder;

import java.io.BufferedReader;

import org.junit.Test;

import static org.junit.Assert.*;

import com.googlecode.simpleret.Utilities;
import com.googlecode.simpleret.recorder.Configuration;
import com.googlecode.simpleret.recorder.Recorder;
import com.googlecode.simpleret.recorder.Signature;

public class RecorderTest {

	@Test
	public void testTraceRecorder() throws Exception {

		Configuration c = new Configuration();
		BufferedReader reader = Utilities.getResurceReader("/files/cfg/trace.cfg.test.txt");
		c.setInputReader(reader);
		c.setTesting(true);
		c.initialize();

		Recorder recorder = new Recorder(c);

		recorder.trace(new Signature("com.example.Alpha", "one", 1L, true));

		recorder.trace(new Signature("com.example.Alpha", "one", 2L, true));
		recorder.trace(new Signature("com.example.Alpha", "two", 2L, true));
		recorder.trace(new Signature("com.example.Alpha", "three", 2L, true));
		recorder.trace(new Signature("com.example.Alpha", "four", 2L, true));

		recorder.trace(new Signature("com.example.Alpha", "two", 1L, true));
		recorder.trace(new Signature("com.example.Alpha", "three", 1L, true));
		recorder.trace(new Signature("com.example.Alpha", "four", 1L, true));

		recorder.trace(new Signature("com.example.Alpha", "four", 1L, false));
		recorder.trace(new Signature("com.example.Alpha", "three", 1L, false));
		recorder.trace(new Signature("com.example.Alpha", "two", 1L, false));

		recorder.trace(new Signature("com.example.Alpha", "four", 2L, false));
		recorder.trace(new Signature("com.example.Alpha", "three", 2L, false));
		recorder.trace(new Signature("com.example.Alpha", "two", 2L, false));
		recorder.trace(new Signature("com.example.Alpha", "one", 2L, false));

		recorder.trace(new Signature("com.example.Alpha", "one", 1L, false));

		String actual = c.getFileWriter().toString();

		String expected = "\n" 
				// id 
				//      level
				//          thread id
				//              time
				+ "1	1	1	0	  com.example.Alpha.one()\n"
				+ "2	1	2	0	  com.example.Alpha.one()\n"
				+ "3	2	2	0	    com.example.Alpha.two()\n"
				+ "4	3	2	0	      com.example.Alpha.three()\n"
				+ "5	4	2	0	        com.example.Alpha.four()\n"
				+ "6	2	1	0	    com.example.Alpha.two()\n"
				+ "7	3	1	0	      com.example.Alpha.three()\n"
				+ "8	4	1	0	        com.example.Alpha.four()\n"
				+ "9	4	1	0	        com.example.Alpha.<<return>>()\n"
				+ "10	3	1	0	      com.example.Alpha.<<return>>()\n"
				+ "11	2	1	0	    com.example.Alpha.<<return>>()\n"
				+ "12	4	2	0	        com.example.Alpha.<<return>>()\n"
				+ "13	3	2	0	      com.example.Alpha.<<return>>()\n"
				+ "14	2	2	0	    com.example.Alpha.<<return>>()\n"
				+ "15	1	2	0	  com.example.Alpha.<<return>>()\n"
				+ "16	1	1	0	  com.example.Alpha.<<return>>()\n";

		assertTrue(actual.endsWith(expected));

	}

}
