/**
 * 
 */
package com.css.dreamdiary.exceptions;

/**
 * @author Chaitanya.Shende
 *
 */
public class DiaryException extends Exception {
	private static final long serialVersionUID = -2632295804793202989L;

	/**
	 * @param detailMessage
	 */
	public DiaryException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public DiaryException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public DiaryException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
