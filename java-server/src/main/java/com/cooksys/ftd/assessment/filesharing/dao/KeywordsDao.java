package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cooksys.ftd.assessment.filesharing.db.Keywords;

public class KeywordsDao extends AbstractDao {

	String sql;
	int id;
	FilesDao filesDao;
	PreparedStatement stmt;
	
	/**
	 * addKeyword: Takes a keyword object and attempts to insert it into the keywords
	 * database
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
	 * searchKeywords: Takes a string to check against the database to see what files have specific keyword
	 * 
	 * @param keyword
	 *            String 
	 * @param userName
	 *            String
	 * @return Array list of integers of all of files that have the keyword from input
	 * @throws SQLException
	 */
	public ArrayList<Integer> searchKeywords(String keyword, String userName) throws SQLException {
		ArrayList<Integer> keywords = new ArrayList<Integer>();
		String sql = "SELECT f.fileid, f.absolute_path FROM files AS f INNER JOIN keywords AS k ON k.fileid = f.fileid WHERE k.keyword = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setString(1, keyword);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			// We wanna make sure the user owns that file!
			if (filesDao.checkOwner(userName, rs.getInt("fileid")) > -1) {
				keywords.add(rs.getInt("fileid"));
			}
		}
		return keywords;
	}
}
