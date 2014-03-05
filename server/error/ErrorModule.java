/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package server.error;

import java.io.*;
import java.net.*;

import server.log.LogManager;
import util.*;

/**
 * Error Module - contains logic for outputting html-friendly error messages.
 * 
 * The singleton design pattern is implemented to ensure that only one instance
 * of this class is instantiated.
 * 
 * Created: 2002.05.18
 * 
 * @author Peter Brandt-Erichsen
 */
public class ErrorModule {

	// singleton reference to this class
	private static ErrorModule singleton = null;

	// constructor is private to guarantee a singleton instance
	private ErrorModule() {
	}

	/**
	 * Returns a reference to this singleton class
	 */
	public static ErrorModule getReference() {
		if (singleton == null)
			singleton = new ErrorModule();
		return singleton;
	}

	/**
	 * Outputs a friendly HTTP 404 Error to the client.
	 * @throws IOException 
	 */
	public void fileNotFoundError(Socket clientSocket, String request, Exception e) throws IOException {
		PrintWriter output = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream(), Constants.DEFAULT_BUFFER_SIZE));
		responseHeader(output, Utilities.getResponseMessage(404));
		responseBody(output, Utilities.getResponseMessage(404));
//		exceptionStackTrace(output, e);
		versionFooter(output);
		pageTerminator(output);
		output.flush();
		output.close();
		
		// log the 404 error
		StringBuffer logEntry = LogManager.getReference().constructLogEntry(clientSocket, request);
		logEntry.append("404 " + String.valueOf(Constants.FILE_SIZE_404 + request.length()));
		LogManager.getReference().log(logEntry.toString());
	}

	/**
	 * Outputs a friendly HTTP 500 Error to the client.
	 * @throws IOException 
	 */
	public void internalServerError(Socket clientSocket, Exception e) throws IOException {
		PrintWriter output = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream(), Constants.DEFAULT_BUFFER_SIZE));
		responseHeader(output, Utilities.getResponseMessage(500));
		responseBody(output, Utilities.getResponseMessage(500));
//		exceptionStackTrace(output, e);
//		versionFooter(output);
		pageTerminator(output);
		output.flush();
		output.close();
	}

	// Outputs the response header.
	//
	private void responseHeader(PrintWriter output, String statusCode) {
		output.println(Constants.HTTP_VERSION + " " + statusCode);
		output.println("Content-Type: text/html");
		output.println();
	}

	// Outputs the response body.
	//
	private void responseBody(PrintWriter output, String statusCode) {
		output.println("<html>");
		output.println("<head><title>" + statusCode + "</title></head>");
		output.println("<body>");
		output.println("<h1>" + statusCode + "</h1>");
	}

	// Outputs the specified exception's stack trace to the client.
	//
	private void exceptionStackTrace(PrintWriter output, Exception e) {
		output.println("<br>");
		output.println("<strong>Exception Stack Trace</strong>");
		output.println("<br>");
		output.println(Utilities.getStackTraceAsStringForHTML(e));
	}

	// Outputs the web server's version footer.
	//
	private void versionFooter(PrintWriter output) {
		output.println("<hr width=100% align=left size=1>");
		output.println(Utilities.versionFooter());
	}

	// Terminates the html listing.
	//
	private void pageTerminator(PrintWriter output) {
		output.println("</body>");
		output.println("</html>");
	}

}// end class
