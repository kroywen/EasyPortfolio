package com.thepegeekapps.easyportfolio.screen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.database.DatabaseManager;
import com.thepegeekapps.easyportfolio.storage.StorageManager;

public class BaseScreen extends Activity {
	
	public static final int PROGRESS_DIALOG_SHOW = 0;
	public static final int PROGRESS_DIALOG_HIDE = 1;
	
	protected DatabaseManager dbManager;
	protected StorageManager storageManager;
	
	protected TextView title; 
	protected Button leftBtn;
	protected Button rightBtn;
	
	protected ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dbManager = DatabaseManager.getInstance(this);
		storageManager = StorageManager.getInstance(this);
	}
	
	protected void setScreenTitle(String title) {
		((TextView) findViewById(R.id.title)).setText(title);
	}
	
	protected void setScreenTitle(int titleResId) {
		((TextView) findViewById(R.id.title)).setText(titleResId);
	}
	
	protected Button getRightBtn() {
		return (Button) findViewById(R.id.rightBtn);
	}
	
	protected Button getLeftButton() {
		return (Button) findViewById(R.id.leftBtn);
	}
	
	protected void showProgressDialog(String message) {
//		handler.sendEmptyMessage(PROGRESS_DIALOG_SHOW);
//		handler.obtainMessage(PROGRESS_DIALOG_SHOW, message).sendToTarget();
		if (progressDialog == null) {
			 progressDialog = new ProgressDialog(BaseScreen.this);
			 progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		 }
		 progressDialog.setMessage(message);
		 progressDialog.show();
	}
	
	protected void hideProgressDialog() {
//		handler.sendEmptyMessage(PROGRESS_DIALOG_HIDE);
//		handler.obtainMessage(PROGRESS_DIALOG_HIDE, null).sendToTarget();
		if (progressDialog != null && progressDialog.isShowing())
			 progressDialog.dismiss();
	}
	
	Handler handler = new Handler() {
		 @Override
		 public void handleMessage(Message msg) {
			 switch (msg.what) {
			 case PROGRESS_DIALOG_SHOW:
				 String message = (String) msg.obj;
				 if (progressDialog == null) {
					 progressDialog = new ProgressDialog(BaseScreen.this);
					 progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				 }
				 progressDialog.setMessage(message);
				 progressDialog.show();
				 break;
			 case PROGRESS_DIALOG_HIDE:
				 if (progressDialog != null && progressDialog.isShowing())
					 progressDialog.dismiss();
				 break;
			 }
		 }
	 };

}
