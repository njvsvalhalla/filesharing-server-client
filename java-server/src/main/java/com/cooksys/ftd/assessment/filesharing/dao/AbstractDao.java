package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.Connection;

/**
 *  AbstractDao.java
 *
 *	Sets a database connection for use for the other DAO's
 *
 */

public abstract class AbstractDao {
	
	private Connection conn;

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

}
