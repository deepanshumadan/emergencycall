package com.help.dmadan.emergencycall.Activity;

import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.help.dmadan.emergencycall.ECUtilities.CustomDialog;
import com.help.dmadan.emergencycall.ECUtilities.Features;
import com.help.dmadan.emergencycall.ECUtilities.Utilities;
import com.help.dmadan.emergencycall.R;

public class MainActivity extends Activity implements LocationListener {

	Button mtButton;
	Button mButton;
	LocationManager mLocationManager;
	private double mLong, mLat;
	private String mProvider;
	private String mPhoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service
			.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// check if enabled and if not send user to the GSP settings
		if (!enabled) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		// Define the criteria how to select the location provider -> use default
		Criteria criteria = new Criteria();
		mProvider = mLocationManager.getBestProvider(criteria, false);
		Location location = mLocationManager.getLastKnownLocation(mProvider);

		// Initialize the location fields
		if (location != null) {
			Log.d("Location provider", "Provider " + mProvider + " has been selected.");
			onLocationChanged(location);
		}
		else {
			//
		}

		addListenerOnButton();
		addListenerOnImageButton();
	}

	private void addListenerOnButton() {
		mButton = (Button) findViewById
			(R.id.button1);

		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, NearByPlacesActivity.class);
				startActivity(i);
			}
		});

	}

	private void addListenerOnImageButton() {

		mtButton = (Button) findViewById
			(R.id.touchButton1);

		// add PhoneStateListener
		PhoneCallListener phoneListener = new PhoneCallListener();
		TelephonyManager telephonyManager = (TelephonyManager) this
			.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);


		mtButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mPhoneNumber = Utilities.getPhoneNumber(MainActivity.this);
				if (mPhoneNumber.length() < 2) {
					//alert to set emergency phone number
					Log.d("entered", "entered" + mPhoneNumber);
					CustomDialog.openSetAlert(MainActivity.this);
				}
				else {
					Log.d("entered", "no entered" + mPhoneNumber);
					try {
						Features.sendSMSMessage(getBaseContext(), mLat, mLong, mPhoneNumber);
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
					Features.callPhoneNmber(getBaseContext(), mPhoneNumber);
					Toast.makeText(MainActivity.this, "Calling: " + mPhoneNumber,
						Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onLocationChanged(Location location) {
		mLong = location.getLongitude();
		mLat = location.getLatitude();
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {

	}

	@Override
	public void onProviderEnabled(String s) {

	}

	@Override
	public void onProviderDisabled(String s) {

	}

	//monitor phone call activities
	private class PhoneCallListener extends PhoneStateListener {

		private boolean isPhoneCalling = false;

		String LOG_TAG = "Emergency Call";

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			if (TelephonyManager.CALL_STATE_RINGING == state) {
				// phone ringing
				Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
			}

			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				// active
				Log.i(LOG_TAG, "OFFHOOK");

				isPhoneCalling = true;
			}

			if (TelephonyManager.CALL_STATE_IDLE == state) {
				// run when class initial and phone call ended,
				// need detect flag from CALL_STATE_OFFHOOK
				Log.i(LOG_TAG, "IDLE");

				if (isPhoneCalling) {

					Log.i(LOG_TAG, "restart app");

					// restart app
					Intent i = getBaseContext().getPackageManager()
						.getLaunchIntentForPackage(
							getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);

					isPhoneCalling = false;
				}

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 /* Inflate the menu; this adds items to the action bar
		 if it is present */
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent i = new Intent(this, UserSettingActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivityForResult(i, 1);
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		mLocationManager.requestLocationUpdates(mProvider, 400, 1, this);
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(this);
	}
}