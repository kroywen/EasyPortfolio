package com.thepegeekapps.easyportfolio.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.adapter.GalleryVideoAdapter;
import com.thepegeekapps.easyportfolio.database.DatabaseHelper;
import com.thepegeekapps.easyportfolio.model.Video;

public class VideosScreen extends BaseScreen implements OnClickListener {
	
	public static final String TAG = VideosScreen.class.getSimpleName();
	
	public static final int ADD_VIDEO_REQUEST_CODE = 0;
	
	public static final int MODE_CAPTURE_FROM_CAMERA = 0;
	public static final int MODE_SELECT_FROM_LIBRARY = 1;
	
	protected Gallery gallery;
	protected TextView name;
	protected TextView selectAction;
	protected ImageView pencil;
	protected Button saveBtn; 
	
	protected int portfolioId;
	
	protected GalleryVideoAdapter adapter;
	protected List<Video> videos;
	protected Video currentVideo;
	
	protected int editMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videos_screen);
		setScreenTitle(R.string.videos);
		getIntentData();
		initializeViews();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_PORTFOLIO_ID)) {
			portfolioId = intent.getIntExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, 0);
			if (videos == null)
				videos = new LinkedList<Video>();
		}
	}
	
	protected void initializeViews() {
		gallery = (Gallery) findViewById(R.id.gallery);
		adapter = new GalleryVideoAdapter(this, videos);
		gallery.setAdapter(adapter);
		gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    		@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    			currentVideo = videos.get(position);
    			if (currentVideo != null)
    				name.setText(currentVideo.getName());
    		}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
    	gallery.setOnItemClickListener(new OnItemClickListener() {
    		@Override
    		public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
    			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videos.get(position).getUri()));
    			startActivity(intent);
    		}
		});
    	registerForContextMenu(gallery);
		
		name = (TextView) findViewById(R.id.name);
		selectAction = (TextView) findViewById(R.id.selectAction);
		selectAction.setOnClickListener(this);
		pencil = (ImageView) findViewById(R.id.pencil);
		pencil.setOnClickListener(this);
		
		saveBtn = getRightBtn();
		saveBtn.setVisibility(View.VISIBLE);
		saveBtn.setText(R.string.save);
		saveBtn.setOnClickListener(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (currentVideo != null) {
			menu.setHeaderTitle(currentVideo.getName());
			
			String[] menuItems = new String[] {getString(R.string.delete)};
			if (menuItems != null)
				for (int i = 0; i < menuItems.length; i++)
					menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int menuItemIndex = item.getItemId();
		switch (menuItemIndex) {
		case 0:
			showConfirmDeleteVideoDialog();
			break;
		}
		return true;
	}
	
	protected void save() {
		dbManager.addVideos(videos);
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rightBtn:
			save();
			break;
		case R.id.selectAction:
			showSelectActionDialog(ADD_VIDEO_REQUEST_CODE);
			break;
		case R.id.pencil:
			if (currentVideo != null)
				showEditVideoNameDialog();
			break;
		}
	}
	
	protected void showConfirmDeleteVideoDialog() {
		if (currentVideo == null)
			return;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete)
		.setMessage(R.string.confirm_delete_video)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteVideo(currentVideo);
				if (currentVideo != null)
					name.setText(currentVideo.getName());
				else
					name.setText("");
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
	
	protected void showEditVideoNameDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.setPadding(10, 10, 10, 10);
		
		final EditText input = new EditText(this);
		input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		input.setHint(R.string.video_name);
		if (currentVideo != null)
			input.setText(currentVideo.getName());
		
		ll.addView(input);
		
		builder
		    .setTitle(R.string.enter_new_name)
			.setView(ll)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					if (currentVideo != null) {
						String videoName = input.getText().toString();
						videoName = (videoName != null && !videoName.equals("")) ? videoName : getString(R.string.new_video);
						currentVideo.setName(videoName);
						dbManager.updateVideo(currentVideo);
						name.setText(videoName);
					}					
					dialog.dismiss();
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
	
	protected void showSelectActionDialog(final int requestCode) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		PackageManager pm = getPackageManager();
		String[] items = null;
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			items = new String[] {getString(R.string.take_video), getString(R.string.select_from_library)};
		} else {
			items = new String[] {getString(R.string.select_from_library)};
		}
		
		builder.setTitle(R.string.select_option)
		.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					editMode = MODE_CAPTURE_FROM_CAMERA;
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
		            startActivityForResult(Intent.createChooser(cameraIntent, "Take video"), requestCode);
		            break;
				case 1:
					editMode = MODE_SELECT_FROM_LIBRARY;
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType("video/*");
					startActivityForResult(intent, requestCode);
				}
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == ADD_VIDEO_REQUEST_CODE) {
			(new AddVideoTask(data)).execute((Void[]) null);
		}
	}
	
	protected void addVideo(Uri uri, int position) {
	    try {
	    	String path = saveVideo(uri);
	    	Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
	    	currentVideo = new Video(0, portfolioId, getString(R.string.new_video), uri.toString(), path, thumbnail);
	    	if (position != -1)
	    		videos.add(position, currentVideo);
	    	else
	    		videos.add(currentVideo);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	Toast.makeText(this, R.string.save_video_error, Toast.LENGTH_SHORT).show();
	    }
	}
	
	protected void deleteVideo(Video video) {
		if (video == null)
			return;
		try {
			File f = new File(video.getPath());
			f.delete();

			int index = videos.indexOf(currentVideo);
			videos.remove(currentVideo);
			if (videos.isEmpty()) {
				currentVideo = null;
			} else {
				index = (index < videos.size()) ? index : videos.size()-1;
				currentVideo = videos.get(index);
			}
			adapter.notifyDataSetChanged();
			if (currentVideo != null)
				gallery.setSelection(index, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected String saveVideo(Uri uri) throws FileNotFoundException, IOException {
		String videoPath = getRealPathFromURI(uri);
		FileInputStream fis = new FileInputStream(videoPath);
		String filename = videoPath.substring(videoPath.lastIndexOf('/')+1);
		
		File videoDir = new File(Environment.getExternalStorageDirectory(), "/easyportfolio/video/");
		videoDir.mkdirs();
		
		File tmpFile = new File(videoDir, filename);
		FileOutputStream fos = new FileOutputStream(tmpFile);
		byte[] buf = new byte[1024];
		int len;
		while ((len = fis.read(buf)) > 0)
			fos.write(buf, 0, len);
		fis.close();
		fos.close();
		return tmpFile.getAbsolutePath();
	}
	
	public String getRealPathFromURI(Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    Cursor c = getContentResolver().query(contentUri, proj, null, null, null);
	    if (c != null && c.moveToFirst()) {
	    	String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
	    	return path;
	    }
	    return "";
	}
	
	
	class AddVideoTask extends AsyncTask<Void, Void, Void> {
		
		protected Intent data;
		
		public AddVideoTask(Intent data) {
			this.data = data;
		}
		
		@Override
		protected void onPreExecute() {
			showProgressDialog(getString(R.string.importing));
		}

		@Override
		protected Void doInBackground(Void... params) {
			addVideo(data.getData(), -1);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			adapter = new GalleryVideoAdapter(VideosScreen.this, videos);
			gallery.setAdapter(adapter);
			gallery.setSelection(videos.size()-1, true);
//			adapter.notifyDataSetChanged();
//	    	gallery.setSelection(videos.size()-1, true);
	    	hideProgressDialog();
		}
		
	}

}
