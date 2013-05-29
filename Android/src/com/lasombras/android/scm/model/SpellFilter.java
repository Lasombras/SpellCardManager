package com.lasombras.android.scm.model;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

import com.lasombras.android.scm.utils.SpellDatas;
import com.lasombras.android.scm.view.ListPreferenceMultiSelect;

public class SpellFilter {

	private String titleName = "";
	
	private int levelMin = LEVEL_MIN;
	private int levelMax = LEVEL_MAX;
	private int playerClassId = -1;
	private ArrayList<Integer> schools = new ArrayList<Integer>();
	private ArrayList<Integer> sources = new ArrayList<Integer>();
	private boolean sortByLevel = false;
	private int searchMode = SEARCH_TITLE;
	
	public final static int SEARCH_TITLE = 0;
	public final static int SEARCH_ORIGINAL_NAME = 1;
	public final static int SEARCH_ALL = 2;

	public final static int LEVEL_MIN = 0;
	public final static int LEVEL_MAX = 9;
	
	private SharedPreferences prefs;

	public SpellFilter(SharedPreferences prefs) {
		this.prefs = prefs;
		reset();
	}
	
	public int getLevelMin() {
		return levelMin;
	}
	public void setLevelMin(int levelMin) {
		this.levelMin = levelMin;
	}
	public int getLevelMax() {
		return levelMax;
	}
	public void setLevelMax(int levelMax) {
		this.levelMax = levelMax;
	}
	public String getTitleName() {
		return titleName;
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public int getPlayerClassId() {
		return playerClassId;
	}
	public void setPlayerClassId(int playerClassId) {
		this.playerClassId = playerClassId;
	}
	public boolean isSortByLevel() {
		return sortByLevel;
	}
	public void setSortByLevel(boolean sortByLevel) {
		this.sortByLevel = sortByLevel;
	}
	public int getSearchMode() {
		return searchMode;
	}
	public void setSearchMode(int searchMode) {
		this.searchMode = searchMode;
	}
	
	public void clearSource() {
		sources.clear();
	}
	public void addSource(int sourceId) {
		sources.add(new Integer(sourceId));
	}
	public boolean availableSource(int sourceId) {
		return sources.contains(new Integer(sourceId));
	}

	public void clearSchool() {
		schools.clear();
	}
	public void addSchool(int schoolId) {
		schools.add(new Integer(schoolId));
	}
	public boolean availableSchool(int schoolId) {
		return schools.contains(new Integer(schoolId));
	}

	public List<Spell> applyFilter(List<Spell> allSpells) {
		List<Spell> spells = new ArrayList<Spell>();
		String filterTitle = this.getTitleName().toLowerCase();
		for(Spell spell : allSpells) {
			//Test si le spell passe le filtre
			if( (	filterTitle.equals("") ||
					((spell.getSearchTitle().toLowerCase().contains(filterTitle) || spell.getTitle().toLowerCase().contains(filterTitle)) && (this.getSearchMode() == SpellFilter.SEARCH_TITLE || this.getSearchMode() == SpellFilter.SEARCH_ALL) ) ||
					(spell.getOriginalName().toLowerCase().contains(filterTitle) && (this.getSearchMode() == SpellFilter.SEARCH_ORIGINAL_NAME || this.getSearchMode() == SpellFilter.SEARCH_ALL) )
				) &&
					this.availableSchool(spell.getSchoolId())  && //Test de l'ecole
					this.availableSource(spell.getSourceId()) ) { //Test de la source
				//Test des classes et niveaux
				for(Level level : spell.getLevels()) {
					if(	(this.getPlayerClassId() <= 0 || this.getPlayerClassId() == level.getPlayerClassId()) &&
						(this.getLevelMin() <= level.getLevel() && this.getLevelMax() >= level.getLevel()) ) {	        				
						spells.add(spell);
						break;
					}
				}
			}
		}
		return spells;
	}
	
	public void applyPreferences() {
		//SOURCE
		String sources = "";
		for(Source source : SpellDatas.instance().getSources())
			sources += source.getId() + ListPreferenceMultiSelect.SEPARATOR;
		sources = prefs.getString("source_preference", sources);
		this.clearSource();
		String[] sourcesId = sources.split(ListPreferenceMultiSelect.SEPARATOR);
		for(String source : sourcesId) {
			if(!source.equals(""))
				this.addSource(Integer.parseInt(source));
		}
		
		//SearchMode
		this.setSearchMode(Integer.parseInt(prefs.getString("search_preference", ""+SpellFilter.SEARCH_TITLE)));
	}
	
	public void reset() {
		this.clearSchool();
		for(School school : SpellDatas.instance().getSchools()) {
			this.addSchool(school.getId());
		}
		
		this.setLevelMin(SpellFilter.LEVEL_MIN);
		this.setLevelMax(SpellFilter.LEVEL_MAX);
		this.setPlayerClassId(-1);
		this.setSortByLevel(false);

		this.applyPreferences();

	}
}
