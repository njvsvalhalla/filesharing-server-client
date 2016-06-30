package com.cooksys.ftd.assessment.filesharing.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.dao.FilesDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;

public class ClientHandler implements Runnable {

	private BufferedReader reader;
	private PrintWriter writer;

	private UserDao userDao;
	private FilesDao filesDao;

	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	@Override
	public void run() {
		while (true) {
			try {
				String echo = this.reader.readLine();
				log.debug("Read line : {}", echo);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public FilesDao getFilesDao() {
		return filesDao;
	}

	public void setFilesDao(FilesDao filesDao) {
		this.filesDao = filesDao;
	}

}
