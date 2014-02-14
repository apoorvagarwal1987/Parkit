package com.applications.googlemapapi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HistoryFragment  extends Fragment  {
	DBTools dbTools;
	GoogleMap recentParkingMap;
	Fragment context;
	LatLng posCar;
	Resources resources;
	final float COL_TURQUOISE = 180.0f; 
	
	String[] daysOfTheWeek;
	int [] dayImages;
	Spinner weekSpinner;
	
	private HashSet<Marker> recentParking = new HashSet<Marker>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		dbTools = ((MainActivity)this.getActivity()).getDBTools();
		context = this;
		resources = getResources();
		daysOfTheWeek = new String[]{  
										resources.getString(R.string.recentDefault),
										resources.getString(R.string.sunday),
										resources.getString(R.string.monday),
										resources.getString(R.string.tuesday),
										resources.getString(R.string.wednesday),
										resources.getString(R.string.thursday),
										resources.getString(R.string.friday),
										resources.getString(R.string.saturday)				
								   };
		
		dayImages = new int[]{
								R.drawable.null_icon,
								R.drawable.red_icon,
								R.drawable.orange_icon, 
								R.drawable.yellow_icon, 
								R.drawable.green_icon, 
								R.drawable.blue_green_icon, 
								R.drawable.blue_icon,
								R.drawable.purple_icon
							};
	}	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.history_fragment,  container, false);
		weekSpinner = (Spinner)view.findViewById(R.id.week_spinner);
		DaysAdapter dAdapt = new DaysAdapter(this.getActivity(), R.layout.spinner_row, daysOfTheWeek);
		weekSpinner.setAdapter(dAdapt);
		weekSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if (position == 0) {
					recentParkings();
				} else {
					ArrayList<ParkInformation> parkInfo = dbTools.getParkingInfo(position);
					updateMapWithParkHistory(parkInfo);
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				//do nothing, but maybe we want to default to displaying the last 10 places?
			}
		});		
		return view;
	}
	
	@Override
	public void onStart () {
		super.onStart();
	}
	
	public void recentParkings (){
		ArrayList<ParkInformation> parkingHistory = dbTools.getLatestParkingLocation(10);
		posCar = ((MainActivity)this.getActivity()).getPosCar();
		updateMapWithParkHistory(parkingHistory);		
	}

	private void updateMapWithParkHistory(ArrayList<ParkInformation> parkingHistory) {
		recentParkingMap = ((MainActivity)context.getActivity()).getGoogleMapHistory();
		recentParkingMap.clear();
		recentParking.clear();
		posCar = ((MainActivity)this.getActivity()).getPosCar();
		for(ParkInformation parkingAddress:parkingHistory){
			Date parkingDate = new Date(parkingAddress.getParkingtime());
			String parkingTitle=parkingDate.toString().toLowerCase();
			LatLng parkingPosition = new LatLng(parkingAddress.getLatitude(), parkingAddress.getLongitude());
			Marker parkedLocation = recentParkingMap.addMarker(new MarkerOptions().position(parkingPosition).title(parkingTitle));
			setDayIcon(parkedLocation, parkingAddress.getParkingtime());
			recentParking.add(parkedLocation);
		}
		recentParkingMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posCar, 15));
		
		recentParkingMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker parkingPosition) {
				new ParkingNavigation(context).execute(Double.toString(parkingPosition.getPosition().latitude),
													Double.toString(parkingPosition.getPosition().longitude),
													resources.getString(R.string.carRecentPosition)+"  "+parkingPosition.getTitle(),
													Double.toString(posCar.latitude),
													Double.toString(posCar.longitude));
				return true;
			}					
		});
		
	}
	private void setDayIcon(Marker parkedLocation, Long parkingtime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(parkingtime);
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case 1:	//Sunday
			parkedLocation.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			break;
		case 2: //Monday
			parkedLocation.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			break;
		case 3: //Tuesday
			parkedLocation.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			break;
		case 4: //Wednesday
			parkedLocation.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			break;
		case 5: //Thursday
			parkedLocation.setIcon(BitmapDescriptorFactory.defaultMarker(COL_TURQUOISE));
			break;
		case 6:	//Friday
			parkedLocation.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
			break;
		case 7:	//Saturday
			parkedLocation.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
			break;
		}
	}
	public class DaysAdapter extends ArrayAdapter<String> {

		public DaysAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}
		
		public View getCustomView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View row = inflater.inflate(R.layout.spinner_row, parent, false);
			TextView label=(TextView)row.findViewById(R.id.rowText);
            label.setText(daysOfTheWeek[position]);

            ImageView icon=(ImageView)row.findViewById(R.id.rowImage);
            icon.setImageResource(dayImages[position]);
            return row;
		}		
	}
}
