/**
 * FilesDao.java
 * 
 * Data access object for the Files table
 * 
 * 
 */

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

	/**
	 * registerFile: Takes a Files object and from that, registers it with the
	 * database + converts the base64 string to a byte array, which is stored in
	 * the blob
	 * 
	 * @param Files
	 *            object
	 * @throws SQLException
	 */
	public int registerFile(Files file) throws SQLException {
		// Retrieves our userId to store properly
		String sql = "SELECT userid FROM user WHERE username = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setString(1, file.getusername());
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			id = rs.getInt("userid");
		}
		// Inserts the file into the database
		sql = "INSERT INTO files (userid, absolute_path, file_data) VALUES (?,?,?)";
		stmt = this.getConn().prepareStatement(sql);
		byte[] buffer = Base64.getDecoder().decode(file.getByteArray());
		stmt.setInt(1, id);
		stmt.setString(2, file.getAbsolutePath());
		stmt.setBytes(3, buffer);
		stmt.executeUpdate();
		sql = "SELECT @@IDENTITY";
		stmt = this.getConn().prepareStatement(sql);
		rs = stmt.executeQuery();
		if (rs.next()) {
			id = rs.getInt("@@IDENTITY");
			return id;
		}
		return -1;
	}

	/**
	 * This takes a fileId, gets the path name and file data from the database
	 * and creates a File object, ready to be sent back to the user
	 * 
	 * @param fileId
	 * @return Files object
	 * @throws SQLException
	 */

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
	/**
	 *  checkOwner checks if the username owns the file at file ID
	 *  
	 * @param userName String
	 * @param fileId int
	 * @return if > 0, they own it, if 0 they don't
	 * @throws SQLException
	 */
	public int checkOwner(String userName, int fileId) throws SQLException {
		
		String sql = "SELECT userid FROM user WHERE username = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setString(1, userName);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			id = rs.getInt("userid");
		}
		
		sql = "SELECT f.fileid FROM files f WHERE f.userid = ? AND f.fileid = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setInt(1, id);
		stmt.setInt(2, fileId);
		rs = stmt.executeQuery();
		if(rs.next()) {
			return rs.getRow();
		}
		return 0;
	}

	/**
	 * Returns a list of files registered to a specific username
	 * 
	 * @param userName
	 *            strrng
	 * @return An array list of an array of Strings
	 * @throws SQLException
	 */
	public ArrayList<String[]> listFiles(String userName) throws SQLException {
		ArrayList<String[]> files = new ArrayList<String[]>();
		String sql = "SELECT userid FROM user WHERE username = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setString(1, userName);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			id = rs.getInt("userid");
		}
		sql = "SELECT f.fileid, f.absolute_path FROM files f WHERE f.userid = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setInt(1, id);
		rs = stmt.executeQuery();
		while (rs.next()) {
			String[] temp = new String[2];
			temp[0] = "" + rs.getInt("f.fileid");
			temp[1] = rs.getString("f.absolute_path");
			files.add(temp);
		}
		return files;
	}

}
