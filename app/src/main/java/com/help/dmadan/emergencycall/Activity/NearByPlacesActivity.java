package com.help.dmadan.emergencycall.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.help.dmadan.emergencycall.ECUtilities.ParseJSON;
import com.help.dmadan.emergencycall.R;

public class NearByPlacesActivity extends FragmentActivity implements LocationListener {

	GoogleMap mGoogleMap;
	Spinner mSprPlaceType;

	String[] mPlaceType = null;
	String[] mPlaceTypeName = null;

	double mLatitude = 0;
	double mLongitude = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby_places);

		// Array of place types
		mPlaceType = getResources().getStringArray(R.array.place_type);

		// Array of place type names
		mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

		// Creating an array adapter with an array of Place types
		// to populate the spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);

		// Getting reference to the Spinner
		mSprPlaceType = (Spinner) findViewById(R.id.spr_place_type);

		// Setting adapter on Spinner to set place types
		mSprPlaceType.setAdapter(adapter);

		Button btnFind;

		// Getting reference to Find Button
		btnFind = (Button) findViewById(R.id.btn_find);

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

		if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();

		}
		else { // Google Play Services are available

			// Getting reference to the SupportMapFragment
			SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

			// Getting Google Map
			mGoogleMap = fragment.getMap();

			// Enabling MyLocation in Google Map
			mGoogleMap.setMyLocationEnabled(true);

			// Getting LocationManager object from System Service LOCATION_SERVICE
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// Creating a criteria object to retrieve provider
			Criteria criteria = new Criteria();

			// Getting the name of the best provider
			String provider = locationManager.getBestProvider(criteria, true);

			// Getting Current Location From GPS
			Location location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				onLocationChanged(location);
			}

			locationManager.requestLocationUpdates(provider, 20000, 0, this);

			// Setting click event lister for the find button
			btnFind.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					int selectedPosition = mSprPlaceType.getSelectedItemPosition();
					String type = mPlaceType[selectedPosition];

					StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
					sb.append("location=" + mLatitude + "," + mLongitude);
					sb.append("&radius=10000");
					sb.append("&types=" + type);
					sb.append("&sensor=true");
					sb.append("&key=AIzaSyCx8RTymjqdONsHrIHFO4DEmwAR9iN4xdg");

					// Creating a new non-ui thread task to download json data
					APIPlacesAsyncTask placesAsyncTask = new APIPlacesAsyncTask();

					// Invokes the "doInBackground()" method of the class PlaceTask
					placesAsyncTask.execute(sb.toString());

				}
			});
		}
	}

	/**
	 * A method to download json data from url
	 */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		}
		catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		}
		finally {
			iStream.close();
			urlConnection.disconnect();
		}

		return data;
	}

	/**
	 * A class, to download Google Places
	 */
	private class APIPlacesAsyncTask extends AsyncTask<String, Integer, String> {

		String data = null;

		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			}
			catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result) {
			APIParserAsyncTask parserAsyncTask = new APIParserAsyncTask();

			// Start parsing the Google places in JSON format
			// Invokes the "doInBackground()" method of the class ParseTask
			parserAsyncTask.execute(result);
		}

	}

	/**
	 * A class to parse the Google Places in JSON format
	 */
	private class APIParserAsyncTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData) {

			List<HashMap<String, String>> places = null;
			ParseJSON placeJsonParser = new ParseJSON();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a List construct */
				places = placeJsonParser.parse(jObject);

			}
			catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			// Clears all the existing markers
			mGoogleMap.clear();

			for (int i = 0; i < list.size(); i++) {

				// Creating a marker
				MarkerOptions markerOptions = new MarkerOptions();

				// Getting a place from the places list
				HashMap<String, String> hmPlace = list.get(i);

				// Getting latitude of the place
				final double lat = Double.parseDouble(hmPlace.get("lat"));

				// Getting longitude of the place
				final double lng = Double.parseDouble(hmPlace.get("lng"));

				// Getting name
				String name = hmPlace.get("place_name");

				// Getting vicinity
				final String vicinity = hmPlace.get("vicinity");

				LatLng latLng = new LatLng(lat, lng);

				// Setting the position for the marker
				markerOptions.position(latLng);

				// Setting the title for the marker.
				//This will be displayed on taping the marker
				markerOptions.title(name + " : " + vicinity);

				// Placing a marker on the touched position
				mGoogleMap.addMarker(markerOptions);
				mGoogleMap.setOnMarkerClickListener(
					new GoogleMap.OnMarkerClickListener() {
						@Override
						public boolean onMarkerClick(Marker marker) {
							openAlert(marker.getTitle());
							return false;
						}
					}
				);
			}
		}
	}

	private String getDestinationString(String vicinity) {
		return vicinity.replace(" ", "+");
	}

	private void openAlert(final String title) {
		final String dest = getDestinationString(title);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NearByPlacesActivity.this);

		alertDialogBuilder.setTitle(this.getTitle() + "Place near me!");
		alertDialogBuilder.setMessage(title);
		// set get direction button
		alertDialogBuilder.setPositiveButton("Get Direction", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String mapURL = "http://maps.google.com/maps?saddr=" + mLatitude + "," + mLongitude + "&daddr=" + dest;
				Log.d("mapurl", mapURL);
				// go to a new activity of the app
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(mapURL));
				startActivity(intent);
			}
		});
		// set negative button
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// cancel the alert box
				dialog.cancel();
			}
		});
		// set neutral button: Exit the activity message
		alertDialogBuilder.setNeutralButton("Exit the app", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// exit the screen and go to the HOME screen
				NearByPlacesActivity.this.finish();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		// show alert
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		LatLng latLng = new LatLng(mLatitude, mLongitude);

		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}