package com.googlecode.simpleret.importer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.googlecode.simpleret.database.HibernateUtility;
import com.googlecode.simpleret.database.Trace;

/**
 * Set additional references in the database.
 * 
 * @author (c) Igor O. Kovalchuk 2008, 2009.
 */
public class TraceFileReader4SetAdditionalReferences {

	static Logger logger = 
		Logger.getLogger(TraceFileReader4SetAdditionalReferences.class);

	Map<Integer, Integer> level2id = new HashMap <Integer, Integer>();
	Map<Integer, String> level2call = new HashMap <Integer, String>();
	
	private static final int batchCounterValue = 100000;
	
	/**
	 * Set end identifiers and end levels.
	 */
	void storeReferences(ImportDataHolder dataHolder) {
		
		Session session = HibernateUtility.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		int batchCounter = batchCounterValue;
		
		String hsql = "select count(*) from Trace";
		Query q = session.createQuery(hsql);
		Long counter = (Long) q.uniqueResult();
		
		int firstResult = (counter.intValue() - 1) - batchCounter + 1; 
		
		if (firstResult < 0 ) {
			firstResult = 0;
		}
		
		hsql = "from Trace trace order by trace.id asc";
		q = session.createQuery(hsql);
		
		while (true) {
			q.setFirstResult(firstResult);
			q.setMaxResults(batchCounter);
			
			List<Trace> list = q.list();
			int lastIndex = list.size() - 1;
			for(int i = lastIndex; i >= 0; i--) {
				Trace t = list.get(i);
				Integer level = t.getLevel();
				Integer thisID = t.getId();
				boolean ret = t.isReturn();	
				if (! ret) {
					Integer endID = dataHolder.startID2endID.get(thisID);
					if (endID != null) {
						t.setEndId(endID);
					}
				} else {
					// 'return'
					this.level2id.put(level, thisID);
					Integer previousLevelFromEnd = level - 1;
					Integer parentIDFromEnd = this.level2id.get(previousLevelFromEnd);
					if (parentIDFromEnd != null) {
						t.setParentId(parentIDFromEnd);
					}
				}
			}
			
			if (firstResult == 0)
				break;
			
			if ((firstResult - batchCounter) < 0) {
				batchCounter = firstResult;
				firstResult = 0;
			} else {
				firstResult = firstResult - batchCounter;
			}
			
			session.flush();
			session.clear();
			
			float percent = 100f * (float)(counter - firstResult) / (float)counter; 
			logger.info("Set references: " + percent + "%");
		}
		
		session.getTransaction().commit();
			
	}
	
}
