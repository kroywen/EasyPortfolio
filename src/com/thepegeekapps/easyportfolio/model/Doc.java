package com.thepegeekapps.easyportfolio.model;

public class Doc extends Record {
	
	public Doc() {
		setType(TYPE_DOC);
	}
	
	public Doc(int id, int portfolioId, String name, String path) {
		super(id, portfolioId, name, TYPE_DOC, path);
	}
	
}
