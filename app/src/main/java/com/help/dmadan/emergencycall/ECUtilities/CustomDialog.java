package com.help.dmadan.emergencycall.ECUtilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.help.dmadan.emergencycall.Activity.NearByPlacesActivity;

/**
 * Created by dmadan on 10/16/14.
 */
public class CustomDialog {

	public void openAlert(final String title, final double mLatitude, final double mLongitude, final Context context, final Activity activity) {
		final String placeAndAdress = getDestinationString(title);
		final String dest = placeAndAdress.substring(placeAndAdress.indexOf(":"), placeAndAdress.length());
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

		alertDialogBuilder.setTitle("Place near me!");
		alertDialogBuilder.setMessage(title);
		// set get direction button
		alertDialogBuilder.setPositiveButton("Get Direction", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String mapURL = "http://maps.google.com/maps?saddr=" + mLatitude + "," + mLongitude + "&daddr=" + dest;
				Log.d("mapurl", mapURL);
				// go to a new activity of the app
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(mapURL));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
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
		alertDialogBuilder.setNeutralButton("Home", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// exit the screen and go to the HOME screen
				activity.finish();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		// show alert
		alertDialog.show();
	}

	private String getDestinationString(String vicinity) {
		return vicinity.replace(" ", "+");
	}

}
