/**
 * 
 */
package com.css.dreamdiary;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.css.dreamdiary.dao.DiaryDatabaseDAO;
import com.css.dreamdiary.exceptions.DiaryException;
import com.css.dreamdiary.model.DiaryEntryBean;
import com.css.dreamdiary.reports.IReport;
import com.css.dreamdiary.reports.ReportTypes;
import com.css.dreamdiary.utils.DreamDiaryUtils;

/**
 * @author Chaitanya.Shende
 *
 */
public abstract class AbstractDiaryActivity extends Activity {

	protected void deleteDiaryEntry(final DiaryEntryBean bean, final Runnable onOkClick, final Runnable onCancelClick) {
		final AbstractDiaryActivity _this = this;
		new AlertDialog.Builder(this).setMessage(R.string.confirmation_message).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Thread thread = new Thread() {
					public void run() {
						DiaryDatabaseDAO dao = new DiaryDatabaseDAO(_this);
						try {
							dao.deleteDreamEntry(bean);
							showAlertDialog(getString(R.string.delete_successful_message));
						} catch (DiaryException e) {
							showAlertDialog(getString(R.string.delete_failure_message));
						}
					}
				};
				thread.start();
				try {
					thread.join();
				} catch (InterruptedException e) {
				}
				dialog.dismiss();
				if(onOkClick != null)
					onOkClick.run();
			}
		}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(onCancelClick != null)
					onCancelClick.run();
			}
		}).show();
	}
	
	
	
	protected void showAlertDialog(final String message, final boolean showAsToast) {
		final AbstractDiaryActivity _this = this;
		runOnUiThread(new Runnable() {
			public void run() {
				if(!showAsToast)
					new AlertDialog.Builder(_this).setMessage(message).show();
				else
					Toast.makeText(_this, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	protected void showAlertDialog(final String message) {
		showAlertDialog(message, true);
	}
	
	protected void exportEntries(final List<DiaryEntryBean> listBeans, final String criteriaString, final boolean silent) {
		if(listBeans == null || listBeans.isEmpty()) {
			showAlertDialog(getString(R.string.no_entries_selected_message));
			return;
		}
		final AbstractDiaryActivity _this = this;
		Thread thread = new Thread() {
			@Override
			public void run() {
				IReport report = ReportTypes.HTML.getReportTypeImpl();
				String fileName = listBeans.size() == 1 ? 
						listBeans.get(0).getTitle() + ReportTypes.HTML.getExtension() : 
						"report_" + DateFormat.getDateTimeInstance().format(new Date())+ ReportTypes.HTML.getExtension();
				fileName = fileName.replace(" ", "").replace(":", "").replace(",", "");
				File externalReportsDir = new File(DreamDiaryUtils.getExportPath(_this));
				externalReportsDir.mkdirs();
				File destinationFile = new File(externalReportsDir, fileName);
				try {
					destinationFile.createNewFile();
				} catch (IOException e) {
					showAlertDialog(getString(R.string.export_failure_message));
					return;
				}
				Uri uri = report.generateReport(criteriaString, listBeans, destinationFile);
				if(!silent) {
					/* right now its only HTML... there may be some more types going ahead */
					Intent intent = ReportTypes.HTML.getHandlerIntent(uri);
					startActivity(intent);
				}
			}
		};
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
	}
	
//	public synchronized static ProgressDialog showProgressDialog(final Activity activity, final String message) {
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				dialog = new ProgressDialog(activity);
//				dialog.setMessage(message);
//				dialog.setIndeterminate(true);
//				dialog.show();
//			}
//		});
//		return dialog;
//	}
//	
//	public synchronized static void hideProgressDialog(final Activity activity) {
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {	
//				if(dialog != null && dialog.isShowing()) {
//					dialog.dismiss();
//					dialog = null;
//				}
//			}
//		});
//	}
}
