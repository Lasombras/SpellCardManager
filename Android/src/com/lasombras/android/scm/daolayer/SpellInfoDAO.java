package com.lasombras.android.scm.daolayer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SpellInfoDAO {
	public final static String TABLE_NAME = "TBL_SPELL_INFO";
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "spell_title VARCHAR(50),"
			+ "note TEXT);";  

	private SQLiteDatabase sqliteDatabase;
	public SpellInfoDAO(SQLiteDatabase db) {
		this.sqliteDatabase = db;
	}

	public String getComment(String spellOriginalName) {
		String comment = "";
		Cursor cursor = sqliteDatabase.query(TABLE_NAME, new String[] {"note"}, "spell_title='" + spellOriginalName.replaceAll("'", "''") + "'", null,  
				null, null, null);  
		if (cursor != null) {
			if(cursor.getCount() > 0) {
				cursor.moveToFirst(); 
				comment = cursor.getString(cursor.getColumnIndex("note"));
			}
			cursor.close();
		}
		return comment;
	}

	public boolean delete(String spellOriginalName) {
		return sqliteDatabase.delete(TABLE_NAME, "spell_title='" + spellOriginalName.replaceAll("'", "''") + "'", null) > 0;
	}

	public long saveComment(String spellOriginalName, String comment) {
		delete(spellOriginalName);
		if(comment != null && comment.length() > 0) {
			ContentValues values = new ContentValues();  
			values.put("spell_title", spellOriginalName);
			values.put("note", comment);
			return sqliteDatabase.insert(TABLE_NAME, null, values);
		}
		return -1;
	}
}
