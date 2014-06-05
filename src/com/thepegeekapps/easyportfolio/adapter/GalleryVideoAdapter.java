package com.thepegeekapps.easyportfolio.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.thepegeekapps.easyportfolio.model.Video;

public class GalleryVideoAdapter extends BaseAdapter {
	
	private Context context;
	private static ImageView imageView;
	private List<Video> videos;
	private static ViewHolder holder;

	public GalleryVideoAdapter(Context context, List<Video> videos) {
		this.context = context;
		this.videos = videos;
	}

	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Video getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return videos.get(position).getId();
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

		holder.imageView.setImageBitmap(videos.get(position).getThumbnail());
		holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		holder.imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return convertView;
	}

	private static class ViewHolder {
		ImageView imageView;
	}

}
