package com.thepegeekapps.easyportfolio.model;


public class Image extends Record {
	
	public Image() {
		setType(TYPE_IMAGE);
	}

	public Image(int id, int portfolioId, String name, String path) {
		super(id, portfolioId, name, TYPE_IMAGE, path);
	}

}
