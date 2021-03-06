package com.help.dmadan.emergencycall.ECUtilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.help.dmadan.emergencycall.R;

/**
 * Created by dmadan on 10/16/14.
 */
public class CustomDialog {

	public void openAlert(final String title, final double mLatitude, final double mLongitude, final Context context, final Activity activity) {
		final String placeAndAdress = getDestinationString(title);
		final String dest = placeAndAdress.substring(placeAndAdress.indexOf(":"), placeAndAdress.length());
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK);
		alertDialogBuilder.setTitle("Place near me!");
		alertDialogBuilder.setMessage(title);
		alertDialogBuilder.setIcon(R.drawable.small_icon);

		// set get direction button
		alertDialogBuilder.setPositiveButton("Get Directions", new DialogInterface.OnClickListener() {
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

	public static void openSetAlert(final Activity activity) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK);
		alertDialogBuilder.setTitle("eHelp");
		alertDialogBuilder.setMessage("Go to Settings and add an emergency number");
		alertDialogBuilder.setIcon(R.drawable.small_icon);
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// cancel the alert box
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show alert
		alertDialog.show();
	}
}
