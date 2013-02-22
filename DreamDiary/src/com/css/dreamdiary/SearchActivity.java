/**
 * 
 */
package com.css.dreamdiary;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.css.dreamdiary.model.SearchTuple;
import com.css.dreamdiary.utils.DiarySearcher;
import com.css.dreamdiary.utils.DreamDiaryConstants;

/**
 * @author Chaitanya.Shende
 *
 */
@SuppressWarnings("deprecation")
public class SearchActivity extends TabActivity{
	
	private class SearchCriteriaAdapter extends ArrayAdapter<SearchTuple> {

		public SearchCriteriaAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		public SearchCriteriaAdapter(Context context, int resource, int textViewResourceId, List<SearchTuple> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		public SearchCriteriaAdapter(Context context, int resource, int textViewResourceId, SearchTuple[] objects) {
			super(context, resource, textViewResourceId, objects);
		}

		public SearchCriteriaAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		public SearchCriteriaAdapter(Context context, int textViewResourceId, List<SearchTuple> objects) {
			super(context, textViewResourceId, objects);
		}

		public SearchCriteriaAdapter(Context context, int textViewResourceId, SearchTuple[] objects) {
			super(context, textViewResourceId, objects);
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = getLayoutInflater().inflate(R.layout.layout_search_tuple, null);
			final SearchTuple tuple = getItem(position);
			
			String[] criteria = SearchActivity.this.getResources().getStringArray(R.array.search_criteria);
			final Spinner spinner = (Spinner) view.findViewWithTag(getString(R.string.search_criteria_tag));
			
			int index = 0;
			for(int i = 0; i < criteria.length; i++) {
				if(criteria[i].equalsIgnoreCase(tuple.getSearchIn())) { 
					index = i;
					break;
				}
			}
			spinner.setSelection(index);
			
			LinearLayout criteriaValueView = (LinearLayout)view.findViewWithTag(getString(R.string.search_value_tag));
			if(!tuple.getSearchIn().equalsIgnoreCase("Between dates") && !tuple.getSearchIn().equalsIgnoreCase("On Date")) {
				final TextView text = new TextView(view.getContext());
				text.setText(tuple.getSearchText());
				if(tuple.getSearchText() == null || tuple.getSearchText().isEmpty()) {
					text.setHint(R.string.enter_search_criteria_tag);
				}
				text.setTag(getString(R.string.search_value_tag) + index);
				text.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				text.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						showSearchTextDialog(text, tuple);
					}
				});
				criteriaValueView.addView(text);
				spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						tuple.setSearchIn(spinner.getSelectedItem().toString());
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {				
					}
				});
			}
			
			
						
			ImageButton deleteButton = (ImageButton)view.findViewWithTag(getString(R.string.delete_tag));
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					remove(getItem(position));
				}
			});
			return view;
		}
		
		private void showSearchTextDialog(final TextView text, final SearchTuple tuple) {
			AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
			final String textToDisplay = tuple.getSearchText();
			final EditText editText = new EditText(SearchActivity.this);
			editText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			editText.setLines(4);
			if(textToDisplay != null) {
				editText.setText(textToDisplay);
			}
			builder.setView(editText);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(editText != null && editText.getText() != null && editText.getText().toString() != null && !editText.getText().toString().isEmpty()) {
//						text.setHint(editText.getText().toString());
						tuple.setSearchText(editText.getText().toString());
						if(editText.getText().toString().length() > searchTextDisplayWidth)
							text.setText(editText.getText().toString().subSequence(0, searchTextDisplayWidth) + "...");
						else
							text.setText(editText.getText().toString());
					}
					dialog.dismiss();
				}
			});
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.show();
		}
	}
	
	private SearchCriteriaAdapter adapter;
	private int screenWidth;
	private float letterWidth;
	private int searchTextDisplayWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search_main);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		Paint p = new Paint();
		letterWidth = p.measureText("M");
		
		searchTextDisplayWidth = (int)((screenWidth/letterWidth) * 0.4f);
		
		TabHost tabHost = getTabHost();
		
		TabSpec criteriaSpec = tabHost.newTabSpec(getString(R.string.criteria_spec_tag));
		criteriaSpec.setIndicator(getString(R.string.criteria_spec_tag));
		criteriaSpec.setContent(R.id.criteria_search_view);
		setupCriteriaView();
		
		TabSpec querySpec = tabHost.newTabSpec(getString(R.string.query_spec_tag));
		querySpec.setIndicator(getString(R.string.query_spec_tag));
		querySpec.setContent(R.id.query_search_view);
		setupQueryView();
		
		tabHost.addTab(criteriaSpec);
		tabHost.addTab(querySpec);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void setupCriteriaView() {
		final String[] criteria = getResources().getStringArray(R.array.search_criteria);
		View view = findViewById(R.id.criteria_search_view);
		adapter = new SearchCriteriaAdapter(this, -1);
		final ListView listView = (ListView)view.findViewById(R.id.search_criteria_list);
		listView.setAdapter(adapter);
		adapter.add(new SearchTuple(criteria[0], "", null, null));
		
		ImageButton addButton = (ImageButton)view.findViewById(R.id.add_new_search_criteria_button);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.add(new SearchTuple(criteria[0], "", null, null));
			}
		});
		
		ImageButton searchButton = (ImageButton)view.findViewById(R.id.search_button);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doCriteriaSearch();
			}
		});
	}
	
	private void setupQueryView() {
		View view = findViewById(R.id.query_search_view);
		ImageButton searchButton = (ImageButton)view.findViewById(R.id.search_button);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doQuerySearch();
			}
		});
	}
	
	private void doCriteriaSearch() {
		final Intent data = new Intent();
//		final View criteriaView = findViewById(R.id.criteria_search_view);
		Thread thread = new Thread() {
			public void run() {
//				populateBeans(criteriaView);
				List<Pair<String, String>> searchCriteria = new ArrayList<Pair<String, String>>();
				for(int i = 0; i < adapter.getCount(); i++) {
					SearchTuple tuple = adapter.getItem(i);
					searchCriteria.add(new Pair<String, String>(tuple.getSearchIn(), tuple.getSearchText()));
				}
				long[] list = DiarySearcher.getInstance().getEntries(SearchActivity.this, searchCriteria, null, null);
				data.putExtra(DreamDiaryConstants.SEARCH_RESULTS_KEY, list);
				setResult(DreamDiaryConstants.SEARCH_ENTRIES_SUCCESS_CODE, data);
			}
		};
		
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
		
		finish();
	}
	
	private void doQuerySearch() {
		final Intent data = new Intent();
		EditText queryText = (EditText)findViewById(R.id.query);
		final String queryString = 
				queryText == null || queryText.getText() == null || queryText.getText().toString() == null || queryText.getText().toString().trim().isEmpty() ?
						"" : queryText.getText().toString();
		Thread thread = new Thread() {
			public void run() {
				long[] list = DiarySearcher.getInstance().getEntries(SearchActivity.this, queryString);
				data.putExtra(DreamDiaryConstants.SEARCH_RESULTS_KEY, list);
				setResult(DreamDiaryConstants.SEARCH_ENTRIES_SUCCESS_CODE, data);
			}
		};
		
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
		
		finish();
	}
	
	
//	private void populateBeans(View view) {
//		final ListView listView = (ListView)view.findViewById(R.id.search_criteria_list);
//		int count = listView.getCount();
//		for(int i = 0; i < count; i++) {
//			View child = listView.getChildAt(i);
//			Spinner spinner = (Spinner)child.findViewWithTag(getString(R.string.search_criteria_tag));
//			String criteria = spinner == null || spinner.getSelectedItem() == null ? null : spinner.getSelectedItem().toString();
//			
//			TextView valueText = (TextView)child.findViewWithTag(getString(R.string.search_value_tag) + i);
//			String value = valueText == null || valueText.getHint() == null ? null : valueText.getHint().toString();
//			
//			adapter.getItem(i).setSearchIn(criteria);
//			adapter.getItem(i).setSearchText(value);
//		}
//	}
}
