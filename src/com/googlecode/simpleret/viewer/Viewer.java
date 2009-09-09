package com.googlecode.simpleret.viewer;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.googlecode.simpleret.Constants;
import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.database.Vocabulary;
import com.googlecode.simpleret.database.VocabularyCache;

public class Viewer extends JEditorPane {

	private static final long serialVersionUID = -1;

	static Logger logger = Logger.getLogger(Viewer.class);

	private static String TITLE = "Simple Reverse Engineering Tool - Viewer";
	
	public final static String HTML_NEW_LINE = "<br>";
	
	private Data data = null;
	
	
	
	private Viewer(Data data) {
		super("text/html", null);
		
		this.data = data;
		
		ViewerKeyListener keyListener = new ViewerKeyListener(data); 
		this.addKeyListener(keyListener);
		
		ViewerHyperlinkListener hyperlinkListener = new ViewerHyperlinkListener(data);
		this.addHyperlinkListener(hyperlinkListener);
	}
	
	
	
	private void showSelection() {
		
		selectionInitializeLoadData();
		selectionLoadData();
		
		selectionShowData();
	}

	private void selectionInitializeLoadData() {
		
		if (data.isRefresh()) {
			Where where = new Where();
			where.append(DatabaseTools.createWhereClause(data));
		
			String hsql = "select count(*) from Trace " + where.toWhere();
			Query query = data.getSession().createQuery(hsql);
			DatabaseTools.usePlaceholders(data, query);
		
			Long count = (Long) query.uniqueResult();
			data.setCount(count);
			
			data.setRefresh(false);
		}
	}
	
	private void selectionLoadData() {
		
	}
	
	private void selectionShowData() {
		StringBuffer content = new StringBuffer(HTML_NEW_LINE);
		content.append("<html>").
		append("<head></head>").
		append("<body style=\"font-family:monospace; font-size:10px;\">").
		append("<pre>");
		
		content.append(HTML_NEW_LINE).append(HTML_NEW_LINE);
		
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
