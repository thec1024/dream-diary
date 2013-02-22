/**
 * 
 */
package com.css.dreamdiary.reports;

import android.content.Intent;
import android.net.Uri;

/**
 * @author Chaitanya.Shende
 *
 */
public enum ReportTypes {
	HTML {
		@Override
		public IReport getReportTypeImpl() {
			return new HTMLReport();
		}

		@Override
		public String getExtension() {
			return ".html";
		}
		
		@Override
		public Intent getHandlerIntent(Uri uri) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(uri, "text/html");
			return intent;
		};
	};
	public abstract IReport getReportTypeImpl();
	public abstract String getExtension();
	public abstract Intent getHandlerIntent(Uri uri);
}
