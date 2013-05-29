package com.lasombras.android.scm.utils;

import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.lasombras.android.scm.model.Component;
import com.lasombras.android.scm.model.Level;
import com.lasombras.android.scm.model.PlayerClass;
import com.lasombras.android.scm.model.School;
import com.lasombras.android.scm.model.Source;
import com.lasombras.android.scm.model.Spell;

public class SpellHandler extends DefaultHandler {
	//r�sultats de notre parsing
	private Hashtable<String, Spell> spells = new Hashtable<String, Spell>();
	private Hashtable<Integer, PlayerClass> playerClasses = new Hashtable<Integer, PlayerClass>();
	private Hashtable<Integer, Component> components = new Hashtable<Integer, Component>();
	private Hashtable<Integer, School> schools = new Hashtable<Integer, School>();
	private Hashtable<Integer, Source> sources = new Hashtable<Integer, Source>();
	
	private Spell spell;
	private Component component;
	private PlayerClass playerClass;
	private School school;
	private Source source;
	private Level level;
	
	private SpellLoadingListener spellListener = null;
	
	//flags nous indiquant la position du parseur
	private boolean inSpell;
	private boolean inLevel;
	//buffer nous permettant de r�cup�rer les donn�es 
	private StringBuffer buffer;

	// simple constructeur
	public SpellHandler(){
		super();
	}

	
	public void setSpellListener(SpellLoadingListener spellListener) {
		this.spellListener = spellListener;
	}


	public Hashtable<String, Spell> getSpells() {
		return spells;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		buffer = new StringBuffer();
		if(inSpell) {
			if(localName.equals("level")){
				level = new Level(Integer.parseInt(attributes.getValue("class")));
				inLevel = true;
			}
		} else {
			if(localName.equals("spells")){
				if(spellListener != null)
					spellListener.setSpellCount(Integer.parseInt(attributes.getValue("count")));
			} else if(localName.equals("spell")){
				spell = new Spell(Integer.parseInt(attributes.getValue("id")));
				inSpell = true;
			} else if(localName.equals("PlayerClass")){
				playerClass = new PlayerClass(Integer.parseInt(attributes.getValue("id")), attributes.getValue("shortName"));
			} else if(localName.equals("Component")){
				component = new Component(Integer.parseInt(attributes.getValue("id")), attributes.getValue("shortName"));
			} else if(localName.equals("Source")){
				source = new Source(Integer.parseInt(attributes.getValue("id")), attributes.getValue("shortName"));
			} else if(localName.equals("school")){
				school = new School(Integer.parseInt(attributes.getValue("id")));
			}
		}
	}
	
	//d�tection fin de balise
	public void endElement(String uri, String localName, String qName)
			throws SAXException{
		
		if(inSpell) {
			if(localName.equals("spell")) {
				if(spells.containsKey(spell.getOriginalName()))
					Log.v("DOUBLE", spell.getOriginalName());
				spells.put(spell.getOriginalName(), spell);
				if(spellListener != null)
					spellListener.addSpell(spell);
				inSpell = false;
			} else if(localName.equals("title")){
				spell.setTitle(buffer.toString());
				spell.setSearchTitle(buildSearchTitle(buffer.toString()));
			} else if(localName.equals("originalName")){
				spell.setOriginalName(buffer.toString());
			} else if(localName.equals("detail")){
				spell.setDetail(buffer.toString());
			} else if(localName.equals("component")){
				if(buffer.toString().length() > 0)
					spell.addComponentId(Integer.parseInt(buffer.toString()));
			} else if(localName.equals("castingTime")){
				spell.setCastingTime(buffer.toString());
			} else if(localName.equals("material")){
				spell.setMaterial(buffer.toString());
			} else if(localName.equals("duration")){
				spell.setDuration(buffer.toString());
			} else if(localName.equals("range")){
				spell.setRange(buffer.toString());
			} else if(localName.equals("target")){
				spell.setTarget(buffer.toString());
			} else if(localName.equals("area")){
				spell.setArea(buffer.toString());
			} else if(localName.equals("savingThrow")){
				spell.setSavingThrow(buffer.toString());
			} else if(localName.equals("effect")){
				spell.setEffect(buffer.toString());
			} else if(localName.equals("source")){
				if(buffer.toString().length() > 0)
					spell.setSourceId(Integer.parseInt(buffer.toString()));
			} else if(localName.equals("school")){
				if(buffer.toString().length() > 0)
					spell.setSchoolId(Integer.parseInt(buffer.toString()));
			} else if(localName.equals("descriptor")){
				spell.setDescriptor(buffer.toString());
			} else if(localName.equals("spellResistance")){
				spell.setSpellResistance(buffer.toString().equals("yes"));
			} else if(localName.equals("image")){
				String spellImage = buffer.toString();
				if(spellImage.equals("no_mini_background.jpg"))
					spellImage = null;
				spell.setImage(spellImage);
			} else if(localName.equals("level") && inLevel){
				level.setLevel(Integer.parseInt(buffer.toString()));
				spell.addLevel(level);
				inLevel = false;
			}
		} else {
			if(localName.equals("PlayerClass")){
				playerClass.setTitle(buffer.toString());
				playerClasses.put(new Integer(playerClass.getId()), playerClass);
			} else if(localName.equals("Component")){
				component.setTitle(buffer.toString());
				components.put(new Integer(component.getId()), component);
			} else if(localName.equals("Source")){
				source.setTitle(buffer.toString());
				sources.put(new Integer(source.getId()), source);
			} else if(localName.equals("school")){
				school.setTitle(buffer.toString());
				schools.put(new Integer(school.getId()), school);
			}
		}
		buffer = null;
	}
	//d�tection de caract�res
	public void characters(char[] ch,int start, int length)
			throws SAXException{
		String lecture = new String(ch,start,length);
		if(buffer != null) buffer.append(lecture);       
	}
	
	//d�but du parsing
	public void startDocument() throws SAXException {
		Log.v("PARSING ", "D�but du parsing");
	}
	//fin du parsing
	public void endDocument() throws SAXException {
		Log.v("PARSING ", "Fin du parsing");
		Log.v("PARSING ", "Resultats du parsing");
	}

	public Hashtable<Integer, PlayerClass> getPlayerClasses() {
		return playerClasses;
	}

	public Hashtable<Integer, Component> getComponents() {
		return components;
	}
	
	public Hashtable<Integer, Source> getSources() {
		return sources;
	}
	
	public Hashtable<Integer, School> getSchools() {
		return schools;
	}
	
	private String buildSearchTitle(String name) {
		name = name.toLowerCase();
		name = name.replace('�','a');
		name = name.replace('�','a');
		name = name.replace('�','a');
		name = name.replace('�','e');
		name = name.replace('�','e');
		name = name.replace('�','e');
		name = name.replace('�','e');
		name = name.replace('�','i');
		name = name.replace('�','i');
		name = name.replace('�','i');
		name = name.replace('�','o');
		name = name.replace('�','o');
		name = name.replace('�','o');
		name = name.replace('�','u');
		name = name.replace('�','u');
		name = name.replace('�','u');
		name = name.replace('�','y');
		name = name.toUpperCase();
		return name;
	}

}
