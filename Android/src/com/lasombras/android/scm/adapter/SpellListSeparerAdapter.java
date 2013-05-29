package com.lasombras.android.scm.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import com.lasombras.android.scm.R;
import com.lasombras.android.scm.model.Level;
import com.lasombras.android.scm.model.Spell;
import com.lasombras.android.scm.utils.SpellComparator;

public class SpellListSeparerAdapter extends BaseAdapter implements SectionIndexer, OnSharedPreferenceChangeListener {


	private final ArrayList<CustomSection> sections = new ArrayList<CustomSection>();  
	private final ArrayAdapter<String> headers;  
    private List<Spell> spells;
    private final Context context;
    private int mode = MODE_ALPHA;
    private int playerClassLevelMode = -1;
    private boolean showCustomImage;
    private boolean originalNameMode;
    
	private final static int TYPE_SECTION_HEADER = 0;  
	
	private final static int MODE_ALPHA = 0;
	private final static int MODE_LEVEL = 1;
	
	private SharedPreferences prefs;
	

	public SpellListSeparerAdapter(Context context,  List<Spell> spells) {
		this(context, spells, -1);
	}
	
	public SpellListSeparerAdapter(Context context,  List<Spell> spells, int playerClass) {
		this.headers = new ArrayAdapter<String>(context, R.layout.list_separator); 
		this.spells = spells;
		this.context = context;
		if(playerClass <= 0) {
			setAlphaMode();
		} else {
			setLevelMode(playerClass);
		}
		
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.registerOnSharedPreferenceChangeListener(this);
		this.showCustomImage = !prefs.getBoolean("checkbox_light_mode_preference", false);
		this.originalNameMode = prefs.getBoolean("checkbox_title_list_preference", false);
		
		buildSection();		
	}

	@Override
	protected void finalize() throws Throwable {
		prefs.unregisterOnSharedPreferenceChangeListener(this);
		super.finalize();
	}
	
    private void buildSection() {
    	sections.clear();
    	headers.clear();
   	
    	//Sort du tableau de spell
		if(originalNameMode)
			Collections.sort(spells, new SpellComparator(SpellComparator.FIELD_ORIGINAL_NAME));
		else
			Collections.sort(spells, new SpellComparator(SpellComparator.FIELD_TITLE_CASE));
   	
    	switch (mode) {
		case MODE_ALPHA:
	    	Map<String, ArrayList<Spell>> sectionSpells =  new LinkedHashMap<String, ArrayList<Spell>>();
	    	
	    	for(Spell spell : spells) {
	    		String sectionName = "";
	    		if(originalNameMode)
	    			sectionName = spell.getOriginalName().substring(0, 1).toUpperCase();
	    		else
	    			sectionName = spell.getSearchTitle().substring(0, 1).toUpperCase();
	    		ArrayList<Spell> spellsList = sectionSpells.get(sectionName);
	    		if(spellsList == null) {
	    			spellsList = new ArrayList<Spell>();
	    			sectionSpells.put(sectionName, spellsList);
	    		}
	    		spellsList.add(spell);
	    	}
			Iterator<String> keys = sectionSpells.keySet().iterator();
			while(keys.hasNext()) {
				String sectionName = keys.next();
				SpellListAdapter spellListAdapter = new SpellListAdapter(context, sectionSpells.get(sectionName), showCustomImage, originalNameMode);
				
				CustomSection customSection = new CustomSection();
				customSection.setLetter(sectionName);
				customSection.setMinIndex(this.getCount());
				customSection.setAdapter(spellListAdapter);
				
				this.headers.add(sectionName);  
				this.sections.add(customSection); 
			}			
			break;
		case MODE_LEVEL:
			Map<Integer, ArrayList<Spell>> levels =  new HashMap<Integer, ArrayList<Spell>>();
			List<Integer> levelsIdx = new ArrayList<Integer>();
			for(Spell spell : spells) {
				for(Level level : spell.getLevels()) {
					if(level.getPlayerClassId() == playerClassLevelMode) {
						Integer group = new Integer(level.getLevel());
						ArrayList<Spell> groupSpell = levels.get(group);
						if(groupSpell == null) {
							groupSpell = new ArrayList<Spell>();
							levels.put(group, groupSpell);	 
							levelsIdx.add(group);
						}
						groupSpell.add(spell);
						break;
					}
				}
			}
			
			Collections.sort(levelsIdx);
			for(Integer level : levelsIdx) {
				SpellListAdapter spellListAdapter = new SpellListAdapter(context, levels.get(level), showCustomImage, originalNameMode);
				
				CustomSection customSection = new CustomSection();
				customSection.setLetter(level.toString());
				customSection.setMinIndex(this.getCount());
				customSection.setAdapter(spellListAdapter);
				
				this.headers.add("Niveau " + level);  
				this.sections.add(customSection); 
			}

			break;
		default:
			break;
		}

    }

    public void setAlphaMode() {
		this.mode = MODE_ALPHA;
		this.playerClassLevelMode = -1;   	
    }
    
