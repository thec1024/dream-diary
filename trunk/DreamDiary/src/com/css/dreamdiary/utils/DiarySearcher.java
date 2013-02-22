/**
 * 
 */
package com.css.dreamdiary.utils;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Pair;

import com.css.dreamdiary.dao.DiaryDatabaseDAO;
import com.css.dreamdiary.exceptions.DiaryException;

/**
 * @author Chaitanya.Shende
 *
 */
public class DiarySearcher {
	private static DiarySearcher instance;
		
	public static DiarySearcher getInstance() {
		if(instance == null) {
			synchronized (DiarySearcher.class) {
				if(instance == null) {
					instance = new DiarySearcher();
				}
			}
		}
		
		return instance;
	}
	
	public long[] getEntries(Context context, List<Pair<String, String>> searchCriteria, Date from, Date to) {
		DiaryDatabaseDAO dao = new DiaryDatabaseDAO(context);
		try {
			return dao.getDreamIdsList(searchCriteria, from, to);
		} catch (DiaryException e) {
			return new long[0];
		}
	}

	public long[] getEntries(Context context, String queryString) {
		DiaryDatabaseDAO dao = new DiaryDatabaseDAO(context);
		if(queryString == null || queryString.isEmpty()) 
			return null;
		try {
			return dao.getDreamIdsList(queryString);
		} catch(DiaryException de) {
			return null;
		}
	}
}
