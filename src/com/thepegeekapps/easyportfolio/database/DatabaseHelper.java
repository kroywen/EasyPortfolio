package com.thepegeekapps.easyportfolio.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "easy_portfolio";
	public static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_GROUPS = "groups";
	public static final String TABLE_PORTFOLIOS = "portfolios";
	
	public static final String TABLE_VIDEOS = "videos";
	public static final String TABLE_NOTES = "notes";
	public static final String TABLE_IMAGES = "images";
	public static final String TABLE_AUDIOS = " audios";
	public static final String TABLE_URLS = "urls";
	public static final String TABLE_DOCS = "docs";
	
	public static final String FIELD_ID = "id";
	public static final String FIELD_PORTFOLIO_ID = "portfolio_id";
	public static final String FIELD_GROUP_ID = "group_id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_URI = "uri";
	public static final String FIELD_PATH = "path";
	public static final String FIELD_TEXT = "text";
	public static final String FIELD_URL = "url";
	
	public static final String CREATE_TABLE_PORTFOLIOS = 
			"create table if not exists " + TABLE_PORTFOLIOS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_GROUP_ID + " integer, " +
			FIELD_NAME + " text);";
	
	public static final String DROP_TABLE_PORTFOLIOS = 
			"drop table if exists " + TABLE_PORTFOLIOS;
	
	public static final String CREATE_TABLE_GROUPS = 
			"create table if not exists " + TABLE_GROUPS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_NAME + " text);";
	
	public static final String DROP_TABLE_GROUPS = 
			"drop table if exists " + TABLE_GROUPS;
	
	public static final String CREATE_TABLE_VIDEOS = 
			"create table if not exists " + TABLE_VIDEOS + " (" +
	        FIELD_ID + " integer primary key autoincrement, " +
			FIELD_PORTFOLIO_ID + " integer, " +
	        FIELD_NAME + " text, " +
			FIELD_URI + " text, " +
	        FIELD_PATH + " text);";
	
	public static final String DROP_TABLE_VIDEOS = 
			"drop table if exists " + TABLE_VIDEOS;
	
	public static final String CREATE_TABLE_IMAGES = 
			"create table if not exists " + TABLE_IMAGES + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_PORTFOLIO_ID + " integer, " +
			FIELD_NAME + " text, " +
			FIELD_PATH + " text);";
	
	public static final String DROP_TABLE_IMAGES = 
			"drop table if exists " + TABLE_IMAGES;
	
	public static final String CREATE_TABLE_AUDIOS =
			"create table if not exists " + TABLE_AUDIOS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_PORTFOLIO_ID + " integer, " +
			FIELD_NAME + " text, " +
			FIELD_PATH + " text);";
	
	public static final String DROP_TABLE_AUDIOS =
			"drop table if exists " + TABLE_AUDIOS;
	
	public static final String CREATE_TABLE_NOTES =
			"create table if not exists " + TABLE_NOTES + " (" +
	        FIELD_ID + " integer primary key autoincrement, " +
			FIELD_PORTFOLIO_ID + " integer, " +
	        FIELD_NAME + " text, " +
			FIELD_TEXT + " text, " +
	        FIELD_PATH + " text);";
	
	public static final String DROP_TABLE_NOTES =
			"drop table if exists " + TABLE_NOTES;
	
	public static final String CREATE_TABLE_URLS = 
			"create table if not exists " + TABLE_URLS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_PORTFOLIO_ID + " integer, " +
			FIELD_NAME + " text, " +
			FIELD_URL + " text, " +
			FIELD_PATH + " text);";
	
	public static final String DROP_TABLE_URLS = 
			"drop table if exists " + TABLE_URLS;
	
	public static final String CREATE_TABLE_DOCS = 
			"create table if not exists " + TABLE_DOCS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_PORTFOLIO_ID + " integer, " +
			FIELD_NAME + " text, " +
			FIELD_PATH + " text);";
	
	public static final String DROP_TABLE_DOCS =
			"drop table if exists " + TABLE_DOCS;

	protected Context context;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTables(db);
		onCreate(db);
	}
	
	protected void createTables(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_PORTFOLIOS);
		db.execSQL(CREATE_TABLE_GROUPS);
		
		db.execSQL(CREATE_TABLE_VIDEOS);
		db.execSQL(CREATE_TABLE_IMAGES);
		db.execSQL(CREATE_TABLE_AUDIOS);
		db.execSQL(CREATE_TABLE_NOTES);
		db.execSQL(CREATE_TABLE_URLS);
		db.execSQL(CREATE_TABLE_DOCS);
	}
	
	protected void dropTables(SQLiteDatabase db) {
		db.execSQL(DROP_TABLE_PORTFOLIOS);
		db.execSQL(DROP_TABLE_GROUPS);
		
		db.execSQL(DROP_TABLE_VIDEOS);
		db.execSQL(DROP_TABLE_IMAGES);
		db.execSQL(DROP_TABLE_AUDIOS);
		db.execSQL(DROP_TABLE_NOTES);
		db.execSQL(DROP_TABLE_URLS);
		db.execSQL(DROP_TABLE_DOCS);
	}

}
