package com.googlecode.simpleret.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.googlecode.simpleret.importer.TraceFileReaderAbstractTest;
import com.googlecode.simpleret.test.db.DatabaseTest;
import com.googlecode.simpleret.test.recorder.RecorderTest;

@RunWith(Suite.class)
@SuiteClasses(
		{
			RecorderTest.class,
			DatabaseTest.class,
			TraceFileReaderAbstractTest.class
		}
)
public class All {	
}
