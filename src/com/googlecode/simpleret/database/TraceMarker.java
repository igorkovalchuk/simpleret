package com.googlecode.simpleret.database;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.googlecode.simpleret.Constants;
import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.viewer.DataFilter;
import com.googlecode.simpleret.viewer.Data;
import com.googlecode.simpleret.viewer.FrameProgressBar;
import com.googlecode.simpleret.viewer.SetOfSets;
import com.googlecode.simpleret.viewer.Range;
import com.googlecode.simpleret.viewer.Viewer;
import com.googlecode.simpleret.viewer.Where;

public class TraceMarker {

	private static Logger logger = Logger.getLogger(TraceMarker.class);

	private Data data = null;

	private DataFilter filter = null;

	/**
	 * A colour to mark with.
	 */
	private Integer colour = null;

	/**
	 * Signatures to mark by a particular colour.
	 */
	private Set<String> signatures = null;

	//private Set<Integer> alreadySet = new HashSet<Integer>();
	
	private FrameProgressBar progressBar;

	/**
	 * For a progress bar.
	 */
	private int wordsNumber = 0;

	/**
	 * For a progress bar.
	 */
	private int wordsProgress = 0;

	private Viewer viewer;

	/**
	 * @param data
	 * 			an object of "Data", the Singleton.
	 * @param filter
	 * 			an object of DataFilter that defines,
	 * 			which additional objects we have to colourise.
	 * @param signaturesListId
	 * 			an identifier of list of signatures. User creates
	 * 			that list of signatures manually, by using a User Interface.
	 * @param colour
	 * 			a colour to mark signatures with.
	 * @param viewer
	 * 			an object of the Viewer (i.e. object of a User Interface).
	 */
	public TraceMarker(Data data, DataFilter filter, String signaturesListId, Color colour, Viewer viewer) {
		this.data = data;
		this.filter = filter;
		if (colour != null)
			this.colour = colour.getRGB();
		this.viewer = viewer;
		signatures = data.getSignatures().getSignaturesByListId(signaturesListId);
		
		logger.debug("colour = " + this.colour);
	}

	/**
	 * Colourise/delete/undelete a list of signatures and other appropriate signatures
	 * defined by rules in DataFilter.
	 */
	public void mark() {
		if (signatures == null || (filter.isMark() && colour == null) || (! filter.isDefined()) ) {
			logger.warn("Useless call of colourise.");
			return;
		}
		
		List<Integer> words = data.getVocabularyCache().getVocabularyList(signatures);
		if (words.size() == 0)
			return;

		viewer.setEnabled(false);
		
		progressBar = new FrameProgressBar(0, Constants.PROGRESS_MAX);

		Iterator<Integer> i = words.iterator();
		Integer wordId;
		wordsNumber = words.size();
		while(i.hasNext()) {
			wordsProgress++;
			wordId = i.next();
			colouriseWordOptimised(wordId);
		}
		
		progressBar.dipose();
		
		viewer.setEnabled(true);
		viewer.grabFocus();
		data.setChanged(true);
		if (filter.isDelete() || filter.isUndelete()) {
			data.setReinitialize(true);
		}
		viewer.showSelection();
	}

