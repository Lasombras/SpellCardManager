package com.lasombras.android.scm.utils;

import java.util.Comparator;

import com.lasombras.android.scm.model.Spell;

public class SpellComparator implements Comparator<Spell> {

	public final static int FIELD_TITLE = 0;
	public final static int FIELD_ORIGINAL_NAME = 1;
	public final static int FIELD_TITLE_CASE = 2;
	
	private int field;
	
	public SpellComparator(int field) {
		this.field = field;
	}
	

	public int compare(Spell object1, Spell object2) {
		switch (field) {
		case FIELD_TITLE:
			return object1.getTitle().compareTo(object2.getTitle());
		case FIELD_ORIGINAL_NAME:
			return object1.getOriginalName().compareTo(object2.getOriginalName());
		case FIELD_TITLE_CASE:
			return object1.getSearchTitle().compareTo(object2.getSearchTitle());
		}
		return 0;
	}
}
