/**
 * 
 */
package com.css.dreamdiary;

import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.css.dreamdiary.model.DiaryEntryBean;

/**
 * @author Chaitanya.Shende
 *
 */
public abstract class AbstractEntryDetailsActivity extends AbstractDiaryActivity {
	private Date selectedDate = new Date();
	private Dialog dateDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_create_diary_entry);
		
		Button addButton = (Button)findViewById(R.id.add_entry_button);
		Button deleteButton = (Button)findViewById(R.id.delete_entry_button);
		
		addButton.setVisibility(getActionButtonVisibility());
		addButton.setText(getActionButtonLabel());
		addButton.setOnClickListener(getActionButtonListener());
		
		deleteButton.setVisibility(getDeleteButtonVisibility());
		deleteButton.setOnClickListener(getDeleteButtonListener());
		
		Button cancelButton = (Button)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(getCancelButtonListener());
		
		ImageButton dateButton = (ImageButton)findViewById(R.id.select_date_button);
		dateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDateView();
			}
		});
		
		updateSelectedDate();
		initializeView();
	}

	protected void showDateView() {
		dateDialog = new Dialog(this);
		View dateView = getLayoutInflater().inflate(R.layout.date_picker_view, null);
		dateDialog.setContentView(dateView);
		dateDialog.show();
	}
	
	protected Date getSelectedDate() {
		return selectedDate;
	}

	protected void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
		updateSelectedDate();
	}
	
	public void onDateSelectOk(View view) {
		if(dateDialog == null) return;
		View dateView = dateDialog.findViewById(R.id.date_picker);
		if(dateView instanceof DatePicker) {
			DatePicker picker = (DatePicker)dateView;
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, picker.getDayOfMonth());
			cal.set(Calendar.MONTH, picker.getMonth());
			cal.set(Calendar.YEAR, picker.getYear());
			setSelectedDate(cal.getTime());
		}
		dateDialog.dismiss();
		dateDialog = null;
	}
	
	public void onDateSelectCancel(View dateView) {
		dateDialog.dismiss();
		dateDialog = null;
	}
	
	protected void updateSelectedDate() {
		TextView selectedDateText = (TextView)findViewById(R.id.selected_date);
		selectedDateText.setText(DateFormat.getMediumDateFormat(this).format(selectedDate));
	}
	
	protected DiaryEntryBean createDiaryEntryBean() {
		EditText titleText = (EditText)findViewById(R.id.entry_title);
		String title = titleText.getText() == null || titleText.getText().toString().trim().isEmpty() ? null : titleText.getText().toString();
		
		Spinner typeSpiner = (Spinner)findViewById(R.id.entry_type);
		String type = typeSpiner.getSelectedItem() == null ? "" : typeSpiner.getSelectedItem().toString();
		
		Spinner moodSpiner = (Spinner)findViewById(R.id.entry_mood);
		String mood = moodSpiner.getSelectedItem() == null ? "" : moodSpiner.getSelectedItem().toString();
		
		EditText contentText = (EditText)findViewById(R.id.entry_content);
		String content = contentText.getText() == null || contentText.getText().toString().trim().isEmpty() ? null : contentText.getText().toString();
		
		EditText sleepHoursText = (EditText)findViewById(R.id.entry_sleep_duration);
		float sleepHours = sleepHoursText.getText() == null || sleepHoursText.getText().toString().trim().isEmpty() ? 0.0f : Float.valueOf(sleepHoursText.getText().toString());
				
		DiaryEntryBean bean = new DiaryEntryBean(-1, title, type, mood, sleepHours, content, getSelectedDate());
		return bean;
	}
	
	protected EditText getTitleControl() {
		return (EditText)findViewById(R.id.entry_title);
	}
	
	protected Spinner getMoodControl() {
		return (Spinner)findViewById(R.id.entry_mood);
	}
	
	protected EditText getContentControl() {
		return (EditText)findViewById(R.id.entry_content);
	}
	
	protected EditText getSleepHoursControl() {
		return (EditText)findViewById(R.id.entry_sleep_duration);
	}
	
	protected String validate(DiaryEntryBean bean) {
		StringBuilder sb = new StringBuilder();
		if(bean != null) {
			String title = bean.getTitle();
			if(title == null || title.trim().isEmpty())
				sb.append(getString(R.string.invalid_entry_title_message)).append("\n");
			String mood = bean.getMood();
			if(mood == null || mood.trim().isEmpty())
				sb.append(getString(R.string.invalid_entry_mood_message)).append("\n");
			boolean found = false;
			String[] moods = getResources().getStringArray(R.array.moods);
			for(int i = 0; i < moods.length; i++) {
				if(mood.equalsIgnoreCase(moods[i])) {
					found = true;
					break;
				}
			}
			if(!found)
				sb.append(getString(R.string.invalid_entry_mood_message)).append("\n");
			String content = bean.getDescription();
			if(content == null || content.trim().isEmpty())
				sb.append(getString(R.string.invalid_entry_content_message)).append("\n");
			float sleepHrs = bean.getSleepHours();
			if(sleepHrs <= 0.0 || sleepHrs > 24.0) 
				sb.append(getString(R.string.invalid_entry_sleep_hrs_message)).append("\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * Suppose to return the listener which handles add or update events
	 * @return
	 */
	protected abstract OnClickListener getActionButtonListener();
	protected abstract OnClickListener getCancelButtonListener();
	protected abstract OnClickListener getDeleteButtonListener();
	protected abstract int getActionButtonVisibility();
	protected abstract int getActionButtonLabel();
	protected abstract int getDeleteButtonVisibility();
	protected abstract void initializeView();
}
