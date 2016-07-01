package com.cooksys.ftd.assessment.filesharing.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.api.GetFileFromClient;
import com.cooksys.ftd.assessment.filesharing.api.RegisterUser;
import com.cooksys.ftd.assessment.filesharing.api.SendFileToClient;
import com.cooksys.ftd.assessment.filesharing.dao.FilesDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.db.User;

public class ClientHandler implements Runnable {

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
				log.debug("input {}", echo);
				if (echo.startsWith("{\"user\":")) {
					log.debug("User requesting to register");
					User u = RegisterUser.unmarshall(echo);
					int reggedUser = userDao.registerUser(u);
					if (reggedUser == 1) {
						this.writer.write(
								"Register failed due to a SQL error. If you are having troubles, plese contact the admin");
						log.info("User registration failed");
						this.writer.flush();
					} else {
						this.writer.write("You successfully registered! You can now log in");
						this.writer.flush();
						log.debug("Return register user result: {}", userDao.registerUser(u));
					}
				} else if (echo.startsWith("passhashget ")) {
					String[] a = echo.split(" ");
					this.writer.write(userDao.passwordHash(a[1]));
					log.debug("{}", userDao.passwordHash(a[1]));
					this.writer.flush();
				} else if (echo.startsWith("getlist ")) {
					String[] a = echo.split(" ");
					ArrayList<String[]> files = new ArrayList<String[]>();
					files = filesDao.listFiles(a[1]);
					for (int i = 0; i < files.size(); i++) {
						String[] x = files.get(i);
						this.writer.write(x[0] + " - " + x[1] + "\n");
						this.writer.flush();
					}
				} else if (echo.startsWith("{\"files\":")) {
					int res = filesDao.registerFile(GetFileFromClient.unmarshall(echo));
					log.info("User is uploading a file! ");
					if (res == -1) {
						log.info("File upload failed.");
						this.writer.write("Something went wrong registering the file. Please try again later");
						this.writer.flush();
					} else {
						log.info("File successful with the id {}", res);
						this.writer.write("Your file successfully registered under the following id: " + res + "!");
						this.writer.flush();
					}
				} else if (echo.startsWith("getfile ")) {
					String[] a = echo.split(" ");
					log.info("User {} requesting file to download file with id {}", a[2], a[1]);
					if (filesDao.checkOwner(a[2], Integer.parseInt((a[1]))) == 0) {
						this.writer.write("You aren't the owner to that file!");
						this.writer.flush();
					} else {
						this.writer.write(SendFileToClient.marshall(filesDao.sendFile(Integer.parseInt((a[1])))));
						this.writer.flush();
					}
				}
			} catch (IOException e) {

				// log.error("There was an issue with the connection {}", e);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
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
