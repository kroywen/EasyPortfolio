package com.thepegeekapps.easyportfolio.model;

public class Url extends Record {
	
	protected String url;
	
	public Url() {
		setType(TYPE_URL);
	}
	
	public Url(int id, int portfolioId, String name, String url, String path) {
		super(id, portfolioId, name, TYPE_URL, path);
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

}
