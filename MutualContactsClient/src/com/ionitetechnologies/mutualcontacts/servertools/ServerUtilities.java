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
package com.ionitetechnologies.mutualcontacts.servertools;

import static com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.SERVER_URL;
import static com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities.TAG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ionitetechnologies.mutualcontacts.Devices;
import com.google.android.gcm.GCMRegistrar;
import com.ionitetechnologies.mutualcontacts.servertools.CommonUtilities;
import com.ionitetechnologies.mutualcontacts.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

	public static final String myPhnNumber = "";

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(final Context context, final String regId) {
		Log.i(CommonUtilities.TAG, "unregistering device (regId = " + regId
				+ ")");
		String serverUrl = CommonUtilities.SERVER_URL + "/unregister";
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		GCMRegistrar.setRegisteredOnServer(context, false);
		String message = context.getString(R.string.server_unregistered);
	}

	/**
	 * Register this account/device pair within the server.
	 *
	 * @return whether the registration succeeded or not.
	 * @throws IOException
	 */
	public static boolean register(final Context context, final String regId,
			final String num) throws IOException {
		Log.i(CommonUtilities.TAG, "registering device (regId = " + regId + ")");

		String serverUrl = CommonUtilities.SERVER_URL + "/register";
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		params.put("Phn", num);
		return sendPostRequest(serverUrl, params);
	}

	public static List<Devices> getDeviceEntry(String myPhnNumber)
			throws IOException {

		String serverUrl = CommonUtilities.SERVER_URL + "/register";
		Map<String, String> params = new HashMap<String, String>();
		params.put("mynumber", myPhnNumber);

		List<Devices> list = SendGetDeviceEntryRequest(serverUrl, params);

		return list;
	}

	private static List<Devices> SendGetDeviceEntryRequest(String endpoint,
			Map<String, String> params) throws IOException {

		StringBuffer sb = callsamemethod(endpoint, params);

		System.out.println("on client side" + sb);

		Gson gson = new Gson();
		List<Devices> list = gson.fromJson(sb.toString(),
				new TypeToken<List<Devices>>() {
				}.getType());
		System.out.println("onclientsideinobjects" + list);

		return list;

	}

	private static StringBuffer callsamemethod(String endpoint,
			Map<String, String> params) throws IOException {
		URL url;
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		endpoint = endpoint + "?" + body;

		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}

		Log.v(CommonUtilities.TAG, "getting '" + body + "' to " + url);

		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "text/plain");

			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {

				throw new IOException("Get failed with error code " + status);

			} else {

				BufferedReader r = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String s;
				while ((s = r.readLine()) != null) {
					sb.append(s);

				}
				return sb;
			}
		}

		finally {
			if (conn != null) {
				conn.disconnect();

			}

		}
	}

	/**
	 * Issue a POST request to the server.
	 *
	 * @param endpoint
	 *            POST address.
	 * @param params
	 *            request parameters.
	 *
	 * @throws IOException
	 *             propagated from POST.
	 */
	public static boolean sendPostRequest(String endpoint,
			Map<String, String> params) throws IOException {
		URL url;

		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		Log.v(CommonUtilities.TAG, "Posting '" + body + "' to " + url);

		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();

			// handle the response
			int status = conn.getResponseCode();
			boolean b = true;
			if (status != 200) {
				b = false;
				throw new IOException("Post failed with error code " + status);

			}
			return b;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}

		}
	}

	public static void uploadNumbers(ArrayList<Devices> list, String number)
			throws IOException {

		String serverUrl = CommonUtilities.SERVER_URL + "/uploadcontacts";
		Map<String, String> params = new HashMap<String, String>();
		params.put("phn", number);

		String numberlist = new Gson().toJson(list);
		params.put("listofnumbers", numberlist);
		Log.v("tag", "client::" + numberlist);

		sendPostRequest(serverUrl, params);

	}

}
