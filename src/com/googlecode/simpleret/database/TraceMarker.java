package com.googlecode.simpleret.database;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.viewer.DataFilter;
import com.googlecode.simpleret.viewer.Data;
import com.googlecode.simpleret.viewer.DatabaseTools;
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
			colouriseWord(wordId);
		}
		
		data.getSession().flush();
		data.getSession().clear();
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
		
		data.getSession().getTransaction().commit();
		data.setSession( HibernateUtility.getSessionFactory().getCurrentSession() );
		data.getSession().beginTransaction();
	}

}
