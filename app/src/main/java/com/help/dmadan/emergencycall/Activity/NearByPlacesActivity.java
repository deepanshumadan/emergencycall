package com.help.dmadan.emergencycall.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.help.dmadan.emergencycall.ECUtilities.CustomDialog;
import com.help.dmadan.emergencycall.ECUtilities.ParseJSON;
import com.help.dmadan.emergencycall.R;

public class NearByPlacesActivity extends FragmentActivity implements LocationListener {

	GoogleMap mGoogleMap;
	Spinner mSprPlaceType;
	Button mFilterbtn1, mFilterbtn2, mFilterbtn3, mFilterbtn4;
	LinearLayout mFilterLayout;
	String[] mPlaceType = null;
	String[] mPlaceTypeName = null;
	private ProgressBar spinner;
	ArrayList<LatLng> markerList;

	double mLatitude = 0;
	double mLongitude = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby_places);

		spinner = (ProgressBar) findViewById(R.id.progressBar1);
		spinner.setVisibility(View.GONE);

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
		markerList = new ArrayList<LatLng>();

		// Getting reference to Find Button
		btnFind = (Button) findViewById(R.id.btn_find);

		mFilterbtn1 = (Button) findViewById(R.id.filter_btn_1);
		mFilterbtn2 = (Button) findViewById(R.id.filter_btn_2);
		mFilterbtn3 = (Button) findViewById(R.id.filter_btn_3);
		mFilterbtn4 = (Button) findViewById(R.id.filter_btn_4);

		//linear layout for filters
		mFilterLayout = (LinearLayout) findViewById(R.id.filter_btns);
		mFilterLayout.setVisibility(View.GONE);

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

			mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
			mGoogleMap.getUiSettings().setCompassEnabled(false);

			// Setting click event lister for the find button
			btnFind.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mFilterLayout.setVisibility(View.VISIBLE);
				}
			});

			//5 miles
			mFilterbtn1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onFilterClickEvent("8040");
				}
			});

			//10 miles
			mFilterbtn2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onFilterClickEvent("16900");
				}
			});

			//20 miles
			mFilterbtn3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onFilterClickEvent("32180");
				}
			});

			//50 miles
			mFilterbtn4.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onFilterClickEvent("80460");
				}
			});

			setInitialZoom();
		}
	}

	private void setInitialZoom() {
		LatLng latLng = new LatLng(mLatitude, mLongitude);

		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
	}

	private static void zoomToCoverAllMarkers(ArrayList<LatLng> latLngList, GoogleMap googleMap) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		/*for (Marker marker : markers)
		{
            builder.include(marker.getPosition());
        }*/
		for (LatLng marker : latLngList) {
			builder.include(marker);
		}

		LatLngBounds bounds = builder.build();
		int padding = 10; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		googleMap.moveCamera(cu);
		googleMap.animateCamera(cu);
	}

	private void onFilterClickEvent(String radius) {
		int selectedPosition = mSprPlaceType.getSelectedItemPosition();
		String type = mPlaceType[selectedPosition];
		markerList.clear();
		StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		sb.append("location=" + mLatitude + "," + mLongitude);
		sb.append("&radius=" + radius);
		sb.append("&types=" + type);
		sb.append("&sensor=true");
		sb.append("&key=AIzaSyCx8RTymjqdONsHrIHFO4DEmwAR9iN4xdg");

		Log.d("PlacesRequest", sb.toString());
		// Creating a new non-ui thread task to download json data
		APIPlacesAsyncTask placesAsyncTask = new APIPlacesAsyncTask();

		// Invokes the "doInBackground()" method of the class PlaceTask
		placesAsyncTask.execute(sb.toString());
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

		protected void onPreExecute() {
			spinner.setVisibility(View.VISIBLE);
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			spinner.setVisibility(View.GONE);
			mFilterLayout.setVisibility(View.GONE);

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

				//adding markers to list
				markerList.add(latLng);

				// Placing a marker on the touched position
				mGoogleMap.addMarker(markerOptions);
				mGoogleMap.setOnMarkerClickListener(
					new GoogleMap.OnMarkerClickListener() {
						@Override
						public boolean onMarkerClick(Marker marker) {
							new CustomDialog().openAlert(marker.getTitle(), mLatitude, mLongitude, getApplicationContext(), NearByPlacesActivity.this);
							return false;
						}
					}
				);
			}
			zoomToCoverAllMarkers(markerList, mGoogleMap);
			Toast.makeText(NearByPlacesActivity.this, "Click on place markers to get directions",
				Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
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