package com.lasombras.android.scm.entrepriselayer.facade;

import android.database.sqlite.SQLiteDatabase;

import com.lasombras.android.scm.daolayer.SpellInfoDAO;

public final class SpellFacade {

	public static String getComment(SQLiteDatabase db, String spell) {
		SpellInfoDAO spellDAO = new SpellInfoDAO(db);
		return spellDAO.getComment(spell);
	}
	
	public static void save(SQLiteDatabase db, String spell, String comment) {
		SpellInfoDAO spellDAO = new SpellInfoDAO(db);
		spellDAO.saveComment(spell, comment);
	}

}
