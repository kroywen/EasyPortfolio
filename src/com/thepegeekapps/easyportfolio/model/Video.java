package com.thepegeekapps.easyportfolio.model;

import android.graphics.Bitmap;


public class Video extends Record {
	
	protected String uri;
	protected Bitmap thumbnail;
	
	public Video() {
		setType(TYPE_VIDEO);
	}
	
	public Video(int id, int portfolioId, String name, String uri, String path, Bitmap thumbnail) {
		super(id, portfolioId, name, TYPE_VIDEO, path);
		this.uri = uri;
		this.thumbnail = thumbnail;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public Bitmap getThumbnail() {
		return thumbnail;
	}
	
	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}

}
