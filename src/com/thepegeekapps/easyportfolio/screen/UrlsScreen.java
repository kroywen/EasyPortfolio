package com.thepegeekapps.easyportfolio.screen;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.adapter.UrlAdapter;
import com.thepegeekapps.easyportfolio.database.DatabaseHelper;
import com.thepegeekapps.easyportfolio.model.Url;

public class UrlsScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	protected Button addUrlBtn;
	protected ListView list;
	protected Button saveBtn; 
	
	protected int portfolioId;
	protected UrlAdapter adapter;
	protected List<Url> urls;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.urls_screen);
		setScreenTitle(R.string.urls);
		getIntentData();
		initializeViews();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_PORTFOLIO_ID)) {
			portfolioId = intent.getIntExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, 0);
			urls = new LinkedList<Url>();
			adapter = new UrlAdapter(this, urls);
		}
	}
	
	protected void initializeViews() {
		addUrlBtn = (Button) findViewById(R.id.addUrlBtn);
		addUrlBtn.setOnClickListener(this);
		
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		registerForContextMenu(list);
		
		saveBtn = getRightBtn();
		saveBtn.setVisibility(View.VISIBLE);
		saveBtn.setText(R.string.save);
		saveBtn.setOnClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Url url = urls.get(position);
		if (url != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url.getUrl()));
			startActivity(Intent.createChooser(intent, getString(R.string.select_web_app)));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rightBtn:
			save();
			break;
		case R.id.addUrlBtn:
			showEditUrlDialog(null);
			break;
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Url url = urls.get(info.position);
			menu.setHeaderTitle(url.getName());
			
			String[] menuItems = new String[] {getString(R.string.edit), getString(R.string.delete)};;
			if (menuItems != null)
				for (int i = 0; i < menuItems.length; i++)
					menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		Url url = urls.get(info.position);
		switch (menuItem.getItemId()) {
		case 0:
			showEditUrlDialog(url);
			break;
		case 1:
			showConfirmDeleteUrlDialog(url);
			break;
		}
		return true;
	}
	
	protected void save() {
		if (urls != null && !urls.isEmpty()) {
			for (int i=0; i<urls.size(); i++) {
				Url url = urls.get(i);
				try {
					saveUrl(url);
					dbManager.addUrl(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		setResult(RESULT_OK);
		finish();
	}
	
	protected void saveUrl(Url url) throws Exception {
		File urlsDir = new File(Environment.getExternalStorageDirectory(), "/easyportfolio/url/");
		urlsDir.mkdirs();
		
		File file = new File(urlsDir, url.toString()+".txt");
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = url.getUrl().getBytes();
		fos.write(buffer);
		fos.flush();
		fos.close();
		url.setPath(file.getAbsolutePath());
	}
	
	protected void showEditUrlDialog(final Url url) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.setPadding(10, 10, 10, 10);
		
		final EditText input = new EditText(this);
		input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setHint(R.string.address_hint);
		if (url != null)
			input.setText(url.getName());

		ll.addView(input);
		
		builder
		.setTitle(R.string.enter_url_address)
			.setView(ll)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					String name = input.getText().toString();
					if (name != null && !name.equals("")) {
						name = name.startsWith("http://") || name.startsWith("https://") ? name : "http://" + name;
						if (url == null) {
							Url url = new Url(0, portfolioId, name, name, null);
							urls.add(url);
						} else {
							url.setName(name);
						}
						dialog.dismiss();
						adapter.setUrls(urls);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(UrlsScreen.this, R.string.empty_name, Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create()
			.show();
	}
	
	protected void showConfirmDeleteUrlDialog(final Url url) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(R.string.confirm_delete_url)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				urls.remove(url);
				adapter.setUrls(urls);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
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

}
