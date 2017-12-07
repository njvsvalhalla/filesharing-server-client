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
	
	public ArrayList<String> viewKeywords(int fileid) throws SQLException {
		ArrayList<String> keywords = new ArrayList<String>();
		String sql = "SELECT keyword FROM keywords WHERE fileid = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setInt(1, fileid);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			keywords.add(rs.getString("keyword"));
		}
		return keywords;
	}
	
	public ArrayList<Integer> searchKeywords(String keyword) throws SQLException {
		ArrayList<Integer> keywords = new ArrayList<Integer>();
		String sql = "SELECT f.fileid, f.absolute_path FROM files AS f INNER JOIN keywords AS k ON k.fileid = f.fileid WHERE k.keyword = ?";
		stmt = this.getConn().prepareStatement(sql);
		stmt.setString(1, keyword);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			keywords.add(rs.getInt("fileid"));
		}
		return keywords;
	}
}
