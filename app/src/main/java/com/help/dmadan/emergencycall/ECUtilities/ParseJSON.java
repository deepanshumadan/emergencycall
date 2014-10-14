package com.help.dmadan.emergencycall.ECUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Created by dmadan on 10/14/14.
 */
public class ParseJSON {
	public static String parseJSONResponse(String jsonString) throws JSONException {

		//Convert String to json object
		JSONObject jsonObject = new JSONObject(jsonString);

		JSONArray jsonArray = jsonObject.getJSONArray("results");
		// get json object
		JSONObject jsonobject = jsonArray.getJSONObject(0);

		// get value from Json Object
		String formattedAddress = jsonobject.getString("formatted_address");

		Log.d("current address", formattedAddress);
		return formattedAddress;
	}
}
