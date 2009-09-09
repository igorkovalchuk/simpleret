package com.googlecode.simpleret.viewer;

import java.net.URL;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Logger;

public class ViewerHyperlinkListener implements HyperlinkListener {

	static Logger logger = Logger.getLogger(ViewerHyperlinkListener.class);
	
	Data data = null;
	
	ViewerHyperlinkListener(Data data) {
		this.data = data;
	}
	
	public void hyperlinkUpdate(HyperlinkEvent event) {
		
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			URL url = event.getURL();
			String query = url.getQuery();

			logger.info( "url: " + event.getURL() + " " + query );

			if (query == null)
				return;

			String [] values = query.split("=");
			if (! values[0].equals("id"))
				return;

			// String traceId = values[1];

			// Trace trace = dataHolder.findTraceById(Integer.parseLong(traceId));
			// if (trace == null)
				// return;

			// data.takenColourMarker =  trace.getColourMarker();
		}
		
	}

}
