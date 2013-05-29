package spell.model;

import java.util.ArrayList;

import spell.Activator;
import spell.model.simple.SimpleModel;



public class Spell extends SimpleModel {

	private int schoolId = 1;
	private String descriptor = "";
	private String castingTime = "";
	private ArrayList<Integer> componentsId = new ArrayList<Integer>();
	private String material;
	private String range = "";
	private String effect = "";
	private String area = "";
	private String duration = "";
	private String savingThrow = "";
	private boolean spellResistance = false;
	private ArrayList<Level> levels = new ArrayList<Level>();
	private String detail = "";
	private String background = "";
	private String target = "";
	private String originalName = "";
	private String page = "";
	private String cardText = "";
	private int sourceId = 1;
	
	public Spell(int id) {
		super(id, "", Activator.SPELL_NO_ICON);
		this.setBackground(Activator.SPELL_NO_BACKGROUND);
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

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
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
		return getDetail(true);
	}
	
	public String getDetail(boolean formatted) {
		if(formatted) {
			return detail;
		} else {
			String transform = detail;
			transform = transform.replaceAll("<b>", "").replaceAll("</b>", "");
			transform = transform.replaceAll("<i>", "").replaceAll("</i>", "");
			transform = removeParameterTag(transform, "a");
			transform = transform.replaceAll("</a>", "");
			transform = removeBlockTag(transform, "table");
			transform = transform.replaceAll("<center>", "");
			transform = transform.replaceAll("</center>", "");
			transform = transform.replaceAll("<ul>", "").replaceAll("</ul>", "");
			transform = transform.replaceAll("</li><li>", "\n<li>");
			transform = transform.replaceAll("<li>", "•").replaceAll("</li>", "");
			
			return transform;
		}
	}
	
	private String removeParameterTag(String str, String tag) {
		tag = "<" + tag + " ";
		while(str.indexOf(tag) > -1) {
			String strStart = str.substring(0,str.indexOf(tag));
			String strEnd = str.substring(str.indexOf(tag));
			strEnd = strEnd.substring(strEnd.indexOf(">")+1);
			str = strStart + strEnd;
		}
		return str;		
	}
	private String removeBlockTag(String str, String tag) {
		String startTab = "<" + tag + ">";
		String endTag = "</" + tag + ">";
		while(str.indexOf(startTab) > -1) {
			String strStart = str.substring(0,str.indexOf(startTab));
			String strEnd = str.substring(str.indexOf(endTag) + endTag.length());
			str = strStart + strEnd;
		}
		return str;		
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
	
	public void removeComponentId(int id) {
		componentsId.remove(new Integer(id));
	}
	
	public void removeAllComponentId() {
		componentsId.clear();
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
	
	public void addLevel(int playerClassID, int level) {
		levels.add(new Level(playerClassID, level));
	}
	
	public void addLevel(Level level) {
		levels.add(level);
	}
	
	public void removeLevel(Level level) {
		levels.remove(level);
	}
	
	public void removeLevel(int playerClassID) {
		for(Level level : levels)
			if(level.getPlayerClassId() == playerClassID) {
				levels.remove(level);
				return;
			}
	}
	
	public void removeAllLevels() {
		levels.clear();
	}
	
	public ArrayList<Level> getLevels() {
		return levels;
	}

	public String getCardText() {
		return cardText;
	}

	public void setCardText(String cardText) {
		this.cardText = cardText;
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

	public Spell clone() {
		Spell spell = new Spell(0);
		spell.setArea(this.getArea());
		spell.setBackground(this.getBackground());
		spell.setCardText(this.getCardText());
		spell.setCastingTime(this.getCastingTime());
		spell.setDescriptor(this.getDescriptor());
		spell.setDetail(this.getDetail());
		spell.setDuration(this.getDuration());
		spell.setEffect(this.getEffect());
		spell.setImage(this.getImage());
		spell.setDirty(this.isDirty());
		spell.setExist(this.isExist());
		spell.setMaterial(this.getMaterial());
		spell.setOriginalName(this.getOriginalName());
		spell.setPage(this.getPage());
		spell.setRange(this.getRange());
		spell.setSavingThrow(this.getSavingThrow());
		spell.setSchoolId(this.getSchoolId());
		spell.setSpellResistance(this.isSpellResistance());
		spell.setTarget(this.getTarget());
		spell.setTitle(this.getTitle());
		spell.setSourceId(this.getSourceId());
		
		for(Integer id : this.getComponentsId()) {
			spell.addComponentId(id.intValue());
		}
		for(Level level : this.getLevels()) {
			spell.addLevel(level.getPlayerClassId(), level.getLevel());
		}
		
		return spell;
	}
	
	public void clearComponents() {
		this.componentsId.clear();
	}
	
	public void clearLevels() {
		this.levels.clear();
	}
}
