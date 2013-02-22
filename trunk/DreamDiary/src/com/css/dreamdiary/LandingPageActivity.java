package com.css.dreamdiary;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.css.dreamdiary.dao.DiaryDatabaseDAO;
import com.css.dreamdiary.exceptions.DiaryException;
import com.css.dreamdiary.model.DiaryEntryBean;
import com.css.dreamdiary.utils.DreamDiaryConstants;
import com.css.dreamdiary.utils.DreamDiaryUtils;

public class LandingPageActivity extends AbstractDiaryActivity {
	
	private List<Long> invisibleEntries = new ArrayList<Long>();
	
	private class LandingPageAdapter extends ArrayAdapter<DiaryEntryBean> {

		public LandingPageAdapter(Context context, int textViewResourceId, DiaryEntryBean[] objects) {
			super(context, textViewResourceId, objects);
		}

		public LandingPageAdapter(Context context, int resource, int textViewResourceId, DiaryEntryBean[] objects) {
			super(context, resource, textViewResourceId, objects);
		}

		public LandingPageAdapter(Context context, int resource, int textViewResourceId, List<DiaryEntryBean> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		public LandingPageAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		public LandingPageAdapter(Context context, int textViewResourceId, List<DiaryEntryBean> objects) {
			super(context, textViewResourceId, objects);
		}

		public LandingPageAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DiaryEntryBean bean = getItem(position);
			View view = getLayoutInflater().inflate(R.layout.diary_entry_line_item, null);
			TextView title = (TextView)view.findViewWithTag(getString(R.string.diary_entry_title_tag));
			TextView content = (TextView)view.findViewWithTag(getString(R.string.diary_entry_short_desc_tag));
				
			title.setText(bean.getTitle());
			content.setText(bean.getDescription());
			view.setVisibility(invisibleEntries.contains(bean.getId()) ? View.GONE : View.VISIBLE);
			return view;
		}
	}
	
