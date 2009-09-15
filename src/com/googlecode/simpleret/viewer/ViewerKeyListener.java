package com.googlecode.simpleret.viewer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.googlecode.simpleret.Constants;

class ViewerKeyListener implements KeyListener {
	
	Data data = null;
	Viewer viewer = null;
	
	ViewerKeyListener(Data data, Viewer viewer) {
		this.data = data;
		this.viewer = viewer;
	}
	
	public void keyTyped(KeyEvent event) {
		char key = event.getKeyChar();
		key = Character.toLowerCase(key);
		
		int difference = 0;
		
		// Forward scroll.
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
		
		// Backward scroll.
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
		
		if (difference != 0) {
			data.setPointer(data.getPointer() + difference);
			viewer.showSelection(); // Display;
			return;
		}
		
		// Change the deepness.
		if (key == 'l') {
			( new FrameInputLevel() ).input(data, viewer);
			return;
		}
		
		// Show the Instrumental Panel.
		if (key == '9') {
			( new FrameInstruments() ).show(data, viewer);
			return;
		}
		
		// Prepare a filter.
		if (key == '1' || key == '2' || key == '3') {
			data.getSignatures().showList(data, String.valueOf(key));
			return;
		}
		
		// Show only coloured/uncoloured content;
		if (key == 'z') {
			data.changeDisplayColouredOnly();
			viewer.showSelection(); // Display;
			return;
		}
		
		if (key == 'u') { // AmaterasUML
		}
		
		if (key == 'h') { // HTML export
		}
		
	}

	public void keyPressed(KeyEvent e) {
	}
	
	public void keyReleased(KeyEvent e) {
	}
	
}
