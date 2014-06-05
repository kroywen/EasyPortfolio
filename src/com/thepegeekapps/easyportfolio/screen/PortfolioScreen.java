package com.thepegeekapps.easyportfolio.screen;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.adapter.RecordAdapter;
import com.thepegeekapps.easyportfolio.database.DatabaseHelper;
import com.thepegeekapps.easyportfolio.model.Audio;
import com.thepegeekapps.easyportfolio.model.Doc;
import com.thepegeekapps.easyportfolio.model.Image;
import com.thepegeekapps.easyportfolio.model.Note;
import com.thepegeekapps.easyportfolio.model.Portfolio;
import com.thepegeekapps.easyportfolio.model.Record;
import com.thepegeekapps.easyportfolio.model.Url;
import com.thepegeekapps.easyportfolio.model.Video;
import com.thepegeekapps.easyportfolio.util.Utils;

public class PortfolioScreen extends BaseScreen implements OnClickListener {
	
	public static final String APP_KEY = "aavyooi5y820cfa";
    public static final String APP_SECRET = "07tpcxancwbfjfp";
    public static final String ACCOUNT_PREFS_NAME = "prefs";
    public static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    public static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    public static final AccessType ACCESS_TYPE = AccessType.DROPBOX;
    
    protected DropboxAPI<AndroidAuthSession> mApi;
    protected boolean loggedIn;
	
	protected Button exportBtn;
	protected ExpandableListView list;
	
	protected int portfolioId;
	protected Portfolio portfolio;
	protected RecordAdapter adapter;
	
