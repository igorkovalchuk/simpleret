package com.googlecode.simpleret.viewer.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class FrameAbout extends JFrame {

	private static FrameAbout itself = null;

	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPane = null;
	private JButton jButton = null;
	private JEditorPane jEditorPane = null;

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();

			jScrollPane.setPreferredSize(new Dimension(700, 400));

			jScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setBorder(null);
			jScrollPane.setViewportView(getJEditorPane());
			jScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Close");

			jButton.setPreferredSize(new Dimension(100, 30));

			jButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					FrameAbout.this.done();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jEditorPane
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getJEditorPane() {
		if (jEditorPane == null) {
			jEditorPane = new JEditorPane();
			jEditorPane.setContentType("text/html");
			jEditorPane.setEditable(false);

			InputStream is = FrameAbout.class.getResourceAsStream("/help.html");

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String s;
			StringBuffer sb = new StringBuffer();

			try {

				while ((s = br.readLine()) != null) {
					sb.append(s);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			jEditorPane.setText(sb.toString());
		}

		return jEditorPane;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FrameAbout.getInstance();
	}

	/**
	 * This is the default constructor
	 */
	private FrameAbout() {
		super();
		initialize();
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				FrameAbout.this.done();
			}

		});

		this.setLocation(50, 50);
		this.getJScrollPane().getVerticalScrollBar().setValue(0);
		this.setVisible(true);
	}

	synchronized static public void getInstance() {
		if (itself == null) {
			itself = new FrameAbout();
		}
	}

	private void done() {
		this.dispose();
		itself = null;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(700, 500);
		this.setTitle("About");

		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		this.add(getJScrollPane(), BorderLayout.PAGE_START);

		JPanel jp2 = new JPanel();
		jp2.setMaximumSize(new Dimension(100, 100));
		jp2.add(getJButton());

		this.add(jp2, BorderLayout.PAGE_END);
	}

}
