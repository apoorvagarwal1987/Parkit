package com.applications.googlemapapi;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

public class ParkingNavigation extends AsyncTask<String, String, String>{
	private final static String TAG = "Parking Navigation";
	
	Fragment context;
	List<Address> addresses= null;
	String title;
	double latitude;
	double longitude;
	double currentLatitude;
	double currentLongitude;
	public ParkingNavigation(Fragment _context){
		super();
		context=_context;
	}
	
	@Override
	protected String doInBackground(String... arguments) {
		
		Geocoder geocoder = new Geocoder(context.getActivity().getApplicationContext(),Locale.getDefault());
		try{
			latitude=Double.parseDouble(arguments[0]);
			longitude=Double.parseDouble(arguments[1]);
			title= new String(arguments[2]);
			currentLatitude=Double.parseDouble(arguments[3]);
			currentLongitude=Double.parseDouble(arguments[4]);			
			addresses = geocoder.getFromLocation(latitude,longitude, 10);				
		}
		catch(IOException e){
			Log.d(TAG, "IO Exception address not found");
		}			
		return null;
	}
	
	protected void onPostExecute(String result){
		StringBuilder completeAddress = new StringBuilder();
		if (addresses.size()>0){
			Address address=addresses.get(0);
			for(int i =0; i<address.getMaxAddressLineIndex();i++){
				completeAddress.append(address.getAddressLine(i)).append("\n");
			}
			completeAddress.append(address.getLocality()).append("\n");
			completeAddress.append(address.getPostalCode()).append("\n");
			completeAddress.append(address.getCountryName());
		}
		else
			completeAddress.append("No address found");
		
		AlertDialog.Builder parkingNavigationAlert = new AlertDialog.Builder(context.getActivity());
	    parkingNavigationAlert
	    .setTitle(title)
	    .setMessage(completeAddress)
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {       
	               //do some thing here which you need
	        }       
	    })
	    .setNegativeButton("Navigate", new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {       
	        	LatLng destPosCar = new LatLng(latitude, longitude);
	        	LatLng currentPosCar = new LatLng(currentLatitude, currentLongitude);
	    		String googleNavigation = getUrl(currentPosCar,destPosCar);
	    		final Intent navigation = new Intent(Intent.ACTION_VIEW,Uri.parse(googleNavigation));
	    		navigation.setClassName("com.google.android.apps.maps",  "com.google.android.maps.MapsActivity");
	    	    context.startActivity(navigation);
	        }
	    });		         
		AlertDialog alert = parkingNavigationAlert.create();
		        alert.show();			
			return;
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

}
