package com.applications.googlemapapi;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	// Tabs
	private NonSwipableViewPager viewPager;
	private NonSwipableViewPager mapViewPager;
	private TabsPagerAdapter mAdapter;
	private TabsPagerAdapterForMaps mMapAdapter;
	private ActionBar actionBar;
	
	private String[] tabs;
	// Google Map
	private SupportMapFragment lockMapFragment;
	private SupportMapFragment suggestionsMapFragment;
	private SupportMapFragment historyMapFragment;
    private GoogleMap parkingMap;
    private GoogleMap suggestionMap;
    private GoogleMap recentParkingMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Double posLongitude;
    private Double posLatitude;
    private LatLng posCar;
    private DBTools dbTools = new DBTools(this);
    Location currentCarPosition;
    Resources resources;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		resources = getResources();
		tabs = new String[]{
					resources.getString(R.string.parkingTab),
					resources.getString(R.string.suggestionTab),
					resources.getString(R.string.recentTab)
				};
		
		try {
            // Loading map
            initilizeMaps();
 
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
		
		//initialize tabs
		viewPager = (NonSwipableViewPager) findViewById(R.id.pager);
		mapViewPager = (NonSwipableViewPager) findViewById(R.id.map_pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		mMapAdapter = new TabsPagerAdapterForMaps(getSupportFragmentManager(), this);
		
		((ViewPager) viewPager).setAdapter(mAdapter);
		((ViewPager) mapViewPager).setAdapter(mMapAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		//Adding tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
		}
	}
    
	protected void onStart(){
		super.onStart();
		String provider;
		int mintimeUpdate = 5000 ;
		int minDistanceUpdate = 5;
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);	
		Criteria criteria;	
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(false);		
		provider = locationManager.getBestProvider(criteria, true);
		currentCarPosition = locationManager.getLastKnownLocation(provider);
		locationListener = new LocationListener(){
			@Override
			public void onLocationChanged(Location location) {
				if(location!=null){
					posLongitude = location.getLongitude();
					posLatitude = location.getLatitude();	
					posCar = new LatLng(posLatitude, posLongitude);
				}
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,Bundle extras) {
				// TODO Auto-generated method stub
				
			}			
		};
		locationManager.requestLocationUpdates(provider,mintimeUpdate,minDistanceUpdate, locationListener);
		updateLocation(currentCarPosition);
	}
	protected void onStop(){
		super.onStop();
		locationManager.removeUpdates(locationListener);
	}
	
	public void updateLocation(Location location) {
		if(location != null) {
			posLongitude = location.getLongitude();
			posLatitude = location.getLatitude();
			posCar = new LatLng(posLatitude, posLongitude);
		}
	}
	
	/**
     * function to load the three maps. If map is not created it will create it for you
     * */
    private void initilizeMaps() {
        if (parkingMap == null) {
        	//since in SupportMapFragment, we aren't able to get the GoogleMap until we call onActivityCreated.
        	lockMapFragment = new SupportMapFragment() {
                    @Override
                    public void onActivityCreated(Bundle savedInstanceState) {
                        super.onActivityCreated(savedInstanceState);
                        parkingMap = lockMapFragment.getMap();
                    }
        		}; 
        }
        
        if (suggestionMap == null) {
        	//since in SupportMapFragment, we aren't able to get the GoogleMap until we call onActivityCreated.
        	suggestionsMapFragment = new SupportMapFragment() {
                    @Override
                    public void onActivityCreated(Bundle savedInstanceState) {
                        super.onActivityCreated(savedInstanceState);
                        suggestionMap = suggestionsMapFragment.getMap();
                    }
        		};
        }
        
        if (recentParkingMap == null) {
        	//since in SupportMapFragment, we aren't able to get the GoogleMap until we call onActivityCreated.
            historyMapFragment = new SupportMapFragment() {
	            @Override
	            public void onActivityCreated(Bundle savedInstanceState) {
	                super.onActivityCreated(savedInstanceState);
	                recentParkingMap = historyMapFragment.getMap();                
	            }
            };
        }
    }
	
    @Override
	public void onResume() {
        super.onResume();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public GoogleMap getGoogleMapLock () {
		return parkingMap;
	}
	
	public GoogleMap getGoogleMapHistory () {
		return recentParkingMap;
	}
	
	public GoogleMap getGoogleMapSuggestions () {
		return suggestionMap;
	}
	public SupportMapFragment getLockMapFragment () {
		return lockMapFragment;
	}
	public SupportMapFragment getHistoryMapFragment () {
		return historyMapFragment;
	}
	public SupportMapFragment getSuggestionsFragment () {
		return suggestionsMapFragment;
	}
	
	public LatLng getPosCar() {
		return posCar;
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction fTransaction) {
		((ViewPager) viewPager).setCurrentItem(tab.getPosition());
		((ViewPager) mapViewPager).setCurrentItem(tab.getPosition());
		if (tab.getPosition() == 1) {
			((TabsPagerAdapter)viewPager.getAdapter()).getSugFrag().loadMap("");
		}
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public DBTools getDBTools () {
		return dbTools;
	}
}
