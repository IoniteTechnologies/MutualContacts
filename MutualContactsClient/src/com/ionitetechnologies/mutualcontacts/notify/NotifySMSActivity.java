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

public class NotifySMSActivity extends Activity {
	Button k;
	TimePicker tp;
	DatePicker dp;
	String msg;
	String ToPhnNum;
	String regId;
	EditText et;
	String description, schedule, text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifysms);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			ToPhnNum = extras.getString("ToPhnNum");
			Log.v("to", ToPhnNum);
		}
		ToPhnNum.trim();

		et = (EditText) findViewById(R.id.etdescrisms);
		k = (Button) findViewById(R.id.btnsendsms);

		tp = (TimePicker) findViewById(R.id.timePicker1sms);
		dp = (DatePicker) findViewById(R.id.datePicker1sms);

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

				Uri uri = Uri.parse("smsto:" + ToPhnNum);
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				intent.putExtra("sms_body", "MUTUAL CONTACTS: \n"
						+ description);
				startActivity(intent);

			}

		});
	}
}
