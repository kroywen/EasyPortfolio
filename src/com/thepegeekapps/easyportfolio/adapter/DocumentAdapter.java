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
import com.thepegeekapps.easyportfolio.model.Doc;

public class DocumentAdapter extends BaseAdapter {
	
	protected Context context;
	protected List<Doc> docs;
	
	public DocumentAdapter(Context context, List<Doc> docs) {
		this.context = context;
		setDocuments(docs);
	}

	@Override
	public int getCount() {
		return docs.size();
	}

	@Override
	public Doc getItem(int position) {
		return docs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return docs.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.doc_list_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Doc doc = docs.get(position);
		holder.name.setText(doc.getName());

		return convertView;
	}
	
	public void setDocuments(List<Doc> docs) {
		this.docs = (docs != null) ? docs : new LinkedList<Doc>(); 
	}
	
	class ViewHolder {
		ImageView icon;
		TextView name;
	}

}
