package com.thepegeekapps.easyportfolio.storage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.thepegeekapps.easyportfolio.model.Doc;
import com.thepegeekapps.easyportfolio.model.EntryRecord;

public class EntryHolder {
	
	protected DropboxAPI<AndroidAuthSession> api;
	protected static EntryHolder instance;
	
	protected EntryRecord root;
	protected EntryRecord current;
	
	protected EntryHolder(DropboxAPI<AndroidAuthSession> api) {
		this.api = api;
		root = null;
	}
	
	public static EntryHolder getInstance(DropboxAPI<AndroidAuthSession> api) {
		if (instance == null)
			instance = new EntryHolder(api);
		return instance;
	}
	
	public boolean initEntryList(DropboxAPI<AndroidAuthSession> api) {
		this.api = api;
//		if (root != null)
//			return true;
		try {
			Entry contact = api.metadata("/", 0, null, true, null);
			root = new EntryRecord(contact, null);
			populateChildEntries(root);
			current = root;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected void populateChildEntries(EntryRecord record) {
		if (record == null)
			return;
		
		Entry entry = record.getEntry();
		if (entry == null)
			return;
		
		List<Entry> contents = entry.contents;
		if (contents == null || contents.isEmpty())
			return;
		
		for (Entry content : contents) {
			if (content.isDir) {
				try {
					Entry contact = api.metadata(content.path, 0, null, true, null);
					EntryRecord dirRecord = new EntryRecord(contact, record);
					populateChildEntries(dirRecord);
					record.getChildren().add(dirRecord);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				EntryRecord fileRecord = new EntryRecord(content, record);
				record.getChildren().add(fileRecord);
			}
		}
	}
	
	public EntryRecord getRoot() {
		return root;
	}
	
	public void setRoot(EntryRecord root) {
		this.root = root;
	}
	
	public EntryRecord getCurrent() {
		return current;
	}
	
	public void setCurrent(EntryRecord current) {
		this.current = current;
	}
	
	public void back() {
		if (current == null || current.getParent() == null)
			return;
		setCurrent(current.getParent());
	}
	
	public int getCheckedCount() {
		return getCheckedCount(root);
	}
	
	protected int getCheckedCount(EntryRecord record) {
		if (record == null)
			return 0;
		int checked = 0;
		Entry entry = record.getEntry();
		if (entry != null) {
			if (entry.isDir) {
				for (EntryRecord r : record.getChildren())
					checked += getCheckedCount(r);
			} else {
				if (record.isChecked())
					checked++;
			}
		}
		return checked;
	}
	
	public List<EntryRecord> getCheckedEntries() {
		List<EntryRecord> entries = new ArrayList<EntryRecord>();
		getCheckedEntries(root, entries);
		return entries;
	}
	
	protected void getCheckedEntries(EntryRecord record, List<EntryRecord> entries) {
		if (record == null)
			return;
		Entry entry = record.getEntry();
		if (entry != null) {
			if (entry.isDir) {
				for (EntryRecord r : record.getChildren())
					getCheckedEntries(r, entries);
			} else {
				if (record.isChecked()) {
					entries.add(record);
				}
			}
		}
	}
	
	public List<EntryRecord> getDownloadedEntries() {
		List<EntryRecord> entries = new ArrayList<EntryRecord>();
		getDownloadedEntries(root, entries);
		return entries;
	}
	
	protected void getDownloadedEntries(EntryRecord record, List<EntryRecord> entries) {
		if (record == null)
			return;
		Entry entry = record.getEntry();
		if (entry != null) {
			if (entry.isDir) {
				for (EntryRecord r : record.getChildren())
					getDownloadedEntries(r, entries);
			} else {
				if (record.getDownloadedPath() != null && record.getDownloadedPath().length() > 0) {
					entries.add(record);
				}
			}
		}
	}
	
	public List<Doc> getCheckedDocuments(int portfolioId) {
		List<Doc> docs = new LinkedList<Doc>();
		
		List<EntryRecord> records = getCheckedEntries();
		if (records != null && !records.isEmpty())
			for (EntryRecord record : records)
				docs.add(new Doc(0, portfolioId, record.getEntry().fileName(), record.getDownloadedPath()));
		
		return docs;
	}
	
	public List<Doc> getDownloadedDocuments(int portfolioId) {
		List<Doc> docs = new LinkedList<Doc>();
		
		List<EntryRecord> records = getDownloadedEntries();
		if (records != null && !records.isEmpty())
			for (EntryRecord record : records)
				docs.add(new Doc(0, portfolioId, record.getEntry().fileName(), record.getDownloadedPath()));
		
		return docs;
	}

}
