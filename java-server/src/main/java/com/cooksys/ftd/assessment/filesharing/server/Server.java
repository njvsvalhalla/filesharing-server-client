package com.cooksys.ftd.assessment.filesharing.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.dao.FilesDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;

public class Server implements Runnable {

	private Logger log = LoggerFactory.getLogger(Server.class);

	private ExecutorService executor;
	private ServerSocket serverSocket;

	private int port;

	private UserDao userDao;
	private FilesDao filesDao;

	@Override
	public void run() {
		log.debug("Server run started");
		try (ServerSocket serverSocket = new ServerSocket(this.port)) {
			log.info("Server started on port {}", this.port);
			while (true) {
				Socket socket = serverSocket.accept();
				ClientHandler handler = this.createClientHandler(socket);
				this.executor.execute(handler);
			}
		} catch (IOException e) {
			this.log.error(
					"The server encountered a fatal error while listening for more connections. Shutting down after error log.",
					e);
		}
	}

	public ClientHandler createClientHandler(Socket socket) throws IOException {
		ClientHandler handler = new ClientHandler();

		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		handler.setReader(reader);
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		handler.setWriter(writer);
		
		handler.setFilesDao(this.filesDao);
		handler.setUserDao(this.userDao);

		return handler;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
