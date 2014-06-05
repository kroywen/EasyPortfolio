package com.thepegeekapps.easyportfolio.screen;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.adapter.EntryRecordAdapter;
import com.thepegeekapps.easyportfolio.model.EntryRecord;
import com.thepegeekapps.easyportfolio.storage.EntryHolder;
import com.thepegeekapps.easyportfolio.util.Utils;

public class DropboxScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	public static final String APP_KEY = "aavyooi5y820cfa";
    public static final String APP_SECRET = "07tpcxancwbfjfp";
    public static final String ACCOUNT_PREFS_NAME = "prefs";
    public static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    public static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    public static final AccessType ACCESS_TYPE = AccessType.DROPBOX;    
    
    protected DropboxAPI<AndroidAuthSession> mApi;
    
	protected Button leftBtn;
	protected Button rightBtn;
	protected ListView list;
	protected ImageView backBtn;
	protected TextView selected;
	
	protected EntryHolder entryHolder;
	
	protected EntryRecordAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dropbox_screen);
		setScreenTitle(R.string.my_dropbox);
		initializeViews();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        entryHolder = EntryHolder.getInstance(mApi);
        (new InitEntryTask()).execute((Void[]) null);
	}
	
	protected void initializeViews() {
		leftBtn = getLeftButton();
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setText(R.string.close);
		leftBtn.setOnClickListener(this);
		
		rightBtn = getRightBtn();
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setText(R.string.download);
		rightBtn.setOnClickListener(this);
		
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(this);
		
		backBtn = (ImageView) findViewById(R.id.back);
		backBtn.setOnClickListener(this);
		
		selected = (TextView) findViewById(R.id.selected);
		selected.setText(String.format(getString(R.string.selected_pattern), 0));
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftBtn:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.rightBtn:
			int checked = entryHolder.getCheckedCount();
			if (checked == 0) {
				Toast.makeText(this, R.string.no_files_selected, Toast.LENGTH_SHORT).show();
			} else {
				(new DownloadTask(entryHolder.getCheckedEntries())).execute((Void[]) null);
			}
			break;
		case R.id.back:
			back();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		EntryRecord record = (EntryRecord) parent.getItemAtPosition(position);
		if (record != null) {
			Entry entry = record.getEntry();
			if (entry != null) {
				if (entry.isDir) {
					entryHolder.setCurrent(record);
					adapter.setContents(record.getChildren());
					adapter.notifyDataSetChanged();
				} else {
					record.setChecked(!record.isChecked());
					adapter.notifyDataSetChanged();
					int checked = entryHolder.getCheckedCount();
					selected.setText(String.format(getString(R.string.selected_pattern), checked));
				}
			}
		}
	}
	
	protected void back() {
		entryHolder.back();
		EntryRecord current = entryHolder.getCurrent();
		if (current != null) {
			adapter.setContents(current.getChildren());
			adapter.notifyDataSetChanged();
		}
	}
	
	protected AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }
	
	protected String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }

    protected void storeKeys(String key, String secret) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    protected void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
    
    class InitEntryTask extends AsyncTask<Void, Void, Void> {
    	
    	@Override
    	protected void onPreExecute() {
    		showProgressDialog(getString(R.string.loading));
    	}

		@Override
		protected Void doInBackground(Void... unused) {
			entryHolder.initEntryList(mApi);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			hideProgressDialog();
			if (entryHolder.getRoot() != null) {
				List<EntryRecord> contents = entryHolder.getRoot().getChildren();
				if (contents != null && !contents.isEmpty()) {
					if (adapter == null) {
						adapter = new EntryRecordAdapter(DropboxScreen.this, contents);
						list.setAdapter(adapter);
					}
				} else {
					adapter.setContents(contents);
					adapter.notifyDataSetChanged();
				}
			}
		}
    }
    
    class DownloadTask extends AsyncTask<Void, Long, Boolean> {
    	
    	protected ProgressDialog dialog;
    	protected List<EntryRecord> records;
    	protected String error;
    	
    	protected String currentName;
    	
    	public DownloadTask(List<EntryRecord> records) {
    		this.records = records;
    		dialog = new ProgressDialog(DropboxScreen.this);
    		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		dialog.setMessage(getString(R.string.downloading));
    		dialog.setIndeterminate(true);
    	}
    	
    	@Override
    	public void onPreExecute() {
    		dialog.show();
    	}

		@Override
		protected Boolean doInBackground(Void... unused) {
			try {
				File docDir = new File(Environment.getExternalStorageDirectory(), "/easyportfolio/doc/");
				docDir.mkdirs();
				
				for (EntryRecord record : records) {
					Entry entry = record.getEntry();
					File file = new File(docDir, entry.fileName());
					currentName = entry.fileName();
					FileOutputStream fos = new FileOutputStream(file);
					mApi.getFile(entry.path, null, fos, new ProgressListener() {
						@Override
						public void onProgress(long bytes, long total) {
							publishProgress(bytes, total);
						}
					});
					record.setDownloadedPath(Environment.getExternalStorageDirectory() + "/easyportfolio/doc/" + entry.fileName());
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				error = getString(R.string.download_error);
				return false;
			}
			return true;
		}
		
		@Override
		protected void onProgressUpdate(Long... values) {
			String message = String.format(getString(R.string.download_pattern), 
					currentName, Utils.getReadableFilesize(values[0]), Utils.getReadableFilesize(values[1]));
			dialog.setMessage(message);
			super.onProgressUpdate(values);
		}
		
		@Override
		public void onPostExecute(Boolean result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			if (!result.booleanValue()) {
				Toast.makeText(DropboxScreen.this, error, Toast.LENGTH_SHORT).show();
			}
		}
    	
    }

}
