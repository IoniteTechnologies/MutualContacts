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
package com.ionitetechnologies.mutualcontacts.gcm;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ionitetechnologies.mutualcontacts.Devices;
import com.ionitetechnologies.mutualcontacts.R;
import com.ionitetechnologies.mutualcontacts.servertools.ServerUtilities;
import com.ionitetechnologies.mutualcontacts.verification.VerifyActivity;
import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	SharedPreferences pref;
	private static final String TAG = "GCMIntentService";
	String fromNumber;

	public GCMIntentService() {
		super();

	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.d(TAG, "Device is registering with gcm: regId = " + registrationId);

		SharedPreferences p = getSharedPreferences("pref", MODE_PRIVATE);
		String number = p.getString("PhnNumber", "0");
		VerifyActivity.setRegId(registrationId);

		try {
			ServerUtilities.register(context, registrationId, number);
			ArrayList<Devices> devi = RetrieveContacts();
			ServerUtilities.uploadNumbers(devi, number);
		} catch (IOException e) {
			// if Entry is duplicate of gcm, update the number only.
			e.printStackTrace();
		}

		// now only the dialog in verify activity should dismiss.
		if (VerifyActivity.dialog != null) {
			if (VerifyActivity.dialog.isShowing()) {
				VerifyActivity.dialog.dismiss();
			} else {
				Log.e("dialog", "It is NOT SHOWING");
			}
		}

		Intent imp = new Intent("COM.IONITETECHNOLOGIES.MUTUALCONTACTS.notify.DEVICELIST");
		imp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		imp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(imp);

	}

	ArrayList<Devices> RetrieveContacts() {

		Cursor cursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		ArrayList<Devices> device = new ArrayList<Devices>();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String phoneNumber = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			Log.v("contact for", phoneNumber);
			Devices dev = new Devices();
			dev.setPhn(phoneNumber);
			device.add(dev);
		}

		return device;

	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		// displayMessage(context, getString(R.string.gcm_unregistered));
		/*
		 * if (GCMRegistrar.isRegisteredOnServer(context)) {
		 * ServerUtilities.unregister(context, registrationId);
		 * Log.i("GCMRegistrar", "implement unregister method"); } else { //
		 * This callback results from the call to unregister made on //
		 * ServerUtilities when the registration to the server failed.
		 * Log.i(TAG, "Ignoring unregister callback"); }
		 */
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");

		String msg = intent.getStringExtra("message");
		generateNotification(context, msg);
		Log.v("GCM", "Received: " + msg);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_stat_gcm;
		long when = System.currentTimeMillis();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(icon, message, when);

		Intent notificationIntent = new Intent(context, DisplayActivity.class);
		notificationIntent.putExtra("message", message);
		// startActivity(notificationIntent);

		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notification.setLatestEventInfo(context, "Event Reminder", message,
				intent);

		notificationManager.notify(0, notification);
	}

}
