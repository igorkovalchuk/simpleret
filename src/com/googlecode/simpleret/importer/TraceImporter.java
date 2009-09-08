package com.googlecode.simpleret.importer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 * 1. Show the dialog.
 * 2. Select the file.
 * 3. Show an information about threads
 * stored in that file.
 * 4. Select an identifier of the thread (if there are
 * several threads).
 * 5. Store vocabulary of calls in the database.
 * 6. Store trace in the database.
 * 7. Create additional references in the database.
 *
 * @author (c) Igor O. Kovalchuk 2008, 2009.
 */
public class TraceImporter extends JPanel {

	private static final long serialVersionUID = -1;
	
	static Logger logger = Logger.getLogger(TraceImporter.class);
	
	final static int BATCH = 20;
	
	final static String TITLE = "Program Trace Reader";
	
	final static String SELECT_PLEASE = "Please select a file to read...";
	
	final static String STORE_TO_DATABASE = "Store to the database...";
	
	final static String SELECT_FILE = "Select a file...";
	final static String READ_FILE = "Read a file...";
	
	public TraceImporter(JFrame frame) {

		this.setLayout(new FlowLayout());

		final JFileChooser fileChooser = new JFileChooser();
		TraceImporterFileFilter ff = new TraceImporterFileFilter();
		fileChooser.addChoosableFileFilter(ff);

		final JTextField jtfFileName = new JTextField();
		jtfFileName.setSize(50, 1);
		jtfFileName.setText(SELECT_PLEASE);
		add(jtfFileName);

		// Button: Select a file.
		JButton buttonSelectFile = new JButton(SELECT_FILE);
		buttonSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int value = fileChooser.showOpenDialog(null);
				if (value == JFileChooser.APPROVE_OPTION) {
					jtfFileName.setText(fileChooser.getSelectedFile()
							.getPath());
				}
			}
		});

		final JTextArea textAreaForMessage = new JTextArea();
		textAreaForMessage.setRows(10);
		textAreaForMessage.setColumns(30);

		// Button: Read selected file.
		JButton buttonReadFile = new JButton(READ_FILE);
		buttonReadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String name = jtfFileName.getText();
				if (name.equals("") || name.equals(SELECT_PLEASE)) {
					return;
				}

				TraceFileReader1Initial reader = new TraceFileReader1Initial();
				reader.startProcessing(name);

				textAreaForMessage.setText(reader.getResult());
			}
		});

		final JTextField textFieldForThreadId = new JTextField();
		textFieldForThreadId.setColumns(10);

		JButton buttonStore = new JButton(STORE_TO_DATABASE);

		buttonStore.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String name = jtfFileName.getText();

				if (name.equals("") || name.equals(SELECT_PLEASE)) {
					return;
				}

				String threadIDstr = textFieldForThreadId.getText().trim();

				if (threadIDstr.equals(""))
					return;

				Long threadID = Long.parseLong(threadIDstr);

				logger.info("Create vocabulary ...");
				TraceFileReader2StoreVocabulary storeVocabulary = 
					new TraceFileReader2StoreVocabulary(threadID);
				storeVocabulary.startProcessing(name);

				logger.info("Vocabulary created...");
				logger.info("Store trace ...");
				ImportDataHolder dataHolder = new ImportDataHolder();
				TraceFileReader3StoreTrace storeTrace =
					new TraceFileReader3StoreTrace(dataHolder, threadID);
				storeTrace.startProcessing(name);

				logger.info("Trace stored...");
				logger.info("Set additional references...");
				TraceFileReader4SetAdditionalReferences references =
						new TraceFileReader4SetAdditionalReferences();
				references.storeReferences(dataHolder);
				logger.info("Normal exit.");
				System.exit(0);
			}
		});

		add(buttonSelectFile);
		add(buttonReadFile);
		add(textFieldForThreadId);
		add(textAreaForMessage);
		add(buttonStore);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new TraceImporter(frame));

		frame.setPreferredSize(new Dimension(500, 500));
		frame.pack();
		frame.setVisible(true);
	}

}
