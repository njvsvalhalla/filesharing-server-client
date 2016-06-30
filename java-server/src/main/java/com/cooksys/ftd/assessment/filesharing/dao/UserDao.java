package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cooksys.ftd.assessment.filesharing.db.User;

public class UserDao extends AbstractDao {
	String sql;
	PreparedStatement stmt;

	public int registerUser(User user) throws SQLException {
		sql = "INSERT INTO user (username, password) VALUES (?, ?)";
		stmt = this.getConn().prepareStatement(sql);
		int id;

		stmt.setString(1, user.getUsername());
		stmt.setString(2, user.getPasshash());
		stmt.executeUpdate();

		sql = "SELECT @@IDENTITY";
		stmt = this.getConn().prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			id = rs.getInt("@@IDENTITY");
			//user.setUserId(id);
			return id;
		}
		return -1;
	}

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
