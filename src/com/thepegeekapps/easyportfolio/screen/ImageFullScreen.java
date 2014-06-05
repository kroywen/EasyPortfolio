package com.thepegeekapps.easyportfolio.screen;

import com.thepegeekapps.easyportfolio.adapter.GalleryFullImageAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.Gallery;

public class ImageFullScreen extends BaseScreen {
	
	protected String[] images;
	protected int index;
	
	protected Gallery gallery;
	protected GalleryFullImageAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getIntentData();
		initializeViews();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra("images")) {
			images = intent.getStringArrayExtra("images");
			index = intent.getIntExtra("index", -1);
		}
	}
	
	protected void initializeViews() {
		gallery = new Gallery(this);
		gallery.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		adapter = new GalleryFullImageAdapter(this, images);
		gallery.setAdapter(adapter);
		
		setContentView(gallery);
		
		if (index != -1)
			gallery.setSelection(index, true);
	}

}
