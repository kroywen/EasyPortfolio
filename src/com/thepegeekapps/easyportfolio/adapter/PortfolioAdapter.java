package com.thepegeekapps.easyportfolio.adapter;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.model.Portfolio;


public class PortfolioAdapter extends BaseAdapter {
	
	protected LayoutInflater inflater;
	protected List<Portfolio> portfolios;
	
	public PortfolioAdapter(Context context, List<Portfolio> portfolios) {
		inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		setPortfolios(portfolios);
	}

	public int getCount() {
		return portfolios.size();
	}

	public Portfolio getItem(int position) {
		return portfolios.get(position);
	}

	public long getItemId(int position) {
		return portfolios.get(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.portfolio_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.portfolioName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Portfolio portfolio = portfolios.get(position);
		holder.name.setText(portfolio.getName());

		return convertView;
	}
	
	public void setPortfolios(List<Portfolio> portfolios) {
		this.portfolios = (portfolios != null) ? portfolios : new LinkedList<Portfolio>();
	}
	
	class ViewHolder {
		TextView name;
	}

}
