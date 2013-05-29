package com.lasombras.android.scm.entrepriselayer.facade;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.lasombras.android.scm.daolayer.DeckDAO;
import com.lasombras.android.scm.model.Deck;

public final class DeckFacade {

	public static List<Deck> getAll(SQLiteDatabase db, boolean complete) {
		DeckDAO deckDAO = new DeckDAO(db);
		return deckDAO.getAll(false);
	}

	public static Deck getByName(SQLiteDatabase db, String name) {
		DeckDAO deckDAO = new DeckDAO(db);
		return deckDAO.get(name);
	}

	
	public static void setFavorite(SQLiteDatabase db, int deckID, boolean favorite) {
		DeckDAO deckDAO = new DeckDAO(db);
		deckDAO.updateFavorite(deckID, favorite);
	}
	
	public static void setSelectionTime(SQLiteDatabase db, int deckID, long selectionTime) {
		DeckDAO deckDAO = new DeckDAO(db);
		deckDAO.updateSelectionTime(deckID, selectionTime);
	}

	public static void addSpell(SQLiteDatabase db, int deckID, String spell) {
		DeckDAO deckDAO = new DeckDAO(db);
		deckDAO.addSpell(deckID, spell);
	}

	public static void removeSpell(SQLiteDatabase db, int deckID, String spell) {
		DeckDAO deckDAO = new DeckDAO(db);
		deckDAO.removeSpell(deckID, spell);
	}
	
	public static long save(SQLiteDatabase db, Deck deck, boolean complete) {
		DeckDAO deckDAO = new DeckDAO(db);
		return deckDAO.save(deck, complete);
	}

	public static boolean delete(SQLiteDatabase db, int deckID) {
		DeckDAO deckDAO = new DeckDAO(db);
		return deckDAO.delete(deckID);
	}

}
