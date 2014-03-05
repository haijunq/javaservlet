/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package server.handlers.resource;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import server.error.*;
import server.log.LogManager;
import util.*;

/**
 * Resource Handler Handles resource requests.
 * 
 * Created: 2002.05.23
 * 
 * @author Peter Brandt-Erichsen
 */
public class ResourceHandler {

	public ResourceHandler() {
	}

	/**
	 * Streams the contents of the specified resource back to the client.
	 * 
	 * @param clientSocket
	 *            is the socket connection received from the client
	 * @param resourceIdentifier
	 *            specifies the requested resource
	 */
	public void handleRequest(Socket clientSocket, String request,
			String resourceIdentifier) throws IOException {
		// sanity checks
		if (clientSocket == null) {
			throw new IllegalArgumentException("the client socket is null");
		}
		if (resourceIdentifier == null) {
			throw new IllegalArgumentException(
					"the resource identifier is null");
		}

		// streams
		BufferedOutputStream output = null;
		BufferedInputStream input = null;

		// locate the requested resource
		File file = null;
		file = new File(Constants.DEFAULT_DIRECTORY + resourceIdentifier);

		// check whether the requested file exists
		if (!file.exists()) {
			ErrorModule.getReference().fileNotFoundError(clientSocket, request,
					new FileNotFoundException(resourceIdentifier));
			return;
		}

		// deal with the directory
		if (file.isDirectory()) {
			// show the file list.
			this.convertDirectoryContentListToHTML(clientSocket,
					resourceIdentifier, file, request);
			return;
		}

		// instantiate the streams
		output = new BufferedOutputStream(clientSocket.getOutputStream(),
				Constants.DEFAULT_BUFFER_SIZE);
		input = new BufferedInputStream(new FileInputStream(file),
				Constants.DEFAULT_BUFFER_SIZE);

		// retrieve the content type
		String contentType = ResourceRegistry.getReference().getContentType(
				resourceIdentifier);
		// if content type not found, display a file not found error to the user
		if (contentType == null) {
			ErrorModule.getReference().fileNotFoundError(clientSocket, request,
					new FileNotFoundException());
			return;
		}

		// construct the HTTP response header
		PrintWriter pwout = new PrintWriter(output);
		StringBuffer header = new StringBuffer(Constants.HTTP_VERSION
				+ Utilities.getResponseMessage(200) + Constants.CRLF);
		header.append("Date: " + new Date().toString() + Constants.CRLF);
		header.append("Server: " + Constants.SERVER_IDENTIFICATION
				+ Constants.CRLF);
		header.append("Content-type: " + contentType + Constants.CRLF);
		header.append("Content-length: " + file.length() + Constants.CRLF);
		header.append("Last-Modified: "
				+ new Date(file.lastModified()).toString() + Constants.CRLF);
		header.append("Accept-Ranges: " + "bytes" + Constants.CRLF);
		header.append(Constants.CRLF);

		// output the HTTP header to the client
		pwout.print(header);
		pwout.flush();

		// load the requested resource into memory
		byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];
		int chunkNum = (int) file.length() / Constants.DEFAULT_BUFFER_SIZE;
		int lastChunkSize = (int) file.length() % Constants.DEFAULT_BUFFER_SIZE;
		byte[] lastbuffer = new byte[lastChunkSize];

		for (int i = 0; i < chunkNum; i++) {
			input.read(buffer);
			// send the resource contents to the client as a stream of bytes
			output.write(buffer);
			// flush the output stream
			output.flush();
		}

		// read and write the last piece
		input.read(lastbuffer);
		output.write(lastbuffer);
		output.flush();

		// close resources
		pwout.close();
		output.close();
		input.close();

		// log the request
		StringBuffer logEntry = LogManager.getReference().constructLogEntry(
				clientSocket, request);
		logEntry.append("200 " + String.valueOf(file.length()));
		LogManager.getReference().log(logEntry.toString());
	}

	/**
	 * Construct an HTML page for the folder list
	 * 
	 * @param clientSocket
	 *            is a socket for client
	 * @param resourceIdentifier
	 *            is the parsed resource identifier
	 * @param dir
	 *            is the directory name
	 * @throws IOException
	 */
	private void convertDirectoryContentListToHTML(Socket clientSocket,
			String resourceIdentifier, File dir, String request)
			throws IOException {
		// define the necessary variables
		Map<String, String> constentList = this.scanDirectoryContentList(dir);
		PrintWriter output = new PrintWriter(new BufferedOutputStream(
				clientSocket.getOutputStream(), Constants.DEFAULT_BUFFER_SIZE));
		StringBuffer str = new StringBuffer("");

		// append the string that will be written to the socket
		// write the header
		str.append(Constants.HTTP_VERSION + Utilities.getResponseMessage(200) + Constants.CRLF);
		str.append("Content-Type: text/html" + Constants.CRLF);
		str.append(Constants.CRLF);
		
		// construct the html file
		str.append("<html>");
		str.append("<head><title>" + "Index of " + resourceIdentifier
				+ "</title></head>");
		str.append("<body>");
		str.append("<h1>"
				+ "Index of "
				+ (resourceIdentifier.endsWith("/") ? resourceIdentifier
						.substring(0, resourceIdentifier.length() - 1)
						: resourceIdentifier) + "</h1>");
		str.append("<hr width=100% align=left size=1>");
		str.append("<table border=\"0\" width=100%>");
		
		// append the file list
		str.append("<tr><th align=left>Name</strong></th><th align=left>Last Modified</strong></th></tr>");
		for (String key : constentList.keySet()) {
			str.append("<tr><td>" + key + "</td><td>" + constentList.get(key)
					+ "</td><tr>");
		}
		str.append("</table>");
		str.append("<hr width=100% align=left size=1>");
		
		// append the footer
		str.append(Utilities.versionFooter());
		str.append("</body>");
		str.append("</html>");
		str.append(Constants.CRLF);
		
		// write to the socket and close resources
		output.write(str.toString());
		output.flush();
		output.close();

		// log the request
		StringBuffer logEntry = LogManager.getReference().constructLogEntry(
				clientSocket, request);
		logEntry.append("200 " + str.length());
		LogManager.getReference().log(logEntry.toString());
	}

	/**
	 * Scan the directory and return the contents in a sorted Map.
	 * 
	 * @param dir
	 *            is the directory to be scanned.
	 * @return a map contains the directory contents
	 */
	private TreeMap<String, String> scanDirectoryContentList(File dir) {
		
		// get all the files in the directory
		TreeMap<String, String> constentList = new TreeMap<>();
		HashSet<File> curfiles = new HashSet<File>(Arrays.asList(dir
				.listFiles()));
		
		// format the date
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

		// add entries to the map
		for (File file : curfiles) {
			constentList.put(
					"<img width=\"30px\" src=\"../icons/"
							+ file.getName().substring(
									file.getName().lastIndexOf('.') + 1)
							+ ".png\"/><a href=\"../" + dir.getName() + "/" + file.getName() + "\">"
							+ file.getName() + "</a>", 
							dateFormat.format(file.lastModified()));
		}

		return constentList;
	}
}// end class
