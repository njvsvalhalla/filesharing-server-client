package com.cooksys.ftd.assessment.filesharing.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.api.RegisterUser;
import com.cooksys.ftd.assessment.filesharing.dao.FilesDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.db.User;

public class ClientHandler extends RegisterUser implements Runnable {

	private BufferedReader reader;
	private PrintWriter writer;

	private UserDao userDao;
	private FilesDao filesDao;

	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	@Override
	public void run() {
		log.debug("Started a connection");
		while (true) {
			try {

				String echo = this.reader.readLine();
				if (echo.startsWith("{\"user\":")) {
					User u = unmarshall(echo);
					log.debug("Return register user result: {}", userDao.registerUser(u));
				}
				if (echo.startsWith("passhashget ")) {
					String[] a = echo.split(" ");
					this.writer.write(userDao.passwordHash(a[1]));
					log.debug("{}", userDao.passwordHash(a[1]));
					this.writer.flush();
				}
				if (echo.startsWith("getlist ")) {
					String[] a = echo.split(" ");
					ArrayList<String[]> files = new ArrayList<String[]>();
					files = filesDao.listFiles(a[1]);
					for (int i = 0; i < files.size(); i++) {
						String[] x = files.get(i);
						this.writer.write(x[0] + " - " + x[1]);
						this.writer.flush();
					}
				}
			} catch (IOException e) {
				log.error("There was an issue with the connection {}", e);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JAXBException e) {
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
