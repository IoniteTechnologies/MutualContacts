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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	SharedPreferences p;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		p = getSharedPreferences("pref", MODE_PRIVATE);
		SharedPreferences.Editor e = p.edit();
		int pg = p.getInt("onpage", 1);

		if (pg == 2) {
			Accept(null);
		}
		if (pg == 3) {
			Intent imp = new Intent("COM.IONITETECHNOLOGIES.MUTUALCONTACTS.notify.DEVICELIST");
			startActivity(imp);
			finish();

		} else {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.main);
		}
	}

	public void Accept(View view) {
		Intent i = new Intent("COM.IONITETECHNOLOGIES.MUTUALCONTACTS.VERIFY");
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
		finish();
	}
}