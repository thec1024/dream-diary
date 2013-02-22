/**
 * 
 */
package com.css.dreamdiary.index;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import android.content.Context;
import android.util.Log;

import com.css.dreamdiary.R;
import com.css.dreamdiary.model.DiaryEntryBean;

/**
 * @author Chaitanya.Shende
 *
 */
public class DiaryIndexer {
	private IndexWriter writer;
	private Directory indexDirectory;
	private IndexWriterConfig writerConfig;
	private String appName;
	
	public DiaryIndexer(Context context) {
		try {
			indexDirectory = new NIOFSDirectory(new File(context.getExternalFilesDir(null), "dreamIndexes"));
			writerConfig = new IndexWriterConfig(Version.LUCENE_41, new StandardAnalyzer(Version.LUCENE_41));
			appName = context.getString(R.string.app_name);
		} catch(Throwable e) {
			Log.e(context.getString(R.string.app_name), "Could not create index writer", e);
		}
	}
	
	public void initializeIndex() {
		try {
			writer = new IndexWriter(indexDirectory, writerConfig);
			writer.close();
		} catch(IOException ioe) {}
	}
	
	public void createIndex(DiaryEntryBean bean) {
		try {
			writer = new IndexWriter(indexDirectory, writerConfig);
			Document doc = new Document();
			
			doc.add(new LongField(DiaryEntryBean.FIELD_ID, bean.getId(), Store.YES));
			doc.add(new StringField(DiaryEntryBean.FIELD_TITLE, bean.getTitle().toLowerCase(Locale.US), Store.NO));			
			doc.add(new StringField(DiaryEntryBean.FIELD_CONTENT, bean.getDescription().toLowerCase(Locale.US), Store.NO));
			doc.add(new StringField(DiaryEntryBean.FIELD_TYPE, bean.getType().toLowerCase(Locale.US), Store.NO));
			doc.add(new StringField(DiaryEntryBean.FIELD_MOOD, bean.getMood().toLowerCase(Locale.US), Store.NO));
			doc.add(new FloatField(DiaryEntryBean.FIELD_SLEEP_HRS, bean.getSleepHours(), Store.NO));
			doc.add(new LongField(DiaryEntryBean.FIELD_CREATED, bean.getCreationDateTime().getTime(), Store.NO));
			writer.addDocument(doc);
		} catch (Throwable e) {
			Log.e(appName, "Could not create index writer", e);
		} finally {
			if(writer != null) {
				try {
					writer.commit();
					writer.close(true);
				} catch (IOException e) {
					Log.e(appName, "Could not close index writer", e);
				}
			}
		}
	}
	
	public void updateIndex(DiaryEntryBean bean) {
//		try {
//			writer = new IndexWriter(indexDirectory, writerConfig);
//			writer.deleteDocuments(new Term(DiaryEntryBean.FIELD_ID, Long.toString(bean.getId())));
//			writer.update
//			writer.commit();
//		} catch(Throwable th) {
//			Log.e(appName, "Could not create delete document with id: " + bean.getId(), th);
//			return;
//		}
		try {			
			Document doc = new Document();
			doc.add(new LongField(DiaryEntryBean.FIELD_ID, bean.getId(), Store.YES));
			doc.add(new StringField(DiaryEntryBean.FIELD_TITLE, bean.getTitle(), Store.NO));			
			doc.add(new TextField(DiaryEntryBean.FIELD_CONTENT, bean.getDescription(), Store.NO));
			doc.add(new StringField(DiaryEntryBean.FIELD_TYPE, bean.getType(), Store.NO));
			doc.add(new StringField(DiaryEntryBean.FIELD_MOOD, bean.getMood(), Store.NO));
			doc.add(new FloatField(DiaryEntryBean.FIELD_SLEEP_HRS, bean.getSleepHours(), Store.NO));
			doc.add(new LongField(DiaryEntryBean.FIELD_CREATED, bean.getCreationDateTime().getTime(), Store.NO));
			writer.updateDocument(new Term(DiaryEntryBean.FIELD_ID, Long.toString(bean.getId())), doc);
		} catch (Throwable e) {
			Log.e(appName, "Could not create index document", e);
		} finally {
			if(writer != null) {
				try {
					writer.commit();
					writer.close(true);
				} catch (IOException e) {
					Log.e(appName, "Could not close index writer", e);
				}
			}
		}
	}

	public void deleteIndex(DiaryEntryBean bean) {
		try {
			writer = new IndexWriter(indexDirectory, writerConfig);
			Term idTerm = new Term(DiaryEntryBean.FIELD_ID, Long.toString(bean.getId()));
			writer.deleteDocuments(idTerm);
		} catch (Throwable e) {
			Log.e(appName, "Could not create index writer", e);
		} finally {
			if(writer != null) {
				try {
					writer.commit();
					writer.close(true);
				} catch (IOException e) {
					Log.e(appName, "Could not close index writer", e);
				}
			}
		}
	}
}
