package com.googlecode.simpleret.viewer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.googlecode.simpleret.Constants;

public class ListOfSignatures {

	private Map<String, Set<String>> signatures = new HashMap<String, Set<String>>();

	public void showList(final Data data, final String listID) {

		Set<String> set = signatures.get(listID);
		StringBuffer listToChange = new StringBuffer();
		if (set != null) {
			Iterator<String> i = set.iterator();
			while (i.hasNext()) {
				listToChange.append(i.next()).append("\n");
			}
		}

		final JFrame frame = new JFrame("Selection List: " + listID);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new FlowLayout());

		final JTextArea area = new JTextArea();
		area.setColumns(80);
		area.setRows(15);
		area.setText(listToChange.toString());
		JScrollPane jsp = new JScrollPane(area);

		JButton b1 = new JButton("Save");
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Set<String> newSet = new LinkedHashSet<String>();
				signatures.put(listID, newSet);

				String text = area.getText();
				String[] list = text.split("\n");
				for (int i = 0; i < list.length; i++) {
					if (list[i].equals(""))
						continue;
					newSet.add(list[i]);
				}
				showSelectionListResult(data, listID);
				frame.dispose();
			}
		});

		JButton b2 = new JButton("Cancel");
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});

		frame.add(jsp);
		frame.add(b1);
		frame.add(b2);

		if (!Constants.isWebStartMode()) {
			frame.setAlwaysOnTop(true);
		}
		frame.setLocation(25,100);
		frame.setPreferredSize(new Dimension(950, 350));
		frame.pack();
		frame.setVisible(true);
	}

	private void showSelectionListResult(Data data, String listID) {
		StringBuffer listToShow = new StringBuffer();
		Set<String> selectionData = signatures.get(listID);
		Set<String> selectionDataResult = new LinkedHashSet<String>();
		if (selectionData != null) {
			Iterator<String> i = selectionData.iterator();
			while (i.hasNext()) {
				List<String> list = data.getVocabularyCache().searchByPattern(
						i.next());
				Iterator<String> j = list.iterator();
				while (j.hasNext()) {
					String call = j.next();
					listToShow.append(call).append("\n");
					selectionDataResult.add(call);
				}
			}
		}

		signatures.put(listID, selectionDataResult);

		final JFrame frame = new JFrame("Result Selection List: " + listID);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new FlowLayout());

		final JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setText(listToShow.toString());
		area.setColumns(80);
		area.setRows(15);
		JScrollPane jsp = new JScrollPane(area);

		JButton b1 = new JButton("Close");
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});

		frame.add(jsp);
		frame.add(b1);

		if (!Constants.isWebStartMode()) {
			frame.setAlwaysOnTop(true);
		}
		frame.setLocation(25,100);
		frame.setPreferredSize(new Dimension(950, 350));
		frame.pack();
		frame.setVisible(true);
	}

	public Set<String> getSignaturesByListId(String id) {
		return signatures.get(id);
	}

}
