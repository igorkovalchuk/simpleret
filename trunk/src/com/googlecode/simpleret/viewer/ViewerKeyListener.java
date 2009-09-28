package com.googlecode.simpleret.viewer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;

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
		if (key == '0') {
			( new FrameInstruments() ).show(data, viewer);
			return;
		}
		
		if (key == '9') {
			( new FrameInputRange() ).input(data, viewer);
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
		
		// Show data only in a specified range of identifiers;
		if (key == 'x') {
			data.changeDisplayRange();
			viewer.showSelection(); // Display;
			return;
		}
		
		if (key == 'u') { // AmaterasUML
			String classUML = "net.java.amateras.uml.sequencediagram.model.SequenceModelBuilder";
			try {
				Class.forName(classUML);
				Class clazz = Class.forName("com.googlecode.simpleret.viewer.ExportAmaterasUML");
				ExportInterface export = (ExportInterface) clazz.newInstance();
				export.export(data, viewer);
			} catch (java.lang.Error e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(viewer,
						e.toString());
				return;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(viewer,
						e.toString());
				return;
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			}
		}
		
		if (key == 'h') { // HTML export
			(new ExportHTML()).export(data, viewer);
			return;
		}
		
	}

	public void keyPressed(KeyEvent e) {
	}
	
	public void keyReleased(KeyEvent e) {
	}
	
}
