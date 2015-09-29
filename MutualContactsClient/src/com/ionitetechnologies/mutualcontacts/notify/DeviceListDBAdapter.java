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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DeviceListDBAdapter {
	public static final String KEY_ID = "id";
	public static final String KEY_PHONENUMBER = "phonenumber";
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "MyDB";
	private static final String DATABASE_TABLE = "devicelist";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = "create table devicelist (id text primary key, phonenumber text unique)";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DeviceListDBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	// inner class DatabaseHelper.
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(DATABASE_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS contacts");
			onCreate(db);
		}
	}

	// define the various methods for opening and closing the database, as well
	// as the methods for adding/editing/deleting rows

	// ---opens the database---
	public DeviceListDBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	// ---insert a contact into the database---
	public long insertDevice(String id, String number) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ID, id);
		initialValues.put(KEY_PHONENUMBER, number);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	// ---retrieves a particular contact---
	public Cursor getDeviceId(String phn) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ID,
				KEY_PHONENUMBER }, KEY_PHONENUMBER + "=" + phn, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---deletes a particular contact---
	public boolean deleteDevice(String number) {
		return db.delete(DATABASE_TABLE, KEY_PHONENUMBER + "=" + number, null) > 0;
	}

	// ---retrieves all the contacts---
	public Cursor getAllDevice() {
		return db.query(DATABASE_TABLE,
				new String[] { KEY_ID, KEY_PHONENUMBER }, null, null, null,
				null, null);
	}
}