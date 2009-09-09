package com.googlecode.simpleret.viewer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.googlecode.simpleret.Constants;

class ViewerKeyListener implements KeyListener {
	
	Data data = null;
	
	ViewerKeyListener(Data data) {
		this.data = data;
	}
	
	public void keyTyped(KeyEvent event) {
		char key = event.getKeyChar();
		key = Character.toLowerCase(key);
		
		int difference = 0;
		
		// Forward.
		
		if (key == 'a') {
			difference = 1;
		}
		if (key == 's') {
			difference = Constants.PAGE_LENGTH;
		}
		if (key == 'd') {
			difference = Constants.PAGE_LENGTH * 3;
		}
		if (key == 'f') {
			difference = Constants.PAGE_LENGTH * 100;
		}
		
		// Backward.
		
		if (key == 'q') {
			difference = - 1;
		}
		if (key == 'w') {
			difference = - Constants.PAGE_LENGTH;
		}
		if (key == 'e') {
			difference = - Constants.PAGE_LENGTH * 3;
		}
		if (key == 'r') {
			difference = - Constants.PAGE_LENGTH * 100;
		}
		
		// Other.
		
		
	}
	
	public void keyPressed(KeyEvent e) {
	}
	
	public void keyReleased(KeyEvent e) {
	}
	
}
