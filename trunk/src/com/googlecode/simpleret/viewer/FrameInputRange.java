package com.googlecode.simpleret.viewer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.googlecode.simpleret.Constants;

public class FrameInputRange {

	static Logger logger = Logger.getLogger(FrameInputRange.class);
	
	JFrame frame = new JFrame(Constants.TITLE_RANGE);
	private JTextField jtfFrom = new JTextField();
	private JTextField jtfTo = new JTextField();
	
	private Data data = null;
	private Viewer viewer = null;
	
	public static void main(String[] args) {
		Data data = new Data();
		data.setRangeFrom(10);
		data.setRangeTo(20);
		( new FrameInputRange() ).input(data, null);
	}
	
	public void input(Data data, Viewer viewer) {
		this.data = data;
		this.viewer = viewer;
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setAlwaysOnTop(false);
		frame.setResizable(false);
		
		Font font = new Font("Dialog", Font.PLAIN, 24);
		
		jtfFrom.setFont(font);
		jtfFrom.setPreferredSize(new Dimension(100,40));
		if (data.getRangeFrom() != null)
			jtfFrom.setText(String.valueOf(data.getRangeFrom()));
		
		jtfTo.setFont(font);
		jtfTo.setPreferredSize(new Dimension(100,40));
		if (data.getRangeTo() != null)
			jtfTo.setText(String.valueOf(data.getRangeTo()));
		
		JButton jbOk = new JButton("Ok");
		jbOk.setPreferredSize(new Dimension(80,40));
		jbOk.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				done();
			}
		});
		
		JButton jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(80,40));
		jbCancel.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				frame.dispose();
			}
		});
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		Insets insets = new Insets(3, 3, 3, 3);
		constraints.insets = insets;
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(jtfFrom, constraints);
		frame.add(jtfFrom);
		
		constraints.gridx = 1;
		layout.setConstraints(jtfTo, constraints);
		frame.add(jtfTo);
		
		constraints.gridx = 2;
		layout.setConstraints(jbOk, constraints);
		frame.add(jbOk);

		constraints.gridx = 3;
		layout.setConstraints(jbCancel, constraints);
		frame.add(jbCancel);

		frame.setPreferredSize(new Dimension(420,100));
		frame.pack();
		frame.setLocation(250,200);
		frame.setLayout(layout);
		frame.setVisible(true);
	}

	public void done() {
		String text = jtfFrom.getText();
		Integer result = null;
		try {
			result = Integer.valueOf(text);
		} catch (NumberFormatException e) {
		}
		data.setRangeFrom(result);
		
		text = jtfTo.getText();
		result = null;
		try {
			result = Integer.valueOf(text);
		} catch (NumberFormatException e) {
		}
		data.setRangeTo(result);
		data.correctRange();

		frame.dispose();
		
		if (viewer != null)
			viewer.showSelection();
	}
	
}
