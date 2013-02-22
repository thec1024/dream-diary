package com.css.dreamdiary.utils;

import org.apache.lucene.search.BooleanClause.Occur;

public enum Operator {
	OPEN_BRACKET("(", 1) {
		@Override
		public Occur getOccur() {
			return null;
		}
		
	},
	CLOSE_BRACKET(")", 1) {
		@Override
		public Occur getOccur() {
			return null;
		}
	},
	NOT("!", 1) {
		@Override
		public Occur getOccur() {
			return Occur.MUST_NOT;
		}
	},
	AND("&", 2) {
		@Override
		public Occur getOccur() {
			return Occur.MUST;
		}
	},
	OR("|", 2) {
		@Override
		public Occur getOccur() {
			return Occur.SHOULD;
		}
	};

	
	private String operator;
	private int numOfOperands = 2;
	private static int lookAhead = -1;
	
	public abstract Occur getOccur();

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public int getNumOfOperands() {
		return numOfOperands;
	}

	public void setNumOfOperands(int numOfOperands) {
		this.numOfOperands = numOfOperands;
	}

	private Operator(String operator, int numOfOperands) {
		this.operator = operator;
		this.numOfOperands = numOfOperands;
	}
	
	public static Operator whoHasHigherPrecedence(String s1, String s2) {
		Operator o1 = fromStringOperator(s1);
		Operator o2 = fromStringOperator(s2);
		if(o1 == null || o2 == null)
			return null;
		
		return o1.ordinal() <= o2.ordinal() ? o1 : o2; 
	}
	
	public static Operator whoHasHigherPrecedence(Operator o1, Operator o2) {
		return o1.ordinal() <= o2.ordinal() ? o1 : o2; 
	}
	
	public static boolean isOperator(String text) {
		if(text == null || text.isEmpty())
			return false;
		for(Operator o : values()) {
			if(o.getOperator().equalsIgnoreCase(text))
				return true;
		}
		return false;
	}
	
	public static Operator fromStringOperator(String text) {
		for(Operator o : values()) {
			if(o.getOperator().equalsIgnoreCase(text))
				return o;
		}
		return null;
	}
	
	public static boolean hasHigherPrecedence(String first, String second) {
		Operator o1 = fromStringOperator(first);
		Operator o2 = fromStringOperator(second);
		if(o1 == null || o2 == null)
			return false;
		
		return o1.ordinal() <= o2.ordinal(); 
	}
	
	public static int lookAhead() {
		if(lookAhead == -1) {
			for(Operator o : values()) {
				if(lookAhead <= o.getOperator().length()) 
					lookAhead = o.getOperator().length();
			}
		}
		
		return lookAhead;
	}
};
