package com.thepegeekapps.easyportfolio.adapter;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.model.Url;

public class UrlAdapter extends BaseAdapter {
	
	protected Context context;
	protected List<Url> urls;
	
	public UrlAdapter(Context context, List<Url> urls) {
		this.context = context;
		setUrls(urls);
	}

	@Override
	public int getCount() {
		return urls.size();
	}

	@Override
	public Url getItem(int position) {
		return urls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return urls.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.url_list_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Url url = urls.get(position);
		holder.name.setText(url.getName());

		return convertView;
	}
	
	public void setUrls(List<Url> urls) {
		this.urls = (urls != null) ? urls : new LinkedList<Url>(); 
	}
	
	class ViewHolder {
		ImageView icon;
		TextView name;
	}

}
