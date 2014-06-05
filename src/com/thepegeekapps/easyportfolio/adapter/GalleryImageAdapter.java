package com.thepegeekapps.easyportfolio.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.thepegeekapps.easyportfolio.model.Image;

public class GalleryImageAdapter extends BaseAdapter {
	
	private Context context;
	private static ImageView imageView;
	private List<Image> images;
	private static ViewHolder holder;

	public GalleryImageAdapter(Context context, List<Image> images) {
		this.context = context;
		setImages(images);
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Image getItem(int position) {
		return images.get(position);
	}

	@Override
	public long getItemId(int position) {
		return images.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			imageView = new ImageView(this.context);
			imageView.setPadding(3, 3, 3, 3);
			convertView = imageView;
			holder.imageView = imageView;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.imageView.setImageBitmap(BitmapFactory.decodeFile(images.get(position).getPath()));
		holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		holder.imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return convertView;
	}
	
	public void setImages(List<Image> images) {
		this.images = (images != null) ? images : new LinkedList<Image>(); 
	}

	private static class ViewHolder {
		ImageView imageView;
	}

}
