/**
 * 
 */
package com.css.dreamdiary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.css.dreamdiary.R;

/**
 * @author Chaitanya.Shende
 *
 */
public class DreamDiaryUtils {

	public static String getExportPath(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(context.getString(R.string.preference_export_path), context.getString(R.string.preference_export_path_default_value));
	}
	
	public static boolean isAutoExportEnabled(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(context.getString(R.string.preference_enable_auto_export), false);
	}
	
	public static int getAutoArchivalFrequency(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.valueOf(prefs.getString(context.getString(R.string.preference_auto_archival_frequency), "30"));
	}
}
