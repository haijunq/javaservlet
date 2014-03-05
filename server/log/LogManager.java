/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package server.log;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import server.error.ErrorModule;
import util.Constants;

/**
 * Log Manager... create a singleton to share access to a single log file 
 *
 * Created: 2002.05.10
 * @author Peter Brandt-Erichsen
 */
public class LogManager {
	// singleton reference to this class
	private static LogManager singleton = null;
	private static SimpleDateFormat dateFormat = null;

	// constructor is private to guarantee a singleton instance
	private LogManager() {
		dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
		File logFileDir = new File(Constants.LOG_DIRECTORY);
		if (!logFileDir.exists()) {
			logFileDir.mkdir();
		}
	}

	/**
	 * Returns a reference to this singleton class
	 */
	public static LogManager getReference() {
		if (singleton == null)
			singleton = new LogManager();
		return singleton;
	}

	/**
	 * Log an entry to the log file, this method is synchronized.
	 * @param logEntry the String that needs to be logged.
	 * @throws IOException
	 */
	public synchronized static void log(String logEntry) throws IOException {
		// if log file not exists, create a new one
		File logFile = new File(Constants.getLogFileName()); 
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		
		// log the entry
		PrintWriter logger = new PrintWriter(new FileOutputStream(logFile, true));
		logger.write(logEntry + "\n");
		logger.flush();
		logger.close();
	}
	
	/**
	 * Construct the first part of a Log entry. 
	 * @param clientSocket is the clientSocket
	 * @param request is the request string
	 * @return the part of the log entry.
	 */
	public StringBuffer constructLogEntry(Socket clientSocket, String request) {
		StringBuffer logEntry = new StringBuffer(clientSocket.getInetAddress().getHostAddress());
		logEntry.append(" - - [" + dateFormat.format(new Date()) + "] ");
		logEntry.append("\"" + request + "\" ");
		return logEntry;
	}
	
}//end class