	/**
	 * Colourise/delete/undelete a particular signature.
	 * Colourise/delete/undelete it/its parents/its sub-elements (see DataFilter).
	 * 
	 * @param wordId
	 * 			a signature' identifier.
	 */
	private void colouriseWordOptimised(Integer wordId) {
		
		logger.debug("word id = " + wordId);
		
		if (wordId == null) {
			logger.warn("Word identifier is null.");
			return;
		}

		if (filter.isDelete()) {
			filter.setParents(false); // don't delete parent elements;
			filter.setSubelements(true); // always delete sub-elements;
		}

		boolean isSubelements = filter.isSubelements();
		boolean isParents = filter.isParents();
		boolean isItself = filter.isItself();
		
		Where where = new Where();
		where.addClause("vocabularyId = :" + Where.PLACEHOLDER_WORD_ID);
		where.addPlaceholder(Where.PLACEHOLDER_WORD_ID, wordId);
		where.addClause("ret = false");
				
		String hsql = "select id from Trace " + where.toWhere();
		
		Query query = data.getSession().createQuery(hsql);
		where.usePlaceholders(query);
		List<Integer> list = query.list();
		
		// Now we have all trace objects for that word identifier (from the whole trace).
		
		Iterator<Integer> i = list.iterator();
		// List that belongs to the current word identifier and return = false;
		SetOfSets<Range> ranges = new SetOfSets<Range>();
		SetOfSets<Integer> ids = new SetOfSets<Integer>();
		SetOfSets<Integer> parents = new SetOfSets<Integer>();
		
		while(i.hasNext()) {
			Integer startID = i.next();
			Integer endID = data.getCurrentId2endId().get(startID);
			if (isSubelements) {
				// use ranges of identifiers;
				ranges.add(new Range(startID, endID));
			} else if (isItself) {
				// use only identifiers;
				ids.add(startID);
				ids.add(endID);
			}
			if (isParents) {
				parents.addAll( data.findParents(startID) );
				parents.addAll( data.findParents(endID) );
			}
		}
		
		int sqlNumber = 0;
		int sqlProgress = 0;
		
		sqlNumber = parents.number();
		
		if (isSubelements) {
			Iterator <SetOfSets<Range>> j = ranges.iteratorSectionBySection();
			sqlNumber += ranges.number();
			while(j.hasNext()) {
				sqlProgress++;
				String sql = this.buildSqlRange(j.next());
				if (filter.isDelete()) {
					hsql = "update Trace set disabled = true where " + sql;
				} else if (filter.isUndelete()) {
					hsql = "update Trace set disabled = false where " + sql;
				} else {
					hsql = "update Trace set colourMarker = :p1 where " + sql;
				}
				logger.debug("SQL (SUBELEMENTS): " + hsql);
				query = data.getSession().createQuery(hsql);
				if (filter.isMark()) {
					query.setInteger("p1", colour);
				}
				int result = query.executeUpdate();
				logger.debug("UPDATED: " + result);
				progressBar.setString(this.wordsProgress + " / " + this.wordsNumber);
				progressBar.setValue(Constants.PROGRESS_MAX * sqlProgress / sqlNumber);
			}
		} else if (isItself) {
			Iterator <SetOfSets<Integer>> j = ids.iteratorSectionBySection();
			sqlNumber += ids.number();
			while(j.hasNext()) {
				sqlProgress++;
				String sql = this.buildSqlIN(j.next());
				if (filter.isDelete()) {
					hsql = "update Trace set disabled = true where id in ( " + sql + " )";
				} else if (filter.isUndelete()) {
					hsql = "update Trace set disabled = false where id in ( " + sql + " )";
				} else {
					hsql = "update Trace set colourMarker = :p1 where id in ( " + sql + " )";
				}
				logger.debug("SQL (ITSELF): " + hsql);
				query = data.getSession().createQuery(hsql);
				if (filter.isMark()) {
					query.setInteger("p1", colour);
				}
				int result = query.executeUpdate();
				logger.debug("UPDATED: " + result);
				progressBar.setString(this.wordsProgress + " / " + this.wordsNumber);
				progressBar.setValue(Constants.PROGRESS_MAX * sqlProgress / sqlNumber);
			}
		}
		
		if (isParents) {
			Iterator <SetOfSets<Integer>> j = parents.iteratorSectionBySection();
			while(j.hasNext()) {
				sqlProgress++;
				String sql = this.buildSqlIN(j.next());
				if (filter.isDelete()) {
					hsql = "update Trace set disabled = true where id in ( " + sql + " )";
				} else if (filter.isUndelete()) {
					hsql = "update Trace set disabled = false where id in ( " + sql + " )";
				} else {
					hsql = "update Trace set colourMarker = :p1 where id in ( " + sql + " )";
				}
				logger.debug("SQL (PARENTS): " + hsql);
				query = data.getSession().createQuery(hsql);
				if (filter.isMark()) {
					query.setInteger("p1", colour);
				}
				int result = query.executeUpdate();
				logger.debug("UPDATED: " + result);
				progressBar.setString(this.wordsProgress + " / " + this.wordsNumber);
				progressBar.setValue(Constants.PROGRESS_MAX * sqlProgress / sqlNumber);
			}
		}

		done();
	}
	
