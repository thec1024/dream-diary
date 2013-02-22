/**
 * 
 */
package com.css.dreamdiary.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Chaitanya.Shende
 *
 */

public class DiaryEntryBean implements Serializable {
	private static final long serialVersionUID = 9035428849289160685L;
	
	public static final String FIELD_ID = "id";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_CONTENT = "content";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_MOOD = "mood";
	public static final String FIELD_SLEEP_HRS = "sleep_hours";
	public static final String FIELD_CREATED = "created";
	
	private long id;
	private String title;
	private String mood;
	private String type;
	private float sleepHours;
	private String description;
	private Date creationDateTime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreationDateTime() {
		return creationDateTime;
	}
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}
	public float getSleepHours() {
		return sleepHours;
	}
	public void setSleepHours(float sleepHours) {
		this.sleepHours = sleepHours;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public DiaryEntryBean(long id, String title, String type, String mood, float sleepHours,
			String description, Date creationDateTime) {
		super();
		this.id = id;
		this.title = title;
		this.type = type;
		this.mood = mood;
		this.sleepHours = sleepHours;
		this.description = description;
		this.creationDateTime = creationDateTime;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id:").append(id).append(" Title:").append(title).append(" Date:").append(creationDateTime);
		return sb.toString();
	}
}
