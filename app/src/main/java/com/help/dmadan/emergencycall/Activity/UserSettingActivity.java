package com.help.dmadan.emergencycall.Activity;

/**
 * Created by dmadan on 10/14/14.
 */

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.help.dmadan.emergencycall.R;

public class UserSettingActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.settings);
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(UserSettingActivity.this, MainActivity.class);
		startActivity(i);
	}
}
