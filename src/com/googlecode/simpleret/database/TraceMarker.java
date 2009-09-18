package com.googlecode.simpleret.database;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.viewer.DataFilter;
import com.googlecode.simpleret.viewer.Data;
import com.googlecode.simpleret.viewer.SetOfSets;
import com.googlecode.simpleret.viewer.Range;
import com.googlecode.simpleret.viewer.Where;

public class TraceMarker {

	private static Logger logger = Logger.getLogger(TraceMarker.class);
	
	private Data data = null;
	private DataFilter filter = null;
	Integer colour = null;
	private Set<String> signatures = null;
	
	Set<Integer> alreadySet = new HashSet<Integer>();
	
	public TraceMarker(Data data, DataFilter filter, String signaturesId, Color colour) {
		this.data = data;
		this.filter = filter;
		if (colour != null)
			this.colour = colour.getRGB();
		signatures = data.getSignatures().getSignaturesByListId(signaturesId);
		
		logger.debug("colour = " + this.colour);
	}
	
	public void colourise() {
		if (signatures == null || colour == null || (! filter.isDefined()) ) {
			logger.warn("Useless call of colourise.");
			return;
		}
		
		List<Integer> words = data.getVocabularyCache().getVocabularyList(signatures);
		if (words.size() == 0)
			return;
		
		Iterator<Integer> i = words.iterator();
		Integer wordId;
		while(i.hasNext()) {
			wordId = i.next();
			colouriseWordOptimised(wordId);
		}
		
		// data.getSession().flush();
		// data.getSession().clear();
	}
	
	private void colouriseWord(Integer wordId) {
		
		logger.debug("word id = " + wordId);
		
		if (wordId == null) {
			logger.warn("Word identifier is null.");
			return;
		}
		Where where = new Where();
		where.addClause("vocabularyId = :" + Where.PLACEHOLDER_WORD_ID);
		where.addPlaceholder(Where.PLACEHOLDER_WORD_ID, wordId);
		where.addClause("ret = false");
				
		String hsql = "from Trace " + where.toWhere();
		
		Query query = data.getSession().createQuery(hsql);
		where.usePlaceholders(query);
		List<Trace> list = query.list();
		
		// Now we have all trace objects for that word identifier (from the whole trace).
		
		Iterator<Trace> i = list.iterator();
		int size = list.size(); 
		int ready = 1;
		// List that belongs to the current word identifier and return = false;
		while(i.hasNext()) {
			Trace trace1 = i.next();
			Integer startID = trace1.getId();
			Integer endID = data.getCurrentId2endId().get(startID);
			colouriseByRange(startID, endID);
			logger.debug("Updated " + ready + "/" + size + " of words.");
			ready++;
		}
		
		done();
		//data.getSession().getTransaction().commit();
		//data.setSession( HibernateUtility.getSessionFactory().getCurrentSession() );
		//data.getSession().beginTransaction();
	}
	
	private void colouriseByRange(Integer startId, Integer endId) {

		logger.debug("range = " + startId + " - " + endId);
		
		boolean includeItself = filter.isItself();
		boolean includeSubelements = filter.isSubelements();
		boolean includeParents = filter.isParents();
		
		if (includeParents) {
			includeItself = true;
		}
		
		if (! includeItself) {
			if ( (startId+1) == endId) {
				return;
			}
		}
		
		String hsql = null;

		Where where = new Where();
		hsql = "update Trace set colourMarker = :" + Where.PLACEHOLDER_COLOUR; 

		if (includeItself) {
			if (includeSubelements) {
				where.addClause("id >= :" + Where.PLACEHOLDER_START_ID);
				if (endId != null) {
					where.addClause("id <= :" + Where.PLACEHOLDER_END_ID);
				}
			} else {
				where.addClause("id = :" + Where.PLACEHOLDER_START_ID);
				if (endId != null) {
					where.addClauseOR("id = :" + Where.PLACEHOLDER_END_ID);
				}
			}
		} else {
			if (includeSubelements) {
				where.addClause("id > :" + Where.PLACEHOLDER_START_ID);
				if (endId != null) {
					where.addClause("id < :" + Where.PLACEHOLDER_END_ID);
				}
			}
		}
		
		where.addPlaceholder(Where.PLACEHOLDER_COLOUR, colour);
		where.addPlaceholder(Where.PLACEHOLDER_START_ID, startId);
		if (endId != null)
			where.addPlaceholder(Where.PLACEHOLDER_END_ID, endId);
		
		hsql = hsql + where.toWhere();
		
		Query query = data.getSession().createQuery(hsql);
		where.usePlaceholders(query);
		
		int result = query.executeUpdate();
		
		if (includeParents) {
			Set<Integer> parentsSet = data.findParents(startId);
			parentsSet.addAll(data.findParents(endId));
			parentsSet.removeAll(alreadySet);
			if (parentsSet.size() > 0) {
				alreadySet.addAll(parentsSet);
				hsql = "update Trace set colourMarker = :p1 where id in (:p2)";
				query = data.getSession().createQuery(hsql);
				query.setInteger("p1", colour);
				query.setParameterList("p2", parentsSet);
				result = query.executeUpdate();
			}
		}
		
		//data.getSession().getTransaction().commit();
		//data.setSession( HibernateUtility.getSessionFactory().getCurrentSession() );
		//data.getSession().beginTransaction();
	}

