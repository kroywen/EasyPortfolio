package com.thepegeekapps.easyportfolio.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryFullImageAdapter extends BaseAdapter {
	
	protected Context context;
	protected String[] images;
	
	public GalleryFullImageAdapter(Context context, String[] images) {
		this.context = context;
		setImages(images);
	}

	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public String getItem(int position) {
		return images[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			ImageView image = new ImageView(context);
			image.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			convertView = image;
			holder.image = image;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.image.setImageBitmap(BitmapFactory.decodeFile(images[position]));

		return convertView;
	}
	
	public void setImages(String[] images) {
		this.images = (images != null) ? images : new String[] {};
	}
	
	private static class ViewHolder {
		ImageView image;
	}

}
