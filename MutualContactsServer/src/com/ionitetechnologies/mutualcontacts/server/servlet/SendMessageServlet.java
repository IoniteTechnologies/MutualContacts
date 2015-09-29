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
package com.ionitetechnologies.mutualcontacts.server.servlet;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ionitetechnologies.mutualcontacts.server.constants.Constants;

/**
 * Servlet that sends a message to a device. This servlet is invoked by
 * AppEngine's Push Queue mechanism.
 */
@SuppressWarnings("serial")
public class SendMessageServlet extends BaseServlet {

	static final String PARAMETER_DEVICE = "device";

	private Sender sender;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		logger.info("Sending message to device ");
		String regId = req.getParameter("regid");
		String data = req.getParameter("data");
		String fromNumber = req.getParameter("fromnumber");
		logger.info("regid: " + regId);
		try {
			Sender sender = new Sender(Constants.AWS_SECRET_KEY);

			ArrayList<String> devicesList = new ArrayList<String>();
			devicesList.add("");

			Message message = new Message.Builder().collapseKey("1")
					.timeToLive(3).delayWhileIdle(true)
					.addData("message", data).build();

			Result result = sender.send(message, regId, 1);

			System.out.println(result.toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception posting " + e);
			e.printStackTrace();
			return;
		}

	private void sendSingleMessage(String regId, HttpServletResponse resp) {
		logger.info("Sending message to device " + regId);
		try {
			Sender sender = new Sender(Constants.AWS_SECRET_KEY);

			ArrayList<String> devicesList = new ArrayList<String>();
			devicesList.add("");

			Message message = new Message.Builder()
					.collapseKey("1")
					.timeToLive(3)
					.delayWhileIdle(true)
					.addData("message",
							"this text will be seen in notification bar!!")
					.build();

			Result result = sender.send(message, "", 1);

			System.out.println(result.toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception posting " + e);
			// resp.setStatus(200);
			e.printStackTrace();
			return;
		}
	}
}
