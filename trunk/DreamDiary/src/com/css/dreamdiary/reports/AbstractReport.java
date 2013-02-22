/**
 * 
 */
package com.css.dreamdiary.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.net.Uri;

import com.css.dreamdiary.model.DiaryEntryBean;

/**
 * @author Chaitanya.Shende
 *
 */
public abstract class AbstractReport implements IReport {

	private List<DiaryEntryBean> listBeans = null;
	private File destinationFile = null;
	private BufferedWriter writer = null;
	private String criteriaString = null;
	
	/* (non-Javadoc)
	 * @see com.css.dreamdiary.reports.IReport#generateReport(java.util.List, java.io.File)
	 */
	@Override
	public Uri generateReport(String criteriaString, List<DiaryEntryBean> listBeans, File destinationFile) {
		if(destinationFile == null || listBeans == null || listBeans.isEmpty())
			return null;
		this.listBeans = listBeans;
		this.destinationFile = destinationFile;
		this.criteriaString = criteriaString;
		writeHeader();
		for(DiaryEntryBean bean : listBeans) {
			writeRecordFor(bean);
		}
		writeFooter();
		try {
			writer.close();
		} catch (IOException e) {
		}
		return Uri.fromFile(destinationFile);
	}
	
	protected BufferedWriter getWriter() throws IOException {
		if(writer == null) 
			writer = new BufferedWriter(new FileWriter(destinationFile));
		return writer;
	}
	
	protected List<DiaryEntryBean> getListBeans() {
		return listBeans;
	}

	protected File getDestinationFile() {
		return destinationFile;
	}
	
	protected String getCriteriaString() {
		return criteriaString;
	}
	
	protected String format(Date date) {
		DateFormat dateFormat = SimpleDateFormat.getDateInstance();
		return dateFormat.format(date);
	}
	
	protected String format(float number) {
		NumberFormat numFormat = NumberFormat.getInstance();
		return numFormat.format(number);
	}
	
	protected String truncate(String string) {
		return truncate(string, 35);
	}
	
	protected String truncate(String string, int lenght) {
		if(string == null || string.isEmpty() || string.length() <= 35)
			return string;
		return string.substring(0, 35) + "...";
	}
	
	protected String getShortCriteriaString() {
		return truncate(getCriteriaString(), 50);
	}

	protected abstract void writeHeader();
	protected abstract void writeRecordFor(DiaryEntryBean bean);
	protected abstract void writeFooter();
}
