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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.googlecode.simpleret.Constants;
import com.googlecode.simpleret.database.TraceMarker;

public class FrameInstruments {

	static Logger logger = Logger.getLogger(FrameInstruments.class);
	
	private final Object[] signatures = {"1","2","3"};
	private String signatureId = (String) signatures[0]; 
	
	private DataFilter dataFilter = new DataFilter();
	
	public static void main(String[] args) {
		Data data = new Data();
		( new FrameInstruments() ).show(data, null);
	}

	public void show(final Data data, Viewer viewer) {
		final JFrame frame = new JFrame(Constants.TITLE_INSTRUMENTS);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setAlwaysOnTop(false);
		
		Font font = new Font("Dialog", Font.PLAIN, 24);

		final JCheckBox itselfJCB = new JCheckBox();
		itselfJCB.setSelected(true);
		
		final JCheckBox parentsJCB = new JCheckBox();
		parentsJCB.setSelected(true);
		
		final JCheckBox subelementsJCB = new JCheckBox();
		subelementsJCB.setSelected(true);

		JLabel itselfJL = new JLabel("Include current element");
		JLabel parentsJL = new JLabel("Include parents");
		JLabel subelementsJL = new JLabel("Include sub-elements");
		
		JButton markJB = new JButton("Mark");
		markJB.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Set<String> list = getSignatures(data);
				if (list == null) {
					logger.warn("An empty list of signatures.");
					return;
				}
				Color colour = JColorChooser.showDialog(
						frame, "Colours", data.getTakenColourAsColour());
				if (colour == null) {
					logger.warn("A colour have not been selected.");
					return;
				}
				dataFilter.setItself(itselfJCB.isSelected());
				dataFilter.setParents(parentsJCB.isSelected());
				dataFilter.setSubelements(subelementsJCB.isSelected());
				TraceMarker marker = new TraceMarker(
					data, dataFilter, signatureId, colour);
				marker.colourise();
				frame.dispose();
			}
		});
		
		JButton cancelJB = new JButton("Cancel");
		cancelJB.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				frame.dispose();
			}
		});
		
		JButton cleanJB = new JButton("Clean colour");
		cleanJB.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Color colour = JColorChooser.showDialog(
						frame, "Colours", data.getTakenColourAsColour());
				if (colour == null) {
					logger.warn("A colour have not been selected.");
					return;
				}
				TraceMarker marker = new TraceMarker(
					data, null, null, colour);
				marker.colourReset();
				frame.dispose();
			}
		});
		
		JButton cleanAllJB = new JButton("Clean all");
		cleanAllJB.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int result = JOptionPane.showConfirmDialog(frame,
						"Clean all colours ?", "",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					TraceMarker marker = new TraceMarker(
							data, null, null, null);
					marker.allColoursReset();
					frame.dispose();
				}
			}
		});
		
		
		JLabel signaturesJL = new JLabel("Signatures:");
		final JComboBox signaturesJCB = new JComboBox(signatures);
		
		signaturesJCB.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (ActionEvent.ACTION_PERFORMED == event.getID()) {
					signatureId = (String) signatures[ signaturesJCB.getSelectedIndex() ];
				}
			}
		});
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		Insets insets = new Insets(3, 3, 3, 3);
		constraints.insets = insets;
		
		constraints.gridx = 1;
		constraints.gridy = 2;
		layout.setConstraints(itselfJCB, constraints);
		frame.add(itselfJCB);
		
		constraints.gridx = 1;
		constraints.gridy = 3;
		layout.setConstraints(parentsJCB, constraints);
		frame.add(parentsJCB);

		constraints.gridx = 1;
		constraints.gridy = 4;
		layout.setConstraints(subelementsJCB, constraints);
		frame.add(subelementsJCB);

		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.WEST;
		layout.setConstraints(itselfJL, constraints);
		frame.add(itselfJL);
		
		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.WEST; 
		layout.setConstraints(parentsJL, constraints);
		frame.add(parentsJL);

		constraints.gridx = 2;
		constraints.gridy = 4;
		constraints.anchor = GridBagConstraints.WEST;
		layout.setConstraints(subelementsJL, constraints);
		frame.add(subelementsJL);
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(signaturesJL, constraints);
		frame.add(signaturesJL);
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		signaturesJCB.setPreferredSize(new Dimension(150,40));
		layout.setConstraints(signaturesJCB, constraints);
		frame.add(signaturesJCB);
		constraints.gridwidth = 1;
		signaturesJCB.setFont(font);
				
		constraints.gridx = 3;
		constraints.gridy = 0;
		cleanJB.setPreferredSize(new Dimension(150,40));
		layout.setConstraints(cleanJB, constraints);
		frame.add(cleanJB);
		
		constraints.gridx = 3;
		constraints.gridy = 1;
		cleanAllJB.setPreferredSize(new Dimension(150,40));
		layout.setConstraints(cleanAllJB, constraints);
		frame.add(cleanAllJB);
		
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		markJB.setPreferredSize(new Dimension(150,40));
		layout.setConstraints(markJB, constraints);
		frame.add(markJB);
		constraints.gridwidth = 1;

		constraints.gridx = 3;
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		cancelJB.setPreferredSize(new Dimension(150,40));
		layout.setConstraints(cancelJB, constraints);
		frame.add(cancelJB);
		constraints.gridwidth = 1;
		
		frame.setPreferredSize(new Dimension(500,350));
		frame.pack();
		frame.setLocation(200,150);
		frame.setLayout(layout);
		frame.setVisible(true);
	}
	
	private Set<String> getSignatures(Data data) {
		return data.getSignatures().getSignaturesByListId(signatureId);
	}
	
}
