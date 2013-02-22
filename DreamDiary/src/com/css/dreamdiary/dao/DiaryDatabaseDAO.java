/**
 * 
 */
package com.css.dreamdiary.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import com.css.dreamdiary.R;
import com.css.dreamdiary.exceptions.DiaryException;
import com.css.dreamdiary.index.DiaryIndexReader;
import com.css.dreamdiary.index.DiaryIndexer;
import com.css.dreamdiary.model.DiaryEntryBean;

/**
 * @author Chaitanya.Shende
 *
 */
public class DiaryDatabaseDAO extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "diaryDatabase";
	private static final int DATABASE_VERSION = 1;
	
	private static final String MOODS_TABLE = "moods";
	private static final String MOODS_ID = "moods_id";
	private static final String MOODS_DESC = "moods_desc";
	
	private static final String TYPES_TABLE = "types";
	private static final String TYPE_ID = "type_id";
	private static final String TYPE_DESC = "type_desc";
	
	private static final String DIARY_TABLE = "diary";
	private static final String DREAM_ID = "dream_id";
	private static final String DREAM_TITLE = "dream_title";
	private static final String DREAM_CONTENT = "dream_content";
	private static final String DREAM_TYPE = "dream_type_id";
	private static final String DREAM_MOOD = "dream_mood_id";
	private static final String DREAM_SLEEP_HOURS = "dream_sleep_hrs";
	private static final String DREAM_CREATED = "created";
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private static final SimpleDateFormat betweenDateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
	private static final int SHORT_DESCRIPTION_LENGTH = 64;
	
	private String appName;
	private Context context;
	private String[] allMoods = null;
	private String[] allTypes = null;

	public DiaryDatabaseDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        appName = context.getString(R.string.app_name);
        allMoods = context.getResources().getStringArray(R.array.moods);
        allTypes = context.getResources().getStringArray(R.array.types);
        this.context = context; 
    }
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createMoodsTableQuery = "CREATE TABLE " + MOODS_TABLE + " (" +
				MOODS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
				MOODS_DESC + " TEXT NOT NULL);";
		db.execSQL(createMoodsTableQuery);
		initMoodsTable(db);
		
		String createTypesTableQuery = "CREATE TABLE " + TYPES_TABLE + " (" +
				TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
				TYPE_DESC + " TEXT NOT NULL);";
		db.execSQL(createTypesTableQuery);
		initTypesTable(db);
		
		
		String createDiaryTable = "CREATE TABLE " + DIARY_TABLE + " (" + 
				DREAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				DREAM_TITLE + " TEXT NOT NULL," +
				DREAM_CONTENT + " TEXT," +
				DREAM_MOOD + " INTEGER NOT NULL," +
				DREAM_TYPE + " INTEGER NOT NULL," +
				DREAM_SLEEP_HOURS + " REAL NOT NULL," + 
				DREAM_CREATED + " DATE NOT NULL , " +
				" FOREIGN KEY(" + DREAM_MOOD + ") REFERENCES " + MOODS_TABLE + "(" + MOODS_ID + ")," +
				" FOREIGN KEY(" + DREAM_TYPE + ") REFERENCES " + TYPES_TABLE + "(" + TYPE_ID + "));";
		db.execSQL(createDiaryTable);
	}
	
	private void initMoodsTable(SQLiteDatabase db) {
		if(allMoods != null && allMoods.length > 0) {
			db.beginTransaction();
			try {
				for(String s : allMoods) {
					ContentValues values = new ContentValues();
					values.put(MOODS_DESC, s);
					db.insert(MOODS_TABLE, null, values);
				}
				db.setTransactionSuccessful();
			} catch(Throwable th) {
				Log.e(appName, "Could not add mood entries");
			}
			db.endTransaction();
		}
	}
	
	private void initTypesTable(SQLiteDatabase db) {
		if(allTypes != null && allTypes.length > 0) {
			db.beginTransaction();
			try {
				for(String s : allTypes) {
					ContentValues values = new ContentValues();
					values.put(TYPE_DESC, s);
					db.insert(TYPES_TABLE, null, values);
				}
				db.setTransactionSuccessful();
			} catch(Throwable th) {
				Log.e(appName, "Could not add type entries");
			}
			db.endTransaction();
		}
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.delete(MOODS_TABLE, null, null);
		db.delete(TYPES_TABLE, null, null);
		initMoodsTable(db);
		initTypesTable(db);
	}
	
	public void addDreamEntry(DiaryEntryBean bean) throws DiaryException {
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(MOODS_TABLE, new String[] { MOODS_ID }, MOODS_DESC + "=?", new String[] { bean.getMood() }, null, null, null, null);
		if(cursor == null) {
			Log.e(appName, "Could not make an entry into db");
		}
		cursor.moveToFirst();
		int moodId = cursor.getInt(0);
	
		cursor = db.query(TYPES_TABLE, new String[] { TYPE_ID }, TYPE_DESC + "=?", new String[] { bean.getType() }, null, null, null, null);
		if(cursor == null) {
			Log.e(appName, "Could not make an entry into db");
		}
		cursor.moveToFirst();
		int typeId = cursor.getInt(0);
		db.close();
		
		/* insert code */
		db = getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(DREAM_TITLE, bean.getTitle());
			values.put(DREAM_CONTENT, bean.getDescription());
			values.put(DREAM_MOOD, moodId);
			values.put(DREAM_TYPE, typeId);
			values.put(DREAM_SLEEP_HOURS, bean.getSleepHours());
			values.put(DREAM_CREATED, dateFormat.format(bean.getCreationDateTime()));
			long id = db.insert(DIARY_TABLE, null, values);
			bean.setId(id);
			db.setTransactionSuccessful();
		} catch(Throwable th) {
			Log.e(appName, "Could not make an entry into db. Reason: " + th.getMessage());
		}
		db.endTransaction();
		db.close();
		
		DiaryIndexer indexer = new DiaryIndexer(context);
		indexer.createIndex(bean);
	}
	
	public DiaryEntryBean getDreamEntry(long id) throws DiaryException {
		return getDreamEntry(id, false);
	}
	
	public DiaryEntryBean getDreamEntry(long id, boolean withShortDescriptions) throws DiaryException {
		SQLiteDatabase db = null;
		try {
			db = getReadableDatabase();
			
			Cursor cursor = db.query(DIARY_TABLE, new String[] {DREAM_ID, DREAM_TITLE, DREAM_CONTENT, DREAM_TYPE, DREAM_MOOD, DREAM_SLEEP_HOURS, DREAM_CREATED}, DREAM_ID + "=?", new String[] {Long.toString(id)} ,null, null, null);
			if(cursor == null) {
				Log.e(appName, "Could not find an entry from db with id: " + id);
				return null;
			}
			if(cursor.getCount() == 0)
				return null;
			cursor.moveToFirst();
			String title = cursor.getString(1);
			String content = cursor.getString(2);
			if(withShortDescriptions && content.length() > SHORT_DESCRIPTION_LENGTH)
				content = content.substring(0, SHORT_DESCRIPTION_LENGTH) + "...";
			String type = "";
			long typeId = cursor.getLong(3);
			
			String mood = "";
			long moodId = cursor.getLong(4);
			
			float sleepHours = cursor.getFloat(5);
			Date creationDateTime = new Date();
			try {
				creationDateTime = dateFormat.parse(cursor.getString(6));
			} catch (ParseException e) {
			}
			
			cursor = db.query(TYPES_TABLE, new String[] { TYPE_DESC }, TYPE_ID + "=?", new String[] { Long.toString(typeId) }, null, null, null, null);
			if(cursor == null) {
				Log.e(appName, "Could not find an entry from db with id: " + id);
			}
			cursor.moveToFirst();
			type = cursor.getString(0);
			
			cursor = db.query(MOODS_TABLE, new String[] { MOODS_DESC }, MOODS_ID + "=?", new String[] { Long.toString(moodId) }, null, null, null, null);
			if(cursor == null) {
				Log.e(appName, "Could not find an entry from db with id: " + id);
			}
			cursor.moveToFirst();
			mood = cursor.getString(0);
			
			DiaryEntryBean bean = new DiaryEntryBean(id, title, type, mood, sleepHours, content, creationDateTime);
			return bean;
		} finally {
			if(db != null && db.isOpen())
				db.close();
		}
	}
	
	public Map<String, List<DiaryEntryBean>> getDreams(List<Long> ids) throws DiaryException {
		Map<String, List<DiaryEntryBean>> result = new HashMap<String, List<DiaryEntryBean>>();
		for(Long id : ids) {
			DiaryEntryBean bean = getDreamEntry(id);
			if(!result.containsKey(bean.getMood())) {
				result.put(bean.getMood(), new ArrayList<DiaryEntryBean>());
			}
			result.get(bean.getMood()).add(bean);
		}
		return result;
	}
	
	/*public Map<String, List<DiaryEntryBean>> getDreams() throws DiaryException {
		return getDreams(null, null, null, null);
	}*/
	
	/*public List<DiaryEntryBean> getDreamList(boolean withShortDescriptions, List<Pair<String, String>> searchCriteria, Date from, Date to) throws DiaryException {
		DiaryIndexReader reader = new DiaryIndexReader(context);
		long[] intIds = reader.findDiaryEntries(searchCriteria, from, to);
		List<DiaryEntryBean> list = new ArrayList<DiaryEntryBean>(intIds.length);
		for(long id : intIds) {
			DiaryEntryBean bean = getDreamEntry(id, withShortDescriptions);
			if(bean != null)
				list.add(bean);
		}
		return list;
	}*/
	
	public long[] getDreamIdsList(List<Pair<String, String>> searchCriteria, Date from, Date to) throws DiaryException {
		DiaryIndexReader reader = new DiaryIndexReader(context);
		long[] intIds = reader.findDiaryEntries(searchCriteria, from, to);
		return intIds;
	}
	
	public List<DiaryEntryBean> getDreamList(boolean withShortDescriptions) throws DiaryException {
		DiaryIndexReader reader = new DiaryIndexReader(context);
		
		long[] intIds = reader.getAllIds(Integer.MAX_VALUE);
		List<DiaryEntryBean> list = new ArrayList<DiaryEntryBean>(intIds.length);
		for(long id : intIds) {
			DiaryEntryBean bean = getDreamEntry(id, withShortDescriptions);
			if(bean != null)
				list.add(bean);
		}
		return list;
	}
	
	/*public Map<String, List<DiaryEntryBean>> getDreams(Date from, Date to, String keywords, String filterMood) throws DiaryException {
		List<Long> ids = new ArrayList<Long>();
		
		DiaryIndexReader reader = new DiaryIndexReader(context);
		
		long[] intIds = reader.findDiaryEntries(from, to, keywords, filterMood);
		for(long i : intIds) {
			ids.add(i);
		}
		
		reader.closeIndex();
		
		return getDreams(ids);
	}
	*/
	public void updateDreamEntry(DiaryEntryBean bean) throws DiaryException {
		SQLiteDatabase db = null;
		try {
			db = getReadableDatabase();
			Cursor cursor = db.query(MOODS_TABLE, new String[] { MOODS_ID }, MOODS_DESC + "=?", new String[] { bean.getMood() }, null, null, null, null);
			if(cursor == null) {
				Log.e(appName, "Could not make an entry into db");
			}
			cursor.moveToFirst();
			int moodId = cursor.getInt(0);
			
			cursor = db.query(TYPES_TABLE, new String[] { TYPE_ID }, TYPE_DESC + "=?", new String[] { bean.getType() }, null, null, null, null);
			if(cursor == null) {
				Log.e(appName, "Could not make an entry into db");
			}
			cursor.moveToFirst();
			int typeId = cursor.getInt(0);
			db.close();
			
			/* update code */
			db = getWritableDatabase();
			db.beginTransaction();
			try {
				ContentValues values = new ContentValues();
				values.put(DREAM_TITLE, bean.getTitle());
				values.put(DREAM_CONTENT, bean.getDescription());
				values.put(DREAM_TYPE, typeId);
				values.put(DREAM_MOOD, moodId);
				values.put(DREAM_SLEEP_HOURS, bean.getSleepHours());
				values.put(DREAM_CREATED, dateFormat.format(bean.getCreationDateTime()));
				db.update(DIARY_TABLE, values, DREAM_ID + "=?", new String[] {Long.toString(bean.getId())});
				db.setTransactionSuccessful();
			} catch(Throwable th) {
				Log.e(appName, "Could not make an entry into db. Reason: " + th.getMessage());
			}
			db.endTransaction();
			db.close();
			
			DiaryIndexer indexer = new DiaryIndexer(context);
			indexer.updateIndex(bean);
		} finally {
			if(db != null && db.isOpen())
				db.close();
		}
	}
	
	public void deleteDreamEntry(DiaryEntryBean bean) throws DiaryException {
		SQLiteDatabase db = null;
		try {
			/* delete code */
			db = getWritableDatabase();
			db.beginTransaction();
			try {
				db.delete(DIARY_TABLE, DREAM_ID + "=?", new String[] {Long.toString(bean.getId())});
				db.setTransactionSuccessful();
			} catch(Throwable th) {
				Log.e(appName, "Could not make an entry into db. Reason: " + th.getMessage());
			}
			db.endTransaction();
			db.close();
			
			DiaryIndexer indexer = new DiaryIndexer(context);
			indexer.deleteIndex(bean);
		} finally {
			if(db != null && db.isOpen())
				db.close();
		}
	}
	
	public void saveEntry(DiaryEntryBean bean) throws DiaryException {
		if(bean.getId() == -1) {
			addDreamEntry(bean);
		} else {
			updateDreamEntry(bean);
		}
	}
	public long[] getDreamIdsList(String queryString) throws DiaryException {
		DiaryIndexReader reader = new DiaryIndexReader(context);
		return reader.findDiaryEntries(queryString);
	}

}
