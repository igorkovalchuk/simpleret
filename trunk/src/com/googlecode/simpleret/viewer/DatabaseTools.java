package com.googlecode.simpleret.viewer;

import org.hibernate.Query;

public class DatabaseTools {

	static public String createWhereClause(Data data) {

		Where where = new Where();

		if (data.isDisplayColouredOnly()) {
			where.append("colourMarker is not null");
		}

		return where.toString();
	}

	static public void usePlaceholders(Data data, Query query) {
	}

}
