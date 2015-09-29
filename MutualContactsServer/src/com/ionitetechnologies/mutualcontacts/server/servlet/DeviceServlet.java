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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ionitetechnologies.mutualcontacts.server.entity.Devices;
import com.google.gson.Gson;
import com.spaceprogram.simplejpa.EntityManagerFactoryImpl;

public class DeviceServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {

		Logger l = Logger.getLogger(this.getClass().getName());
		l.info("In Post Method");
		System.out.println("in post");

		Connection conn = DatabaseUtil.getConnection();

		String phn = req.getParameter("Phn");
		String regid = req.getParameter("regId");

		PreparedStatement ps;
		try {
			ps = conn
					.prepareStatement("INSERT INTO DEVICE(ID,PHONENUMBER) VALUES(?,?)");
			ps.setString(1, regid);
			ps.setString(2, phn);

			ps.execute();

			conn.commit();
		} catch (SQLException e) {
			// TODO HERE: UPDATE ID ONLY
			e.printStackTrace();

		} finally {
			try {
				if (conn != null && !(conn.isClosed()))
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Logger l = Logger.getLogger(this.getClass().getName());
		l.info("In Get Method");
		System.out.println("in get");

		Connection conn = DatabaseUtil.getConnection();

		String myNumber = req.getParameter("mynumber");

		List<Devices> ld = new ArrayList<Devices>();

		PreparedStatement ps, ps2;
		try {
			ps = conn
					.prepareStatement("SELECT RIGHT(PHONENUM , 10) FROM N"
							+ myNumber
							+ " AS S1 JOIN DEVICE AS S2 ON S1.PHONENUM=S2.PHONENUMBER LIMIT 1");
			System.out.println("1." + myNumber);
			// ps.setString(1, numstarts+"%");
			ResultSet rs = ps.executeQuery();

			/*
			 * Devices d =(Devices) rs.getObject(0);
			 * System.out.println(d.getPhn());
			 */

			while (rs.next()) {
				Devices d = new Devices();
				String temp = rs.getString("RIGHT(PHONENUM , 10)");
				// RETRIEVING ID WITH ANOTHER QUERY
				ps2 = conn
						.prepareStatement("SELECT * FROM DEVICE WHERE PHONENUMBER LIKE '%"
								+ temp + "'");
				ResultSet rs2 = ps2.executeQuery();
				while (rs2.next()) {
					d.setId(rs2.getString("ID"));
					d.setPhn(rs2.getString("PHONENUMBER"));
				}

				ld.add(d);
				conn.commit();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String s = new Gson().toJson(ld);
		System.out.println("onserverside" + s);
		resp.getOutputStream().write(s.getBytes());
		try {
			if (conn != null && !(conn.isClosed()))
				conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
