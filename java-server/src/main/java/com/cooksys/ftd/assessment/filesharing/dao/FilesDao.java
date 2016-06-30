package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cooksys.ftd.assessment.filesharing.db.Files;

public class FilesDao extends AbstractDao {

	String sql;
	PreparedStatement stmt;
	int id;

	public void registerFile(Files file) {
		// TODO
	}

	public void sendFile(Files file) {
		// TODO
	}

	public ArrayList<String[]> listFiles(String userName) throws SQLException {
		ArrayList<String[]> files = new ArrayList<String[]>();
		String sql = "SELECT userid FROM user WHERE username = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setString(1, userName);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			id = rs.getInt("userid");
		}

		sql = "SELECT f.user_file_number, f.absolute_path FROM files f WHERE f.userid = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setInt(1, id);
		rs = stmt.executeQuery();
		while (rs.next()) {
			String[] temp = new String[2];
			temp[0] = "" + rs.getInt("f.user_file_number");
			temp[1] = rs.getString("f.absolute_path");
			files.add(temp);
		}
		return files;
	}

}
