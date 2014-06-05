package com.thepegeekapps.easyportfolio.screen;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.adapter.DocumentAdapter;
import com.thepegeekapps.easyportfolio.database.DatabaseHelper;
import com.thepegeekapps.easyportfolio.model.Doc;
import com.thepegeekapps.easyportfolio.storage.EntryHolder;

public class DocumentsScreen extends BaseScreen implements OnClickListener {
	
	public static final int DOWNLOAD_FROM_DROPBOX_REQUEST_CODE = 0;
	
	public static final String APP_KEY = "aavyooi5y820cfa";
    public static final String APP_SECRET = "07tpcxancwbfjfp";
    
    public static final AccessType ACCESS_TYPE = AccessType.DROPBOX;
    
    protected Button addDocBtn;
    protected Button rightBtn;
    protected ListView list;
    
    protected int portfolioId;
    protected DocumentAdapter adapter;
    protected List<Doc> docs;
    
    protected DropboxAPI<AndroidAuthSession> mApi;
    protected boolean loggedIn;
    public static final String ACCOUNT_PREFS_NAME = "prefs";
    public static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    public static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    
    protected boolean showDropboxDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.documents_screen);
		setScreenTitle(R.string.documents);
		getIntentData();
		initializeViews();
		
		AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);        
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_PORTFOLIO_ID)) {
			portfolioId = intent.getIntExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, 0);
			docs = EntryHolder.getInstance(mApi).getDownloadedDocuments(portfolioId);
	        
	        if (adapter == null)
	        	adapter = new DocumentAdapter(this, docs);
	        adapter.setDocuments(docs);
		}
	}
	
	protected void initializeViews() {
		addDocBtn = (Button) findViewById(R.id.addDocBtn);
		addDocBtn.setOnClickListener(this);
		
		rightBtn = getRightBtn();
		rightBtn.setText(R.string.save);
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setOnClickListener(this);
		
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.addDocBtn) {
			ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo info = conManager.getActiveNetworkInfo();
			if (info.getState() != NetworkInfo.State.CONNECTED) {
				Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
			} else {
				if (loggedIn) {
					showMyDropboxDialog();
	            } else {
	            	showDropboxDialog = true;
	                mApi.getSession().startAuthentication(this);
	            }
			}
		} else if (v.getId() == R.id.rightBtn) {
			dbManager.addDocs(docs);
			setResult(RESULT_OK);
			finish();
		}
	}
	
	protected void showMyDropboxDialog() {
		Intent intent = new Intent(this, DropboxScreen.class);
		startActivityForResult(intent, DOWNLOAD_FROM_DROPBOX_REQUEST_CODE);
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
                
                if (showDropboxDialog) {
                	showDropboxDialog = false;
                	showMyDropboxDialog();
                }
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == DOWNLOAD_FROM_DROPBOX_REQUEST_CODE) {
				docs.addAll(EntryHolder.getInstance(mApi).getDownloadedDocuments(portfolioId));
		        adapter.setDocuments(docs);
		        adapter.notifyDataSetChanged();
			}
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

}
