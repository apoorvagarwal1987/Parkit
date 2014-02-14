package com.applications.googlemapapi;

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

public class SuggestionsFragment extends Fragment implements ParkingSuggestionResponse {
	Spinner locationTypeSpinner;
	DBTools dbTools;
	GoogleMap suggestionMap;
	Fragment context;
	LatLng posCar;
	Marker currentLocation;
	Resources resources;
	
	String[] locationSuggestions;
	int []locationImages;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		dbTools = ((MainActivity)this.getActivity()).getDBTools();
		context = this;
		resources = getResources();
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.history_fragment,  container, false);
		
		/*Button recentBtn = (Button) view.findViewById(R.id.btnRecent);
		recentBtn.setOnClickListener(this);*/
		
		locationSuggestions= new String[]{
					resources.getString(R.string.parkingLoc),
					resources.getString(R.string.restaurantLoc),
					resources.getString(R.string.gasLoc),
					resources.getString(R.string.cafeLoc),
					resources.getString(R.string.barLoc),
					resources.getString(R.string.hospitalLoc),
					resources.getString(R.string.groceryLoc),
					resources.getString(R.string.airportLoc)
		};
		
		locationImages = new int[]{
				R.drawable.map_marker,
				R.drawable.fork_and_knife,
				R.drawable.fuel,
				R.drawable.coffee,
				R.drawable.beer_mug,
				R.drawable.medical_bag,
				R.drawable.shopping_cart,
				R.drawable.airplane
		};
		locationTypeSpinner = (Spinner)view.findViewById(R.id.week_spinner);
		LocationAdapter lAdapt = new LocationAdapter(this.getActivity(), R.layout.spinner_row, locationSuggestions);
		locationTypeSpinner.setAdapter(lAdapt);
		
		
		locationTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String type="" ;
				switch (position){
				case 0: 
					type="parking";
					break;
						
				case 1:
					type="restaurant";
					break;
						
				case 2:
					type="gas_station";
					break;
					
				case 3:
					type="restaurant";
					break;
					
				case 4:
					type="cafe";
					break;
					
				case 5:
					type="hospital";
					break;
					
				case 6:
					type="grocery_or_supermarket";
					break;
					
				case 7:
					type="airport";
					break;
				}
				
				loadMap(type);
				return;
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				//do nothing, but maybe we want to default to displaying the last 10 places?
			}
		});		
		
		return view;
	}
	
	public void loadMap (String type) {
		if (type=="") 
			type="parking";
		
		posCar = ((MainActivity)this.getActivity()).getPosCar();
		suggestionMap = ((MainActivity)context.getActivity()).getGoogleMapSuggestions();
		suggestionMap.clear();
		MarkerOptions currentPosition=new MarkerOptions()
											.position(posCar)
											.title(resources.getString(R.string.carPosition))				
											.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		currentLocation = suggestionMap.addMarker(currentPosition);	

		String placesSearchStr = placeSearchURL(type);
		
		//execute query
		new ParkingSuggestion(context).execute(placesSearchStr);
	}
	
	private String placeSearchURL(String type){
		
		String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
				"json?location="+posCar.latitude+","+posCar.longitude+
				"&radius=1000&sensor=true" +
				"&types="+type+
				"&key=<YOUR_BROWSER_KEY>";
		return placesSearchStr;
	}
	
	public class LocationAdapter extends ArrayAdapter<String> {

		public LocationAdapter(Context context, int textViewResourceId, String[] objects) {
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
            label.setText(locationSuggestions[position]);

            ImageView icon=(ImageView)row.findViewById(R.id.rowImage);
            icon.setImageResource(locationImages[position]);
            return row;
		}
		
	}
	@Override
	public void processFinish(MarkerOptions[] places) {
		
		suggestionMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posCar, 15));  		
		for (MarkerOptions parkingOption: places) {
			//Marker parkedLocation = googleMap.addMarker(new MarkerOptions().position(posCar).title("Parked"));
			@SuppressWarnings("unused")
			Marker parkedLocation = suggestionMap.addMarker(parkingOption);					
		}
		
		suggestionMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker parkingPosition) {
				if (parkingPosition.getTitle().equals(resources.getString(R.string.carPosition))) 
					return false;
				
				new ParkingNavigation(context).execute(Double.toString(parkingPosition.getPosition().latitude),
													Double.toString(parkingPosition.getPosition().longitude),
													parkingPosition.getTitle(),
													Double.toString(posCar.latitude),
													Double.toString(posCar.longitude));
				return true;
			}					
		});
	}
}
