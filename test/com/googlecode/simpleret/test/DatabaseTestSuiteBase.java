package com.googlecode.simpleret.test;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.junit.AfterClass;

import com.googlecode.simpleret.database.HibernateUtility;

/**
 * Use this as a base class for a DBUnit test suite.
 */
abstract public class DatabaseTestSuiteBase extends DatabaseTestBase {

	@AfterClass
	public static void afterClass() {
		Session s = HibernateUtility.getSessionFactory().getCurrentSession();
		Transaction t = s.getTransaction();
		if (t.isActive()) {
			System.err.println("Warning. Active transaction has been detected.");
			t.rollback();
		}

		HibernateUtility.reset();
	}

}
