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

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ionitetechnologies.mutualcontacts.Devices;
import com.ionitetechnologies.mutualcontacts.R;

public class DeviceListAdapter extends BaseAdapter {

	private List<Devices> devicelist;
	private static DeviceList dlactivity;

	public DeviceListAdapter(List<Devices> d, DeviceList dl) {
		devicelist = d;
		dlactivity = dl;
	}

	@Override
	public int getCount() {
		return devicelist.size();
	}

	@Override
	public Object getItem(int position) {
		return getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static String getContactNameFromNumber(String number) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(number));

		String name = null;
		Cursor cursor = dlactivity.getContentResolver().query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup.TYPE },
				null, null, null);
		if (cursor.moveToFirst()) {
			name = cursor.getString(cursor
					.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		}
		return name;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {// if listview is empty, inflate it with one
									// and then we'll add values to it.
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.devicelistitem, parent,
					false);
		}

		final Devices dev = devicelist.get(position);

		TextView TVphnnum = (TextView) convertView
				.findViewById(R.id.phnnum_view);
		final String name = getContactNameFromNumber(dev.getPhn());

		TVphnnum.setText(name);

		TextView TVid = (TextView) convertView.findViewById(R.id.id_view);

		TVid.setText("Select to Notify");

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent i = new Intent("COM.IONITETECHNOLOGIES.MUTUALCONTACTS.notify.NOTIFYACTIVITY");
				i.putExtra("PhnNum", dev.getPhn());
				dlactivity.startActivity(i);
				dlactivity.overridePendingTransition(R.anim.right_slide_in,
						R.anim.right_slide_out);

			}
		});

		return convertView;
	}

}