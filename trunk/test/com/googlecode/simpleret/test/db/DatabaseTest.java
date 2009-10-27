package com.googlecode.simpleret.test.db;

import org.hibernate.classic.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.database.Trace;
import com.googlecode.simpleret.database.Vocabulary;
import com.googlecode.simpleret.test.DatabaseTestBase;

public class DatabaseTest extends DatabaseTestBase {

	private static Session session = null;

	@Before
	public void before() throws Exception {
		session = HibernateUtility.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	}

	@After
	public void after() throws Exception {
		session.getTransaction().rollback();
	}

	@Test
	public void test001Vocabulary() {
		session = HibernateUtility.getSessionFactory().getCurrentSession();
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
