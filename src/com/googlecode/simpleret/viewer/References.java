package com.googlecode.simpleret.viewer;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.classic.Session;

public class References {

	static Logger logger = Logger.getLogger(References.class);

	static void collectReferences(Data data) {
		
		Map<Integer, Integer> currentId2endId = data.getCurrentId2endId();
		Map<Integer, Integer> endId2currentId = data.getEndId2currentId();
		Map<Integer, Integer> currentId2parentId = data.getCurrentId2parentId();
		Set<Integer> currentIdIsReturn = data.getCurrentIdIsReturn();
		
		Session session = data.getSession();
		
		logger.info("Set references... " + (new Date()).toString());
		
		String hsql = "select count(*) from Trace";
		Query q = session.createQuery(hsql);
		Long counter = (Long) q.uniqueResult();
		
		logger.info("Set references: start " + (new Date()).toString());
		
		int batchRead = 100000;
		//int batchRead = 5;
		int firstResult = 0;
		
		hsql = "select id, endId, parentId, return from Trace trace where id >= ? and  id <= ? order by trace.id";
		q = session.createQuery(hsql);
		
		List<Object[]> list = null;
		//int size = 0;
		do {
			q.setInteger(0, firstResult);
			q.setInteger(1, firstResult + batchRead - 1);
			
			if (firstResult <= counter)
				logger.info("Set references: " + firstResult + " / " + counter);
			
			list = q.list();
			//size = list.size();
			Iterator<Object[]> i = list.iterator();
			
			Integer id;
			Integer endId;
			Integer parentId;
			boolean ret;
			
			while (i.hasNext()) {
				Object[] t = i.next();
				id = (Integer) t[0];// .getId();
				endId = (Integer) t[1];// .getEndId();
				parentId = (Integer) t[2];
				ret = (Boolean) t[3];// .isReturn();

				currentId2endId.put(id, endId);
				endId2currentId.put(endId, id);
				currentId2parentId.put(id, parentId);
				if (ret) {
					currentIdIsReturn.add(id);
				}
			}

			firstResult += batchRead;

			session.clear();
		} while (list.size() > 0);

		logger.info("Set references: done " + (new Date()).toString());
	}

}
