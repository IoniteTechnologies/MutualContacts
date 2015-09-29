/*
 * Copyright 2015 Shivakshi Chaudhary/Ionite Technologies LLP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.ionitetechnologies.mutualcontacts.verification;

import static com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.SENDER_ID;
import static com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.SERVER_URL;
import static com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.EXTRA_MESSAGE;
import static com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.SENDER_ID;
import static com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.EXTRA_MESSAGE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.ionitetechnologies.mutualcontacts.Devices;
import com.ionitetechnologies.mutualcontacts.R;
import com.ionitetechnologies.mutualcontacts.R.id;
import com.ionitetechnologies.mutualcontacts.R.layout;
import com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities;
import com.ionitetechnologies.mutualcontacts.servertools.ServerUtilities;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VerifyActivity extends Activity {
	Button btnsendSMS;
	EditText etnumber;
	String number;

	IntentFilter intentFilter;

	TextView mDisplay;
	AsyncTask<Void, Void, Void> mRegisterTask;

	int c = (int) (Math.random() * 10000);
	String code = Integer.toString(c);

	private static String regId;
	public static Activity context;

	Cursor cursor;
	Devices dev;
	ArrayList<Devices> devices;

	public static ProgressDialog dialog;

	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ---display the SMS received ---
			String sms = (intent.getExtras().getString("sms"));
			if (sms.equals(code)) {
				Log.i(CommonUtilities.TAG, "Number Verified");

				registerDevice();

			} else {// dialog.dismiss();

				Toast.makeText(VerifyActivity.this,
						"Message code do not match. Verification Denied.",
						Toast.LENGTH_SHORT).show();
			}

		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verify);

		context = this;
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		pref.edit().putInt("onpage", 2).commit();

		intentFilter = new IntentFilter();
		intentFilter.addAction("SMS_RECEIVED_ACTION");

		Log.d("initial code ", code);

		btnsendSMS = (Button) findViewById(R.id.btnverify);
		etnumber = (EditText) findViewById(R.id.etphno);
		btnsendSMS.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				number = etnumber.getText().toString();

				sendSMS(number, code);

				Toast.makeText(VerifyActivity.this,
						"Sending Message. Please Wait...", Toast.LENGTH_SHORT)
						.show();

			}
		});

	}

	public void sendSMS(String ph, String msg) {
		SmsManager sm = SmsManager.getDefault();
		sm.sendTextMessage(ph, null, msg, null, null);
	}

	private void registerDevice() {
		dialog = ProgressDialog.show(this, "Registering you", "Please Wait...");

		updateNumberOnSP();

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		regId = GCMRegistrar.getRegistrationId(this);
		Log.v("regID", getRegId());
		if (getRegId().equals("")) {
			GCMRegistrar.register(this, getString(R.string.gcm_project_id));

		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				Log.v("GCM", "GCM already registered");

			} else {
				// boolean registered;
				Log.i(CommonUtilities.TAG, "before reg");

				try {
					ServerUtilities.register(getBaseContext(), getRegId(),
							number); // TAKE RETURN VALUE TO CHECK IF CONTINUE
										// TO ANOTHER ACTIVITY OR TRY AGAIN.
					ArrayList<Devices> devi = RetrieveContacts();
					ServerUtilities.uploadNumbers(devi, number);
					openMainPage();
				} catch (IOException e) {
					Log.i(CommonUtilities.TAG, "error reg");
					e.printStackTrace();
					// registered =false;
				}

				// At this point all attempts to register with the app
				// server failed, so we need to unregister the device
				// from GCM - the app will try to register again when
				// it is restarted. Note that GCM will send an
				// unregistered callback upon completion, but
				// GCMIntentService.onUnregistered() will ignore it.
				Log.i(CommonUtilities.TAG, "after reg");

			}
		}

	}

	ArrayList<Devices> RetrieveContacts() {

		cursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		ArrayList<Devices> device = new ArrayList<Devices>();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String phoneNumber = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			Log.v("contact for", phoneNumber);
			dev = new Devices();
			dev.setPhn(phoneNumber);
			device.add(dev);
		}

		return device;

	}

	private void updateNumberOnSP() {
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		pref.edit().putString("PhnNumber", number).commit();

	}

	private void openMainPage() {
		Toast.makeText(this, "Verification Complete", Toast.LENGTH_SHORT)
				.show();
		dialog.dismiss();
		Intent imp = new Intent(
				"COM.IONITETECHNOLOGIES.MUTUALCONTACTS.notify.DEVICELIST");
		imp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(imp);
		finish();
	}

	public static String getRegId() {
		return regId;
	}

	public static String setRegId(String regid) {
		return VerifyActivity.regId = regid;
	}

}
