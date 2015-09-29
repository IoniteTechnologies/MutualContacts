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

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ionitetechnologies.mutualcontacts.notify.DeviceList;

public class InviteAdapter extends BaseAdapter {

	private ArrayList<Contacts> contactlist = new ArrayList<Contacts>();
	private InviteActivity invitecontext;
	Contacts c;

	public InviteAdapter(ArrayList<Contacts> cl, InviteActivity ia) {
		contactlist = cl;
		invitecontext = ia;

	}

	@Override
	public int getCount() {
		return contactlist.size();
	}

	@Override
	public Object getItem(int position) {
		return getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {// if listview is empty, inflate it with one
									// and then we'll add values to it.
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.invitelistitem, parent,
					false);
		}

		c = contactlist.get(position);

		TextView TVname = (TextView) convertView.findViewById(R.id.name_view);
		Log.v("while put" + position, c.getName());
		TVname.setText(c.getName());
		final String tophone = c.getphn();

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent("COM.IONITETECHNOLOGIES.MUTUALCONTACTS.notify.NOTIFYSMSACTIVITY");
				i.putExtra("ToPhnNum", tophone);
				Log.v("to", tophone);
				invitecontext.startActivity(i);
			}
		});

		return convertView;
	}

}
