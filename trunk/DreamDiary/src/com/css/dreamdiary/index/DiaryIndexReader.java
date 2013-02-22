/**
 * 
 */
package com.css.dreamdiary.index;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.css.dreamdiary.R;
import com.css.dreamdiary.exceptions.DiaryException;
import com.css.dreamdiary.model.DiaryEntryBean;
import com.css.dreamdiary.utils.QueryParser;

/**
 * @author Chaitanya.Shende
 *
 */
public class DiaryIndexReader {
	private Directory indexDirectory;
	private IndexSearcher searcher;
	private String appName;
	
	private static int MAX_RESULTS_TO_QUERY = 10;
	
	public DiaryIndexReader(Context context) {
		try {
			File dir = new File(context.getExternalFilesDir(null), "dreamIndexes");
			if(!dir.exists()) {
				dir.mkdirs();
				DiaryIndexer indexer = new DiaryIndexer(context);
				indexer.initializeIndex();
			}
			indexDirectory = new NIOFSDirectory(dir);
			appName = context.getString(R.string.app_name);
			DirectoryReader dirReader = DirectoryReader.open(indexDirectory);
			searcher = new IndexSearcher(dirReader);
		} catch(Throwable e) {
			Log.e(context.getString(R.string.app_name), "Could not create index reader/searcher", e);
		}
	}
	
	public void closeIndex() {
		try {
			searcher.getIndexReader().close();
			indexDirectory.close();
		} catch(Throwable e) {
			Log.e(appName, "Could not close index reader/searcher", e);
		}		
	}
	
	public long[] getAllIds() {
		return getAllIds(MAX_RESULTS_TO_QUERY);
	}
	
	public long[] getAllIds(int maxResultsToQuery) {
		try {
			TopDocs topDocs = searcher.search(new MatchAllDocsQuery(), maxResultsToQuery);
			
			if(topDocs.totalHits > 0) {
				long[] results = new long[topDocs.scoreDocs.length];
				int i = 0;
				for(ScoreDoc d : topDocs.scoreDocs) {
					int docId = d.doc;
					Document doc = searcher.doc(docId);
					results[i] = Long.valueOf(doc.get(DiaryEntryBean.FIELD_ID));
					i++;
				}
				return results;
			}
		} catch (Throwable tr) {
			Log.e(appName, "Could not get list of dreams.", tr);
		}
		return new long[0];
	}
	
	/*public long[] findDiaryEntries(Date from, Date to, String keyword, String mood) {
		return findDiaryEntries(from, to, keyword, mood, MAX_RESULTS_TO_QUERY);
	}
	
	public long[] findDiaryEntries(Date from, Date to, String keyword, String mood, int maxResultsToQuery) {
		
		BooleanQuery query = new BooleanQuery();
		if(from != null && to != null) {
			Long fromLong = from.getTime();
			Long toLong = to.getTime();
			NumericRangeQuery<Long> rangeQuery = NumericRangeQuery.newLongRange(DiaryEntryBean.FIELD_CREATED, fromLong, toLong, true, true);
			query.add(new BooleanClause(rangeQuery, Occur.MUST));
		}
		
		if(mood != null) {
			FuzzyQuery moodQuery = new FuzzyQuery(new Term(DiaryEntryBean.FIELD_MOOD, mood));
			query.add(new BooleanClause(moodQuery, Occur.MUST));
		}
		
		if(keyword != null && !keyword.isEmpty()) {
			FuzzyQuery titleQuery = new FuzzyQuery(new Term(DiaryEntryBean.FIELD_TITLE, keyword));
			FuzzyQuery contentQuery = new FuzzyQuery(new Term(DiaryEntryBean.FIELD_CONTENT, keyword));
			query.add(titleQuery, Occur.MUST);
			query.add(contentQuery, Occur.MUST);
		}
		
		if(query.getClauses().length == 0)
			query.add(new BooleanClause(new MatchAllDocsQuery(), Occur.SHOULD));
		
		try {
			TopDocs topDocs = searcher.search(query, maxResultsToQuery);
			
			if(topDocs.totalHits > 0) {
				long[] results = new long[topDocs.scoreDocs.length];
				int i = 0;
				for(ScoreDoc d : topDocs.scoreDocs) {
					int docId = d.doc;
					Document doc = searcher.doc(docId);
					results[i] = Long.valueOf(doc.get(DiaryEntryBean.FIELD_ID));
					i++;
				}
				return results;
			}
		} catch (Throwable tr) {
			Log.e(appName, "Could not get search results for '" + keyword + "'", tr);
		}
		return new long[0];
	}*/
	
