package com.thepegeekapps.easyportfolio.screen;

import java.io.File;
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
import android.graphics.BitmapFactory;
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
import com.thepegeekapps.easyportfolio.adapter.GalleryImageAdapter;
import com.thepegeekapps.easyportfolio.database.DatabaseHelper;
import com.thepegeekapps.easyportfolio.model.Image;
import com.thepegeekapps.easyportfolio.util.Utils;

public class ImagesScreen extends BaseScreen implements OnClickListener {
	
	public static final String TAG = ImagesScreen.class.getSimpleName();
	
	public static final int ADD_IMAGE_REQUEST_CODE = 0;
	
	public static final int MODE_CAPTURE_FROM_CAMERA = 0;
	public static final int MODE_SELECT_FROM_LIBRARY = 1;
	
	protected Gallery gallery;
	protected TextView name;
	protected TextView selectAction;
	protected ImageView pencil;
	protected Button saveBtn; 
	
	protected int portfolioId;
	
	protected GalleryImageAdapter adapter;
	protected List<Image> images;
	protected Image currentImage;
	
	protected int editMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.images_screen);
		setScreenTitle(R.string.images);
		getIntentData();
		initializeViews();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_PORTFOLIO_ID)) {
			portfolioId = intent.getIntExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, 0);
			if (images == null)
				images = new LinkedList<Image>();
			
		}
	}
	
	protected void initializeViews() {
		gallery = (Gallery) findViewById(R.id.gallery);
		adapter = new GalleryImageAdapter(this, images);
		gallery.setAdapter(adapter);
		gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    		@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    			currentImage = images.get(position);
    			if (currentImage != null)
    				name.setText(currentImage.getName());
    		}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
    	gallery.setOnItemClickListener(new OnItemClickListener() {
    		@Override
    		public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
    			Intent intent = new Intent(ImagesScreen.this, ImageFullScreen.class);
    			intent.putExtra("images", Utils.getImagesPathAsStringArray(images));
    			intent.putExtra("index", images.indexOf(currentImage));
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
		if (currentImage != null) {
			menu.setHeaderTitle(currentImage.getName());
			
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
			showConfirmDeleteImageDialog();
			break;
		}
		return true;
	}
	
	protected void save() {
		dbManager.addImages(images);
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
			showSelectActionDialog(ADD_IMAGE_REQUEST_CODE);
			break;
		case R.id.pencil:
			if (currentImage != null)
				showEditImageNameDialog();
			break;
		}
	}
	
	protected void showConfirmDeleteImageDialog() {
		if (currentImage == null)
			return;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete)
		.setMessage(R.string.confirm_delete_image)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteImage(currentImage);
				if (currentImage != null)
					name.setText(currentImage.getName());
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
	
	protected void showEditImageNameDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.setPadding(10, 10, 10, 10);
		
		final EditText input = new EditText(this);
		input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		input.setHint(R.string.image_name);
		if (currentImage != null)
			input.setText(currentImage.getName());
		
		ll.addView(input);
		
		builder
		    .setTitle(R.string.enter_new_name)
			.setView(ll)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					if (currentImage != null) {
						String imageName = input.getText().toString();
						imageName = (imageName != null && !imageName.equals("")) ? imageName : getString(R.string.new_image);
						currentImage.setName(imageName);
						dbManager.updateImage(currentImage);
						name.setText(imageName);
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
			items = new String[] {getString(R.string.take_image), getString(R.string.select_from_library)};
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
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		            startActivityForResult(Intent.createChooser(cameraIntent, "Take image"), requestCode);
		            break;
				case 1:
					editMode = MODE_SELECT_FROM_LIBRARY;
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType("image/*");
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
		if (resultCode == RESULT_OK && requestCode == ADD_IMAGE_REQUEST_CODE) {
			(new AddImageTask(data)).execute((Void[]) null);
		}
	}
	
	protected void addImage(Bitmap bitmap, int position) {
	    try {
	    	String path = saveImage(bitmap);
	    	currentImage = new Image(0, portfolioId, getString(R.string.new_image), path);
	    	if (position != -1) {
	    		images.add(position, currentImage);
	    	} else {
	    		images.add(currentImage);
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	Toast.makeText(this, R.string.save_image_error, Toast.LENGTH_SHORT).show();
	    }
	}
	
	public void editImage(Bitmap bitmap) {
		int index = images.indexOf(currentImage);
		deleteImage(currentImage);
		index = (index < images.size()) ? index : images.size()-1;
		addImage(bitmap, index);
	}
	
	protected void deleteImage(Image image) {
		if (image == null)
			return;
		try {
			File f = new File(image.getPath());
			f.delete();

			int index = images.indexOf(currentImage);
			images.remove(currentImage);
			if (images.isEmpty()) {
				currentImage = null;
			} else {
				index = (index < images.size()) ? index : images.size()-1;
				currentImage = images.get(index);
			}
			adapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected String saveImage(Bitmap image) throws FileNotFoundException, IOException {
		File imageDir = new File(Environment.getExternalStorageDirectory(), "/easyportfolio/image/");
		imageDir.mkdirs();
		
		File tmpFile = new File(imageDir, image.toString()+".png");
		FileOutputStream fos = new FileOutputStream(tmpFile);
		image.compress(Bitmap.CompressFormat.PNG, 90, fos);
		fos.close();
		return tmpFile.getAbsolutePath();
	}
	
	public String getRealPathFromURI(Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	class AddImageTask extends AsyncTask<Void, Void, Boolean> {
		
		protected Intent data;
		
		public AddImageTask(Intent data) {
			this.data = data;
		}
		
		@Override
		protected void onPreExecute() {
			showProgressDialog(getString(R.string.importing));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Bitmap bitmap = null;
			try {
				if (data != null && data.hasExtra("data")) {
					bitmap = (Bitmap) data.getExtras().get("data");
				} else {
					// TODO
					bitmap = BitmapFactory.decodeFile(getRealPathFromURI(data.getData()));
				}				
				if (bitmap != null) {
					addImage(bitmap, -1);
					bitmap.recycle();
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			hideProgressDialog();
			if (result) {
				adapter.notifyDataSetChanged();
		    	gallery.setSelection(images.size()-1);
			} else {
				Toast.makeText(ImagesScreen.this, R.string.add_image_error, Toast.LENGTH_SHORT).show();
			}
		}
		
	}

}
