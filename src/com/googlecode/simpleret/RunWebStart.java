package com.googlecode.simpleret;

import javax.swing.JOptionPane;

import org.hibernate.cfg.Configuration;

import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.importer.TraceImporter;
import com.googlecode.simpleret.viewer.Viewer;

public class RunWebStart {

	public static void main(String[] args) {
		System.getProperties().setProperty("simpleret.test", "true");

		Configuration cnf;

		try {
			cnf = Utilities.getConfiguration("/hibernate-webstart");
			cnf.setProperty("hibernate.hbm2ddl.auto", "create");
			HibernateUtility.getSessionFactory(cnf);

			TraceImporter ti = new TraceImporter();
			ti.process();

		} catch (Throwable e) {
			JOptionPane.showMessageDialog(null, "Software Error: " + e, "", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		Viewer.main();
	}

}
