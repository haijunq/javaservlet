/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package server;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.lang.*;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import server.error.*;
import server.handlers.connection.*;
import server.handlers.resource.ResourceRegistry;
import server.log.*;
import server.parse.*;
import util.*;

/**
 * Web Server
 * 
 * Created: 2002.05.11
 * 
 * @author Peter Brandt-Erichsen
 */
public class WebServer {
	private final String KEYSTORE = "./etc/haijun_key.store"; 		// keystore
	private final String KEYSTOREPASS = "password"; 				// keystore password
	private final String PROTOCOL = "SSLv3"; 						// protocol, ssl or tls
	private final String KEYMANAGERFAC = "SunX509"; 				// keymanager protocol
	private final String KEYSTORETYPE = "JKS"; 						// keystore type
	private ConnectionHandler connectionHandler = null; 			// reference to the connection handler
	private ResourceParser resourceParser = null; 					// reference to the HTTP request parser
	private ServerSocket sslServerSocket = null; 					// reference to the server socket
	private static int clientRequestCount = 0; 						// tracks the number of client connection requests
	private int port = -1; 											// server port number
	private int verbosity = 0; 										// stores the output verbosity level
	private boolean initialized = false; 							// ensures proper initialization

	/**
	 * Constructs the server
	 * 
	 * @throws Exception
	 */
	public WebServer(int port, int verbosity) throws Exception {
		this.setPort(port);
		this.setVerbosity(verbosity);
		init();
		acceptConnections();
	}

	/**
	 * Sets the port number for the server.
	 * 
	 * @param port
	 *            is the specified port number
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Sets the verbosity for the server.
	 * 
	 * @param verbosity
	 *            specifies the level of server output to the console
	 */
	public void setVerbosity(int verbosity) {
		this.verbosity = verbosity;
	}

	/**
	 * Initialize the web server and all subsystems.
	 */
	private void init() {

		try {
			// ensure a valid port number
			if (port < 0)
				port = Constants.DEFAULT_PORT;

			this.sslServerSocket = this.initSSLServerSocket(this.port);

			// store the web server identification footer in util.Constants
			Constants.setFooterIdentification("Haijun's Server");

			// instantiate the resource parser
			this.resourceParser = new ResourceParser();

			// server successfully initialized
			initialized = true;
			System.out.println("- server is running");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(this.getClass().getName()
					+ ".init(): failed initializaton, terminate execution.");
			System.exit(1);
		}
	}

	/**
	 * Listens for connection requests from the client.
	 * 
	 * @throws Exception
	 */
	private void acceptConnections() throws Exception {

		// ensure valid server initialization
		if (!initialized) {
			throw new IllegalStateException(this.getClass().getName()
							+ ".acceptConnections(): server not properly initialized, terminate execution.");
		}

		Socket sslClientSocket = null;

		// accept a socket, create a thread to run
		while (true) {
			sslClientSocket = this.sslServerSocket.accept();
			this.clientRequestCount++;
			this.connectionHandler = new ConnectionHandler(sslClientSocket, resourceParser);
			new Thread(this.connectionHandler).start();
		}
	}

	/**
	 * Initialize and return an SSLServerSocket.
	 * (referred to https://forums.oracle.com/thread/1533716) 
	 * @param port
	 * @return
	 * @throws Exception 
	 */
	private ServerSocket initSSLServerSocket(int port) throws Exception {

		// set the keystore and password
		System.setProperty("javax.net.ssl.keyStore", KEYSTORE);
		System.setProperty("javax.net.ssl.keyStorePassword", KEYSTOREPASS);
		KeyStore keyStore = KeyStore.getInstance(KEYSTORETYPE);
		keyStore.load(new FileInputStream(KEYSTORE), KEYSTOREPASS.toCharArray());

		// set the keymanagerfactory
		KeyManagerFactory keyManagerFactory = KeyManagerFactory
				.getInstance(KEYMANAGERFAC);
		keyManagerFactory.init(keyStore, KEYSTOREPASS.toCharArray());

		// set the ssl/tls protocol
		SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
		sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

		// let the factory initialize an SSLServerSocket
		SSLServerSocketFactory sslServerSocketFactory = sslContext
				.getServerSocketFactory();
		ServerSocket sslServerSocket = sslServerSocketFactory
				.createServerSocket(port);

		return sslServerSocket;
	}

	// driver
	public static void main(String args[]) throws Exception {

		// get the parameters from the command line
		int portNumber = Integer.parseInt(args[0]);
		int verbosity = Integer.parseInt(args[1]);

		// error check
		if (portNumber < 0)
			portNumber = Constants.DEFAULT_PORT;
		if (verbosity < 0)
			verbosity = 0;

		// set the default directory
		Constants.setDefaultDirectory();

		// create an object of webServer
		WebServer webServer = new WebServer(portNumber, verbosity);

		System.out.println();
		System.out.println("-- so far so good --");
		System.out.println();
	}

}// end class
