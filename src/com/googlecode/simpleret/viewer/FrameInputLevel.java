package com.googlecode.simpleret.viewer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

import com.googlecode.simpleret.Constants;

public class FrameInputLevel {

	public static void main(String[] args) {
		Data data = new Data();
		data.setLevel(3);
		(new FrameInputLevel()).input(data, null);
	}

	public void input(final Data data, final Viewer viewer) {
		final JFrame frame = new JFrame(Constants.TITLE_LEVEL);

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		Insets insets = new Insets(3, 3, 3, 3);
		constraints.insets = insets;

		final JTextField jtf = new JTextField();
		jtf.setFont(FrameConstants.LARGE_FONT);
		jtf.setText(String.valueOf(data.getLevel()));

		jtf.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent event) {
			}

			public void keyPressed(KeyEvent event) {
				char key = event.getKeyChar();
				if ((int) key == 10) {
					String text = jtf.getText();
					int result = 0;
					try {
						result = Integer.valueOf(text);
					} catch (NumberFormatException e) {
					}
					data.setLevel(result);
					frame.dispose();
					if (viewer != null) {
						viewer.showSelection();
					}
				}
			}

			public void keyReleased(KeyEvent event) {
			}
		});

		jtf.setPreferredSize(new Dimension(100, 40));

		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(jtf, constraints);
		frame.add(jtf);

		frame.setLayout(layout);
		frame.setPreferredSize(new Dimension(150, 100));
		frame.setLocation(400, 300);
		if (!Constants.isWebStartMode()) {
			frame.setAlwaysOnTop(true);
		}
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

}
