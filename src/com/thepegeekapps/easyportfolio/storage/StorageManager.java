package com.thepegeekapps.easyportfolio.storage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import com.thepegeekapps.easyportfolio.model.Record;

public class StorageManager {
	
	public enum StorageLocation { INTERNAL, EXTERNAL };
	
	public static final String APP_DIRECTORY = "easyportfolio/";
	public static final String[] APP_DIRECTORY_RECORD = { "videos/", "images/", "audios/", "notes/", "urls/", "docs/" };
	
	public static final int UNDEFINED = -1;
	public static final int OK = 0;
	public static final int NOT_ENOUGH_SPACE = 1;
	public static final int SAVE_RECORD_ERROR = 2;
	public static final int DELETE_RECORD_ERROR = 3;
	public static final int CREATE_DIRECTORIES_ERROR = 4;
	
	protected static Context context;
	protected static StorageManager instance;
	protected StorageLocation storageLocation;
	
	protected StorageManager() {
		if (mediaMounted() == OK) {
			setStorageLocation(StorageLocation.EXTERNAL);
			createDirectories(StorageLocation.EXTERNAL);
		} else {
			setStorageLocation(StorageLocation.INTERNAL);
			createDirectories(StorageLocation.INTERNAL);
		}
	}

	public static StorageManager getInstance(Context ctx) {
		context = ctx;
		if (instance == null)
			instance = new StorageManager();
		return instance;
	}
	
	public int mediaMounted() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
			return OK;
		else
			return UNDEFINED;
	}
	
	public long getAvailableExternalSpace() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
	}
	
	public long getavailableInternalSpace() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
    }
	
	public int createDirectories(StorageLocation loc) {
		try {
			String rootDir = (loc == StorageLocation.INTERNAL) ? Environment.getRootDirectory().getAbsolutePath() :
				Environment.getExternalStorageDirectory().getPath();
			
			File appDir = new File(rootDir, APP_DIRECTORY);
			if (appDir != null && !appDir.exists())
				appDir.mkdir();
			
			for (int i=0; i<APP_DIRECTORY_RECORD.length; i++) {
				String path = APP_DIRECTORY_RECORD[i];
				if (appDir != null && appDir.exists()) {
					File recordDir = new File(appDir, path);
					if (recordDir != null && !recordDir.exists())
						recordDir.mkdir();
				}
			}
					
			return OK;
		} catch (Exception e) {
			e.printStackTrace();
			return CREATE_DIRECTORIES_ERROR;
		}
	}
	
	public Uri getRecordUri(Record record) {
		return Uri.fromFile(getRecordFile(record));
	}
	
	public File getRecordFile(Record record) {
		String path = getRecordDirectory(record);
		
		File dir = new File(path);
		if (dir != null && !dir.exists())
			dir.mkdirs();
		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String filename = (record.getType() == Record.TYPE_VIDEO) ? "vid_" + timeStamp + ".mp4" :
			(record.getType() == Record.TYPE_IMAGE) ? "img_" + timeStamp + ".png" :
				record.getPath().substring(record.getPath().lastIndexOf(File.separator) + 1);
		File file = new File(dir, filename);
		return file;
	}
	
	public int saveRecord(Record record) {
		if (record == null)
			return SAVE_RECORD_ERROR;
		try {
			// TODO
			return OK;
		} catch (Exception e) {
			e.printStackTrace();
			return SAVE_RECORD_ERROR;
		}	
	}
	
	public int deleteRecord(Record record) {
		if (record == null)
			return DELETE_RECORD_ERROR;
		try {
			File f = new File(record.getPath());
			if (f != null && f.exists() && f.isFile()) {
				return f.delete() ? OK : DELETE_RECORD_ERROR; 
			} else {
				return DELETE_RECORD_ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return DELETE_RECORD_ERROR;
		}
	}
	
	public StorageLocation getStorageLocation() {
		return storageLocation;
	}
	
	public void setStorageLocation(StorageLocation storageLocation) {
		this.storageLocation = storageLocation;
	}
	
	public String getRecordDirectory(Record record) {
		String rootDir = (storageLocation == StorageLocation.INTERNAL) ? Environment.getRootDirectory().getAbsolutePath() :
			Environment.getExternalStorageDirectory().getPath();
		return rootDir + File.separator + APP_DIRECTORY + APP_DIRECTORY_RECORD[record.getType()] + File.separator;
	}

}
