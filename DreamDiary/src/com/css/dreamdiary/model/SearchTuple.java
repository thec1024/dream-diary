/**
 * 
 */
package com.css.dreamdiary.model;

import java.util.Date;

/**
 * @author Chaitanya.Shende
 *
 */
public class SearchTuple {

	private String searchIn;
	private String searchText;
	private Date startDate;
	private Date endDate;
	public String getSearchIn() {
		return searchIn;
	}
	public void setSearchIn(String searchIn) {
		this.searchIn = searchIn;
	}
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public SearchTuple(String searchIn, String searchText, Date startDate,
			Date endDate) {
		super();
		this.searchIn = searchIn;
		this.searchText = searchText;
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
