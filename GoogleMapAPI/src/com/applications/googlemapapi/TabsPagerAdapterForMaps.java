package com.applications.googlemapapi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

//This class provides an adapter to control tabs of fragments, specifically, the MapFragments
public class TabsPagerAdapterForMaps extends FragmentPagerAdapter {
	MainActivity mainAct;
	
	
	public TabsPagerAdapterForMaps(FragmentManager fm, MainActivity mainAct) {
		//auto generated constructor
		super(fm);
		this.mainAct = mainAct;	//use this context for getting the google maps
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			return mainAct.getLockMapFragment();
		case 1:
			return mainAct.getSuggestionsFragment();
		case 2:
			return mainAct.getHistoryMapFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		return 3;
	}

}

