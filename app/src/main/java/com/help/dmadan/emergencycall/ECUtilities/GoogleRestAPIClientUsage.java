package com.help.dmadan.emergencycall.ECUtilities;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.help.dmadan.emergencycall.Services.GoogleRestAPIClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by dmadan on 10/14/14.
 */
public class GoogleRestAPIClientUsage {

	private static String mUserLocation = "";

	public static String getLocation(Double latitude, Double longitude) throws JSONException {

		GoogleRestAPIClient.get(getRelativeUrl(latitude, longitude), null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// If the response is JSONObject instead of expected JSONArray
				try {
					mUserLocation = ParseJSON.parseJSONResponse(response.toString());
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
				Log.d("Place response:", response.toString());
			}
		});
		return mUserLocation;
	}

	private static String getRelativeUrl(Double latitude, Double longitude) {
		String ApiKey = "AIzaSyCx8RTymjqdONsHrIHFO4DEmwAR9iN4xdg";
		String url = "geocode/json?latlng=" + latitude + "," + longitude + "&key=" + ApiKey;
		return url;
	}
}
