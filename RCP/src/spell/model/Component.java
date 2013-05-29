package spell.model;

import spell.model.simple.SimpleModel;

public class Component extends SimpleModel {
	
	private String shortName;
	
	public Component(int id, String title, String icon, String shorName) {
		super(id, title, icon);
		this.shortName = shorName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
}

