package com.googlecode.simpleret.importer;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.classic.Session;

import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.database.Vocabulary;

/**
 * Now we have a thread identifier, so we can select all calls of this thread.
 * 
 * Store each Vocabulary record (i.e. type of call) into the database.
 * 
 * @author (c) Igor O. Kovalchuk 2008, 2009.
 */
public class TraceFileReader2StoreVocabulary extends TraceFileReaderAbstract {

	static Logger logger = Logger.getLogger(
				TraceFileReader2StoreVocabulary.class );

	Long threadIdentifier = null;

	ThreadsData td = new ThreadsData();

	TraceFileReader2StoreVocabulary(Long threadID) {
		super();
		if (threadID == null)
			throw new RuntimeException("Thread identifier.");
		this.threadIdentifier = threadID;
	}

	@Override
	protected void beforeRead() {
		// does nothing;

		// Just for verify JDBC connection... to avoid time wasting if the database is not active.
		HibernateUtility.getSessionFactory().getCurrentSession();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processString(String[] values) {

		long threadID = Long.parseLong(values[2]);
		if (!threadIdentifier.equals(threadID)) {
			// Ignore other threads.
			return;
		}

		String call = values[4].trim();

		boolean ret = false;
		if (call.endsWith(".<<return>>()")) {
			ret = true;
		}

		// Collect information about trace, about its vocabulary;
		if (!ret) {
			td.addOperation(call);
		}
	}

	@Override
	protected void afterRead(boolean errors) {

		if (errors) {
			return;
		}

		Session session = HibernateUtility.getSessionFactory()
				.getCurrentSession();
		session.beginTransaction();

		String hqlDelete = "delete Vocabulary";
		session.createQuery(hqlDelete).executeUpdate();
		session.flush();

		hqlDelete = "delete Trace";
		session.createQuery(hqlDelete).executeUpdate();
		session.flush();

		session.getTransaction().commit();

		session = HibernateUtility.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Set<String> set = td.getOperations();
		Iterator<String> calls = set.iterator();

		// Store each Vocabulary record into the database.
		while (calls.hasNext()) {
			String call = calls.next();
			Vocabulary v = new Vocabulary();
			v.setWord(call);
			session.save(v);
		}

		session.getTransaction().commit();
	}

}
