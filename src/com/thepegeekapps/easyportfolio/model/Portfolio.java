package com.thepegeekapps.easyportfolio.model;

import java.util.LinkedList;
import java.util.List;

public class Portfolio extends Item {
	
	protected int groupId;
	
	protected List<Video> videos;
	protected List<Image> images;
	protected List<Audio> audios;
	protected List<Note> notes;
	protected List<Url> urls;
	protected List<Doc> docs;
	
	public Portfolio(String name) {
		this(0, 0, name);
	}
	
	public Portfolio(int id, int groupId, String name) {
		setId(id);
		setGroupId(groupId);
		setName(name);
	}

	public int getGroupId() {
		return groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public List<Video> getVideos() {
		return videos;
	}
	
	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}
	
	public List<Image> getImages() {
		return images;
	}
	
	public void setImages(List<Image> images) {
		this.images = images;
	}
	
	public List<Audio> getAudios() {
		return audios;
	}
	
	public void setAudios(List<Audio> audios) {
		this.audios = audios;
	}
	
	public List<Note> getNotes() {
		return this.notes;
	}
	
	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
	
	public List<Url> getUrls() {
		return urls;
	}
	
	public void setUrls(List<Url> urls) {
		this.urls = urls;
	}
	
	public List<Doc> getDocs() {
		return docs;
	}
	
	public void setDocs(List<Doc> docs) {
		this.docs = docs;
	}

	@Override
	public int getType() {
		return TYPE_PORTFOLIO;
	}	
	
	public List<Record> asRecordList(int recordType) {
		List<Record> records = new LinkedList<Record>();
		List<?> fromRecords = null;
		
		switch (recordType) {
		case Record.TYPE_VIDEO: fromRecords = videos; break;
		case Record.TYPE_IMAGE: fromRecords = images; break;
		case Record.TYPE_AUDIO: fromRecords = audios; break;
		case Record.TYPE_NOTE:  fromRecords = notes;  break;
		case Record.TYPE_URL:   fromRecords = urls;   break;
		case Record.TYPE_DOC:   fromRecords = docs;   break;
		}
		if (fromRecords != null && !fromRecords.isEmpty())
			for (Object o : fromRecords)
				records.add((Record) o);
		return records;
	}

}
