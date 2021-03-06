package com.googlecode.simpleret;

import javax.swing.JOptionPane;

import org.hibernate.cfg.Configuration;

import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.importer.TraceImporter;
import com.googlecode.simpleret.utilities.FileOrResource;
import com.googlecode.simpleret.viewer.Viewer;

public class RunWebStart {

	public static void main(String[] args) {

		Constants.setWebStartMode(true);

		Configuration cnf;

		try {
			cnf = Utilities.getConfiguration("/hibernate-webstart");
			cnf.setProperty("hibernate.hbm2ddl.auto", "create");
			HibernateUtility.getSessionFactory(cnf);

			FileOrResource resource = new FileOrResource("/example-webstart.txt", 2018L);
			TraceImporter ti = new TraceImporter();
			ti.processWebStart(resource, 1L);

		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Software Error: " + e, "", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		Viewer.main();
	}

}
