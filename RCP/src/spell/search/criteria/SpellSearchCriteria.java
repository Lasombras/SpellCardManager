package spell.search.criteria;

public class SpellSearchCriteria {

	public final static int EQUAL = 0;
	public final static int GREATHER_THAN = 1;
	public final static int LEATHER_THAN = 2;
	public final static int NOT_EQUAL = 3;
	
	private String name = null;
	private String originalName = null;
	private int schoolId = -1;
	private int playerClassId = -1;
	private int level = -1;
	private int levelSign = EQUAL;
	private int sourceId = -1;
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getLevelSign() {
		return levelSign;
	}
	public void setLevelSign(int levelSign) {
		this.levelSign = levelSign;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPlayerClassId() {
		return playerClassId;
	}
	public void setPlayerClassId(int playerClassId) {
		this.playerClassId = playerClassId;
	}
	public int getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public int getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	
}
