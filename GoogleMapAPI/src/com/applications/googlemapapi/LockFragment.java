package com.applications.googlemapapi;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class LockFragment extends Fragment implements OnClickListener {
	GoogleMap parkingMap;
    private LatLng posCar;
    private DBTools dbTools;
    Fragment context;
    Resources resources;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		dbTools = ((MainActivity)this.getActivity()).getDBTools();
		context = this;
		resources = getResources();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.lock_fragment,  container, false);
		Button lockButton = (Button) view.findViewById(R.id.btnLockPos);
		Button navigationButton = (Button) view.findViewById(R.id.btnNav);
		lockButton.setOnClickListener(this);
		navigationButton.setOnClickListener(this);
		
		return view;
	}
	
	public void lockPosition(View view) {
		parkingMap = ((MainActivity)this.getActivity()).getGoogleMapLock();
		posCar = ((MainActivity)this.getActivity()).getPosCar();
		
		ParkInformation parkInformation;
		if(posCar==null){
			Toast.makeText(this.getActivity().getApplicationContext(), "Error: Location cannot be determined", Toast.LENGTH_SHORT).show();
			return;
		}
		@SuppressWarnings("unused")
		Marker parkedLocation = parkingMap.addMarker(new MarkerOptions().position(posCar).title(resources.getString(R.string.carParking)));
		parkingMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posCar, 15));  
		
		parkInformation= new ParkInformation();
		parkInformation.setLatitude(posCar.latitude);
		parkInformation.setLongitude(posCar.longitude);
		parkInformation.setParkingtime(System.currentTimeMillis());
		 // Call for the HashMap to be added to the database         
        dbTools.insertParkingInfo(parkInformation);
        new ParkingAddress(context).execute(Double.toString(posCar.latitude),Double.toString(posCar.longitude),resources.getString(R.string.carParking));
	}
	
	public void navigateToCar (View view){
		posCar = ((MainActivity)this.getActivity()).getPosCar();
		ParkInformation parkInformation = (dbTools.getLatestParkingLocation(1)).get(0);
		LatLng destPosCar = new LatLng(parkInformation.getLatitude(), parkInformation.getLongitude());
		String googleNavigation = getUrl(posCar,destPosCar);
		final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(googleNavigation));
		intent.setClassName("com.google.android.apps.maps",  "com.google.android.maps.MapsActivity");
	    startActivity(intent);
	}
	
	
	private String getUrl(LatLng src, LatLng dest){
		 
		StringBuilder urlString = new StringBuilder();
		 
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		urlString.append("&saddr=");
		urlString.append(Double.toString((double) src.latitude ));
		urlString.append(",");
		urlString.append(Double.toString((double) src.longitude));
		urlString.append("&daddr=");// to
		urlString.append(Double.toString((double) dest.latitude));
		urlString.append(",");
		urlString.append(Double.toString((double) dest.longitude));
		urlString.append("&ie=UTF8&0&om=0&output=kml");
		 
		return urlString.toString();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnLockPos:
			lockPosition(v);
			break;
		case R.id.btnNav:
			navigateToCar(v);
			break;
		}
	}
}
