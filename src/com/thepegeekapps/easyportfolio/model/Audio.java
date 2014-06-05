package com.thepegeekapps.easyportfolio.model;

public class Audio extends Record {
	
	public Audio() {
		setType(TYPE_AUDIO);
	}
	
	public Audio(int id, int portfolioId, String name, String path) {
		super(id, portfolioId, name, TYPE_AUDIO, path);
	}

}
