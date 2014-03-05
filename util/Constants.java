/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package util;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Shared system constants.
 * 
 * Created: 2002.05.18
 * 
 * @author Peter Brandt-Erichsen
 */
public class Constants {

	// list of server-side resources
	public static final String[] resources = { "/", "/index.html",
		"/linkTest.html", "/imageTest.html", "/images/guitarman.bmp",
		"/images/working.gif", "/images/hippie.gif", "/images/car.jpg",
		"/images/ubc_logo.png", "/images/mosaic.gif",
		"/images/spygmosdisk_f.gif", "images/bunny.jpeg",
		"/files/test.avi", "/files/test.class", "/files/test.doc",
		"/files/test.exe", "/files/test.html", "/files/test.jar",
		"/files/test.java", "/files/test.mov", "/files/test.mp3",
		"/files/test.mpeg", "/files/test.mpg", "/files/test.pdf",
		"/files/test.text", "/files/test.txt", "/files/test.wav",
		"/files/test.xls", "/files/test.zip" };

	// configuration constants
	public static final int DEFAULT_PORT = 8443;
	public static final int DEFAULT_BUFFER_SIZE = 2048;

	// text formatting constants
	public static final String CRLF = "\r\n";

	// web server parameters
	public static final String SERVER_IDENTIFICATION = "Haijun's HTTP server";
	public static String SERVER_IDENTIFICATION_FOOTER = null;

	public static final String HTTP_VERSION = "HTTP/1.0";
	public static String DEFAULT_DIRECTORY = null;
	public static final String DEFAULT_RELATIVE_DIRECTORY = "./html";
	public static final String DEFAULT_RESOURCE_IDENTIFIER = "/index.html";
	public static final String ADMIN_EMAIL = "haijunq@mss.icics.ubc.ca";

	public static final String LOG_DIRECTORY = "./logs/";
	public static final String LOG_FILENAME_POSTFIX = ".request.log";
	
	public static final String MIME_TYPE_FILE = "./docs/mime.types";

	public static final int FILE_SIZE_404 = 102;

	/** Constructs the server's footer identification string */
	public static void setFooterIdentification(String footer) {
		SERVER_IDENTIFICATION_FOOTER = footer;
	}

	/**
	 * Get the log file name for a day.
	 * @return
	 */
	public static String getLogFileName() {
		Calendar now = GregorianCalendar.getInstance();
		return LOG_DIRECTORY + now.get(Calendar.YEAR) + "_"
				+ String.format("%02d", now.get(Calendar.MONTH) + 1) + "_"
				+ String.format("%02d", now.get(Calendar.DAY_OF_MONTH))
				+ LOG_FILENAME_POSTFIX;
	}
	
	/**
	 * Find the absolute path for the default directory.
	 * @throws IOException 
	 */
	public static void setDefaultDirectory() throws IOException {
		File path = new File(DEFAULT_RELATIVE_DIRECTORY);
		DEFAULT_DIRECTORY = path.getCanonicalPath();
	}

}// end class
