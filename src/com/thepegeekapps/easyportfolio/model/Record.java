package com.thepegeekapps.easyportfolio.model;

public class Record {
	
	public static final int TYPE_VIDEO = 0;
	public static final int TYPE_IMAGE = 1;
	public static final int TYPE_AUDIO = 2;
	public static final int TYPE_NOTE = 3;
	public static final int TYPE_URL = 4;
	public static final int TYPE_DOC = 5;
	
	protected int id;
	protected int portfolioId;
	protected String name;
	protected int type;
	protected boolean checked;
	protected String path;
	
	public Record() {}
	
	public Record(int id, int portfolioId, String name, int type, String path) {
		this.id = id;
		this.portfolioId = portfolioId;
		this.name = name;
		this.type = type;
		this.path = path;
		this.checked = false;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getPortfolioId() {
		return portfolioId;
	}
	
	public void setPortfolioId(int portfolioId) {
		this.portfolioId = portfolioId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

}
