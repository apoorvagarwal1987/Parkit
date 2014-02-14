package com.applications.googlemapapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ParkingSuggestion extends AsyncTask<String, String, String>{
	
	private final static String TAG = "Parking Suggestion";
	public ParkingSuggestionResponse delegate=null;
	
	Fragment context;

	public ParkingSuggestion(Fragment _context){
		super();
		context=_context;
		delegate=(ParkingSuggestionResponse)context;
	}
	
	@Override
	protected String doInBackground(String... parkingURL) {
		
		StringBuilder parkingLocations = new StringBuilder();
		String parkingSuggestionURL = parkingURL[0];		
		HttpClient parkingClient = new DefaultHttpClient();
		try {
			
			HttpGet parkingGet = new HttpGet(parkingSuggestionURL);			
			HttpResponse parkingResponse = parkingClient.execute(parkingGet);			
			StatusLine parkingSearchStatus = parkingResponse.getStatusLine();
			//response is OK
			if (parkingSearchStatus.getStatusCode() == 200) {				
				HttpEntity parkingEntity = parkingResponse.getEntity();				
				InputStream parkingContent = parkingEntity.getContent();				
				InputStreamReader parkingInput = new InputStreamReader(parkingContent);				
				BufferedReader parkingReader = new BufferedReader(parkingInput);				
				String lineIn;
				while ((lineIn = parkingReader.readLine()) != null) {
					parkingLocations.append(lineIn);
				}
			}
			else{
				Log.d(TAG, "Response was not OK in parking search HTTP request");
			}
		}
		catch(Exception e){ 
			Log.d(TAG, "Error in parking search HTTP request: "+e.getMessage());
			e.printStackTrace(); 
		}
		return parkingLocations.toString();
	}
	
	protected void onPostExecute(String result) {
		MarkerOptions[] places;
		try {
			//parse JSON
			
			//create JSONObject, pass stinrg returned from doInBackground
			JSONObject parkingResult = new JSONObject(result);
			//get "results" array
			JSONArray parkingAreas = parkingResult.getJSONArray("results");
			//marker options for each place returned
			places = new MarkerOptions[parkingAreas.length()];
			//loop through places
			for (int parkingArea=0; parkingArea<parkingAreas.length(); parkingArea++) {
				//parse each place
				//if any values are missing we won't show the marker
				boolean missingValue=false;
				LatLng parkingLoaction=null;
				String parkingName="";
				String vicinity="";
				try{
					missingValue=false;
					JSONObject parkingLocationInformation = parkingAreas.getJSONObject(parkingArea);
					JSONObject loc = parkingLocationInformation.getJSONObject("geometry").getJSONObject("location");
					parkingLoaction = new LatLng(Double.valueOf(loc.getString("lat")), 	Double.valueOf(loc.getString("lng")));
					vicinity = parkingLocationInformation.getString("vicinity");
					parkingName = parkingLocationInformation.getString("name");
				}
				catch(JSONException exception){
					Log.d(TAG, "missing value");
					missingValue=true;
					exception.printStackTrace();
				}
				//if values missing we don't display
				if(missingValue)	places[parkingArea]=null;
				else{
					MarkerOptions parkingSuggestion=new MarkerOptions()
											.position(parkingLoaction)
											.title(parkingName)				
											.snippet(vicinity);
					places[parkingArea]=parkingSuggestion;					
				}
			}
			delegate.processFinish(places);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
