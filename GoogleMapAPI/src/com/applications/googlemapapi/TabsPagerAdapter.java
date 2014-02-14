package com.applications.googlemapapi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

//This class provides an adapter to control tabs of fragments
public class TabsPagerAdapter extends FragmentPagerAdapter {
	private HistoryFragment hisFrag;
	private LockFragment lockFrag;
	private SuggestionsFragment sugFrag;
	
	public TabsPagerAdapter(FragmentManager fm) {
		//auto generated constructor
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			lockFrag = new LockFragment();
			return lockFrag;
		case 1:
			sugFrag = new SuggestionsFragment();
			return sugFrag;
		case 2:
			hisFrag = new HistoryFragment();
			return hisFrag;
		}
		return null;
	}
	public HistoryFragment getHisFrag() {
		return hisFrag;
	}
	public LockFragment getLockFrag() {
		return lockFrag;
	}
	public SuggestionsFragment getSugFrag() {
		return sugFrag;
	}
	
	@Override
	public int getCount() {
		return 3;
	}

}
