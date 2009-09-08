package com.googlecode.simpleret.test;

import org.hibernate.classic.Session;

import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.database.Trace;
import com.googlecode.simpleret.database.Vocabulary;

import junit.framework.TestCase;

/**
 * @author (c) Igor O. Kovalchuk 2008, 2009.
 */
public class DatabaseTest extends TestCase {

	private static Session session = null;

	protected void setUp() throws Exception {
		session = HibernateUtility.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	}

	protected void tearDown() throws Exception {
		session.getTransaction().rollback();
	}

	public void test001Vocabulary() {
		Vocabulary object = new Vocabulary();
		object.setWord("com.googlecode.Test");
		session.save(object);
		session.flush();
	}

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
