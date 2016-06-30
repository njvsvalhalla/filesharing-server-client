package com.cooksys.ftd.assessment.filesharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static Logger log = LoggerFactory.getLogger(Main.class);
	private static String driver = "com.mysql.cj.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/filesharing?useSSL=false";
	private static String username = "root";
	private static String pass = "bondstone";

	public static void main(String[] args) throws SQLException {
		log.debug("Program started. Attempting to connect to SQL");
		
		try (Connection conn = DriverManager.getConnection(url, username, pass)) {
			log.debug("Connection successful");
		}
	}

}
