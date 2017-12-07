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
					// User Registration function
					if (echo.startsWith("{\"user\":")) {
						log.info("User requesting to register");
						User u = RegisterUser.unmarshall(echo);
						int reggedUser = userDao.registerUser(u);
						switch (reggedUser) {
						case -1:
							this.writer.write(
									"Register failed due to a SQL error. If you are having troubles, plese contact the admin");
							log.info("User registration failed");
							this.writer.flush();
							break;
						case 0:
							this.writer.write("This username has already been taken. Please try again.");
							log.info("User registration failed. Username taken");
							this.writer.flush();
							break;
						default:
							this.writer.write("You successfully registered! You can now log in");
							this.writer.flush();
							log.info("Return register user result: ID {}", reggedUser);
							break;
						}
						// File uploading function
					} else if (echo.startsWith("{\"files\":")) {
						int res = filesDao.registerFile(GetFileFromClient.unmarshall(echo));
						log.info("User is uploading a file! ");
						switch (res) {
						case -1:
							log.info("File upload failed.");
							this.writer.write("Something went wrong registering the file. Please try again later");
							this.writer.flush();
							break;
						default:
							log.info("File successful with the id {}", res);
							this.writer.write("Your file successfully registered under the following id: " + res + "!");
							this.writer.flush();
							break;
						}
						// If neither of those, lets parse our messages
					} else {
						Message m = ParseMessage.unmarshall(echo);
						switch (m.getCommand()) {

						// gethash - sends the login hash to the client
						case "gethash":
							log.info("Sending login hash to client.");
							this.writer.write(userDao.passwordHash(m.getUsername()));
							this.writer.flush();
							break;

						// getfiles - sends files registered to user. Also checks if there are keywords
						case "getfiles":
							ArrayList<String[]> files = new ArrayList<String[]>();
							files = filesDao.listFiles(m.getUsername());
							for (int i = 0; i < files.size(); i++) {
								String[] x = files.get(i);
								this.writer.write(x[0] + " - " + x[1] + " - " + x[2] + "\n");
								this.writer.flush();
							}
							break;

						// viewkeyword - Gets the list of keywords for a file, if we own it!
						case "viewkeyword":
							if (filesDao.checkOwner(m.getUsername(), m.getFileid()) == -1) {
								this.writer.write("You aren't the owner to that file!");
								this.writer.flush();
							} else {
								ArrayList<String> keywords = new ArrayList<String>();
								keywords = keywordsDao.viewKeywords(m.getFileid());
								for (int i = 0; i < keywords.size(); i++) {
									this.writer.write(keywords.get(i) + "\n");
									this.writer.flush();
								}
							}
							break;

						// searchkeyword - searches for keywords attached to specific files

						case "searchkeyword":
							ArrayList<Integer> keys = new ArrayList<Integer>();
							keys = keywordsDao.searchKeywords(m.getKeyword(), m.getUsername());
							for (int i = 0; i < keys.size(); i++) {
								this.writer.write(keys.get(i) + "\n");
								this.writer.flush();
							}
							break;

						// addkeyword - adds a keyword to file
						case "addkeyword":
							log.info("User {} is requesting to add keyword to {}", m.getUsername(), m.getFileid());
							// Check if we have ownership rights to file first
							if (filesDao.checkOwner(m.getUsername(), m.getFileid()) == -1) {
								this.writer.write("You aren't the owner to that file!");
								this.writer.flush();
							} else {
								// If we're good, create the keywords object, and insert it into the db
								Keywords k = new Keywords();
								k.setFileid(m.getFileid());
								k.setKeyword(m.getKeyword());
								int keywordId = keywordsDao.addKeyword(k);
								switch (keywordId) {
								case -1:
									this.writer.write(
											"Add keyword failed due to a SQL error. If you are having troubles, plese contact the admin");
									log.info("Keyword registration failed");
									this.writer.flush();
									break;
								case 0:
									this.writer.write("Add keyword failed due to duplicate keyword on file.");
									log.info("Duplicate keyword on file.");
									this.writer.flush();
									break;
								default:
									this.writer.write("You successfully added the keyword.");
									this.writer.flush();
									log.info("Return add keword result: {}", keywordId);
									break;

								}
							}
							break;

						// download - sends the file to client
						case "download":
							log.info("User {} requesting file to download file with id {}", m.getUsername(),
									m.getFileid());
							if (filesDao.checkOwner(m.getUsername(), m.getFileid()) == -1) {
								this.writer.write("You aren't the owner to that file!");
								this.writer.flush();
							} else {
								this.writer.write(SendFileToClient.marshall(filesDao.sendFile(m.getFileid())));
								this.writer.flush();
							}
							break;
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
