package com.googlecode.simpleret.viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.googlecode.simpleret.Constants;

public class ListOfIdentifiers {

	static Logger logger = Logger.getLogger(ListOfIdentifiers.class);
	
	Object[] list = null;

	public void initialize(Data data, Where where) {
		Session session = data.getSession();
		String hsql = "select id from Trace trace " + where.toWhere() + "order by trace.id";
		logger.debug(hsql);
		Query query = session.createQuery(hsql);
		where.usePlaceholders(query);
		list = query.list().toArray();
		logger.debug("number of records = " + list.length);
	}

	public List<Integer> get(Data data) {
		logger.debug("pointer/count(1) = " + data.getPointer() + "/" + data.getCount());
		correctPointer(data);
		int pointer = data.getPointer();
		int count = data.getCount();
		logger.debug("pointer/count(2) = " + pointer + "/" + count);
		List<Integer> result = new ArrayList<Integer>();

		for(int i = 0; i < Constants.PAGE_LENGTH; i++) {
			if ( (count - 1) >= pointer ) {
				result.add( (Integer) list[pointer] );
				pointer++;
			}
		}
		logger.debug("number of records to show = " + result.size());
		return result;
	}
	
	private void correctPointer(Data data) {
		boolean proceed = false;
		if ( ( data.getLastListSize() == 0 ) || 
				( data.getLastPointer() == data.getPointer() ) ) {
			proceed = true;
		}

		data.setPointer(data.getPointer()); // maybe pointer >= count; correction;

		int pointer = data.getPointer();
		int count = data.getCount();

		if (proceed) {
			int lastId = data.getLastIdentifier();
			if ( (count - 1) >= pointer ) {
				int id = ( (Integer) list[pointer] ).intValue();
				if (id != lastId) {
					// Structure of pointer2id has been changed, because
					// pointers are the same but identifiers are not.
					// Example: previous list had 5 levels of deepness,
					// but the current list have only 2 levels of deepness.
					// So, we must change the current pointer,
					// we must find the closest id to the last id.
					int result = Arrays.binarySearch(list, lastId);
					if (result < 0) {
						result = - result;
						result--;
						result--;
					}
					// we don't worry if pointer is incorrect here,
					// because the next method will correct it if necessary;
					data.setPointer(result);
				}
			}
		}
	}

}
