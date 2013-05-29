package com.lasombras.android.scm.model;

public class SimpleModel implements Comparable<SimpleModel>  {

	private int id;
	private String title;

	public SimpleModel(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title == null ? "" : title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int compareTo(SimpleModel another) {
		return this.getTitle().compareTo(another.getTitle());
	}
	
	@Override
	public String toString() {
		return this.getTitle();
	}

	
}