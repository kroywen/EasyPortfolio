package com.thepegeekapps.easyportfolio.screen;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.adapter.ItemAdapter;
import com.thepegeekapps.easyportfolio.database.DatabaseHelper;
import com.thepegeekapps.easyportfolio.model.Group;
import com.thepegeekapps.easyportfolio.model.Item;
import com.thepegeekapps.easyportfolio.model.Portfolio;
import com.thepegeekapps.easyportfolio.util.Utils;
import com.thepegeekapps.easyportfolio.view.wheel.WheelView;
import com.thepegeekapps.easyportfolio.view.wheel.adapters.ArrayWheelAdapter;

public class GroupsScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	protected EditText search;
	protected ListView list;
	protected Button addGroup;
	protected Button addPortfolio;
	
	protected ItemAdapter adapter;
	
	protected List<Item> items;
	protected List<Group> groups;
	protected List<Portfolio> portfolios;
	protected List<Portfolio> allPortfolios;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups_screen);
		setScreenTitle(R.string.portfolios);
		
		items = new LinkedList<Item>();
		groups = dbManager.getGroups();
		portfolios = dbManager.getPortfoliosByGroupId(0);
		if (groups != null && !groups.isEmpty())
			items.addAll(groups);
		if (portfolios != null && !portfolios.isEmpty())
			items.addAll(portfolios);
		allPortfolios = dbManager.getPortfolios();
		
		adapter = new ItemAdapter(this, items, allPortfolios);
		
		initializeViews();
	}
	
	protected void initializeViews() {
		search = (EditText) findViewById(R.id.search);
		search.addTextChangedListener(filter);
		
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		registerForContextMenu(list);
		
		addGroup = (Button) findViewById(R.id.addGroup);
		addGroup.setOnClickListener(this);
		
		addPortfolio = (Button) findViewById(R.id.addPortfolio);
		addPortfolio.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.portfolios_screen_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.addGroup:
            showEditGroupDialog(null);
            return true;
        case R.id.addPortfolio:
        	showEditPortfolioDialog(null);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.addGroup) {
			showEditGroupDialog(null);
		} else if (v.getId() == R.id.addPortfolio) {
			showEditPortfolioDialog(null);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Item item = adapter.getItem(info.position);
			menu.setHeaderTitle(item.getName());
			
			String[] menuItems = new String[] {getString(R.string.edit), getString(R.string.delete)};;
			if (menuItems != null)
				for (int i = 0; i < menuItems.length; i++)
					menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		Item item = adapter.getItem(info.position);
		if (item.getType() == Item.TYPE_GROUP)
			processGroupContextMenu(menuItem.getItemId(), info.position);
		else
			processPortfolioContextMenu(menuItem.getItemId(), info.position);
		return true;
	}
	
	protected void processGroupContextMenu(int menuItemIndex, int position) {
		Group group = (Group) adapter.getItem(position);
		switch (menuItemIndex) {
		case 0:
			showEditGroupDialog(group);
			break;
		case 1:
			showConfirmDeleteGroupDialog(group);
			break;
		}
	}
	
	protected void processPortfolioContextMenu(int menuItemIndex, int position) {
		Portfolio portfolio = (Portfolio) adapter.getItem(position);
		switch (menuItemIndex) {
		case 0:
			showEditPortfolioDialog(portfolio);
			break;
		case 1:
			showConfirmDeletePortfolioDialog(portfolio);
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		refreshItems();
	}
	
	protected void refreshItems() {
		items.clear();
		groups = dbManager.getGroups();
		portfolios = dbManager.getPortfoliosByGroupId(0);
		if (groups != null && !groups.isEmpty())
			items.addAll(groups);
		if (portfolios != null && !portfolios.isEmpty())
			items.addAll(portfolios);
		allPortfolios = dbManager.getPortfolios();
		
		adapter.setItems(items);
		adapter.setPortfolios(allPortfolios);
		adapter.notifyDataSetChanged();
	}
	
	protected void showEditGroupDialog(final Group group) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.setPadding(10, 10, 10, 10);
		
		final EditText input = new EditText(this);
		input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setHint(R.string.group_name);
		if (group != null)
			input.setText(group.getName());
		
		ll.addView(input);
		
		builder
		.setTitle(R.string.enter_new_name)
			.setView(ll)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					String name = input.getText().toString();
					if (name != null && !name.equals("")) {
						if (group == null) {
							Group group = new Group(0, name, null);
							dbManager.addGroup(group);
						} else {
							group.setName(name);
							dbManager.updateGroup(group);
						}
						dialog.dismiss();
						refreshItems();
					} else {
						Toast.makeText(GroupsScreen.this, R.string.empty_name, Toast.LENGTH_SHORT).show();
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
	
	protected void showEditPortfolioDialog(final Portfolio portfolio) {
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.add_portfolio_dialog, null);
		
		final EditText input = (EditText) dialogView.findViewById(R.id.name);
		if (portfolio != null)
			input.setText(portfolio.getName());
		
		final WheelView groupWheel = (WheelView) dialogView.findViewById(R.id.group);
		String[] items = Utils.getGroupNames(groups);
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, items);
		groupWheel.setViewAdapter(adapter);
		
		final CheckBox cb = (CheckBox) dialogView.findViewById(R.id.noGroup);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				groupWheel.setEnabled(!isChecked);
				if (isChecked) {
					ArrayWheelAdapter<String> groupAdapter = new ArrayWheelAdapter<String>(GroupsScreen.this, new String[] {""});
					groupWheel.setViewAdapter(groupAdapter);
				} else {
					String[] items = Utils.getGroupNames(groups);
					ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(GroupsScreen.this, items);
					groupWheel.setViewAdapter(adapter);
				}
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
						Toast.makeText(GroupsScreen.this, R.string.empty_name, Toast.LENGTH_SHORT).show();
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
	
	protected void showConfirmDeleteGroupDialog(final Group group) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(R.string.confirm_delete_group)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbManager.deleteGroup(group);
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
	
	private TextWatcher filter = new TextWatcher() {
		public void afterTextChanged(Editable s) {}
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	    	adapter.filterPortfolios(s.toString());
	    }
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Item item = adapter.getItem(position);
		if (item.getType() == Item.TYPE_GROUP) {
			List<Portfolio> portfolios = ((Group) item).getPortfolios();
			if (portfolios != null && !portfolios.isEmpty()) {
				Intent intent = new Intent(this, PortfoliosScreen.class);
				intent.putExtra(DatabaseHelper.FIELD_GROUP_ID, item.getId());
				intent.putExtra(DatabaseHelper.FIELD_NAME, item.getName());
				startActivity(intent);
			}
		} else {
			Intent intent = new Intent(this, PortfolioScreen.class);
			intent.putExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, item.getId());
			startActivity(intent);
		}
	}

}
