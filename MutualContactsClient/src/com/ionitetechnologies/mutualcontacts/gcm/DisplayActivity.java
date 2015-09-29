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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ionitetechnologies.mutualcontacts.R;

public class DisplayActivity extends Activity {

	TextView tvreminder;
	Button Ok;
	String message;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display);

		tvreminder = (TextView) findViewById(R.id.tvmessage);
		Ok = (Button) findViewById(R.id.btnOK);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			message = extras.getString("message");
			getIntent().removeExtra("message");

		}
		Log.v("msg", message);
		tvreminder.setText(message);

	}

	public void clickOK(View v) {
		finish();
	}
}
