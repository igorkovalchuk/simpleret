package com.googlecode.simpleret.viewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;

import com.googlecode.simpleret.viewer.ui.FrameAbout;

public class FrameListener implements PropertyChangeListener {

	Data data = null;
	Viewer viewer = null;

	public FrameListener(Viewer viewer, Data data) {
		this.viewer = viewer;
		this.data = data;
	}

	public void propertyChange(PropertyChangeEvent evt) {

		String name = evt.getPropertyName();

		System.out.println("Event: " + name);

		if (FrameConstants.EVENT_DIFFERENCE.equals(name)) {
			this.eventDifference(evt);
		} else if (FrameConstants.EVENT_INPUT_LEVEL.equals(name)) {
			this.eventInputLevel();
		} else if (FrameConstants.EVENT_INPUT_RANGE.equals(name)) {
			this.eventInputRange();
		} else if (FrameConstants.EVENT_INPUT_DEEPNESS.equals(name)) {
			this.eventInputLevel();
		} else if (FrameConstants.EVENT_INSTRUMENTAL_BOX.equals(name)) {
			this.eventInstrumentalBox();
		} else if (FrameConstants.EVENT_EDIT_SIGNATURES.equals(name)) {
			this.eventEditSignatures(evt);
		} else if (FrameConstants.EVENT_SHOW_COLORIZED.equals(name)) {
			this.eventShowColorized();
		} else if (FrameConstants.EVENT_SHOW_RANGE.equals(name)) {
			this.eventShowRange();
		} else if (FrameConstants.EVENT_EXPORT_HTML.equals(name)) {
			this.eventExportHTML();
		} else if (FrameConstants.EVENT_EXPORT_AMATERAS_UML.equals(name)) {
			this.eventExportAmaterasUML();
		} else if (FrameConstants.EVENT_HELP.equals(name)) {
			this.eventHelp();
		} else if (FrameConstants.EVENT_EXIT.equals(name)) {
			this.eventExit();
		} else {
			if (name.startsWith("simpleret"))
				System.err.println("Unknown event: " + name);
		}
	}

	private void eventDifference(PropertyChangeEvent evt) {
		data.setPointer(data.getPointer() + (Integer) evt.getNewValue());
		viewer.showSelection(); // Display;
	}

	private void eventInputLevel() {
		(new FrameInputLevel()).input(data, viewer);
	}

	private void eventInputRange() {
		(new FrameInputRange()).input(data, viewer);
	}

	private void eventInstrumentalBox() {
		(new FrameInstruments()).show(data, viewer);
	}

	private void eventEditSignatures(PropertyChangeEvent evt) {
		data.getSignatures().showList(data, String.valueOf(evt.getNewValue()));
	}

	private void eventShowColorized() {
		data.changeDisplayColouredOnly();
		viewer.showSelection(); // Display;
	}

	private void eventShowRange() {
		data.changeDisplayRange();
		viewer.showSelection(); // Display;
	}

	private void eventExportHTML() {
		(new ExportHTML()).export(data, viewer);
	}

	private void eventExportAmaterasUML() {
		String classUML = "net.java.amateras.uml.sequencediagram.model.SequenceModelBuilder";
		try {
			Class.forName(classUML);
			Class clazz = Class
					.forName("com.googlecode.simpleret.viewer.ExportAmaterasUML");
			ExportInterface export = (ExportInterface) clazz.newInstance();
			export.export(data, viewer);
		} catch (java.lang.Error e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(viewer, e.toString());
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(viewer, e.toString());
			return;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	private void eventHelp() {
		new Thread(new Runnable() {
			public void run() {
				FrameAbout.getInstance();
			}
		}).start();
	}

	private void eventExit() {
		System.exit(0);
	}

}
