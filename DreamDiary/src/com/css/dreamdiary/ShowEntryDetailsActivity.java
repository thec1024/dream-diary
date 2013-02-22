package com.css.dreamdiary;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.css.dreamdiary.dao.DiaryDatabaseDAO;
import com.css.dreamdiary.exceptions.DiaryException;
import com.css.dreamdiary.model.DiaryEntryBean;
import com.css.dreamdiary.utils.DreamDiaryConstants;
import com.css.dreamdiary.utils.DreamDiaryUtils;

public class ShowEntryDetailsActivity extends AbstractEntryDetailsActivity {
	private DiaryEntryBean oldBean = null;
	private DiaryEntryBean newBean = null;
	
	@Override
	protected OnClickListener getActionButtonListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				newBean = createDiaryEntryBean();
				if(newBean != null)
					newBean.setId(oldBean.getId());
				String errors = validate(newBean);
				if(!errors.isEmpty()) {
					showAlertDialog(errors);
					return;
				}
				Thread thread = new Thread() {
					@Override
					public void run() {
						if(newBean != null) {
							DiaryDatabaseDAO dao = new DiaryDatabaseDAO(ShowEntryDetailsActivity.this);
							try {
								dao.updateDreamEntry(newBean);
							} catch (DiaryException e) {
								Log.e(getString(R.string.app_name), getString(R.string.entry_update_failure_message), e);
								showAlertDialog(getString(R.string.entry_update_failure_message));
							}
						}
					}
				};
				thread.start();
				try {
					thread.join();
				} catch(InterruptedException ie) {}
				if(DreamDiaryUtils.isAutoExportEnabled(ShowEntryDetailsActivity.this)) {
					List<DiaryEntryBean> listBeans = new ArrayList<DiaryEntryBean>(1);
					listBeans.add(newBean);
					exportEntries(listBeans, newBean.toString(), true);
				}
				
				Intent result = new Intent();
				if(newBean == null) {
					result.putExtra(DreamDiaryConstants.FAILURE_MESSAGE_KEY, getString(R.string.entry_update_failure_message));
					setResult(DreamDiaryConstants.ENTRY_UPDATE_FAILURE_RESPONSE_CODE, result);
					finish();
				} else {
					result.putExtra(DreamDiaryConstants.NEW_ENTRY_KEY, newBean);
					result.putExtra(DreamDiaryConstants.OLD_ENTRY_KEY, oldBean);
					setResult(DreamDiaryConstants.ENTRY_UPDATE_SUCCESS_RESPONSE_CODE, result);
					finish();
				}
				
			}
		};
	}

	@Override
	protected OnClickListener getCancelButtonListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(DreamDiaryConstants.ACTIVITY_CREATION_CANCELLED_RESPONSE_CODE);
				finish();
			}
		};
	}
	
	@Override
	protected int getActionButtonVisibility() {
		return View.VISIBLE;
	}
	
	@Override
	protected int getActionButtonLabel() {
		return R.string.update_entry_button_label;
	}
	
	@Override
	protected int getDeleteButtonVisibility() {
		return View.VISIBLE;
	}
	
	protected OnClickListener getDeleteButtonListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteDiaryEntry(oldBean, new Runnable() {
					@Override
					public void run() {
						Intent result = new Intent();
						result.putExtra(DreamDiaryConstants.NEW_ENTRY_KEY, newBean);
						result.putExtra(DreamDiaryConstants.OLD_ENTRY_KEY, oldBean);
						setResult(DreamDiaryConstants.ENTRY_UPDATE_SUCCESS_RESPONSE_CODE, result);
						finish();
					}
				}, null);
			}
		};
	}

	@Override
	protected void initializeView() {
		oldBean = (DiaryEntryBean) getIntent().getSerializableExtra(DreamDiaryConstants.OLD_ENTRY_KEY);
		if(oldBean == null)
			return;
		if(oldBean.getTitle() != null)
			getTitleControl().setText(oldBean.getTitle());
		if(oldBean.getCreationDateTime() != null)
			setSelectedDate(oldBean.getCreationDateTime());
		if(oldBean.getDescription() != null)
			getContentControl().setText(oldBean.getDescription());
		int position = -1;
		String[] moods = getResources().getStringArray(R.array.moods);
		for(int i = 0; i < moods.length; i++) {
			if(oldBean.getMood().equalsIgnoreCase(moods[i])) {
				position = i;
				break;
			}
				
		}
		if(position != -1)
			getMoodControl().setSelection(position);
		getSleepHoursControl().setText(Float.toString(oldBean.getSleepHours()));
	}
}
