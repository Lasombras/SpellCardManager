package spell.model;

import spell.model.simple.SimpleModel;

public class PlayerClass extends SimpleModel {
	
	private String shortName;
	private boolean base;
	public PlayerClass(int id, String title, String icon, String shortName, boolean base) {
		super(id, title, icon);
		this.shortName = shortName;
		this.base = base;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public boolean isBase() {
		return base;
	}
	public void setBase(boolean base) {
		this.base = base;
	}
	
}
