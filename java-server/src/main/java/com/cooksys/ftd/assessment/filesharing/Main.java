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
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.server.Server;

public class Main {
	private static Logger log = LoggerFactory.getLogger(Main.class);
	private static String driver = "com.mysql.cj.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/filesharing?useSSL=false";
	private static String username = "root";
	private static String pass = "bondstone";

	private static int port = 667;

	public static void main(String[] args) throws ClassNotFoundException, JAXBException {
		
		
		log.debug("Program started. Attempting to connect to SQL");

		ExecutorService executor = Executors.newCachedThreadPool();

		try (Connection conn = DriverManager.getConnection(url, username, pass)) {
			log.debug("SQL connection successful");

			Class.forName(driver);
			Server server = new Server();
			server.setPort(port);
			server.setExecutor(executor);

			UserDao userDao = new UserDao();
			userDao.setConn(conn);
			server.setUserDao(userDao);

			FilesDao filesDao = new FilesDao();
			filesDao.setConn(conn);
			server.setFilesDao(filesDao);

			Future<?> serverFuture = executor.submit(server);
			serverFuture.get();

		} catch (SQLException | InterruptedException | ExecutionException e) {
			log.error("We have encountered an error trying to start the server. {}", e);
		} finally {
			executor.shutdown();
		}
	}

}
