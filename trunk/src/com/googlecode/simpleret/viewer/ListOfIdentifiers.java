package com.googlecode.simpleret.viewer;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.googlecode.simpleret.Constants;

public class ListOfIdentifiers {

	Object[] list = null;
	int length = 0;

	public void initialize(Data data, Where where) {
		Session session = data.getSession();
		String hsql = "select id from Trace trace " + where.toWhere() + "order by trace.id";
		Query query = session.createQuery(hsql);
		list = query.list().toArray();
		length = list.length;
		
		int pages = (length + Constants.PAGE_LENGTH - 1) / Constants.PAGE_LENGTH;
		data.setPages(pages);
	}

	public List<Integer> get(Data data) {
		int page = data.getPage();
		List<Integer> result = new ArrayList<Integer>();
		int pointer = Constants.PAGE_LENGTH * (page - 1);
		for(int i = 0; i < Constants.PAGE_LENGTH; i++) {
			if ( (length - 1) >= pointer ) { 
				result.add( (Integer) list[pointer] );
				pointer++;
			}
		}
		return result;
	}

}
