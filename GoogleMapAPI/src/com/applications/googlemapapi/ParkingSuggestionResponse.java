package com.applications.googlemapapi;

import com.google.android.gms.maps.model.MarkerOptions;

public interface ParkingSuggestionResponse {
	void processFinish(MarkerOptions[] places);
}
