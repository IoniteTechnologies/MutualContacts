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
package com.ionitetechnologies.mutualcontacts;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;

public class InviteActivity extends Activity {

	ArrayList<Contacts> contactlist;
	Contacts c;
	static Cursor cursor;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite);

		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Please Wait...");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();

		contactlist = new ArrayList<Contacts>();

		cursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String name = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumber = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			Log.v("contacts", name);
			c = new Contacts();
			c.setName(name);
			c.setPhn(phoneNumber);
			contactlist.add(c);
		}

		ListView listView = (ListView) findViewById(R.id.invitelist);

		InviteAdapter ia = new InviteAdapter(contactlist, this);
		listView.setAdapter(ia);
		dialog.dismiss();
	}
}
