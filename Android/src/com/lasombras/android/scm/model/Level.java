package com.lasombras.android.scm.model;

public class Level {

	private int playerClassId;
	private int level;
	
	public Level(int playerClassId) {
		this.playerClassId = playerClassId;
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getPlayerClassId() {
		return playerClassId;
	}

	public void setPlayerClassId(int playerClassId) {
		this.playerClassId = playerClassId;
	}

	public boolean equals(Object obj) {
		if(obj instanceof Level) {
			Level level = (Level)obj;
			return(	this.getPlayerClassId() == level.getPlayerClassId() && this.getLevel() == level.getLevel());
		}
			
		return false;
	}
}
