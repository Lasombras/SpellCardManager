package com.lasombras.android.scm.model;


public class Component extends SimpleModel {
	
	private String shortName;
	
	public Component(int id, String shorName) {
		super(id);
		this.shortName = shorName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
}

