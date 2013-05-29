package com.lasombras.android.scm.model;

import java.util.ArrayList;
import java.util.Date;

public class Deck {

	private int id = -1;
	private String title = "";
	private int playerClassId = -1;
	private boolean favorite = false;
	private long selectionTime = -1;
	private long idFile = -1;
	private ArrayList<String> spellTitles = new ArrayList<String>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ArrayList<String> getSpellTitles() {
		return spellTitles;
	}
	public void setSpellIds(ArrayList<String> spellTitles) {
		this.spellTitles = spellTitles;
	}
	public void addSpellTitle(String spellTitle) {
		if(!this.spellTitles.contains(spellTitle))
			this.spellTitles.add(spellTitle);
	}
	public void clearSpells() {
		this.spellTitles.clear();
	}
	public int getPlayerClassId() {
		return playerClassId;
	}
	public void setPlayerClassId(int playerClassId) {
		this.playerClassId = playerClassId;
	}
	public boolean isFavorite() {
		return favorite;
	}
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	public long getSelectionTime() {
		return selectionTime;
	}
	public void setSelectionTime(long selectionTime) {
		this.selectionTime = selectionTime;
	}
	
	public void updateSelectionTime() {
		this.selectionTime = new Date().getTime();
	}
	public long getIdFile() {
		return idFile;
	}
	public void setIdFile(long idFile) {
		this.idFile = idFile;
	}
	
	
}
