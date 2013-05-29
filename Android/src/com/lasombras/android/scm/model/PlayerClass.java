package com.lasombras.android.scm.model;


public class PlayerClass extends SimpleModel {
	
	private String shortName;

	public PlayerClass(int id, String shortName) {
		super(id);
		this.shortName = shortName;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
}
