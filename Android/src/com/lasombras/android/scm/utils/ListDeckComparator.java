package com.lasombras.android.scm.utils;

import java.util.Comparator;

import com.lasombras.android.scm.model.Deck;

public class ListDeckComparator implements Comparator<Deck> {

	private boolean sortAsc = true;
	private int sortField = FIELD_TITLE;

	public final static int FIELD_TITLE = 1;
	public final static int FIELD_SELECTION_TIME = 2;
	public final static int FIELD_FAVORITE = 3;

	
	public ListDeckComparator() {
		this(FIELD_TITLE, true);
	}
	
	public ListDeckComparator(int field) {
		this(field, true);
	}
	
	public ListDeckComparator(int field, boolean sortAsc) {
		this.sortField = field;
		this.sortAsc = sortAsc;
	}

	public int compare(Deck arg0, Deck arg1) {
		int order = 0;
		switch (sortField) {
		case FIELD_TITLE:
			order = arg0.getTitle().compareTo(arg1.getTitle());
			break;

		case FIELD_SELECTION_TIME:
			if(arg0.getSelectionTime() < arg1.getSelectionTime())
				order = -1;
			else if(arg0.getSelectionTime() > arg1.getSelectionTime())
				order = 1;
			break;
		case FIELD_FAVORITE:
			if(arg0.isFavorite() && !arg1.isFavorite())
				order = -1;
			else if(!arg0.isFavorite() && arg1.isFavorite())
				order = 1;
			break;
		default:
			break;
		}

		if(!sortAsc)
			order = 0-order;
		return order;
	}

	/**
	 * Recupere l'Id du champ qui sert de tri
	 * @return Id du champ trié
	 */
	public int getSortField() {
		return sortField;
	}

	/**
	 * Tri ascendant
	 * @return tri ascendant
	 */
	public boolean isSortAsc() {
		return sortAsc;
	}

	/**
	 * Position le tri en mode ascendant
	 * @param sortAsc vrai si ascendant
	 */
	public void setSortAsc(boolean sortAsc) {
		this.sortAsc = sortAsc;
	}

	/**
	 * Defini l'ID du champ qui sera trié
	 * @param sortField ID du champ à trier
	 */
	public void setSortField(int sortField) {
		this.sortField = sortField;
	}


}
