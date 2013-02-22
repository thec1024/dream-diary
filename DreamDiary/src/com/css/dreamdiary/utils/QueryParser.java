/**
 * 
 */
package com.css.dreamdiary.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Stack;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;

/**
 * @author Chaitanya.Shende
 *
 */
public class QueryParser {
	private String queryString;
	
	private static final int lookAhead = Operator.lookAhead();

	public QueryParser(String queryString) {
		this.queryString = queryString;
	}

	public org.apache.lucene.search.Query parseQueryString() throws Exception {
		try {
			Stack<String> operatorStack = new Stack<String>();
			ArrayList<String> postFixString = new ArrayList<String>();
			StringBuilder text = new StringBuilder();
			//ABC&&DEF
			for(int i = 0; i < queryString.length(); i++) {
				char c = queryString.charAt(i);
				String lookAheadString = i + lookAhead > queryString.length() ? queryString.substring(i) : queryString.substring(i, i+lookAhead);
				if(Operator.isOperator(lookAheadString)) {
					if(!text.toString().isEmpty())
						postFixString.add(text.toString());
					if(operatorStack.empty()) {
						if(lookAheadString.equalsIgnoreCase(Operator.CLOSE_BRACKET.getOperator()))
							throw new Exception();
						operatorStack.push(lookAheadString);
						text.setLength(0);
					} else {
						if(lookAheadString.equalsIgnoreCase(Operator.CLOSE_BRACKET.getOperator())) {
							while(true) {
								String popString = operatorStack.pop();
								if(popString.equalsIgnoreCase(Operator.OPEN_BRACKET.getOperator())) 
									break;
								if(operatorStack.isEmpty())
									throw new Exception();
								postFixString.add(popString);
							}
							text.setLength(0);
						} else {
							String onStack = operatorStack.peek();
							if(onStack.equalsIgnoreCase(Operator.OPEN_BRACKET.getOperator()) || Operator.hasHigherPrecedence(lookAheadString, onStack)) {
								operatorStack.push(lookAheadString);
								text.setLength(0);
							} else {
								while(true) {
									String popString = operatorStack.pop();
									postFixString.add(popString);
									if(operatorStack.isEmpty() || Operator.hasHigherPrecedence(operatorStack.peek(), lookAheadString)) {
										text.setLength(0);
										break;
									}
								}
								operatorStack.push(lookAheadString);
								text.setLength(0);
							}
						}
					}
					continue;
				} 
				text.append(c);
			}
			if(!text.toString().isEmpty()) {
				postFixString.add(text.toString());
			}
			while(!operatorStack.empty()) {
				String popString = operatorStack.pop();
				postFixString.add(popString);
			}			
			
			operatorStack.clear();
	
			ArrayDeque<org.apache.lucene.search.Query> resultQueue = new ArrayDeque<org.apache.lucene.search.Query>();
			for(int i = 0; i < postFixString.size(); i++) {
				String string = postFixString.get(i);
				if(string == null || string.trim().isEmpty()) 
					continue;
				if(!Operator.isOperator(string)) {
					String[] parts = string.split(":");
					String in = parts[0].trim();
					String what = parts[1].trim();
					WildcardQuery inner = new WildcardQuery(new Term(in, what)); 
					resultQueue.offer(inner);
				} else {
					Operator o = Operator.fromStringOperator(string);
					if(o == null)
						throw new Exception();
					int n = o.getNumOfOperands();
					BooleanQuery query = new BooleanQuery();
					for(int j = 0; j < n; j++) {
						org.apache.lucene.search.Query innerQuery = resultQueue.pollLast();
						if(o == Operator.NOT && innerQuery instanceof WildcardQuery) {
							String field = ((WildcardQuery)innerQuery).getTerm().field();
							String termText = "* -" + field + ":" + ((WildcardQuery)innerQuery).getTerm().text();
							org.apache.lucene.queryparser.classic.QueryParser parser = new org.apache.lucene.queryparser.classic.QueryParser(Version.LUCENE_41, field, new KeywordAnalyzer());
							parser.setAllowLeadingWildcard(true);
							query.add(parser.parse(field + ":" + termText), Occur.SHOULD);
						} else {
							query.add(innerQuery, o.getOccur());
						}
					}
					resultQueue.offer(query);					
				}
			}

			if(resultQueue.size() != 1)
				throw new Exception();
			return resultQueue.remove();
		} catch(Throwable th) {
			throw new Exception("Invalid query");
		}
	}
}
