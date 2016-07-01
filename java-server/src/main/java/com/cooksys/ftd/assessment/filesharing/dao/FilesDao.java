package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

import com.cooksys.ftd.assessment.filesharing.db.Files;

public class FilesDao extends AbstractDao {

	String sql;
	PreparedStatement stmt;
	int id;

	public void registerFile(Files file) throws SQLException {
		String sql = "SELECT userid FROM user WHERE username = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setString(1, file.getusername());
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			id = rs.getInt("userid");
		}

		sql = "INSERT INTO files (userid, absolute_path, file_data) VALUES (?,?,?)";
		stmt = this.getConn().prepareStatement(sql);
		byte[] buffer = Base64.getDecoder().decode(file.getByteArray());
				//Base64().getDecoder().decode(file.getByteArray());
		stmt.setInt(1, id);
		stmt.setString(2, file.getAbsolutePath());
		stmt.setBytes(3, buffer);
		int x = stmt.executeUpdate();
		System.out.println(x);
	}

	public Files sendFile(int fileId) throws SQLException {
		Files f = new Files();
		String sql = "SELECT absolute_path, file_data FROM files WHERE fileid = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setInt(1, fileId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			f.setAbsolutePath(rs.getString("absolute_path"));
			byte[] buffer = rs.getBytes("file_data");
			String tob64 = Base64.getEncoder().encodeToString(buffer);
			f.setByteArray(tob64);
		}
		return f;
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
