package com.thepegeekapps.easyportfolio.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thepegeekapps.easyportfolio.model.Audio;
import com.thepegeekapps.easyportfolio.model.Doc;
import com.thepegeekapps.easyportfolio.model.Group;
import com.thepegeekapps.easyportfolio.model.Image;
import com.thepegeekapps.easyportfolio.model.Note;
import com.thepegeekapps.easyportfolio.model.Portfolio;
import com.thepegeekapps.easyportfolio.model.Url;
import com.thepegeekapps.easyportfolio.model.Video;

public class DatabaseManager {
	
	protected static DatabaseManager instance;
	protected DatabaseHelper dbHelper;
	protected Context context;
	protected SQLiteDatabase db;
	
	protected DatabaseManager(Context context) {
		this.context = context;
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	public static DatabaseManager getInstance(Context context) {
		if (instance == null)
			instance = new DatabaseManager(context);
		return instance;
	}
	
	/************************************************************************************************************************
	 ***************************************************** G R O U P S ****************************************************** 
	 ************************************************************************************************************************/
	
	public List<Group> getGroups() {
		List<Group> groups = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_GROUPS,
					null, null, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				groups = new LinkedList<Group>();
				do {
					int id = c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID));
					groups.add(new Group(
						id,
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						getPortfoliosByGroupId(id)
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groups;
	}
	
	public Group getGroupById(int id) {
		Group group = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_GROUPS,
					null, DatabaseHelper.FIELD_ID+"="+id, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				group = new Group(
					id,
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
					getPortfoliosByGroupId(id)
				);
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return group;
	}
	
	public void addGroup(Group group) {
		if (group == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, group.getName());
		db.insert(DatabaseHelper.TABLE_GROUPS, null, values);
		
//		List<Portfolio> portfolios = group.getPortfolios();
//		if (portfolios != null && !portfolios.isEmpty())
//			for (Portfolio portfolio : portfolios)
//				addPortfolio(portfolio, group.getId());
	}
	
	public void updateGroup(Group group) {
		if (group == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, group.getName());
		db.update(DatabaseHelper.TABLE_GROUPS, values, DatabaseHelper.FIELD_ID+"="+group.getId(), null);
		
//		List<Portfolio> portfolios = group.getPortfolios();
//		if (portfolios != null && !portfolios.isEmpty())
//			for (Portfolio portfolio : portfolios)
//				updatePortfolio(portfolio);
	}
	
	public void deleteGroup(Group group) {
		if (group == null)
			return;
		db.delete(DatabaseHelper.TABLE_GROUPS, DatabaseHelper.FIELD_ID+"="+group.getId(), null);
		
		List<Portfolio> portfolios = group.getPortfolios();
		if (portfolios != null && !portfolios.isEmpty())
			for (Portfolio portfolio : portfolios)
				deletePortfolio(portfolio);
	}
	
	/************************************************************************************************************************
	 ************************************************* P O R T F O L I O S ************************************************** 
	 ************************************************************************************************************************/
	
	public List<Portfolio> getPortfolios() {
		List<Portfolio> portfolios = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_PORTFOLIOS, 
					null, null, null, null, null, null);
			if (c.moveToFirst()) {
				portfolios = new LinkedList<Portfolio>();
				do {
					portfolios.add(new Portfolio(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME))
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return portfolios;
	}
	
	public List<Portfolio> getPortfoliosByGroupId(int groupId) {
		List<Portfolio> portfolios = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_PORTFOLIOS, 
					null, DatabaseHelper.FIELD_GROUP_ID+"="+groupId, null, null, null, null);
			if (c.moveToFirst()) {
				portfolios = new LinkedList<Portfolio>();
				do {
					portfolios.add(new Portfolio(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME))
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return portfolios;
	}
	
	public Portfolio getPortfolioById(int id) {
		Portfolio portfolio = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_PORTFOLIOS, 
					null, DatabaseHelper.FIELD_ID+"="+id, null, null, null, null);
			if (c.moveToFirst()) {
				portfolio = new Portfolio(
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_ID)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME))
				);
				portfolio.setVideos(getVideosByPortfolioId(id));
				portfolio.setImages(getImagesByPortfolioId(id));
				portfolio.setAudios(getAudiosByPortfolioId(id));
				portfolio.setNotes(getNotesByPortfolioId(id));
				portfolio.setUrls(getUrlsByPortfolioId(id));
				portfolio.setDocs(getDocsByPortfolioId(id));
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return portfolio;
	}
	
	public void addPortfolio(Portfolio portfolio) {
		if (portfolio == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, portfolio.getName());
		values.put(DatabaseHelper.FIELD_GROUP_ID, portfolio.getGroupId());
		db.insert(DatabaseHelper.TABLE_PORTFOLIOS, null, values);
	}
	
	public void updatePortfolio(Portfolio portfolio) {
		if (portfolio == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, portfolio.getName());
		values.put(DatabaseHelper.FIELD_GROUP_ID, portfolio.getGroupId());
		db.update(DatabaseHelper.TABLE_PORTFOLIOS, values, DatabaseHelper.FIELD_ID+"="+portfolio.getId(), null);
	}
	
	public void deletePortfolio(Portfolio portfolio) {
		if (portfolio == null)
			return;
		db.delete(DatabaseHelper.TABLE_PORTFOLIOS, DatabaseHelper.FIELD_ID+"="+portfolio.getId(), null);
	}
	
	/************************************************************************************************************************
	 ***************************************************** V I D E O S ****************************************************** 
	 ************************************************************************************************************************/
	
	public List<Video> getVideosByPortfolioId(int portfolioId) {
		List<Video> videos = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_VIDEOS, 
					null, DatabaseHelper.FIELD_PORTFOLIO_ID+"="+portfolioId, null, null, null, null);
			if (c.moveToFirst()) {
				videos = new LinkedList<Video>();
				do {
					videos.add(new Video(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_PORTFOLIO_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_URI)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_PATH)),
						null
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return videos;
	}
	
	public void addVideos(List<Video> videos) {
		if (videos == null || videos.isEmpty())
			return;
		for (Video video : videos)
			addVideo(video);
	}
	
	public void addVideo(Video video) {
		if (video == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, video.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, video.getName());
		values.put(DatabaseHelper.FIELD_URI, video.getUri());
		values.put(DatabaseHelper.FIELD_PATH, video.getPath());
		db.insert(DatabaseHelper.TABLE_VIDEOS, null, values);
	}
	
	public void updateVideos(List<Video> videos) {
		if (videos == null || videos.isEmpty())
			return;
		for (Video video : videos)
			updateVideo(video);
	}
	
	public void updateVideo(Video video) {
		if (video == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, video.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, video.getName());
		values.put(DatabaseHelper.FIELD_URI, video.getUri());
		values.put(DatabaseHelper.FIELD_PATH, video.getPath());
		db.update(DatabaseHelper.TABLE_VIDEOS, values, DatabaseHelper.FIELD_ID+"="+video.getId(), null);
	}
	
	public void deleteVideos(List<Video> videos) {
		if (videos == null || videos.isEmpty())
			return;
		for (Video video : videos)
			deleteVideo(video);
	}
	
	public void deleteVideo(Video video) {
		if (video == null)
			return;
		db.delete(DatabaseHelper.TABLE_VIDEOS, DatabaseHelper.FIELD_ID+"="+video.getId(), null);
	}
	
	public void deleteVideosByPortfolioId(int portfolioId) {
		db.delete(DatabaseHelper.TABLE_VIDEOS, DatabaseHelper.FIELD_PORTFOLIO_ID+"="+portfolioId, null);
	}
	
	/************************************************************************************************************************
	 ***************************************************** I M A G E S ****************************************************** 
	 ************************************************************************************************************************/
	
	public List<Image> getImagesByPortfolioId(int portfolioId) {
		List<Image> images = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_IMAGES, 
					null, DatabaseHelper.FIELD_PORTFOLIO_ID+"="+portfolioId, null, null, null, null);
			if (c.moveToFirst()) {
				images = new LinkedList<Image>();
				do {
					images.add(new Image(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_PORTFOLIO_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_PATH))
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return images;
	}
	
	public void addImages(List<Image> images) {
		if (images == null || images.isEmpty())
			return;
		for (Image image : images)
			addImage(image);
	}
	
	public long addImage(Image image) {
		if (image == null)
			return -1;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, image.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, image.getName());
		values.put(DatabaseHelper.FIELD_PATH, image.getPath());
		return db.insert(DatabaseHelper.TABLE_IMAGES, null, values);
	}
	
	public void updateImages(List<Image> images) {
		if (images == null || images.isEmpty())
			return;
		for (Image image : images)
			updateImage(image);
	}
	
	public void updateImage(Image image) {
		if (image == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, image.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, image.getName());
		values.put(DatabaseHelper.FIELD_PATH, image.getPath());
		db.update(DatabaseHelper.TABLE_IMAGES, values, DatabaseHelper.FIELD_ID+"="+image.getId(), null);
	}
	
	public void deleteImages(List<Image> images) {
		if (images == null || images.isEmpty())
			return;
		for (Image image : images)
			deleteImage(image);
	}
	
	public void deleteImage(Image image) {
		if (image == null)
			return;
		db.delete(DatabaseHelper.TABLE_IMAGES, DatabaseHelper.FIELD_ID+"="+image.getId(), null);
	}
	
	public void deleteImagesByPortfolioId(int portfolioId) {
		db.delete(DatabaseHelper.TABLE_IMAGES, DatabaseHelper.FIELD_PORTFOLIO_ID+"="+portfolioId, null);
	}
	
	/************************************************************************************************************************
	 ***************************************************** A U D I O S ****************************************************** 
	 ************************************************************************************************************************/
	
	public List<Audio> getAudiosByPortfolioId(int portfolioId) {
		List<Audio> audios = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_AUDIOS, null, 
					DatabaseHelper.FIELD_PORTFOLIO_ID+"="+portfolioId, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				audios = new LinkedList<Audio>();
				do {
					audios.add(new Audio(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						portfolioId,
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_PATH))
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return audios;
	}
	
	public void addAudio(Audio audio) {
		if (audio == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, audio.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, audio.getName());
		values.put(DatabaseHelper.FIELD_PATH, audio.getPath());
		db.insert(DatabaseHelper.TABLE_AUDIOS, null, values);
	}
	
	public void updateAudio(Audio audio) {
		if (audio == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, audio.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, audio.getName());
		values.put(DatabaseHelper.FIELD_PATH, audio.getPath());
		db.update(DatabaseHelper.TABLE_AUDIOS, values, DatabaseHelper.FIELD_ID+"="+audio.getId(), null);
	}
	
	public void deleteAudio(Audio audio) {
		if (audio == null)
			return;
		db.delete(DatabaseHelper.TABLE_AUDIOS, DatabaseHelper.FIELD_ID+"="+audio.getId(), null);
	}
	
	/************************************************************************************************************************
	 ****************************************************** N O T E S ******************************************************* 
	 ************************************************************************************************************************/
	
	public List<Note> getNotesByPortfolioId(int portfolioId) {
		List<Note> notes = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_NOTES, null, 
					DatabaseHelper.FIELD_PORTFOLIO_ID+"="+portfolioId, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				notes = new LinkedList<Note>();
				do {
					notes.add(new Note(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						portfolioId,
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_PATH)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_TEXT))
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notes;
	}
	
	public void addNote(Note note) {
		if (note == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, note.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, note.getName());
		values.put(DatabaseHelper.FIELD_TEXT, note.getText());
		values.put(DatabaseHelper.FIELD_PATH, note.getPath());
		db.insert(DatabaseHelper.TABLE_NOTES, null, values);
	}
	
	public void updateNote(Note note) {
		if (note == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, note.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, note.getName());
		values.put(DatabaseHelper.FIELD_TEXT, note.getText());
		values.put(DatabaseHelper.FIELD_PATH, note.getPath());
		db.update(DatabaseHelper.TABLE_NOTES, values, DatabaseHelper.FIELD_ID+"="+note.getId(), null);
	}
	
	public void deleteNote(Note note) {
		if (note == null)
			return;
		db.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.FIELD_ID+"="+note.getId(), null);
	}
	
	/************************************************************************************************************************
	 ******************************************************* U R L S ******************************************************** 
	 ************************************************************************************************************************/
	
	public List<Url> getUrlsByPortfolioId(int portfolioId) {
		List<Url> urls = null;
		try {
			 Cursor c = db.query(DatabaseHelper.TABLE_URLS, null, DatabaseHelper.FIELD_PORTFOLIO_ID+"="+portfolioId, null, null, null, null);
			 if (c.moveToFirst()) {
				 urls = new LinkedList<Url>();
				 do {
					 urls.add(new Url(
					     c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
					     portfolioId,
					     c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
					     c.getString(c.getColumnIndex(DatabaseHelper.FIELD_URL)),
					     c.getString(c.getColumnIndex(DatabaseHelper.FIELD_PATH))
					 ));
				 } while (c.moveToNext());
			 }
			 if (c != null && !c.isClosed())
				 c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urls;
	}
	
	public void addUrl(Url url) {
		if (url == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, url.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, url.getName());
		values.put(DatabaseHelper.FIELD_URL, url.getUrl());
		values.put(DatabaseHelper.FIELD_PATH, url.getPath());
		db.insert(DatabaseHelper.TABLE_URLS, null, values);
	}
	
	public void updateUrl(Url url) {
		if (url == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, url.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, url.getName());
		values.put(DatabaseHelper.FIELD_URL, url.getUrl());
		values.put(DatabaseHelper.FIELD_PATH, url.getPath());
		db.update(DatabaseHelper.TABLE_URLS, values, DatabaseHelper.FIELD_ID+"="+url.getId(), null);
	}
	
	public void deleteUrl(Url url) {
		if (url == null)
			return;
		db.delete(DatabaseHelper.TABLE_URLS, DatabaseHelper.FIELD_ID+"="+url.getId(), null);
	}
	
	/************************************************************************************************************************
	 ******************************************************* D O C S ******************************************************** 
	 ************************************************************************************************************************/
	
	public List<Doc> getDocsByPortfolioId(int portfolioId) {
		List<Doc> docs = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_DOCS, null, DatabaseHelper.FIELD_PORTFOLIO_ID+"="+portfolioId, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				docs = new LinkedList<Doc>();
				do {
					docs.add(new Doc(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						portfolioId,
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_PATH))
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return docs;
	}
	
	public void addDocs(List<Doc> docs) {
		if (docs == null || docs.isEmpty())
			return;
		for (Doc doc : docs)
			addDoc(doc);
	}
	
	public void addDoc(Doc doc) {
		if (doc == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, doc.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, doc.getName());
		values.put(DatabaseHelper.FIELD_PATH, doc.getPath());
		db.insert(DatabaseHelper.TABLE_DOCS, null, values);
	}
	
	public void updateDoc(Doc doc) {
		if (doc == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_PORTFOLIO_ID, doc.getPortfolioId());
		values.put(DatabaseHelper.FIELD_NAME, doc.getName());
		values.put(DatabaseHelper.FIELD_PATH, doc.getPath());
		db.update(DatabaseHelper.TABLE_DOCS, values, DatabaseHelper.FIELD_ID+"="+doc.getId(), null);
	}
	
	public void deleteDoc(Doc doc) {
		if (doc == null)
			return;
		db.delete(DatabaseHelper.TABLE_DOCS, DatabaseHelper.FIELD_ID+"="+doc.getId(), null);
	}

}
