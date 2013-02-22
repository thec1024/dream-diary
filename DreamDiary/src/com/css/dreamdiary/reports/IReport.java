package com.css.dreamdiary.reports;

import java.io.File;
import java.util.List;

import android.net.Uri;

import com.css.dreamdiary.model.DiaryEntryBean;

public interface IReport {
	public Uri generateReport(String criteriaString, List<DiaryEntryBean> listBeans, File destinationFile);
}
