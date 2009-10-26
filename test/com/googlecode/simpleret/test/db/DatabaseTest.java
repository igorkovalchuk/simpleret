package com.googlecode.simpleret.test.db;

import org.hibernate.classic.Session;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.database.Trace;
import com.googlecode.simpleret.database.Vocabulary;

public class DatabaseTest {

	private static Session session = null;

	@BeforeClass
	public static void before() throws Exception {
		session = HibernateUtility.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	}

	@AfterClass
	public static void after() throws Exception {
		session.getTransaction().rollback();
	}

	@Test
	public void test001Vocabulary() {
		Vocabulary object = new Vocabulary();
		object.setWord("com.googlecode.Test");
		session.save(object);
		session.flush();
	}

	@Test
	public void test002Trace() {
		Trace object = new Trace();
		object.setId(100);
		object.setStartId(2123456789);
		object.setEndId(0);
		object.setParentId(1);
		object.setReturn(false);
		object.setVocabularyId(1000);
		object.setLevel(2);
		object.setColourMarker(0);
		session.save(object);
		session.flush();
	}

}
