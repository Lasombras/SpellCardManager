package com.lasombras.android.scm.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lasombras.android.scm.R;
import com.lasombras.android.scm.database.DBAdapter;
import com.lasombras.android.scm.entrepriselayer.facade.DeckFacade;
import com.lasombras.android.scm.model.Component;
import com.lasombras.android.scm.model.Deck;
import com.lasombras.android.scm.model.PlayerClass;
import com.lasombras.android.scm.model.School;
import com.lasombras.android.scm.model.Source;
import com.lasombras.android.scm.model.Spell;
import com.lasombras.android.scm.view.ListPreferenceMultiSelect;

public class SpellDatas {

	private static SpellDatas spellDatasInstance;
	private Hashtable<String, Spell> spells = new Hashtable<String, Spell>();
	
	private Hashtable<Integer, Component> components;
	private Hashtable<Integer, PlayerClass> playerClasses;
	private Hashtable<Integer, School> schools;
	private Hashtable<Integer, Source> sources;
	
	private ArrayList<PlayerClass> playerClassAvailables;
		
	private boolean loaded = false;
	private Handler handler;
	private Context appContext;
	private SharedPreferences prefs;
	private SpellDatas() {
		
	}
	
	public final static SpellDatas instance() {
		if(spellDatasInstance == null) {
			spellDatasInstance = new SpellDatas();
		}
		return spellDatasInstance;
	}
	
	public void load(Context context, Handler handler) {
		this.appContext = context;
		this.handler = handler;	
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);

		if(!loaded) {	
			//Chargement XML
		   	InputStream is = null;		   	
		    try {
		    	InputSource inputSource = null;
		    	//Detection de la source SD ou Appli
		    	File databaseSD = new File(RessourceCacheManager.APP_FOLDER + "datas.adf");
		    	try {
		    		if(databaseSD.exists())
		    			is = new FileInputStream(databaseSD);
		    	} catch (Exception e) {}
		    	if(is == null) {
			    	is = context.getResources().openRawResource(R.raw.cards);			    	
		    	}
		    	inputSource = new InputSource(unzip(is));
		    	
		    	
		    	inputSource.setEncoding("ISO-8859-1");
	
		    	SAXParserFactory fabrique = SAXParserFactory.newInstance();
		    	SAXParser parseur = fabrique.newSAXParser();
	
		    	SpellHandler gestionnaire = new SpellHandler();
		    	gestionnaire.setSpellListener(new SpellLoadingListener() {
					public void addSpell(Spell spell) {
						incrementStep(1);
					}
					
					public void setSpellCount(int count) {
				        setNewStep(appContext.getString(R.string.appTitle), appContext.getString(R.string.spellsLoading), count);
					}
				});
		    	parseur.parse(inputSource, gestionnaire);
		    	    	
		    	//Chargement des composantes
				components = gestionnaire.getComponents();
				
		    	//Chargement des classes de personnages
				playerClasses = gestionnaire.getPlayerClasses();
				applyPreferenceFilter();
				
		    	//Chargement des ecoles
				schools = gestionnaire.getSchools();
				
		    	//Chargement des sources
				sources =  gestionnaire.getSources();
	
				//Sorts
		    	spells = gestionnaire.getSpells();
		    	loaded = true;
	    	} catch (Exception e) {
	    		e.printStackTrace();
			} finally {
				try {if(is != null) is.close();} catch (Exception e) {}
			}
		}
		
