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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ionitetechnologies.mutualcontacts.server.entity.Devices;
import com.ionitetechnologies.mutualcontacts.server.entity.MusicFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysql.jdbc.Statement;

public class UploadContactsServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {

		Logger l = Logger.getLogger(this.getClass().getName());
		l.info("In ContactPost Method");
		System.out.println("in contactpost");

		Connection conn = DatabaseUtil.getConnection();

		String phn = req.getParameter("phn");
		String list = req.getParameter("listofnumbers");

		System.out.println("server:: " + list);

		Gson gson = new Gson();
		List<Devices> listofnumbers = gson.fromJson(list.toString(),
				new TypeToken<List<Devices>>() {
				}.getType());

		PreparedStatement ps;
		try {

			ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS N" + phn
					+ " (PHONENUM VARCHAR(20))");

			System.out.println("Table created");

			ps.execute();

			long start = System.currentTimeMillis();
			StringBuilder build = new StringBuilder("insert into N" + phn
					+ " (PHONENUM) VALUES");
			for (int i = 0; i < listofnumbers.size(); i++) {
				build.append("(?)");
				if (i < (listofnumbers.size()) - 1) {
					build.append(", ");
				} else {
					build.append(";");
				}
			}

			conn.setAutoCommit(false);
			PreparedStatement ps2 = conn.prepareStatement(build.toString());
			for (int i = 0; i < listofnumbers.size(); i++) {
				Devices d = listofnumbers.get(i);
				ps2.setString(i + 1, d.getPhn());
			}

			int result = ps2.executeUpdate();

			conn.commit();

			long end = System.currentTimeMillis();
			System.out.println("Insert time was " + (end - start));

			ps2.close();

		} catch (SQLException e) {
			System.out.println("contacts upload failed.");
			e.printStackTrace();

		} finally {
			System.out.println("server side updation complete");
			try {
				if (conn != null && !(conn.isClosed()))
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