	protected boolean showConfirmUploadingDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portfolio_screen);
		getIntentData();
		adapter = new RecordAdapter(this, portfolio);
		if (portfolio != null)
			setScreenTitle(portfolio.getName());
		else
			setScreenTitle(R.string.portfolio);
		initializeViews();
		
		AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_PORTFOLIO_ID)) {
			portfolioId = intent.getIntExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, 0);
			portfolio = dbManager.getPortfolioById(portfolioId);
		}
	}
	
	protected void initializeViews() {
		list = (ExpandableListView) findViewById(R.id.list);
		list.setAdapter(adapter);
//		list.setOnItemClickListener(this);
//		list.setOnChildClickListener(this);
		registerForContextMenu(list);
		
		exportBtn = getRightBtn();
		exportBtn.setVisibility(View.VISIBLE);
		exportBtn.setText(R.string.export);
		exportBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.rightBtn) {
			ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo info = conManager.getActiveNetworkInfo();
			if (info.getState() != NetworkInfo.State.CONNECTED) {
				Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
			} else {
				if (loggedIn) {
					List<Record> records = adapter.getCheckedRecords();
					if (records == null || records.isEmpty()) {
						Toast.makeText(this, R.string.no_checked_records, Toast.LENGTH_SHORT).show();
					} else {
						showConfirmUploadDialog();
					}
	            } else {
	            	showConfirmUploadingDialog = true;
	                mApi.getSession().startAuthentication(this);
	            }
			}
		}
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        
        AndroidAuthSession session = mApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);
                loggedIn = true;
                
                if (showConfirmUploadingDialog) {
                	showConfirmUploadingDialog = false;
                	List<Record> records = adapter.getCheckedRecords();
					if (records == null || records.isEmpty()) {
						Toast.makeText(this, R.string.no_checked_records, Toast.LENGTH_SHORT).show();
					} else {
						showConfirmUploadDialog();
					}
                }
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			ExpandableListView.ExpandableListContextMenuInfo info =
				    (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
			int type = ExpandableListView.getPackedPositionType(info.packedPosition);
			if (type != ExpandableListView.PACKED_POSITION_TYPE_CHILD) 
				return;
			int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
			int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
			Record record = portfolio.asRecordList(group).get(child);
			
			menu.setHeaderTitle(record.getName());
			String[] menuItems = new String[] {getString(R.string.delete)};;
			if (menuItems != null)
				for (int i = 0; i < menuItems.length; i++)
					menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		ExpandableListView.ExpandableListContextMenuInfo info =
			    (ExpandableListView.ExpandableListContextMenuInfo) menuItem.getMenuInfo();
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
		    int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		    int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
		    
		    if (menuItem.getItemId() == 0)
		    	deleteRecord(groupPosition, childPosition);
		}
		return true;
	}
	
	protected void export() {
		List<Record> records = adapter.getCheckedRecords();
		(new UploadTask(records)).execute((Void[]) null);
	}
	
	protected void deleteRecord(int groupPosition, int childPosition) {
		Record record = portfolio.asRecordList(groupPosition).get(childPosition);
		switch (groupPosition) {
		case Record.TYPE_VIDEO:
			deleteRecord(((Video) record).getPath());
			dbManager.deleteVideo((Video) record);
			break;
		case Record.TYPE_IMAGE:
			deleteRecord(((Image) record).getPath());
			dbManager.deleteImage((Image) record);
			break;
		case Record.TYPE_AUDIO:
			deleteRecord(((Audio) record).getPath());
			dbManager.deleteAudio((Audio) record);
			break;
		case Record.TYPE_NOTE:
			deleteRecord(((Note) record).getPath());
			dbManager.deleteNote((Note) record);
			break;
		case Record.TYPE_URL:
			deleteRecord(((Url) record).getPath());
			dbManager.deleteUrl((Url) record);
			break;
		case Record.TYPE_DOC:
			deleteRecord(((Doc) record).getPath());
			dbManager.deleteDoc((Doc) record);
			break;
		}
		portfolio = dbManager.getPortfolioById(portfolioId);
		adapter.setPortfolio(portfolio);
		adapter.notifyDataSetChanged();
	}
	
	protected void deleteRecord(String path) {
		if (path == null)
			return;
		try {
			File f = new File(path);
			f.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void showConfirmUploadDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.export_to_dropbox)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					export();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create()
			.show();
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
	
	class UploadTask extends AsyncTask<Void, Long, Boolean> {
    	
    	protected ProgressDialog dialog;
    	protected List<Record> records;
    	protected String error;
    	
    	protected String currentName;
    	
    	public UploadTask(List<Record> records) {
    		this.records = records;
    		dialog = new ProgressDialog(PortfolioScreen.this);
    		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		dialog.setMessage(getString(R.string.uploading));
    		dialog.setIndeterminate(true);
    	}
    	
    	@Override
    	public void onPreExecute() {
    		dialog.show();
    	}
    		
    	protected String getRecordFolder(int type) {
    		String path = "/";
    		switch (type) {
    		case Record.TYPE_VIDEO:	path += "videos";	break;
    		case Record.TYPE_IMAGE:	path += "images";	break;
    		case Record.TYPE_AUDIO:	path += "audios";	break;
    		case Record.TYPE_NOTE:	path += "notes";	break;
    		case Record.TYPE_URL:	path += "urls";		break;
    		case Record.TYPE_DOC:	path += "docs";		break;
    		}
    		return path;
    	}

		@Override
		protected Boolean doInBackground(Void... unused) {
			try {				
				for (Record record : records) {
					try { mApi.createFolder("/" + portfolio.getName()); } catch (Exception e) { e.printStackTrace(); }
					
					if (record.getType() == Record.TYPE_VIDEO)
						try { mApi.createFolder("/" + portfolio.getName() + "/videos"); } catch (Exception e) { e.printStackTrace(); }
					if (record.getType() == Record.TYPE_IMAGE)
						try { mApi.createFolder("/" + portfolio.getName() + "/images"); } catch (Exception e) { e.printStackTrace(); }
					if (record.getType() == Record.TYPE_AUDIO)
						try { mApi.createFolder("/" + portfolio.getName() + "/audios"); } catch (Exception e) { e.printStackTrace(); }
					if (record.getType() == Record.TYPE_NOTE)
						try { mApi.createFolder("/" + portfolio.getName() + "/notes"); } catch (Exception e) { e.printStackTrace(); }
					if (record.getType() == Record.TYPE_URL)
						try { mApi.createFolder("/" + portfolio.getName() + "/urls"); } catch (Exception e) { e.printStackTrace(); }
					if (record.getType() == Record.TYPE_DOC)
						try { mApi.createFolder("/" + portfolio.getName() + "/docs"); } catch (Exception e) { e.printStackTrace(); }
					
					File file = new File(record.getPath());
					FileInputStream is = new FileInputStream(file);
					
					currentName = record.getName();
					
					String filename = record.getPath().substring(record.getPath().lastIndexOf('/')+1);
					String path = "/" + portfolio.getName() + getRecordFolder(record.getType()) + "/" + filename;
					
					try {
						mApi.putFileOverwrite(path, is, file.length(), new ProgressListener() {
							@Override
							public void onProgress(long bytes, long total) {
								publishProgress(bytes, total);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				error = getString(R.string.upload_error);
				return false;
			}
			return true;
		}
		
		@Override
		protected void onProgressUpdate(Long... values) {
			String message = String.format(getString(R.string.upload_pattern), 
					currentName, Utils.getReadableFilesize(values[0]), Utils.getReadableFilesize(values[1]));
			dialog.setMessage(message);
			super.onProgressUpdate(values);
		}
		
		@Override
		public void onPostExecute(Boolean result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			if (!result.booleanValue()) {
				Toast.makeText(PortfolioScreen.this, error, Toast.LENGTH_SHORT).show();
			}
		}
    	
    }

}
