package com.help.dmadan.emergencycall.ECUtilities;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dmadan on 10/14/14.
 */
public class Features {

	public static void sendSMSMessage(Context context, Double mLat, Double mLong, String mPhoneNumber) throws JSONException {
		new GoogleRestAPIClientUsage().getLocation(mLat, mLong, mPhoneNumber, context);
	}

	public static void callPhoneNmber(Context context, String phoneNumber) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + phoneNumber));
		callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(callIntent);
	}
}