	    //Chargement des decks sur la SD	    
        DBAdapter dbAdapter = new DBAdapter(context);
	    SQLiteDatabase db = null;
	    try {
	    	File rep = new File(RessourceCacheManager.APP_FOLDER);
	    	if(rep.exists()) {
	    		String[] files = rep.list(new FilenameFilter() {					
					public boolean accept(File dir, String filename) {
						return filename.endsWith(".lst");
					}
				});
		        setNewStep(appContext.getString(R.string.appTitle), appContext.getString(R.string.deckLoading), files.length);
		        db = dbAdapter.open();
		        for(String file : files) {
		        	FileReader fr = null;
		        	try {
		        		String deckName = file.substring(0, file.indexOf(".lst"));
			        	Deck deck = DeckFacade.getByName(db, deckName);
			        	if(deck == null) {
			        		deck = new Deck();
			        		deck.setTitle(deckName);
			        	}
			        	File fileDeck = new File(RessourceCacheManager.APP_FOLDER + file);
			        	if(deck.getIdFile() != fileDeck.lastModified()) {
			        		deck.setIdFile(fileDeck.lastModified());
				        	deck.clearSpells();
				        	//Lecture du fichier
			        		fr = new FileReader(fileDeck);
				        	BufferedReader bfr = new BufferedReader(fr, 1024);
				        	String line = bfr.readLine();
				        	while(line != null && !line.equals("")) {
				        		if(line.startsWith("playerClass#")) {
				        			deck.setPlayerClassId(Integer.parseInt(line.substring("playerClass#".length())));
				        		} else if(line.startsWith("spell#")) {
				        			deck.addSpellTitle(line.substring("spell#".length()));
				        		}
				        		line = bfr.readLine();
							}
				        	bfr.close();
				        	//Sauvegarder le deck
				        	deck.updateSelectionTime();
				        	DeckFacade.save(db, deck, true);
			        	}
		        	} finally {
			    		try {if(fr != null) fr.close();}catch (Exception e3) {}
			    	}
	        	
		        	incrementStep(1);
	    		}
	    	}
	    } catch (Exception e) {
	    	Log.v("ERROR", e.getMessage());
		} finally {
			if(db != null) db.close();
			dbAdapter.close();
		}
	}
	
	
	public void applyPreferenceFilter() {
		playerClassAvailables = new ArrayList<PlayerClass>();
		//PLAYER_CLASSES
		String playerClassesStr = "";
		Enumeration<Integer> enPlayerClass = playerClasses.keys();
		while(enPlayerClass.hasMoreElements())
			playerClassesStr += enPlayerClass.nextElement() + ListPreferenceMultiSelect.SEPARATOR;
		playerClassesStr = prefs.getString("player_class_preference", playerClassesStr);
		String[] playerClassesId = playerClassesStr.split(ListPreferenceMultiSelect.SEPARATOR);
		for(String playerClass : playerClassesId) {
			if(!playerClass.equals(""))
				playerClassAvailables.add(playerClasses.get(Integer.parseInt(playerClass)));
		}

	}
	
	public void unload() {
		loaded = false;
		
		this.appContext = null;
		this.handler = null;	
		this.prefs = null;
		this.components.clear();
		this.playerClasses.clear();
		this.schools.clear();
		this.sources.clear();
		this.spells.clear();
	}

	
	private ByteArrayInputStream unzip(InputStream is) {		
		ByteArrayInputStream databaseFile = null;
		try {
			// Open the ZipInputStream
			ZipInputStream inputStream = new ZipInputStream(is);

			// Loop through all the files and folders
			for (ZipEntry entry = inputStream.getNextEntry(); entry != null; entry = inputStream.getNextEntry()) {
				if(entry.getName().equals("cards.xml")) {
					// Create a file output stream
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					final int BUFFER = 2048;

					// Write the contents
					int count = 0;
					byte[] data = new byte[BUFFER];
					while ((count = inputStream.read(data, 0, BUFFER)) != -1) {
						outputStream.write(data, 0, count);
					}
					databaseFile = new ByteArrayInputStream(outputStream.toByteArray());
					// Flush and close the buffers
					outputStream.flush();
					outputStream.close();
					
				} else if(entry.getName().startsWith("Spell_") && entry.getName().endsWith(".jpg")) {
					//Image
					RessourceCacheManager.instance().setSpellImage(entry.getName().substring("Spell_".length()),BitmapFactory.decodeStream(inputStream));
				}


				// Close the current entry
				inputStream.closeEntry();
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return databaseFile;
	}
    
    public List<Spell> getList() {  	
    	return new ArrayList<Spell>(spells.values());
    }
  
	public Spell get(String originalName) {
		return spells.get(originalName);
	}

	public PlayerClass getPlayerClass(int id) {
		return playerClasses.get(new Integer(id));
	}
	
	public List<PlayerClass> getPlayerClasses(boolean useFilter) {
		if(!useFilter)
			return new ArrayList<PlayerClass>(playerClasses.values());
		return new ArrayList<PlayerClass>(playerClassAvailables);
	}
	
	public List<PlayerClass> getPlayerClasses() {
    	return getPlayerClasses(true);
	}

	public Component getComponent(int id) {
		return components.get(new Integer(id));
	}

	public School getSchool(int id) {
		return schools.get(new Integer(id));
	}
	
	public Source getSource(int id) {
		return sources.get(new Integer(id));
	}

	public List<School> getSchools() {
    	return new ArrayList<School>(schools.values());
	}
	public List<Source> getSources() {
    	return new ArrayList<Source>(sources.values());
	}
	
	public void incrementStep(int increment) {
		Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("increment", increment);
        msg.setData(b);
        handler.sendMessage(msg);
	}
	
	public void setNewStep(String title, String message, int max) {
		Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("message", message);
        b.putInt("max", max);
        msg.setData(b);
        handler.sendMessage(msg);
		
	}
	

}

