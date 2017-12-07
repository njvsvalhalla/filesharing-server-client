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
import com.cooksys.ftd.assessment.filesharing.api.ParseMessage;
import com.cooksys.ftd.assessment.filesharing.api.RegisterUser;
import com.cooksys.ftd.assessment.filesharing.api.SendFileToClient;
import com.cooksys.ftd.assessment.filesharing.dao.FilesDao;
import com.cooksys.ftd.assessment.filesharing.dao.KeywordsDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.db.Keywords;
import com.cooksys.ftd.assessment.filesharing.db.Message;
import com.cooksys.ftd.assessment.filesharing.db.User;

public class ClientHandler implements Runnable {

	private BufferedReader reader;
	private PrintWriter writer;

	private UserDao userDao;
	private FilesDao filesDao;
	private KeywordsDao keywordsDao;

	private Logger log = LoggerFactory.getLogger(ClientHandler.class);
	private Boolean bool = true;

	@Override
	public void run() {
		log.info("Started a connection");
		while (bool) {
			try {
				String echo = this.reader.readLine();
				if (echo != null) {
					if (echo.startsWith("{\"user\":")) {
						log.info("User requesting to register");
						User u = RegisterUser.unmarshall(echo);
						int reggedUser = userDao.registerUser(u);
						if (reggedUser == -1) {
							this.writer.write(
									"Register failed due to a SQL error. If you are having troubles, plese contact the admin");
							log.info("User registration failed");
							this.writer.flush();
						} else if (reggedUser == 0) {
							this.writer.write(
									"This username has already been taken. Please try again.");
							log.info("User registration failed. Username taken");
							this.writer.flush();
						} else {
							this.writer.write("You successfully registered! You can now log in");
							this.writer.flush();
							log.info("Return register user result: ID {}", reggedUser);
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
					} else {
						Message m = ParseMessage.unmarshall(echo);
						if (m.getCommand().compareTo("gethash") == 0) {
							log.info("Sending login hash to client.");
							this.writer.write(userDao.passwordHash(m.getUsername()));
							this.writer.flush();
						} else if (m.getCommand().compareTo("getfiles") == 0) {
							ArrayList<String[]> files = new ArrayList<String[]>();
							files = filesDao.listFiles(m.getUsername());
							for (int i = 0; i < files.size(); i++) {
								String[] x = files.get(i);
								this.writer.write(x[0] + " - " + x[1] + " - " + x[2] + "\n");
								this.writer.flush();
							}
						} else if (m.getCommand().compareTo("viewkeyword") == 0) {
							ArrayList<String> keywords = new ArrayList<String>();
							keywords = keywordsDao.viewKeywords(m.getFileid());
							for (int i = 0; i < keywords.size(); i++) {
								this.writer.write(keywords.get(i) + "\n");
								this.writer.flush();
							}
						} else if (m.getCommand().compareTo("searchkeyword") == 0) {
							ArrayList<Integer> keywords = new ArrayList<Integer>();
							keywords = keywordsDao.searchKeywords(m.getKeyword());
							for (int i = 0; i < keywords.size(); i++) {
								this.writer.write(keywords.get(i) + "\n");
								this.writer.flush();
							}
						} else if (m.getCommand().compareTo("addkeyword") == 0) {
							log.info("User {} is requesting to add keyword to {}", m.getUsername(), m.getFileid());
							if (filesDao.checkOwner(m.getUsername(), m.getFileid()) == -1) {
								this.writer.write("You aren't the owner to that file!");
								this.writer.flush();
							} else {
								Keywords k = new Keywords();
								k.setFileid(m.getFileid());
								k.setKeyword(m.getKeyword());
								int keywordId = keywordsDao.addKeyword(k);
								if (keywordId == -1) {
									this.writer.write(
											"Add keyword failed due to a SQL error. If you are having troubles, plese contact the admin");
									log.info("Keyword registration failed");
									this.writer.flush();
								} else if (keywordId == 0) {
									this.writer.write("Add keyword failed due to duplicate keyword on file.");
									log.info("Duplicate keyword on file.");
									this.writer.flush();
								} else {
									this.writer.write("You successfully added the keyword.");
									this.writer.flush();
									log.info("Return add keword result: {}", keywordId);
								}
							}

						} else if (m.getCommand().compareTo("download") == 0) {
							log.info("User {} requesting file to download file with id {}", m.getUsername(),
									m.getFileid());
							if (filesDao.checkOwner(m.getUsername(), m.getFileid()) == 0) {
								this.writer.write("You aren't the owner to that file!");
								this.writer.flush();
							} else {
								this.writer.write(SendFileToClient.marshall(filesDao.sendFile(m.getFileid())));
								this.writer.flush();
							}
						} else {
							log.info("{}", m.getCommand());
						}
					}
				}
			} catch (IOException e) {
				bool = false;
				log.error("There was an issue with the File I/O {}", e);
			} catch (SQLException e) {
				bool = false;
				log.error("There was an issue with the SQL commands {}", e);
			} catch (JAXBException e) {
				bool = false;
				log.error("There was a JAXB Exception {}", e);
			}
		}

	}

	public KeywordsDao getKeywordsDao() {
		return keywordsDao;
	}

	public void setKeywordsDao(KeywordsDao keywordsDao) {
		this.keywordsDao = keywordsDao;
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
