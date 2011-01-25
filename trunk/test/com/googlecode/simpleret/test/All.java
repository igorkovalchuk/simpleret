package com.googlecode.simpleret.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.googlecode.simpleret.test.importer.TraceFileReader1InitialTest;
import com.googlecode.simpleret.test.importer.TraceFileReader2StoreVocabularyTest;
import com.googlecode.simpleret.test.importer.TraceFileReaderAbstractTest;
import com.googlecode.simpleret.test.db.DatabaseTest;

@RunWith(Suite.class)
@SuiteClasses(
		{
			DatabaseTest.class,
			TraceFileReaderAbstractTest.class,
			TraceFileReader1InitialTest.class,
			TraceFileReader2StoreVocabularyTest.class
		}
)
public class All extends DatabaseTestSuiteBase {

	public static final String CONFIGURATION = "/hibernate-test";

}
