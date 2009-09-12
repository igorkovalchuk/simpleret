package com.googlecode.simpleret.viewer;

import java.awt.Dimension;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.googlecode.simpleret.Constants;
import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.database.Trace;
import com.googlecode.simpleret.database.Vocabulary;
import com.googlecode.simpleret.database.VocabularyCache;

public class Viewer extends JEditorPane {

	private static final long serialVersionUID = -1;

	static Logger logger = Logger.getLogger(Viewer.class);

	private static String TITLE = "Simple Reverse Engineering Tool - Viewer";
	
	public final static String HTML_NEW_LINE = "<br>";
	
	private Data data = null;
	
	private ListOfIdentifiers identifiers = new ListOfIdentifiers();
	
	private Viewer(Data data) {
		super("text/html", null);
		
		this.data = data;
		
		ViewerKeyListener keyListener = new ViewerKeyListener(data, this); 
		this.addKeyListener(keyListener);
		
		ViewerHyperlinkListener hyperlinkListener = new ViewerHyperlinkListener(data);
		this.addHyperlinkListener(hyperlinkListener);
	}
	
	
	
	public void showSelection() {
		data.defineState();
		selectionInitializeLoadData();
		if (data.isChanged()) {
			List<Trace> list = selectionLoadData();
			selectionShowData(list);
		}
		data.resetState();
	}

	private void selectionInitializeLoadData() {
		if (data.isReinitialize()) {
			Where where = new Where();
			where.append(DatabaseTools.createWhereClause(data));

			String hsql = "select count(*) from Trace " + where.toWhere();
			Query query = data.getSession().createQuery(hsql);
			DatabaseTools.usePlaceholders(data, query);

			Long count = (Long) query.uniqueResult();
			data.setCount(count);

			identifiers.initialize(data, where);
		}
	}
	
	private List<Trace> selectionLoadData() {
		
		List<Integer> list = identifiers.get(data);
		
		Query query = data.getSession().createQuery("from Trace trace where trace.id in (:idList)");
		query.setParameterList("idList", list);
		List <Trace> result = query.list();
		return result;
	}
	
	private void selectionShowData(List<Trace> list) {
		logger.info("Show results.");

		StringBuffer content = new StringBuffer(HTML_NEW_LINE);
		content.append("<html>").
		append("<head></head>").
		append("<body style=\"font-family:monospace; font-size:10px;\">").
		append("<pre>");

		content.append(HTML_NEW_LINE).append(HTML_NEW_LINE);

		Iterator<Trace> i = list.iterator();
		while (i.hasNext()) {
			Trace trace = i.next();
			content.append( trace.getTextView(data) ) . 
				append(HTML_NEW_LINE);
		}

		content.append(HTML_NEW_LINE);

		content.append("</pre></body></html>");
		this.setText(content.toString());
	}
	
	
	
	public static void use(Data initialData) {
		
		if (initialData == null) {
			throw new RuntimeException("Software error. No input parameter.");
		}
		
		JFrame frame = new JFrame(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Viewer viewer = new Viewer(initialData);
		viewer.showSelection();
		viewer.setEditable(false);
		
		JScrollPane sp = new JScrollPane(viewer);
		
		frame.setContentPane(sp);
		frame.setPreferredSize(	new Dimension(
				Constants.VIEWER_WIDTH, Constants.VIEWER_HEIGHT));
		
		frame.pack();
		frame.setVisible(true);
	}

	public static void main() {
		Session session = HibernateUtility.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		Data data = new Data(); 
		data.setSession(session);
		
		// Load vocabulary.
		List <Vocabulary> list = session.createQuery("from Vocabulary").list();
		VocabularyCache vc = new VocabularyCache();
		vc.setList(list);
		data.setVocabularyCache(vc);
		
		// Load references;
		References.collectReferences(data);
		
		use(data);
	}

}
