package com.googlecode.simpleret.viewer;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

import com.googlecode.simpleret.Constants;

public class FrameInputLevel {

	public static void test(String[] args) {
		Data data = new Data();
		data.setLevel(3);
		( new FrameInputLevel() ).input(data, null);
	}
	
	public void input(final Data data, final Viewer viewer) {
		final JFrame frame = new JFrame(Constants.TITLE_LEVEL);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(200, 100);
		frame.setLocation(350,250);

		final JTextField jtf = new JTextField();
		jtf.setText(String.valueOf(data.getLevel()));

		Font font = new Font("Dialog", Font.PLAIN, 24);
		jtf.setFont(font);

		jtf.addKeyListener(
				new KeyListener() {
					public void keyTyped(KeyEvent event) {	
					}
					public void keyPressed(KeyEvent event) {
						char key = event.getKeyChar();
						if ( (int)key == 10 ) {
							String text = jtf.getText();
							int result = 0;
							try {
								result = Integer.valueOf(text);
							} catch (NumberFormatException e) {
							}
							data.setLevel(result);
							frame.dispose();
							if (viewer != null)
								viewer.showSelection();

						}
					}
					public void keyReleased(KeyEvent event) {
					}
				}
		);
		frame.add(jtf);
		frame.setVisible(true);
	}
	
}