	private void colouriseWordOptimised(Integer wordId) {
		
		logger.debug("word id = " + wordId);
		
		if (wordId == null) {
			logger.warn("Word identifier is null.");
			return;
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
		
		if (isSubelements) {
			Iterator <SetOfSets<Range>> j = ranges.iteratorSectionBySection();
			while(j.hasNext()) {
				String sql = this.buildSqlRange(j.next());
				hsql = "update Trace set colourMarker = :p1 where " + sql;
				logger.debug("SQL (SUBELEMENTS): " + hsql);
				query = data.getSession().createQuery(hsql);
				query.setInteger("p1", colour);
				int result = query.executeUpdate();
				logger.debug("UPDATED: " + result);
			}
		} else if (isItself) {
			Iterator <SetOfSets<Integer>> j = ids.iteratorSectionBySection();
			while(j.hasNext()) {
				String sql = this.buildSqlIN(j.next());
				hsql = "update Trace set colourMarker = :p1 where id in ( " + sql + " )";
				logger.debug("SQL (ITSELF): " + hsql);
				query = data.getSession().createQuery(hsql);
				query.setInteger("p1", colour);
				int result = query.executeUpdate();
				logger.debug("UPDATED: " + result);
			}
		}
		
		if (isParents) {
			Iterator <SetOfSets<Integer>> j = parents.iteratorSectionBySection();
			while(j.hasNext()) {
				String sql = this.buildSqlIN(j.next());
				hsql = "update Trace set colourMarker = :p1 where id in ( " + sql + " )";
				logger.debug("SQL (PARENTS): " + hsql);
				query = data.getSession().createQuery(hsql);
				query.setInteger("p1", colour);
				int result = query.executeUpdate();
				logger.debug("UPDATED: " + result);
			}
		}

		done();
		//data.getSession().getTransaction().commit();
		//data.setSession( HibernateUtility.getSessionFactory().getCurrentSession() );
		//data.getSession().beginTransaction();

	}
	
	
	public String buildSqlRange(SetOfSets<Range> list) {
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
	
	public String buildSqlIN(SetOfSets<Integer> list) {
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

	public void colourReset() {
		Session session = data.getSession();
		
		Where where = new Where();
		where.addClause("colourMarker = :" + Where.PLACEHOLDER_COLOUR);
		where.addPlaceholder(Where.PLACEHOLDER_COLOUR, colour);
		
		String hsql = "update Trace set colourMarker = null " + where.toWhere(); 
		
		Query query = session.createQuery(hsql);
		where.usePlaceholders(query);
		int result = query.executeUpdate();
		logger.debug("COLOUR RESET: " + result + " records.");
		done();
	}

	public void allColoursReset() {
		Session session = data.getSession();				
		String hsql = "update Trace set colourMarker = null where colourMarker is not null"; 
		Query query = session.createQuery(hsql);
		int result = query.executeUpdate();
		logger.debug("COLOUR RESET: " + result + " records.");
		done();
	}

	private void done() {
		data.getSession().getTransaction().commit();
		data.setSession( HibernateUtility.getSessionFactory().getCurrentSession() );
		data.getSession().beginTransaction();
		
		//data.getSession().flush();
		//data.getSession().clear();
	}

}
