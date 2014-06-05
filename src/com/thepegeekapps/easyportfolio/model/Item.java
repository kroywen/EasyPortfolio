package com.thepegeekapps.easyportfolio.model;

public abstract class Item {
	
	public static final int TYPE_GROUP = 0;
	public static final int TYPE_PORTFOLIO = 1;
	
	protected int id;
	protected int type;
	protected String name;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public abstract int getType();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
