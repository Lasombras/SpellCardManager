package com.lasombras.android.scm.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lasombras.android.scm.daolayer.DeckDAO;
import com.lasombras.android.scm.daolayer.SpellInfoDAO;

public class DBAdapter 
{
    
    private static final String DATABASE_NAME = "spellCardManager";
    private static final int DATABASE_VERSION = 3;
       
    private final Context context; 
    
    private DatabaseHelper DBHelper;

    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            Log.v("BD", "Create Database ");
            db.execSQL(DeckDAO.SQL_CREATE_TABLE_DECK);
            db.execSQL(DeckDAO.SQL_CREATE_TABLE_DECK_CARDS);
            db.execSQL(SpellInfoDAO.SQL_CREATE_TABLE);
       }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
        {
            db.execSQL("ALTER TABLE tbl_decks ADD deck_file_id LONG");
//            db.execSQL("DROP TABLE IF EXISTS tbl_decks");
//            db.execSQL("DROP TABLE IF EXISTS tbl_decks_cards");
            //onCreate(db);
        }
    }    
    
    //---opens the database---
    public SQLiteDatabase open() throws SQLException 
    {
     	return DBHelper.getWritableDatabase();
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
}