/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package server.handlers.resource;

import java.io.*;
import java.util.*;

import util.Constants;

/**
 * Resource Registry Stores all of the content types supported by this web
 * server.
 * 
 * The singleton design pattern is implemented to ensure that only one instance
 * of this class is instantiated.
 * 
 * Created: 2002.05.24
 * 
 * @author Peter Brandt-Erichsen
 */
public class ResourceRegistry {

	// singleton reference to this class
	private static ResourceRegistry singleton = null;

	// stores the media type mappings
	private HashMap registry = null;
	
	// map size
	private static int MAPSIZE = 128;

	// constructor is private to guarantee a singleton instance
	private ResourceRegistry() throws IOException {
		init();
	}

	/**
	 * Returns a reference to this singleton class
	 * @throws IOException 
	 */
	public static ResourceRegistry getReference() throws IOException {
		if (singleton == null)
			singleton = new ResourceRegistry();
		return singleton;
	}

	/**
	 * Returns the content type associated with the specified file extension.
	 * 
	 * The specified resource identifier will have the format: /index.html
	 * 
	 * This is parsed to get .html
	 * 
	 * And the associated content type will be returned: text/html
	 * @throws IOException 
	 */
	public String getContentType(String resourceIdentifier) throws IOException {

		// sanity checks
		if (registry == null) {
			throw new IllegalStateException(this.getClass().getName()
					+ ".getContentType(): the registry is null");
		}
		if (resourceIdentifier == null) {
			return null;
		}

		// return the content type associated with the file extension
		int index = resourceIdentifier.lastIndexOf(".");

		// error check for malformed resource identifiers
		if (index < 0) {
			return null;
		} else {
			return (String) registry.get(resourceIdentifier.substring(index+1));
		}
	}

	// Initializes this registry with the supported media-type mappings
	//
	private void init() throws IOException {

		registry = new HashMap(MAPSIZE);
		
		BufferedReader in = new BufferedReader(new FileReader(Constants.MIME_TYPE_FILE));
		String line = null; 
		String [] segments;
		
		while (in.ready()) {
			line = in.readLine();
			if (line.startsWith("#"))  
				continue;
			line = line.trim().replace("\t", " ").replace("\n", "");
			segments = line.split(" ");
			if (segments.length < 2) 
				continue;
			
			for (int i = 1; i < segments.length; i++ ) {
				if (segments[i].trim().equals(" ") || segments[i].trim() == null)
					continue;
				registry.put(segments[i].trim(), segments[0].trim());
			}
		}
		
		registry.put("text", registry.get("txt"));
		registry.put("java", "text/x-java-source");
		registry.put("jar", "application/java-archive");
		
//		for (Object key : registry.keySet())
//			System.out.println(key + "=" + registry.get(key));
//		System.out.println(registry.size());
	}

}// end class
