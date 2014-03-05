/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */
package util;

import java.io.*;

/**
 * Created: 2002.05.13
 * 
 * @author Peter Brandt-Erichsen
 */
public class Utilities {

	/**
	 * Returns an exception stack trace as a string.
	 * 
	 * @param e
	 *            is the exception
	 */
	public static String getStackTraceAsString(Exception e) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(bytes, true);
		e.printStackTrace(writer);
		return bytes.toString();
	}

	/**
	 * Returns an exception stack trace as a string formatted for HTML display.
	 * 
	 * @param e
	 *            is the exception
	 */
	public static String getStackTraceAsStringForHTML(Exception e) {
		StringBuffer str = new StringBuffer("");
		str.append("<p>");
		str.append(e.toString());
		str.append("</p>");
		return str.toString();
	}

	/**
	 * Constructs and returns the web server identification footer
	 */
	public static String versionFooter() {
		return Constants.SERVER_IDENTIFICATION_FOOTER;
	}

	/**
	 * Converts all occurrences of the HTML encoding for white space, %20, to
	 * the ASCII equivalent for whitespace, " ".
	 * 
	 * @param str
	 *            is the string to convert.
	 */
	public static String htmlToWhiteSpace(String str) {

		// the encodings
		String htmlSpace = "%20";
		String textSpace = " ";

		// allocate a string buffer
		StringBuffer sb = new StringBuffer(str);

		// scan the input string
		int i = 0;
		while (i < sb.length()) {

			// encode "%20" to " "
			if (i < (sb.length() - htmlSpace.length())) {
				if (sb.substring(i, i + htmlSpace.length()).equals(htmlSpace)) {
					sb = sb.replace(i, i + htmlSpace.length(), textSpace);
				}
			}
			// advance index one character
			i++;
		}

		// convert to a string and return
		return sb.toString();
	}

	/**
	 * Returns the HTTP response message associated with the specified status
	 * code.
	 */
	public static String getResponseMessage(int statusCode) {

		switch (statusCode) {
		case 200:
			return "200 OK";
		case 400:
			return "400 Bad Request";
		case 401:
			return "401 Unauthorized";
		case 403:
			return "403 Forbidden";
		case 404:
			return "404 Not Found";
		case 500:
			return "500 Internal Server Error";
		case 501:
			return "501 Not Implemented";
		case 502:
			return "502 Bad Gateway";
		case 503:
			return "503 Service Unavailable";
		default:
			return "status code not recognized";
		}
	}
}// end class