	/**
	 * @param list
	 * 			a list of identifiers to build SQL.
	 * @return
	 * 		a string or a combination of strings like these <br>
	 * 		(id&lt;123)<br>
	 * 		(id&gt;123)<br>
	 * 		(id&gt;123 and id&lt;456)<br>
	 * 		(id&lt;=123)<br>
	 * 		(id&gt;=123)<br>
	 * 		(id&gt;=123 and id=&lt;456)<br>
	 * 		or an empty string.<br>
	 * 		<br>
	 * 		An example:<br>
	 * 		(id&lt;5) or (id&gt;123 and id&lt;456) or (id&gt;700)
	 */
	public String buildSqlRange(SetOfSets<Range> list) {

		// TODO: A complicated design here.
		// Please change SetOfSets object
		// to an Iterator.

		StringBuilder result = new StringBuilder();
		
		String lt = "<";
		String gt = ">";
		if (filter.isItself()) {
			lt = "<=";
			gt = ">=";
		}
		
		Range object;
		Integer from;
		Integer to;
		boolean started = false;
				
		Iterator<Range> i = list.iterator();
		while(i.hasNext()) {
			object = i.next();
			from = object.getFrom();
			to = object.getTo();
			if (from == null && to == null) {
				continue;
			}
			if (started) {
				result.append(" or ");
			} else {
				started = true;
			}
			
			if (from == null) {
				result.append("(id").append(lt).append(to).append(")");
			} else if (to == null) {
				result.append("(id").append(gt).append(from).append(")");
			} else {
				result.append("(id").append(gt).append(from.toString()).append(" and id").
					append(lt).append(to.toString()).append(")");
			}
		}
		return result.toString().trim();
	}
	
	/**
	 * @param list
	 * 			a list of identifiers to build SQL.
	 * @return
	 * 		a string like this "1,2,3,4,5" or an empty string.
	 */
	public String buildSqlIN(SetOfSets<Integer> list) {

		// TODO: A complicated design here.
		// Please change SetOfSets object
		// to an Iterator.

		StringBuffer result = new StringBuffer();
		Iterator<Integer> i = list.iterator();
		boolean started = false;
		Integer object;
		while(i.hasNext()) {
			object = i.next();
			if (object == null)
				continue;
			if (started) {
				result.append(",");
			} else {
				started = true;
			}
			result.append(object.toString());
		}
		return result.toString();
	}

	/**
	 * Clean/delete/undelete records by a particular colour in the database
	 * (Clean: set it to null. Delete: set disabled = true.)
	 */
	public void singleColour() {

		viewer.setEnabled(false);

		Session session = data.getSession();

		Where where = new Where();
		where.addClause("colourMarker = :" + Where.PLACEHOLDER_COLOUR);
		where.addPlaceholder(Where.PLACEHOLDER_COLOUR, colour);

		String hsql;
		if (filter.isDelete()) {
			hsql = "update Trace set disabled = true " + where.toWhere();
		} else if (filter.isUndelete()) {
			hsql = "update Trace set disabled = false " + where.toWhere();
		} else {
			hsql = "update Trace set colourMarker = null " + where.toWhere();
		}

		Query query = session.createQuery(hsql);
		where.usePlaceholders(query);
		int result = query.executeUpdate();

		if (filter.isDelete()) {
			logger.debug("COLOUR DELETE: " + result + " records.");
		} else if (filter.isUndelete()) {
			logger.debug("COLOUR UNDELETE: " + result + " records.");
		} else {
			logger.debug("COLOUR RESET: " + result + " records.");
		}

		done();

		viewer.setEnabled(true);
		viewer.grabFocus();
		data.setChanged(true);
		if (filter.isDelete() || filter.isUndelete()) {
			data.setReinitialize(true);
		}
		viewer.showSelection();
	}

	/**
	 * Set all colours in the database to null.
	 */
	public void allColours() {

		viewer.setEnabled(false);

		Session session = data.getSession();
		String hsql;

		if (filter.isDelete()) {
			hsql = "update Trace set disabled = true where disabled = false";
		} else if (filter.isUndelete()) {
			hsql = "update Trace set disabled = false where disabled = true";
		} else {
			hsql = "update Trace set colourMarker = null where colourMarker is not null";
		}

		Query query = session.createQuery(hsql);
		int result = query.executeUpdate();

		logger.debug("ALL COLOUR RESET/DELETE/UNDELETE: " + result + " records.");

		done();

		viewer.setEnabled(true);
		viewer.grabFocus();
		data.setChanged(true);
		if (filter.isDelete() || filter.isUndelete()) {
			data.setReinitialize(true);
		}
		viewer.showSelection();
	}

	/**
	 * Commit and start a new database transaction.
	 */
	private void done() {
		data.getSession().getTransaction().commit();
		data.setSession( HibernateUtility.getSessionFactory().getCurrentSession() );
		data.getSession().beginTransaction();
	}

}
