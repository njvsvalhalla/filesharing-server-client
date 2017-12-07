package com.cooksys.ftd.assessment.filesharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.dao.FilesDao;
import com.cooksys.ftd.assessment.filesharing.dao.KeywordsDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.server.Server;

public class Main {
	//We're gonna initiate our logger and mysql here.
	private static Logger log = LoggerFactory.getLogger(Main.class);
	private static String driver = "com.mysql.cj.jdbc.Driver";
	//I had this weird connection error where I had to add a timezone to the end of it, may be my OS or mariadb config
	private static String url = "jdbc:mysql://localhost:3306/filesharing?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static String username = "root";
	private static String pass = "root";
	
	//Depending on your system and permissions you may have to change this
	private static int port = 1024;

	public static void main(String[] args) throws ClassNotFoundException, JAXBException {
		
		//We will test the connection to our sql server, and if not throw an exception
		log.debug("Program started. Attempting to connect to SQL");

		ExecutorService executor = Executors.newCachedThreadPool();

		try (Connection conn = DriverManager.getConnection(url, username, pass)) {
			log.debug("SQL connection successful");
			
			//Once we initiate the sql connection, let's start listening on the server!
			Class.forName(driver);
			Server server = new Server();
			server.setPort(port);
			server.setExecutor(executor);

			//Let's start up our userDao and filesDao objects
			UserDao userDao = new UserDao();
			userDao.setConn(conn);
			server.setUserDao(userDao);

			FilesDao filesDao = new FilesDao();
			filesDao.setConn(conn);
			server.setFilesDao(filesDao);
			
			KeywordsDao keywordsDao = new KeywordsDao();
			keywordsDao.setConn(conn);
			server.setKeywordsDao(keywordsDao);
			
			Future<?> serverFuture = executor.submit(server);
			serverFuture.get();

		} catch (SQLException | InterruptedException | ExecutionException e) {
			log.error("We have encountered an error trying to start the server. {}", e);
		} finally {
			executor.shutdown();
		}
	}

}

