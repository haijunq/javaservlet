/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package server.parse;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import HTTPClient.Request;
import server.error.*;
import server.handlers.resource.*;
import server.log.LogManager;
import server.parse.*;
import util.*;

/**
 * Resource Parser Parses an HTTP Request Header for its resource identifier.
 * 
 * Typical HTTP-Request Header format: GET /index.html HTTP/1.0 Accept:
 * image/gif, image/x-xbitmap, image/jpeg, image/pjpeg Referer:
 * http://bucky.dyndns.org/ Accept-Language: en-ca User-Agent: Mozilla/4.0
 * (compatible; MSIE 5.01; Windows NT 5.0) Host: bucky.dyndns.org Connection:
 * Keep-Alive
 * 
 * Target: GET /index.html HTTP/1.0
 * 
 * Extract: /index.html
 * 
 * Created: 2002.05.18
 * 
 * @author Peter Brandt-Erichsen
 */
public class ResourceParser {
	// max numbers of readLines
	private final int MAX_READ_LINES = 3; 

	public ResourceParser() {
	}

	/**
	 * Parses the HTTP header, see class level documentation for a full
	 * description.
	 * 
	 * @param clientSocket
	 *            is a reference to the client socket.
	 */
	public void parse(Socket clientSocket) throws Exception {

		// input stream
		BufferedReader in = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		if (in == null) 
			return;
		
		// retrieve the HTTP request header from the input stream
		String request = null;
		int readCount = 0; 
				
		// try 3 lines, if still can't find "GET", give up
		while (readCount < MAX_READ_LINES) {
			try{
				request = in.readLine();
			} catch (Exception e) {
				// debug the exception
//				e.printStackTrace();
				return;
			}
			readCount++; 
			if (request == null) return;
			if (request.startsWith("GET")) {
				break;
			}
		}
		
		// parse the resource identifier
		String resourceIdentifier = null;
		resourceIdentifier = parseResourceIdentifier(request);

		// error check the resource identifier
		if (resourceIdentifier == null) {
			// if it is parsed
			ErrorModule.getReference().fileNotFoundError(clientSocket, request,
					new FileNotFoundException());
		}
		// default resource request
		else if (resourceIdentifier.equals("/")) {
			new ResourceHandler().handleRequest(clientSocket, request,
					Constants.DEFAULT_RESOURCE_IDENTIFIER);
		}
		// if starts with //, direct to 404 not found
		else if (resourceIdentifier.startsWith("//")) {
			ErrorModule.getReference().fileNotFoundError(clientSocket, request,
					new FileNotFoundException(resourceIdentifier));			
		}
		// general resource request
		else {
			new ResourceHandler().handleRequest(clientSocket, request,
					resourceIdentifier);
		}
	}

	// Parses an HTTP request header for the resource identifier.
	//
	// Typical format: GET /index.html HTTP/1.0
	//
	// Returns: /index.html
	//
	private String parseResourceIdentifier(String request) {
		if (request.isEmpty())
			return null;

		// split the String, return the second piece
		String[] results = request.split(" ");
		if (results.length >= 2)
			return results[1];
		else
			return null;
	}

}// end class
