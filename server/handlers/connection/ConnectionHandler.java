/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package server.handlers.connection;

import java.io.*;
import java.net.*;

import server.error.*;
import server.parse.*;
import util.*;

/**
 * Developing a Simple Web Server.
 * 
 * Connection Handler Handles client connection requests.
 * 
 * Created: 2002.05.18
 * 
 * @author Peter Brandt-Erichsen
 */
public class ConnectionHandler implements Runnable {
	ResourceParser parser = null;
	Socket clientSocket = null; 
	
	/**
	 * Constructs the connection handler
	 * 
	 * @param parser
	 *            is the reference to the Resource Parser
	 */
	public ConnectionHandler(Socket clientSocket, ResourceParser parser) {
		this.clientSocket = clientSocket;
		this.parser = parser;
	}

	/**
	 * Handles a connection request from the client.
	 * 
	 * @param clientSocket
	 *            is a reference to the client socket.
	 * @throws Exception
	 */
	public void handleConnection(Socket clientSocket) throws Exception {

		// sanity checks
		if (clientSocket == null) {
			throw new IllegalArgumentException("the client socket is null");
		}

		this.parser.parse(clientSocket);
	}
	
	/**
	 * Implement the run() method.
	 */
	@Override
	public void run() {
		try {
			this.handleConnection(this.clientSocket);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				this.clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}// end class
