/**
 * 
 */
package com.css.dreamdiary;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Chaitanya.Shende
 *
 */
public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.dream_diary_preferences);
	}
}
