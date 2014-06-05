package com.thepegeekapps.easyportfolio.model;

public class Note extends Record{
	
	protected String text;
	
	public Note() {
		setType(TYPE_NOTE);
	}
	
	public Note(int id, int portfolioId, String name, String path, String text) {
		super(id, portfolioId, name, TYPE_NOTE, path);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}	

}
