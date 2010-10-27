package com.googlecode.simpleret.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;

import com.googlecode.simpleret.Constants;
import com.googlecode.simpleret.database.TraceMarker;

public class FrameInstruments {

	static Logger logger = Logger.getLogger(FrameInstruments.class);

	private final Object[] signatures = { "1", "2", "3" };
	private String signatureId = (String) signatures[0];

	private DataFilter dataFilter = new DataFilter();

	public static void main(String[] args) {
		Data data = new Data();
		(new FrameInstruments()).show(data, null);
	}

	public void show(final Data data, final Viewer viewer) {
		final JFrame frame = new JFrame(Constants.TITLE_INSTRUMENTS);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setAlwaysOnTop(false);

		Font font = new Font("Dialog", Font.PLAIN, 24);

		final JCheckBox itselfJCB = new JCheckBox();
		itselfJCB.setSelected(true);

		final JCheckBox parentsJCB = new JCheckBox();
		parentsJCB.setSelected(false);

		final JCheckBox subelementsJCB = new JCheckBox();
		subelementsJCB.setSelected(false);

		JLabel itselfJL = new JLabel("Include current element");
		JLabel parentsJL = new JLabel("Include parents");
		JLabel subelementsJL = new JLabel("Include sub-elements");

		final JRadioButton markJRB = new JRadioButton();
		markJRB.setSelected(true);

		final JRadioButton deleteJRB = new JRadioButton();
		deleteJRB.setSelected(false);

		final JRadioButton undeleteJRB = new JRadioButton();
		undeleteJRB.setSelected(false);

		ButtonGroup bg = new ButtonGroup();
		bg.add(markJRB);
		bg.add(deleteJRB);
		bg.add(undeleteJRB);

		JLabel markJL = new JLabel("Mark");
		JLabel deleteJL = new JLabel("Delete");
		JLabel undeleteJL = new JLabel("Undelete");

		JButton markJB = new JButton("Signatures");
		markJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Set<String> list = getSignatures(data);
				if (list == null) {
					logger.warn("An empty list of signatures.");
					return;
				}

				dataFilter.setMode(deleteJRB.isSelected(), undeleteJRB.isSelected());

				dataFilter.setItself(itselfJCB.isSelected());
				dataFilter.setParents(parentsJCB.isSelected());
				dataFilter.setSubelements(subelementsJCB.isSelected());

				if (dataFilter.isDelete()) {
					String warning = "";
					if (dataFilter.isParents()) {
						warning = "You can't delete parent elements.\n";
					}
					if (! dataFilter.isSubelements()) {
						warning += "You have to delete subelements too.";
					}
					if (! warning.equals("")) {
						JOptionPane.showMessageDialog(frame,
							warning, "", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}

				int result = JOptionPane.showConfirmDialog(frame,
					dataFilter.getMode("Mark") + " a list of signatures N " + signatureId + " ?", "",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if (result != JOptionPane.OK_OPTION) {
					return;
				}

				Color colour = null;

				if (dataFilter.isMark()) {
					colour = JColorChooser.showDialog(
							frame, "Colours", data.getTakenColourAsColour());
					if (colour == null) {
						logger.warn("A colour have not been selected.");
						return;
					}
				}

				final TraceMarker marker = new TraceMarker(data, dataFilter,
						signatureId, colour, viewer);

				frame.setEnabled(false);

				Thread c = new Thread(new Runnable() {
					public void run() {
						marker.mark();
					}
				});

				c.start();

				frame.dispose();
			}
		});

		JButton cancelJB = new JButton("Cancel");
		cancelJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				frame.dispose();
			}
		});

		JButton cleanJB = new JButton("By a selected color");
		cleanJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				Color colour = JColorChooser.showDialog(frame, "Colours", data
						.getTakenColourAsColour());
				if (colour == null) {
					logger.warn("A colour have not been selected.");
					return;
				}

				dataFilter.setMode(deleteJRB.isSelected(), undeleteJRB.isSelected());

				int result = JOptionPane.showConfirmDialog(frame,
					dataFilter.getMode("Clean") + " records of the selected color ?", "",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if (result != JOptionPane.OK_OPTION) {
					return;
				}

				TraceMarker marker = new TraceMarker(data, dataFilter, null, colour, viewer);
				marker.singleColour();
				frame.dispose();
			}
		});

		JButton cleanAllJB = new JButton("All records");
		cleanAllJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dataFilter.setMode(deleteJRB.isSelected(), undeleteJRB.isSelected());
				int result = JOptionPane.showConfirmDialog(frame,
					dataFilter.getMode("Clean all colors of") + " all records.", "",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if (result != JOptionPane.OK_OPTION) {
					return;
				}
				TraceMarker marker = new TraceMarker(data, dataFilter, null, null, viewer);
				marker.allColours();
				frame.dispose();
			}
		});

		JLabel signaturesJL = new JLabel("Signatures:");
		final JComboBox signaturesJCB = new JComboBox(signatures);

		signaturesJCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (ActionEvent.ACTION_PERFORMED == event.getID()) {
					signatureId = (String) signatures[signaturesJCB
							.getSelectedIndex()];
				}
			}
		});

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		Insets insets = new Insets(3, 3, 3, 3);
		constraints.insets = insets;

		constraints.gridx = 2;
		constraints.gridy = 2;
		layout.setConstraints(markJRB, constraints);
		frame.add(markJRB);

		constraints.gridx = 2;
		constraints.gridy = 3;
		layout.setConstraints(deleteJRB, constraints);
		frame.add(deleteJRB);

		constraints.gridx = 2;
		constraints.gridy = 4;
		layout.setConstraints(undeleteJRB, constraints);
		frame.add(undeleteJRB);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 3;
		constraints.gridy = 2;
		layout.setConstraints(markJL, constraints);
		frame.add(markJL);

		constraints.gridx = 3;
		constraints.gridy = 3;
		layout.setConstraints(deleteJL, constraints);
		frame.add(deleteJL);

		constraints.gridx = 3;
		constraints.gridy = 4;
		layout.setConstraints(undeleteJL, constraints);
		frame.add(undeleteJL);

		constraints.gridx = 4;
		constraints.gridy = 2;
		layout.setConstraints(itselfJCB, constraints);
		frame.add(itselfJCB);

		constraints.gridx = 4;
		constraints.gridy = 3;
		layout.setConstraints(parentsJCB, constraints);
		frame.add(parentsJCB);

		constraints.gridx = 4;
		constraints.gridy = 4;
		layout.setConstraints(subelementsJCB, constraints);
		frame.add(subelementsJCB);

		constraints.gridx = 5;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.WEST;
		layout.setConstraints(itselfJL, constraints);
		frame.add(itselfJL);

		constraints.gridx = 5;
		constraints.gridy = 3;
		layout.setConstraints(parentsJL, constraints);
		frame.add(parentsJL);

		constraints.gridx = 5;
		constraints.gridy = 4;
		layout.setConstraints(subelementsJL, constraints);
		frame.add(subelementsJL);

		JSeparator js1 = new JSeparator();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 6;
		layout.setConstraints(js1, constraints);
		js1.setPreferredSize(new Dimension(550, 5));
		frame.add(js1);
		constraints.gridwidth = 1;

		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(signaturesJL, constraints);
		frame.add(signaturesJL);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		signaturesJCB.setPreferredSize(new Dimension(150, 40));
		layout.setConstraints(signaturesJCB, constraints);
		frame.add(signaturesJCB);
		constraints.gridwidth = 1;
		signaturesJCB.setFont(font);

		JSeparator js2 = new JSeparator();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 6;
		layout.setConstraints(js2, constraints);
		js2.setPreferredSize(new Dimension(550, 5));
		frame.add(js2);
		constraints.gridwidth = 1;

		constraints.gridx = 2;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		cleanJB.setPreferredSize(new Dimension(150, 40));
		layout.setConstraints(cleanJB, constraints);
		frame.add(cleanJB);
		constraints.gridwidth = 1;

		constraints.gridx = 4;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		cleanAllJB.setPreferredSize(new Dimension(150, 40));
		layout.setConstraints(cleanAllJB, constraints);
		frame.add(cleanAllJB);
		constraints.gridwidth = 1;

		constraints.gridx = 1;
		constraints.gridy = 6;
		markJB.setPreferredSize(new Dimension(150, 40));
		layout.setConstraints(markJB, constraints);
		frame.add(markJB);

		JSeparator js3 = new JSeparator();
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = 6;
		layout.setConstraints(js3, constraints);
		js3.setPreferredSize(new Dimension(550, 5));
		frame.add(js3);
		constraints.gridwidth = 1;

		constraints.gridx = 4;
		constraints.gridy = 8;
		constraints.gridwidth = 2;
		cancelJB.setPreferredSize(new Dimension(150, 40));
		layout.setConstraints(cancelJB, constraints);
		frame.add(cancelJB);
		constraints.gridwidth = 1;

		frame.setPreferredSize(new Dimension(600, 450));
		frame.setResizable(false);
		frame.pack();
		frame.setLocation(200, 150);
		frame.setLayout(layout);
		frame.setVisible(true);
	}

	private Set<String> getSignatures(Data data) {
		return data.getSignatures().getSignaturesByListId(signatureId);
	}

}
