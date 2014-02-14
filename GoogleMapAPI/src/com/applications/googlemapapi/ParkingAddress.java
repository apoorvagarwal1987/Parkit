package com.applications.googlemapapi;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

public class ParkingAddress extends AsyncTask<String, String, String>{
	private final static String TAG = "Parking Address";
	
	Fragment context;
	List<Address> addresses= null;
	String title;
	double latitude;
	double longitude;
	

	public ParkingAddress(Fragment _context){
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
		
		AlertDialog.Builder parkingAddressAlert = new AlertDialog.Builder(context.getActivity());
	    parkingAddressAlert
	    .setTitle(title)
	    .setMessage(completeAddress)
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {       
	               //do some thing here which you need
	    }
	    });		         
	AlertDialog alert = parkingAddressAlert.create();
	        alert.show();			
		return;
	}
}