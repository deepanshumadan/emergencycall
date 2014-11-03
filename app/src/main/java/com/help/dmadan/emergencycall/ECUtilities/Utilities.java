package com.help.dmadan.emergencycall.ECUtilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by dmadan on 10/14/14.
 */
public class Utilities {
	public static String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String time = dateFormat.format(cal.getTime());
		return time;
	}

	public static String getPhoneNumber(Context context) {
		SharedPreferences sharedPrefs = PreferenceManager
			.getDefaultSharedPreferences(context);
		String phoneNumber = sharedPrefs.getString("prefPhonenumber", "");
		Log.d("Stored phone number:", phoneNumber);
		return phoneNumber;
	}
}
