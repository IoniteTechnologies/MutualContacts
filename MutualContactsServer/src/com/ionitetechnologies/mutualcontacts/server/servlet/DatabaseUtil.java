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
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

	static public Connection getConnection() {

		String url = "jdbc:mysql://xxxxinstance.xxxxxxx.us-east-1.rds.amazonaws.com:3306/";
		String userName = "xxxxxxxxx";
		String password = "xxxxxxxxx";
		String dbName = "xxxxxxxx";
		String driver = "com.mysql.jdbc.Driver";

		Connection connection = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url + dbName, userName,
					password);
			connection.setAutoCommit(false);
			if (connection != null) {
				System.out.println("connected to database.");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}
}
