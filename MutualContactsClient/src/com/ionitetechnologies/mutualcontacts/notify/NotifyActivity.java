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
 package com.ionitetechnologies.mutualcontacts.notify;

import java.io.IOException;
import java.util.Date;

import com.ionitetechnologies.mutualcontacts.R;
import com.ionitetechnologies.mutualcontacts.servertools.GCMUtilities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class NotifyActivity extends Activity {
	Button k;
	TimePicker tp;
	DatePicker dp;
	String msg;
	String PhnNum;
	String regId;
	EditText et;
	String description, text;
	String schedule;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notify);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			PhnNum = extras.getString("PhnNum");

		}
		PhnNum.trim();

		et = (EditText) findViewById(R.id.etdescri);

		k = (Button) findViewById(R.id.btnsendmsg);

		tp = (TimePicker) findViewById(R.id.timePicker1);
		dp = (DatePicker) findViewById(R.id.datePicker1);

		tp.setIs24HourView(true);
		schedule = "\n on " + dp.getMonth() + 1 + "/" + dp.getDayOfMonth()
				+ "/" + dp.getYear() + "\n at " + tp.getCurrentHour() + ":"
				+ tp.getCurrentMinute() + "hrs";

		k.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				text = et.getText().toString();
				description = text + schedule;
				Log.v("desc", description);

				DeviceListDBAdapter db = new DeviceListDBAdapter(
						NotifyActivity.this);
				db.open();
				Cursor c = db.getDeviceId(PhnNum);
				if (c.moveToFirst()) {
					regId = c.getString(0);
					Log.v("gcm", "sending message to regID: " + regId);
					try {
						// check if file is on server before sending message to
						// ask for upload
						GCMUtilities.sendMessage(regId, description);
						Toast.makeText(NotifyActivity.this,
								"Sent " + description + " to " + PhnNum + ".",
								Toast.LENGTH_SHORT).show();
						finish();

					} catch (IOException e) {
						Log.e("gcm", "ERROR sending message to regID: " + regId);
						e.printStackTrace();
					}
				} else
					Toast.makeText(NotifyActivity.this,
							"Could not find Registration Id", Toast.LENGTH_LONG)
							.show();

				db.close();

			}

		});
	}
}
