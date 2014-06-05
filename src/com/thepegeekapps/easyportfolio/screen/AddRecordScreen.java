package com.thepegeekapps.easyportfolio.screen;

import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.database.DatabaseHelper;
import com.thepegeekapps.easyportfolio.model.Group;
import com.thepegeekapps.easyportfolio.model.Portfolio;
import com.thepegeekapps.easyportfolio.util.Utils;
import com.thepegeekapps.easyportfolio.view.wheel.OnWheelChangedListener;
import com.thepegeekapps.easyportfolio.view.wheel.WheelView;
import com.thepegeekapps.easyportfolio.view.wheel.adapters.ArrayWheelAdapter;

public class AddRecordScreen extends BaseScreen implements OnClickListener {
	
	protected ImageView addVideoBtn;
	protected ImageView addImageBtn;
	protected ImageView addAudioBtn;
	protected ImageView addNoteBtn;
	protected ImageView addUrlBtn;
	protected ImageView addDocBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_record_screen);
		setScreenTitle(R.string.add_record);
		initializeViews();
	}
	
	protected void initializeViews() {
		addVideoBtn = (ImageView) findViewById(R.id.addVideoBtn);
		addVideoBtn.setOnClickListener(this);
		addImageBtn = (ImageView) findViewById(R.id.addImageBtn);
		addImageBtn.setOnClickListener(this);
		addAudioBtn = (ImageView) findViewById(R.id.addAudioBtn);
		addAudioBtn.setOnClickListener(this);
		addNoteBtn = (ImageView) findViewById(R.id.addNoteBtn);
		addNoteBtn.setOnClickListener(this);
		addUrlBtn = (ImageView) findViewById(R.id.addUrlBtn);
		addUrlBtn.setOnClickListener(this);
		addDocBtn = (ImageView) findViewById(R.id.addDocBtn);
		addDocBtn.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addVideoBtn:
			showSelectPortfolioDialog(VideosScreen.class);
			break;
		case R.id.addImageBtn:
			showSelectPortfolioDialog(ImagesScreen.class);
			break;
		case R.id.addAudioBtn:
			showSelectPortfolioDialog(AudiosScreen.class);
			break;
		case R.id.addNoteBtn:
			showSelectPortfolioDialog(NotesScreen.class);
			break;
		case R.id.addUrlBtn:
			showSelectPortfolioDialog(UrlsScreen.class);
			break;
		case R.id.addDocBtn:
			showSelectPortfolioDialog(DocumentsScreen.class);
			break;
		}
	}
	
	protected void showSelectPortfolioDialog(final Class<?> clazz) {
		final List<Group> groups = dbManager.getGroups();
		final List<Portfolio> noGroupPortfolios = dbManager.getPortfoliosByGroupId(0);
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.add_record_dialog, null);
		
		final WheelView groupWheel = (WheelView) dialogView.findViewById(R.id.selectGroup);
		String[] groupItems = Utils.getGroupNames(groups);
		ArrayWheelAdapter<String> groupAdapter = new ArrayWheelAdapter<String>(this, groupItems);
		groupWheel.setViewAdapter(groupAdapter);
		
		final WheelView portfoliosWheel = (WheelView) dialogView.findViewById(R.id.selectPortfolio);
		String[] portfolioItems = (groups != null && !groups.isEmpty()) ? Utils.getPortfolioNames(groups.get(0).getPortfolios()) : new String[] {""}; 
		ArrayWheelAdapter<String> portfolioAdapter = new ArrayWheelAdapter<String>(this, portfolioItems);
		portfoliosWheel.setViewAdapter(portfolioAdapter);		
		
		final CheckBox cb = (CheckBox) dialogView.findViewById(R.id.noGroup);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				groupWheel.setEnabled(!isChecked);
				if (isChecked) {
					ArrayWheelAdapter<String> groupAdapter = new ArrayWheelAdapter<String>(AddRecordScreen.this, new String[] {""});
					groupWheel.setViewAdapter(groupAdapter);
					
					String[] portfolioItems = Utils.getPortfolioNames(noGroupPortfolios);
					ArrayWheelAdapter<String> portfolioAdapter = new ArrayWheelAdapter<String>(AddRecordScreen.this, portfolioItems);
					portfoliosWheel.setViewAdapter(portfolioAdapter);
				} else {
					String[] groupItems = Utils.getGroupNames(groups);
					ArrayWheelAdapter<String> groupAdapter = new ArrayWheelAdapter<String>(AddRecordScreen.this, groupItems);
					groupWheel.setViewAdapter(groupAdapter);
					
					String[] portfolioItems = (groups != null && !groups.isEmpty() &&
							groups.get(groupWheel.getCurrentItem()) != null &&
							groups.get(groupWheel.getCurrentItem()).getPortfolios() != null && !groups.get(groupWheel.getCurrentItem()).getPortfolios().isEmpty())
							? Utils.getPortfolioNames(groups.get(groupWheel.getCurrentItem()).getPortfolios()) : new String[] {""};
					ArrayWheelAdapter<String> portfolioAdapter = new ArrayWheelAdapter<String>(AddRecordScreen.this, portfolioItems);
					portfoliosWheel.setViewAdapter(portfolioAdapter);
				}
				portfoliosWheel.setCurrentItem(0, true);
			}
		});
		
		groupWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String[] portfolioItems = Utils.getPortfolioNames(groups.get(wheel.getCurrentItem()).getPortfolios());
				ArrayWheelAdapter<String> portfolioAdapter = new ArrayWheelAdapter<String>(AddRecordScreen.this, portfolioItems);
				portfoliosWheel.setViewAdapter(portfolioAdapter);
				portfoliosWheel.setCurrentItem(0, true);
			}
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_portfolio)
		.setView(dialogView)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				Intent intent = new Intent(AddRecordScreen.this, clazz);
				if (cb.isChecked()) {
					if (noGroupPortfolios != null && !noGroupPortfolios.isEmpty()) {
						Portfolio portfolio = noGroupPortfolios.get(portfoliosWheel.getCurrentItem());
						intent.putExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, portfolio.getId());
						intent.putExtra("mode", "add");
						startActivity(intent);
					} else {
						Toast.makeText(AddRecordScreen.this, R.string.portfolio_not_selected, Toast.LENGTH_SHORT).show();
					}
				} else {
					if (groups != null) {
						List<Portfolio> portfolios = groups.get(groupWheel.getCurrentItem()).getPortfolios();
						if (portfolios != null && !portfolios.isEmpty()) {
							Portfolio portfolio = portfolios.get(portfoliosWheel.getCurrentItem());
							intent.putExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, portfolio.getId());
							startActivity(intent);
						} else {
							Toast.makeText(AddRecordScreen.this, R.string.portfolio_not_selected, Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(AddRecordScreen.this, R.string.portfolio_not_selected, Toast.LENGTH_SHORT).show();
					}
				}
				dialog.dismiss();
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

}
