package com.lasombras.android.scm.daolayer;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lasombras.android.scm.model.Deck;

public class DeckDAO {
	public static final String SQL_CREATE_TABLE_DECK = "CREATE TABLE tbl_decks ("
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "deck_name VARCHAR(50) not null,"
			+ "playerClass_id INTEGER,"
			+ "favorite INTEGER,"
			+ "deck_last_selection LONG,"
			+ "deck_file_id LONG); ";  
	public static final String SQL_CREATE_TABLE_DECK_CARDS = "CREATE TABLE tbl_decks_cards ("
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "deck_id INTEGER,"
			+ "spell_title VARCHAR(50));";  

	private SQLiteDatabase sqliteDatabase;
	public DeckDAO(SQLiteDatabase db) {
		this.sqliteDatabase = db;
	}

	public List<Deck> getAll(boolean complete) {
		ArrayList<Deck> decks = new ArrayList<Deck>();
		Cursor cursor = sqliteDatabase.query("tbl_decks", new String[] { "id",  
				"deck_name", "playerClass_id", "favorite", "deck_last_selection", "deck_file_id"}, null, null,  
				null, null, "deck_name");  
		if (cursor != null) {  
			while(cursor.moveToNext()) {
				Deck deck = new Deck();
				deck.setId(cursor.getInt(cursor.getColumnIndex("id")));
				deck.setTitle(cursor.getString(cursor.getColumnIndex("deck_name")));
				deck.setPlayerClassId(cursor.getInt(cursor.getColumnIndex("playerClass_id")));
				deck.setFavorite(cursor.getInt(cursor.getColumnIndex("favorite")) > 0);
				deck.setSelectionTime(cursor.getLong(cursor.getColumnIndex("deck_last_selection")));
				deck.setIdFile(cursor.getLong(cursor.getColumnIndex("deck_file_id")));
				if(complete)
					deck.setSpellIds(getSpells(deck.getId()));
				decks.add(deck);
			}
			cursor.close();
		}
		return decks;
	}

	public Deck get(int deckId) {
		Deck deck =null;
		Cursor cursor = sqliteDatabase.query("tbl_decks", new String[] { "id",  
				"deck_name", "playerClass_id", "favorite", "deck_last_selection","deck_file_id"}, "id=" + deckId, null,  
				null, null, null);  
		if (cursor != null) {
			if(cursor.getCount() > 0) {
				cursor.moveToFirst(); 
				deck = build(cursor);
			}
			cursor.close();
		}
		return deck;
	}

	public Deck get(String name) {
		Deck deck =null;
		Cursor cursor = sqliteDatabase.query("tbl_decks", new String[] { "id",  
				"deck_name", "playerClass_id", "favorite", "deck_last_selection","deck_file_id"}, "deck_name='" + name.replaceAll("'", "''") + "'", null,  
				null, null, null);  
		if (cursor != null) {
			if(cursor.getCount() > 0) {
				cursor.moveToFirst(); 
				deck = build(cursor);
			}
			cursor.close();
		}
		return deck;
	}
	
	private Deck build(Cursor cursor) {
		Deck deck = new Deck();
		deck.setId(cursor.getInt(cursor.getColumnIndex("id")));
		deck.setTitle(cursor.getString(cursor.getColumnIndex("deck_name")));
		deck.setPlayerClassId(cursor.getInt(cursor.getColumnIndex("playerClass_id")));
		deck.setFavorite(cursor.getInt(cursor.getColumnIndex("favorite")) > 0);
		deck.setSelectionTime(cursor.getLong(cursor.getColumnIndex("deck_last_selection")));
		deck.setIdFile(cursor.getLong(cursor.getColumnIndex("deck_file_id")));
		deck.setSpellIds(getSpells(deck.getId()));
		return deck;
	}


	public long save(Deck deck, boolean complete) {
		ContentValues values = new ContentValues();  
		values.put("deck_name", deck.getTitle());
		values.put("playerClass_id", deck.getPlayerClassId());
		values.put("favorite", deck.isFavorite()?1:0);
		values.put("deck_last_selection", deck.getSelectionTime());
		values.put("deck_file_id", deck.getIdFile());
		if (deck.getId() <= 0) {
			deck.setId((int)sqliteDatabase.insert("tbl_decks", null, values));
		} else {
			sqliteDatabase.update("tbl_decks", values, "id=" + deck.getId(), null);
		}
		if(complete) {
			removeAllSpell(deck.getId());
			for(String spell : deck.getSpellTitles())
				addSpell(deck.getId(), spell);
		}
		return deck.getId();
	}
	
	public long updateSelectionTime(int deckId, long selectionTime) {
		ContentValues values = new ContentValues();  
		values.put("deck_last_selection", selectionTime);
		return sqliteDatabase.update("tbl_decks", values, "id=" + deckId, null);		
	}
	
	public long updateFavorite(int deckId, boolean favorite) {
		ContentValues values = new ContentValues();  
		values.put("favorite", favorite?1:0);
		return sqliteDatabase.update("tbl_decks", values, "id=" + deckId, null);		
	}
	

	public boolean delete(int deckId) {
		removeAllSpell(deckId);
		return sqliteDatabase.delete("tbl_decks", "id=" + deckId,     null) > 0;
	}
	
	public ArrayList<String> getSpells(int deckId) {
		ArrayList<String> spells = new ArrayList<String>();
		Cursor cursor = sqliteDatabase.query("tbl_decks_cards", new String[] {"spell_title"},
				"deck_id=" + deckId, null,  
				null, null, "spell_title");
		if (cursor != null) {
			while(cursor.moveToNext()) {
				spells.add(cursor.getString(cursor.getColumnIndex("spell_title")));
			}
			cursor.close();
		}
		return spells;
	}
	
	
	public void addSpell(int deckId, String spell) {
		//Enlever avant de le remettre le cas echeant
		removeSpell(deckId, spell);
		ContentValues values = new ContentValues();  
		values.put("deck_id", deckId);
		values.put("spell_title", spell);
		sqliteDatabase.insert("tbl_decks_cards", null, values);
	}
	
	
	public boolean removeSpell(int deckId, String spell) {
		return sqliteDatabase.delete("tbl_decks_cards", "deck_id=" + deckId + " AND spell_title='" + spell.replaceAll("'", "''") + "'", null) > 0;
	}
	
	public boolean removeAllSpell(int deckId) {
		return sqliteDatabase.delete("tbl_decks_cards", "deck_id=" + deckId, null) > 0;
	}


}