    public void setLevelMode(int playerClass) {
		this.mode = MODE_LEVEL;
		this.playerClassLevelMode = playerClass;
    }
    
	//Renvoi la position d'un clique
	public Object getItem(int position) {  
		for(CustomSection section : this.sections) {  
			SpellListAdapter adapter = section.getAdapter();  
			int size = adapter.getCount() + 1;  

			// récupération de la position dans la section 
			if(position == 0) return section;  
			if(position < size) return adapter.getItem(position - 1);  

			// passe à la section suivant  
			position -= size;  
		}  
		return null;  
	}
	
	//Renvoi la position d'un clique
	public Spell getSpell(int position) {  
		for(CustomSection section : this.sections) {  
			SpellListAdapter adapter = section.getAdapter();  
			int size = adapter.getCount() + 1;  

			// récupération de la position dans la section 
			if(position == 0) return null;  
			if(position < size) return adapter.getSpell(position - 1);  

			// passe à la section suivant  
			position -= size;  
		}  
		return null;  
	}

	// renvoi le nombre d'item
	public int getCount() {  
		// 	total de l'ensemble des sections, plus une pour chaque tête de section
		int total = 0;  
		for(CustomSection section : this.sections) {  
			SpellListAdapter adapter = section.getAdapter();  
			total += adapter.getCount() + 1;  
		}
		return total;  
	}  

	public int getViewTypeCount() {  
		int total = 1;  
		for(CustomSection section : this.sections) {  
			SpellListAdapter adapter = section.getAdapter();  
			total += adapter.getViewTypeCount();
		}
		return total;  
	}  

	public int getItemViewType(int position) {  
		int type = 1;  
		for(CustomSection section : this.sections) {  
			SpellListAdapter adapter = section.getAdapter();  
			int size = adapter.getCount() + 1;  

			// Récupération de la position dans la section
			if(position == 0) return TYPE_SECTION_HEADER;  
			if(position < size) return type + adapter.getItemViewType(position - 1);  

			// passe a la section suivante moins un par l'entête 
			position -= size;  
			type += adapter.getViewTypeCount();  
		}  
		return -1;  
	}  

	public boolean areAllItemsSelectable() {  
		return false;  
	}  

	public boolean isEnabled(int position) {  
		return (getItemViewType(position) != TYPE_SECTION_HEADER);  
	} 

	public View getView(int position, View convertView, ViewGroup parent) {  
		int sectionnum = 0;  
		for(CustomSection section : this.sections) {  
			SpellListAdapter adapter = section.getAdapter();  
			int size = adapter.getCount() + 1;  
			
			// Récupération de la position dans la section  
			if(position == 0) return headers.getView(sectionnum, convertView, parent);
			if(position < size) return adapter.getView(position - 1, convertView, parent);  

			// otherwise jump into next section  
			position -= size;  
			sectionnum++;  
		}  
		return null;  
	}  


    public void notifyDataSetChanged(List<Spell> spells) {
    	this.spells = spells;
    	this.buildSection();
    	super.notifyDataSetChanged();
    }

    public void removeSpell(Spell spell) {
    	this.spells.remove(spell);
    	notifyDataSetChanged(this.spells);
    }
    
	public long getItemId(int position) {  
		return position;  
	}

	public int getPositionForSection(int section) {
		return sections.get(section).getMinIndex();
	}

	public int getSectionForPosition(int position) {
		for(int i = 0; i < sections.size(); i++) {
			CustomSection section = sections.get(i);
			if(section.getMinIndex() <= position && section.getMaxIndex() >= position)
				return i;
		}
		return 0;
	}

	public Object[] getSections() {
		Object[] list = new String[sections.size()];
		for(int i = 0; i < sections.size(); i++) {
			list[i] = sections.get(i).getLetter();
		}
		return list;
	}
	
	
	class CustomSection {
		private String letter;
		private int minIndex = -1;
		private SpellListAdapter adapter;
		
		
		public SpellListAdapter getAdapter() {
			return adapter;
		}
		public void setAdapter(SpellListAdapter adapter) {
			this.adapter = adapter;
		}
		public int getMinIndex() {
			return minIndex;
		}
		public void setMinIndex(int minIndex) {
			this.minIndex = minIndex;
		}
		public int getMaxIndex() {
			return this.minIndex + adapter.getCount();
		}
		public String getLetter() {
			return letter;
		}
		public void setLetter(String letter) {
			this.letter = letter;
		}
		
		
	}


	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (key.equals("checkbox_light_mode_preference") ) {
			boolean newShowCustomImage = !prefs.getBoolean("checkbox_light_mode_preference", false);
           	if(newShowCustomImage != this.showCustomImage) {
           		this.showCustomImage = newShowCustomImage;
	           	notifyDataSetChanged(spells);		           	
	        }
        } else if (key.equals("checkbox_title_list_preference") ) {
			boolean newOriginalNameMode = prefs.getBoolean("checkbox_title_list_preference", false);
           	if(newOriginalNameMode != this.originalNameMode) {
           		this.originalNameMode = newOriginalNameMode;
	           	notifyDataSetChanged(spells);		           	
	        }
        }

	}

}