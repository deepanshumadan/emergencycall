package com.help.dmadan.emergencycall.ECUtilities;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.help.dmadan.emergencycall.Services.GoogleRestAPIClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by dmadan on 10/14/14.
 */
public class GoogleRestAPIClientUsage {

	static String mUserLocation;

	public String getLocation(Double latitude, Double longitude, final String mPhoneNumber, final Context context) throws JSONException {

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
				if (mUserLocation.equals("")) {
					mUserLocation = "<No GPS location available>";
				}
				String message = mUserLocation + " at " + Utilities.getTime();
				try {
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(mPhoneNumber, null, "I am at " + message, null, null);
					Toast.makeText(context, "SMS sent.",
						Toast.LENGTH_LONG).show();
					Log.d("current-address", message);
				}
				catch (Exception e) {
					Toast.makeText(context,
						"SMS faild, please try again.",
						Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});

		return mUserLocation;
	}

	private String getRelativeUrl(Double latitude, Double longitude) {
		String ApiKey = "AIzaSyCx8RTymjqdONsHrIHFO4DEmwAR9iN4xdg";
		String url = "geocode/json?latlng=" + latitude + "," + longitude + "&key=" + ApiKey;
		return url;
	}
}