	public long[] findDiaryEntries(String queryString) throws DiaryException {
		if(queryString == null || queryString.isEmpty())
			throw new DiaryException("Invalid query");
		
		QueryParser parser = new QueryParser(queryString);
		try {
			Query query = parser.parseQueryString();
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			if(topDocs.totalHits > 0) {
				long[] results = new long[topDocs.scoreDocs.length];
				int i = 0;
				for(ScoreDoc d : topDocs.scoreDocs) {
					int docId = d.doc;
					Document doc = searcher.doc(docId);
					results[i] = Long.valueOf(doc.get(DiaryEntryBean.FIELD_ID));
					i++;
				}
				return results;
			}
		} catch(Throwable tr) {
			Log.e(appName, "Could not get search results", tr);
		}
		
		return new long[0];
	}
	
	public long[] findDiaryEntries(List<Pair<String, String>> searchCriteria, Date from, Date to) {
		return findDiaryEntries(searchCriteria, from, to, MAX_RESULTS_TO_QUERY);
	}
	
	public long[] findDiaryEntries(List<Pair<String, String>> searchCriteria, Date from, Date to, int maxResultsToQuery) {
		BooleanQuery query = new BooleanQuery();
		if(searchCriteria != null && !searchCriteria.isEmpty()) {
			for(Pair<String, String> searchCriterion : searchCriteria) {
				String in = searchCriterion.first.toLowerCase(Locale.US);
				String what = "*" + searchCriterion.second.toLowerCase(Locale.US) + "*";
				WildcardQuery inner = new WildcardQuery(new Term(in, what));
				query.add(new BooleanClause(inner, Occur.MUST));
			}
		}
		
		if(from != null && to != null) {
			Long fromLong = from.getTime();
			Long toLong = to.getTime();
			NumericRangeQuery<Long> rangeQuery = NumericRangeQuery.newLongRange(DiaryEntryBean.FIELD_CREATED, fromLong, toLong, true, true);
			query.add(new BooleanClause(rangeQuery, Occur.MUST));
		}
		
		if(query.getClauses().length == 0)
			query.add(new BooleanClause(new MatchAllDocsQuery(), Occur.SHOULD));
		try {
			TopDocs topDocs = searcher.search(query, maxResultsToQuery);
			
			if(topDocs.totalHits > 0) {
				long[] results = new long[topDocs.scoreDocs.length];
				int i = 0;
				for(ScoreDoc d : topDocs.scoreDocs) {
					int docId = d.doc;
					Document doc = searcher.doc(docId);
					results[i] = Long.valueOf(doc.get(DiaryEntryBean.FIELD_ID));
					i++;
				}
				return results;
			}
		} catch (Throwable tr) {
			Log.e(appName, "Could not get search results", tr);
		}
		return new long[0];
	}
	
	public long[] findDiaryEntryIds(String value, int maxResultsToQuery) {
		if(value != null && !value.isEmpty()) {
			WildcardQuery titleQuery = new WildcardQuery(new Term(DiaryEntryBean.FIELD_TITLE, value));
			WildcardQuery contentQuery = new WildcardQuery(new Term(DiaryEntryBean.FIELD_CONTENT, value));
			BooleanQuery query = new BooleanQuery();
			query.add(titleQuery, Occur.SHOULD);
			query.add(contentQuery, Occur.SHOULD);
			try {
				TopDocs topDocs = searcher.search(query, maxResultsToQuery);
				
				if(topDocs.totalHits > 0) {
					long[] results = new long[topDocs.scoreDocs.length];
					int i = 0;
					for(ScoreDoc d : topDocs.scoreDocs) {
						int docId = d.doc;
						Document doc = searcher.doc(docId);
						results[i] = Long.valueOf(doc.get(DiaryEntryBean.FIELD_ID));
						i++;
					}
					return results;
				}
			} catch (Throwable tr) {
				Log.e(appName, "Could not get search results for '" + value + "'", tr);
			}
		}
		return new long[0];
	}
	
	public long[] findDiaryEntryIds(String value) {
		return findDiaryEntryIds(value, MAX_RESULTS_TO_QUERY);
	}
}
