/**
 * 
 */
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

/**
 * @author Chaitanya.Shende
 *
 */
public class AddNewDreamEntryActivity extends AbstractEntryDetailsActivity {

	@Override
	protected OnClickListener getActionButtonListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				final DiaryEntryBean bean = createDiaryEntryBean();
				String errors = validate(bean);
				if(!errors.isEmpty()) {
					showAlertDialog(errors);
					return;
				}
				Thread thread = new Thread() {
					public void run() {
						DiaryDatabaseDAO dao = new DiaryDatabaseDAO(getApplicationContext());
						try {
							dao.addDreamEntry(bean);
						} catch (DiaryException e) {
							Log.e(getString(R.string.app_name), getString(R.string.entry_creation_failure_message), e);
							showAlertDialog(getString(R.string.entry_creation_failure_message));
						}
					}
				};
				thread.start();
				try {
					thread.join();
				} catch (InterruptedException e) {
				}
				
				if(DreamDiaryUtils.isAutoExportEnabled(AddNewDreamEntryActivity.this)) {
					List<DiaryEntryBean> listBeans = new ArrayList<DiaryEntryBean>(1);
					listBeans.add(bean);
					exportEntries(listBeans, bean.toString(), true);
				}
				
				Intent result = new Intent();
				if(bean == null) {
					result.putExtra(DreamDiaryConstants.FAILURE_MESSAGE_KEY, getString(R.string.entry_creation_failure_message));
					setResult(DreamDiaryConstants.ACTIVITY_NOT_CREATED_SUCCESFULLY_RESPONSE_CODE, result);
					finish();
				} else {
					result.putExtra(DreamDiaryConstants.NEW_ENTRY_KEY, bean);
					setResult(DreamDiaryConstants.ACTIVITY_CREATED_SUCCESFULLY_RESPONSE_CODE, result);
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
		return R.string.add_entry_button_label;
	}

	@Override
	protected int getDeleteButtonVisibility() {
		return View.GONE;
	}

	@Override
	protected void initializeView() {
		// nothing special to do
	}
	
	protected OnClickListener getDeleteButtonListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				// nothing special to do
			}
		};
	}
}
