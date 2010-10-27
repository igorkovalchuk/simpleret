package com.googlecode.simpleret.viewer;

import java.awt.Dimension;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.googlecode.simpleret.Constants;

public class FrameProgressBar {

	private JProgressBar progress;
	private JFrame frame;
	boolean active = true;

	public static void main(String[] args) throws Exception {
		int max = 100;
		FrameProgressBar p = new FrameProgressBar(0,max);
		for (int i = 0; i <= max; i++) {
			Thread.sleep(10);
			p.setValue(i);
			p.setString(String.valueOf(i) + " of " + max);
			if (! p.isActive()) {
				break;
			}
		}
	}

	public FrameProgressBar(int min, int max) {
		frame = new JFrame();
		progress = new JProgressBar(min, max);
		progress.setStringPainted(true);
		progress.setIndeterminate(false);
		progress.setPreferredSize(new Dimension(450, 40));
		progress.setVisible(true);
		frame.add(progress);

		frame.setLocation(350, 300);
		if (!Constants.isWebStartMode()) {
			frame.setAlwaysOnTop(true);
		}
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				active = false;
			}
		});
	}

	
	
	public void setString(final String string) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progress.setString(string);
			}
		});
	}

	public void setValue(final int value) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progress.setValue(value);
			}
		});
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setIndeterminate(boolean value) {
		progress.setIndeterminate(value);
	}

	public void dipose() {
		frame.dispose();
	}
}
