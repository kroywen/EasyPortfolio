package com.thepegeekapps.easyportfolio.screen;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.thepegeekapps.easyportfolio.R;

public class MainScreen extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_screen);
//        setTitle(R.string.portfolios);
        initTabs();
    }
    
    protected void initTabs() {
    	Resources res = getResources();
    	final TabHost tabHost = getTabHost();
    	
    	tabHost.getTabWidget().setDividerDrawable(res.getDrawable(R.drawable.tab_divider));
    	tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String arg0) {
				for(int i=0; i<tabHost.getTabWidget().getChildCount(); i++)
		    		tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_background);
				tabHost.getCurrentTabView().setBackgroundColor(Color.parseColor("#444444"));
			}
		});
    	
    	Intent portfoliosIntent = new Intent(this, GroupsScreen.class);
    	TabSpec tabSpecPortfolios = tabHost
    			.newTabSpec(getString(R.string.portfolios))
    			.setIndicator(getString(R.string.portfolios), res.getDrawable(R.drawable.ic_tab_portfolios))
    			.setContent(portfoliosIntent);
    	
    	Intent addRecordIntent = new Intent(this, AddRecordScreen.class);
    	TabSpec tabSpecAddRecord = tabHost
    			.newTabSpec(getString(R.string.add_record))
    			.setIndicator(getString(R.string.add_record), res.getDrawable(R.drawable.ic_tab_record))
    			.setContent(addRecordIntent);
    	
    	Intent infoIntent = new Intent(this, InfoScreen.class);
    	TabSpec tabSpecInfo = tabHost
    			.newTabSpec(getString(R.string.info))
    			.setIndicator(getString(R.string.info), res.getDrawable(R.drawable.ic_tab_info))
    			.setContent(infoIntent);
    	
    	tabHost.addTab(tabSpecPortfolios);
    	tabHost.addTab(tabSpecAddRecord);
    	tabHost.addTab(tabSpecInfo);
    	
    	for(int i=0; i<tabHost.getTabWidget().getChildCount(); i++)
    		tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_background);
    	
    	tabHost.setCurrentTab(0);
    	
    	tabHost.getCurrentTabView().setBackgroundColor(Color.parseColor("#444444"));
    }
}
