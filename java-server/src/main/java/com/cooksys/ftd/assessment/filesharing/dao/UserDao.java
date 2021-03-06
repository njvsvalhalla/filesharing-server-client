/**
 * UserDao.java
 * 
 * Data access object for the Files table
 * 
 * 
 */
package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cooksys.ftd.assessment.filesharing.db.User;

public class UserDao extends AbstractDao {
	String sql;
	PreparedStatement stmt;

	/**
	 * registerUser registers a User object into the database
	 * 
	 * @param User
	 *            object
	 * @return returns the user ID if it was successful, if not it will return 0 or 99.
	 * @throws SQLException
	 */
	public int registerUser(User user) throws SQLException {

		String sql = "SELECT username FROM user WHERE username = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setString(1, user.getUsername());
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return 0;
		} else {
			sql = "INSERT INTO user (username, password) VALUES (?, ?)";
			stmt = this.getConn().prepareStatement(sql);
			int id;

			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPasshash());
			stmt.executeUpdate();

			sql = "SELECT @@IDENTITY";
			stmt = this.getConn().prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				id = rs.getInt("@@IDENTITY");
				return id;
			}
		}
		return -1;
	}

	/**
	 * passwordHash retrieves the hash from the database, used for
	 * authentication
	 * 
	 * @param Username
	 *            string
	 * @return a password hash
	 * @throws SQLException
	 */
	public String passwordHash(String user) throws SQLException {
		String sql = "SELECT password FROM user WHERE username = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setString(1, user);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return rs.getString("password");
		}
		return "NULL";
	}
}
