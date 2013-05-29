package com.lasombras.android.scm.model;

import java.util.ArrayList;



public class Spell extends SimpleModel{

	private int schoolId = -1;
	private String descriptor = "";
	private String castingTime = "";
	private ArrayList<Integer> componentsId = new ArrayList<Integer>();
	private String material = "";
	private String range = "";
	private String effect = "";
	private String area = "";
	private String duration = "";
	private String savingThrow = "";
	private String searchTitle = "";
	private boolean spellResistance = false;
	private ArrayList<Level> levels = new ArrayList<Level>();
	private String detail = "";
	private String target = "";
	private String originalName = "";
	private String page = "";
	private int sourceId = -1;
	private String image = "";
	private String note = "";
	
	public Spell(int id) {
		super(id);
	}
		
	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getPage() {
		return page==null?"":page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCastingTime() {
		return castingTime;
	}

	public void setCastingTime(String castingTime) {
		this.castingTime = castingTime;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getSavingThrow() {
		return savingThrow;
	}

	public void setSavingThrow(String savingThrow) {
		this.savingThrow = savingThrow;
	}

	public boolean isSpellResistance() {
		return spellResistance;
	}

	public void setSpellResistance(boolean spellResistance) {
		this.spellResistance = spellResistance;
	}

	public String getDetail() {
		return detail;
	}
	
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public boolean containsComponents(int id) {
		return componentsId.contains(new Integer(id));
	}
	
	public void addComponentId(int id) {
		componentsId.add(new Integer(id));
	}
	
	public ArrayList<Integer> getComponentsId() {
		return componentsId;
	}
	
	public boolean containsLevel(int playerClassId) {
		for(Level level : levels)
			if(level.getPlayerClassId() == playerClassId)
				return true;
		return false;
	}
		
	public void addLevel(Level level) {
		levels.add(level);
	}
	
	public ArrayList<Level> getLevels() {
		return levels;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public void setComponentsId(ArrayList<Integer> componentsId) {
		this.componentsId = componentsId;
	}
		
	public String getSearchTitle() {
		return searchTitle;
	}

	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

}
