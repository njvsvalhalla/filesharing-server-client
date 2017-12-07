package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cooksys.ftd.assessment.filesharing.db.Keywords;

public class KeywordsDao extends AbstractDao {

	String sql;
	int id;
	PreparedStatement stmt;

	/**
	 * addKeyword: Takes a keyword object and attempts to insert it into the
	 * keywords database
	 * 
	 * @param Keywords
	 *            object
	 * @return integer
	 * @throws SQLException
	 */

	public int addKeyword(Keywords keyword) throws SQLException {

		String sql = "SELECT keyword FROM keywords WHERE fileid = ? AND keyword = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setInt(1, keyword.getFileid());
		stmt.setString(2, keyword.getKeyword());
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return 0;
		} else {
			// Inserts the keyword into the database
			sql = "INSERT INTO keywords (keyword, fileid) VALUES (?,?)";
			System.out.println(sql);
			stmt = this.getConn().prepareStatement(sql);
			stmt.setString(1, keyword.getKeyword());
			stmt.setInt(2, keyword.getFileid());
			stmt.executeUpdate();

			// get the new keyword id
			sql = "SELECT @@IDENTITY";
			stmt = this.getConn().prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				id = rs.getInt("@@IDENTITY");
				return id;
			}
			return -1;
		}
	}

	/**
	 * viewKeywords: Takes a fileid to check what keywords are set onto the file
	 * 
	 * @param string
	 * 
	 * @param fileid
	 *            Integer
	 * @return Array list of strings of all of keywords attached to file
	 * @throws SQLException
	 */
	public ArrayList<String> viewKeywords(int fileId) throws SQLException {
		ArrayList<String> keywords = new ArrayList<String>();

		String sql = "SELECT keyword FROM keywords WHERE fileid = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setInt(1, fileId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			keywords.add(rs.getString("keyword"));
		}
		return keywords;
	}

	/**
	 * searchKeywords: Takes a string to check against the database to see what
	 * files have specific keyword
	 * 
	 * @param keyword
	 *            String
	 * @param userName
	 *            String
	 * @return Array list of integers of all of files that have the keyword from
	 *         input
	 * @throws SQLException
	 */
	public ArrayList<String[]> searchKeywords(String keyword, String userName) throws SQLException {
		ArrayList<String[]> keywords = new ArrayList<String[]>();
		String sql = "SELECT f.fileid, f.absolute_path FROM files AS f INNER JOIN keywords AS k ON k.fileid = f.fileid INNER JOIN user AS u ON u.userid = (SELECT user.userid FROM user WHERE user.username = ?) WHERE k.keyword = ? AND f.userid = u.userid";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setString(1, userName);
		stmt.setString(2, keyword);
		System.out.println(stmt.toString());
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			String[] temp = new String[2];
			temp[0] = "" + rs.getInt("f.fileid");
			temp[1] = rs.getString("f.absolute_path");
			keywords.add(temp);
		}
		return keywords;
	}
}
