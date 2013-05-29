package com.lasombras.android.scm.model;


public class Source extends SimpleModel {
	
	private String shortName;
	
	public Source(int id, String shorName) {
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
