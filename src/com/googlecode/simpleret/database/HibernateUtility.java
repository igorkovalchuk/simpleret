package com.googlecode.simpleret.database;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Just to initialize and to take cached instance of
 * org.hibernate.SessionFactory.
 * 
 * See: hibernate.org , Reference Documentation ,
 * The first Hibernate Application.
 */
public class HibernateUtility {

	static Logger logger = 
		Logger.getLogger(HibernateUtility.class.getPackage().getName());

	private static final SessionFactory sessionFactory;

	static {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = new Configuration().configure()
					.buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			logger.error("Initial SessionFactory creation failed ... " + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * @return - object of org.hibernate.SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}