	private LandingPageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing_page);
		ListView entriesList = (ListView)findViewById(R.id.diary_entries_list);
		entriesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				DiaryEntryBean bean = adapter.getItem(position);
				showEntryDetails(bean);
			}
		});
		registerForContextMenu(entriesList);
		initArchivalThread();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_landing_page, menu);
		return true;
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_add_new:
			handleAddNewMenuClick();
			break;
		case R.id.menu_search:
			onSearchRequested();
			break;
		case R.id.menu_clear_search:
			handleClearSearchMenuClick();
			break;
		case R.id.menu_export_selected:
			handleExportSelectedMenuClick();
			break;
		case R.id.menu_settings:
			handleSettingsMenuClick();
			break;
		case R.id.menu_exit:
			handleExitMenuClick();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.activity_list_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		switch(item.getItemId()) {
		case R.id.menu_edit_entry:
			handleEditEntryContextMenuClick(info.position);
			break;
		case R.id.menu_export_entry:
			handleExportEntryContextMenuClick(info.position);
			break;
		case R.id.menu_delete_entry:
			handleDeleteEntryContextMenuClick(info.position);
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void loadData() {
		new Thread() {
			@Override
			public void run() {
				ListView entriesList = (ListView)findViewById(R.id.diary_entries_list);				
				if(adapter == null) {
					adapter = new LandingPageAdapter(LandingPageActivity.this, -1);
					entriesList.setAdapter(adapter);
				} else {
					adapter.clear();
				}
				
				DiaryDatabaseDAO dao = new DiaryDatabaseDAO(getApplicationContext());
				List<DiaryEntryBean> listBeans = null;
				try {
					listBeans = dao.getDreamList(true);
					for(DiaryEntryBean bean : listBeans) {
						if(!invisibleEntries.contains(bean.getId()))
							adapter.add(bean);
					}
				} catch (DiaryException e) {
					showAlertDialog(getString(R.string.unable_to_load_entries_message));
				}
			}
		}.run();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null)
			return;
		switch(requestCode) {
		case DreamDiaryConstants.CERATE_ENTRY_REQUEST_CODE:
			handleCreateEntryResponse(resultCode, data);
			break;
		case DreamDiaryConstants.SEARCH_ENTRIES_REQUEST_CODE:
			handleSearchEntriesResponse(resultCode, data);
			break;
		case DreamDiaryConstants.ENTRY_UPDATE_REQUEST_CODE:
			handleEntryUpdateResponse(resultCode, data);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void handleCreateEntryResponse(int resultCode, Intent data) {
		if(resultCode == DreamDiaryConstants.ACTIVITY_CREATED_SUCCESFULLY_RESPONSE_CODE) {
			Object o = data.getExtras() == null ? null : data.getExtras().get(DreamDiaryConstants.NEW_ENTRY_KEY);
			if(o != null && o instanceof DiaryEntryBean) {
				adapter.add((DiaryEntryBean)o);
			}
		} else if(resultCode == DreamDiaryConstants.ACTIVITY_NOT_CREATED_SUCCESFULLY_RESPONSE_CODE) {
//			String message = data.getExtras() == null ? "" : data.getExtras().getString(DreamDiaryConstants.FAILURE_MESSAGE_KEY);
//			if(message != null && !message.isEmpty()) {
//				DreamDiaryUtils.showAlertDialog(this, message);
//			}
		}
	}
	
	private void handleSearchEntriesResponse(int resultCode, Intent data) {
		long[] ids = data.getExtras().getLongArray(DreamDiaryConstants.SEARCH_RESULTS_KEY);
		if(ids != null && ids.length > 0) {
			List<Long> list = new ArrayList<Long>(ids.length);
			for(long id : ids)
				list.add(id);
			showEntries(list);
		} else {
			showAlertDialog(getString(R.string.no_search_results_message));
			handleClearSearchMenuClick();
		}
	}

	@Override
	protected void onStart() {
		super.onStart(); 
		loadData();
	}

	@Override
	public boolean onSearchRequested() {
		Intent intent = new Intent(this, SearchActivity.class);
		startActivityForResult(intent, DreamDiaryConstants.SEARCH_ENTRIES_REQUEST_CODE);
		return false;
	}

	private void hideEntry(Long id) {
		invisibleEntries.add(id);
	}
	
	private void showEntry(Long id) {
		invisibleEntries.remove(id);
	}
	
	private void showEntries(List<Long> ids) {
		for(int i = 0; i < adapter.getCount(); i++) {
			DiaryEntryBean bean = adapter.getItem(i);
			if(!ids.contains(bean.getId())) 
				hideEntry(bean.getId());
			else 
				showEntry(bean.getId());
		}
	}
	
	private void showEntryDetails(DiaryEntryBean bean) {
		Intent intent = new Intent(this, ShowEntryDetailsActivity.class);
		intent.putExtra(DreamDiaryConstants.OLD_ENTRY_KEY, bean);
		startActivityForResult(intent, DreamDiaryConstants.ENTRY_UPDATE_REQUEST_CODE);
	}
	
	private void handleEntryUpdateResponse(int resultCode, Intent data) {
		if(resultCode == DreamDiaryConstants.ENTRY_UPDATE_SUCCESS_RESPONSE_CODE) {
			DiaryEntryBean newBean = (DiaryEntryBean) data.getExtras().getSerializable(DreamDiaryConstants.NEW_ENTRY_KEY);
			DiaryEntryBean oldBean = (DiaryEntryBean) data.getExtras().getSerializable(DreamDiaryConstants.OLD_ENTRY_KEY);
			if(oldBean != null)
				adapter.remove(oldBean);
			if(newBean != null)
				adapter.add(newBean);
		} else if(resultCode == DreamDiaryConstants.ENTRY_UPDATE_FAILURE_RESPONSE_CODE) {
			
		}
	}
	
	private void handleAddNewMenuClick() {
		Intent intent = new Intent(LandingPageActivity.this, AddNewDreamEntryActivity.class);
		startActivityForResult(intent, DreamDiaryConstants.CERATE_ENTRY_REQUEST_CODE);
	}
	
	private void handleExitMenuClick() {
		System.exit(0);
	}
	
	private void handleSettingsMenuClick() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	private void handleExportSelectedMenuClick() {
		int count = adapter.getCount();
		List<DiaryEntryBean> listBeans = new ArrayList<DiaryEntryBean>(count);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < count; i++) {
			DiaryEntryBean bean = adapter.getItem(i);
			listBeans.add(bean);
			if(sb.length() != 0)
				sb.append(", ").append(bean.toString());
			else
				sb.append(bean.toString());
		}
		exportEntries(listBeans, sb.toString(), false);
	}
	
	private void handleClearSearchMenuClick() {
		invisibleEntries.clear();
		loadData();
	}
	
	private void handleDeleteEntryContextMenuClick(int position) {
		final DiaryEntryBean bean = adapter.getItem(position);
		deleteDiaryEntry(bean, new Runnable() {
			@Override
			public void run() {
				handleClearSearchMenuClick();
			}
		}, null);
	}

	private void handleExportEntryContextMenuClick(int position) {
		DiaryEntryBean bean = adapter.getItem(position);
		List<DiaryEntryBean> listBeans = new ArrayList<DiaryEntryBean>(1);
		listBeans.add(bean);
		exportEntries(listBeans, bean.toString(), false);
	}

	private void handleEditEntryContextMenuClick(int position) {
		DiaryEntryBean bean = adapter.getItem(position);
		showEntryDetails(bean);
	}
	
	private void initArchivalThread() {
		new Thread() {
			public void run() {
				File externalDir = new File(DreamDiaryUtils.getExportPath(LandingPageActivity.this));
				File[] files = externalDir.listFiles(new FileFilter() {
					private final long now = new Date().getTime();
					@Override
					public boolean accept(File pathname) {
						if(!pathname.getAbsolutePath().endsWith(".zip") && pathname.isFile() && Math.abs((now - pathname.lastModified())) >= (DreamDiaryUtils.getAutoArchivalFrequency(LandingPageActivity.this) * 24l * 60l * 60l * 1000l))
							return true;
						return false;
					}
				});
				if(files == null || files.length == 0)
					return;
				ZipOutputStream zos = null;
				try {
					File outputZip = new File(externalDir, "archive." + DateFormat.getDateInstance().format(new Date()) + ".zip");
					zos = new ZipOutputStream(new FileOutputStream(outputZip));
					byte[] buffer = new byte[4096]; // Create a buffer for copying
					int bytesRead;
					for(File f : files) {
						FileInputStream in = new FileInputStream(f); // Stream to read file
					    ZipEntry entry = new ZipEntry(f.getName()); // Make a ZipEntry
					    zos.putNextEntry(entry); // Store entry
					    while ((bytesRead = in.read(buffer)) != -1)
					    	zos.write(buffer, 0, bytesRead);
					    in.close();
					    f.delete();
					}
					zos.close();
					zos = null;
				} catch(Throwable th) {
					Log.e(getString(R.string.app_name), "Unable to archive. Will try next time...", th);
				} finally {
					if(zos != null) {
						try {
							zos.close();
						} catch(Throwable th) {}
					}
				}
			}
		}.start();
	}
}
