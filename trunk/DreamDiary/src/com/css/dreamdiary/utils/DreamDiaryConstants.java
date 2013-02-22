/**
 * 
 */
package com.css.dreamdiary.utils;

/**
 * @author Chaitanya.Shende
 *
 */
public abstract class DreamDiaryConstants {

	public static final int CERATE_ENTRY_REQUEST_CODE = 1001; 
	
	public static final int ACTIVITY_CREATED_SUCCESFULLY_RESPONSE_CODE = 5001;
	public static final int ACTIVITY_NOT_CREATED_SUCCESFULLY_RESPONSE_CODE = 5002;
	public static final int ACTIVITY_CREATION_CANCELLED_RESPONSE_CODE = 5003;
	
	public static final int ENTRY_UPDATE_REQUEST_CODE = 6001;
	public static final int ENTRY_UPDATE_SUCCESS_RESPONSE_CODE = 6002;
	public static final int ENTRY_UPDATE_FAILURE_RESPONSE_CODE = 6003;
	
	public static final int SEARCH_ENTRIES_REQUEST_CODE = 7001;
	public static final int SEARCH_ENTRIES_SUCCESS_CODE = 7002;
	public static final int SEARCH_ENTRIES_FAILURE_CODE = 7002;
	
	public static final String NEW_ENTRY_KEY = "com.css.dreamdiary.new_entry_key";
	public static final String OLD_ENTRY_KEY = "com.css.dreamdiary.old_entry_key";
	public static final String FAILURE_MESSAGE_KEY = "com.css.dreamdiary.failure_message_key";
	
	public static final String SEARCH_RESULTS_KEY = "com.css.dreamdiary.search_results_key";
}
