package com.thepegeekapps.easyportfolio.adapter;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.model.Group;
import com.thepegeekapps.easyportfolio.model.Item;
import com.thepegeekapps.easyportfolio.model.Portfolio;

public class ItemAdapter extends BaseAdapter {
	
	protected Context context; 
	protected List<Item> items;
	protected List<Portfolio> portfolios;
	protected List<Item> filteredItems;
	
	public ItemAdapter(Context context, List<Item> items, List<Portfolio> portfolios) {
		this.context = context;
		setItems(items);
		setPortfolios(portfolios);
	}

	public int getCount() {
		return filteredItems.size();
	}

	public Item getItem(int position) {
		return filteredItems.get(position);
	}

	public long getItemId(int position) {
		return filteredItems.get(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.group_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Item item = filteredItems.get(position);
		holder.name.setText(item.getName());
		if (item.getType() == Item.TYPE_GROUP) {
			Group group = (Group) item;
			List<Portfolio> portfolios = group.getPortfolios();
			int count = (portfolios != null && !portfolios.isEmpty()) ? portfolios.size() : 0;
			String countStr = String.format(context.getResources().getString(R.string.portfolios_count_pattern), count);
			holder.count.setText(countStr);
			holder.count.setVisibility(View.VISIBLE);
			
			holder.name.setTextColor(Color.BLUE);
		} else {
			holder.count.setVisibility(View.GONE);
			holder.name.setTextColor(Color.WHITE);
		}

		return convertView;
	}
	
	public void setItems(List<Item> items) {
		this.items = (items != null) ? items : new LinkedList<Item>();
		filteredItems = new LinkedList<Item>();
		if (items != null && !items.isEmpty())
			filteredItems.addAll(items);
	}
	
	public void setPortfolios(List<Portfolio> portfolios) {
		this.portfolios = (portfolios != null) ? portfolios : new LinkedList<Portfolio>();
	}
	
	public void filterPortfolios(String s) {
		if (s != null && !s.equals("")) {
			filteredItems.clear();
			for (int i=0; i<portfolios.size(); i++) {
				if (portfolios.get(i).getName().contains(s))
					filteredItems.add(portfolios.get(i));
			}
		} else {
			filteredItems.clear();
			filteredItems.addAll(items);
		}
		notifyDataSetChanged();
	}
	
	class ViewHolder {
		TextView name;
		TextView count;
	}

}
