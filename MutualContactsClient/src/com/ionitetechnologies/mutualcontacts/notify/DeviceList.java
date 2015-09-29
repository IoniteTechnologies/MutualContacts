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
import java.util.ArrayList;
import java.util.List;

import com.ionitetechnologies.mutualcontacts.Devices;
import com.ionitetechnologies.mutualcontacts.InviteActivity;
import com.ionitetechnologies.mutualcontacts.R;
import com.ionitetechnologies.mutualcontacts.R.layout;
import com.ionitetechnologies.mutualcontacts.servertools.ServerUtilities;
import com.ionitetechnologies.mutualcontacts.verification.VerifyActivity;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceList extends Activity {

	DeviceListDBAdapter db;
	AsyncTask<Void, Void, Void> getDeviceTask;
	List<Devices> d;
	SharedPreferences p;
	TextView tv;
	ListView listview;
	DeviceListAdapter ma;
	String myPhnNumber;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ProgressDialog dialog = ProgressDialog.show(this,
				"Initializing", "Please Wait...");
		setContentView(R.layout.devicelist);

		// if initializing for first time, run following and save in sp/sqlite.
		// if not, retrieve data from sp. Implement gcm for adding updates.
		p = getSharedPreferences("pref", MODE_PRIVATE);
		SharedPreferences.Editor e = p.edit();
		e.putInt("onpage", 3).commit();

		int reqdevicelist = p.getInt("reqdevicelist", 0);
		myPhnNumber = p.getString("PhnNumber", "0");
		Log.i("devlist", "" + reqdevicelist);

		listview = (ListView) findViewById(R.id.devicelist);

		if (reqdevicelist == 1) {
			db = new DeviceListDBAdapter(this);
			db.open();
			Cursor c = db.getAllDevice();
			List<Devices> d = new ArrayList<Devices>();

			if (c.moveToFirst()) {
				do {
					Devices dev = new Devices();
					dev.setId(c.getString(0));
					dev.setPhn(c.getString(1));
					d.add(dev);
				} while (c.moveToNext());
			}
			db.close();

			ma = new DeviceListAdapter(d, this);
			listview.setAdapter(ma);
			dialog.dismiss();

		}

		if (reqdevicelist == 0) // devicelist requested on server and updated on
								// sqlite only once.
		{
			getDeviceTask = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					try {

						d = ServerUtilities.getDeviceEntry(myPhnNumber);

					} catch (IOException e) {
						e.printStackTrace();
					}

					return null;
				}

				@Override
				protected void onPostExecute(Void result) {

					p.edit().putInt("reqdevicelist", 1).commit();

					ma = new DeviceListAdapter(d, DeviceList.this);
					listview.setAdapter(ma);

					Log.i("devlist", "sqlite updating");
					db = new DeviceListDBAdapter(DeviceList.this);
					// ---add a contact---
					db.open();
					for (int i = 0; i < d.size(); i++) {
						Devices dev = d.get(i);
						long id = db.insertDevice(dev.getId(), dev.getPhn());
					}
					db.close();
					dialog.dismiss();

				}

			};

			getDeviceTask.execute(null, null, null);

		}

	}

	public void refreshdevicelist(View v) {
		GCMRegistrar.unregister(this);
		GCMRegistrar.onDestroy(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.invite, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.invite:
			Intent i = new Intent(this, InviteActivity.class);
			this.startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
