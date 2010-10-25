package com.googlecode.simpleret.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;

public class TestUtilities {

	/*
	 * @return
	 * 		Configuration for HibernateUtility
	 * 		- hibernate-test.cfg.xml, hibernate-test.properties
	 *
	public static Configuration getConfiguration() throws IOException {
		Configuration cnf = getConfiguration("/hibernate-test");
		return cnf;
	}
	*/

	public static IDatabaseTester getDatabaseTester(String prefix) {

		IDatabaseTester tester = null;

		try {
			InputStream inStream = TestUtilities.class.getResourceAsStream(prefix
					+ ".properties");
			Properties properties = new Properties();
			properties.load(inStream);

			// TODO Optimize properties loading.
			tester = new JdbcDatabaseTester(
				properties.getProperty("hibernate.connection.driver_class"),
				properties.getProperty("hibernate.connection.url"),
				properties.getProperty("hibernate.connection.username"),
				properties.getProperty("hibernate.connection.password")
			);

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return tester;
	}

}
