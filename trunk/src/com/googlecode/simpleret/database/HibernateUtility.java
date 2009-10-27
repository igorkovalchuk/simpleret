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

	private static SessionFactory sessionFactory;

	/**
	 * Get SessionFactory. Default Configuration - hibernate.cfg.xml
	 * 
	 * @return - object of org.hibernate.SessionFactory
	 */
	public static synchronized SessionFactory getSessionFactory() {

		if (sessionFactory == null) {
			try {
				// Create the SessionFactory from hibernate.cfg.xml
				Configuration cnf = new Configuration();
				cnf.configure();
				sessionFactory = cnf.buildSessionFactory();
			} catch (Throwable ex) {
				// Make sure you log the exception, as it might be swallowed
				logger.error("Initial SessionFactory creation failed ... " + ex);
				throw new ExceptionInInitializerError(ex);
			}
		}

		return sessionFactory;
	}

	/**
	 * Get SessionFactory. Custom Configuration.
	 * @return - object of org.hibernate.SessionFactory
	 */
	public static synchronized SessionFactory getSessionFactory(Configuration configuration) {

		if (sessionFactory == null) {
			try {
				sessionFactory = configuration.buildSessionFactory();
			} catch (Throwable ex) {
				logger.error("Initial SessionFactory creation failed ... " + ex);
				throw new ExceptionInInitializerError(ex);
			}
		}

		return sessionFactory;
	}

	/**
	 * Set SessionFactory singleton = null.
	 */
	public static synchronized void reset() {
		sessionFactory = null;
	}

}