package com.thepegeekapps.easyportfolio.screen;

import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.adapter.PortfolioAdapter;
import com.thepegeekapps.easyportfolio.database.DatabaseHelper;
import com.thepegeekapps.easyportfolio.model.Group;
import com.thepegeekapps.easyportfolio.model.Portfolio;
import com.thepegeekapps.easyportfolio.util.Utils;
import com.thepegeekapps.easyportfolio.view.wheel.WheelView;
import com.thepegeekapps.easyportfolio.view.wheel.adapters.ArrayWheelAdapter;

public class PortfoliosScreen extends BaseScreen implements OnItemClickListener {
	
	protected ListView list;
	
	protected int groupId;
	protected List<Portfolio> portfolios;
	protected List<Group> groups;
	protected PortfolioAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portfolios_screen);
		setScreenTitle(R.string.portfolios);
		groups = dbManager.getGroups();
		getIntentData();
		initializeViews();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_GROUP_ID) && intent.hasExtra(DatabaseHelper.FIELD_NAME)) {
			groupId = intent.getIntExtra(DatabaseHelper.FIELD_GROUP_ID, 0);
			setScreenTitle(intent.getStringExtra(DatabaseHelper.FIELD_NAME));
			portfolios = dbManager.getPortfoliosByGroupId(groupId);
		}
	}
	
	protected void initializeViews() {
		list = (ListView) findViewById(R.id.list);
		adapter = new PortfolioAdapter(this, portfolios);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		registerForContextMenu(list);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Portfolio portfolio = portfolios.get(info.position);
			menu.setHeaderTitle(portfolio.getName());
			
			String[] menuItems = new String[] {getString(R.string.edit), getString(R.string.delete)};;
			if (menuItems != null)
				for (int i = 0; i < menuItems.length; i++)
					menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		Portfolio portfolio = portfolios.get(info.position);
		switch (menuItem.getItemId()) {
		case 0:
			showEditPortfolioDialog(portfolio);
			break;
		case 1:
			showConfirmDeletePortfolioDialog(portfolio);
			break;
		}
		return true;
	}
	
	protected void refreshItems() {
		portfolios = dbManager.getPortfoliosByGroupId(groupId);
		adapter.setPortfolios(portfolios);
		adapter.notifyDataSetChanged();
	}
	
	protected void showEditPortfolioDialog(final Portfolio portfolio) {
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.add_portfolio_dialog, null);
		
		final EditText input = (EditText) dialogView.findViewById(R.id.name);
		if (portfolio != null)
			input.setText(portfolio.getName());
		
		final WheelView groupWheel = (WheelView) dialogView.findViewById(R.id.group);
		String[] items = Utils.getGroupNames(groups);
		if (items == null || items.length == 0)
			items = new String[] {""};
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, items);
		groupWheel.setViewAdapter(adapter);
		
		final CheckBox cb = (CheckBox) dialogView.findViewById(R.id.noGroup);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				groupWheel.setEnabled(!isChecked);
			}
		});
		
		if (portfolio != null && portfolio.getGroupId() != 0) {
			int position = getGroupPosition(portfolio.getGroupId());
			groupWheel.setCurrentItem(position);
			groupWheel.setEnabled(true);
			cb.setChecked(false);
		} else {
			groupWheel.setEnabled(false);
			cb.setChecked(true);
		}
		
		if (groups == null || groups.isEmpty()) {
			groupWheel.setEnabled(false);
			cb.setChecked(true);
			cb.setEnabled(false);
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
		.setTitle(R.string.enter_new_name)
			.setView(dialogView)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					String name = input.getText().toString();
					if (name != null && !name.equals("")) {
						if (portfolio == null) {
							Portfolio p = new Portfolio(name);
							int groupId = cb.isChecked() ? 0 : groups.get(groupWheel.getCurrentItem()).getId();
							p.setGroupId(groupId);
							dbManager.addPortfolio(p);
						} else {
							portfolio.setName(name);
							int groupId = cb.isChecked() ? 0 : groups.get(groupWheel.getCurrentItem()).getId();
							portfolio.setGroupId(groupId);
							dbManager.updatePortfolio(portfolio);
						}
						dialog.dismiss();
						refreshItems();
					} else {
						Toast.makeText(PortfoliosScreen.this, R.string.empty_name, Toast.LENGTH_SHORT).show();
					}
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
	
	protected int getGroupPosition(int groupId) {
		if (groups == null || groups.isEmpty())
			return -1;
		for (int i=0; i<groups.size(); i++)
			if (groups.get(i).getId() == groupId)
				return i;
		return -1;
	}
	
	protected void showConfirmDeletePortfolioDialog(final Portfolio portfolio) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(R.string.confirm_delete_portfolio)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbManager.deletePortfolio(portfolio);
				dialog.dismiss();
				refreshItems();
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Portfolio portfolio = portfolios.get(position);
		Intent intent = new Intent(this, PortfolioScreen.class);
		intent.putExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, portfolio.getId());
		startActivity(intent);
	}

}
