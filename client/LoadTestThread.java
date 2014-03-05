/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import util.StopWatch;
import HTTPClient.*;

/**
 * Load Test Thread
 * 
 * A threaded, client-side load tester.
 * 
 * This class incorporates a random delay between each resource request sent to
 * the server, to more realistically simulate typical client/server exchange.
 * 
 * Created: 2002.05.13
 * 
 * @author Peter Brandt-Erichsen
 * @author brad.zdanivsky@gmail.com
 */
public class LoadTestThread extends Thread {

	// thread delay (ms)
	public static final int THREAD_DELAY = 200;
	
	// show console output?
	public static boolean DEBUG = false;

	// reference to a random number generator
	private static Random rand = new Random();
	
	// reference to the parent class - the Load Tester
	private LoadTester loadTester = null;

	// reference to the statistics manager
	private StatisticsManager statisticsManager = null;
	
	// target URL and port 
	private String url = null;
	private int port = 0;
	
	// loop counter 
	private int requests = 0;

	/**
	 * Constructs a load testing thread
	 * 
	 * @param loadTester
	 *            is a reference to the parent Load Tester class
	 * @param url
	 *            is the address of the server
	 * @param port
	 *            specifies the port to connect to
	 * @param requests
	 *            specifies how many requests to send to the server
	 */
	public LoadTestThread(LoadTester loadTester, String url, int port, int requests) {

		// store parameters
		this.loadTester = loadTester;
		this.url = url;
		this.port = port;
		this.requests = requests;

		// reference the statistics manager
		statisticsManager = StatisticsManager.getReference();
	};

	public void run() {

		// start timing
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		// start sending requests
		for (int i = 0; i < requests; i++) {
			try {
				// delay thread by [0, n) milliseconds
				Thread.sleep((long) rand.nextInt(THREAD_DELAY));
			} catch (Exception e) {
				break;
			}

			// send a resource request to the server
			try {
				sendRequest();
			} catch (MalformedURLException | ProtocolNotSuppException e) {
				// this should not be reached because setproperties();
				e.printStackTrace();
			}
		}

		// stop timing
		stopwatch.stop();

		// store the timing
		statisticsManager.incrementTotalRequestTime(stopwatch);

		// inform the load tester that this thread is finished
		loadTester.threadFinished();
	}

	/** Sends an HTTP request to the server 
	 * @throws MalformedURLException 
	 * @throws ProtocolNotSuppException */
	private void sendRequest() throws MalformedURLException, ProtocolNotSuppException {

		// reference to the http response object
		HTTPResponse res = null;

		// new http connection
//		HTTPConnection connection = new HTTPConnection("https", url + ":" + port, -1);
		HTTPConnection connection = new HTTPConnection(url, port);
		try {
			
			if(DEBUG) // look at the header, see javadocs for params 
				System.out.println("\n--- HTTP CONNECTION ---\n"
						+connection.Trace(""));
			
			// create a new random number
			int random = rand.nextInt(util.Constants.resources.length);

			// request a random html resource
			res = connection.Get(util.Constants.resources[random]);
//			res = connection.Get("index.html");

			// request failure
			if (res.getStatusCode() >= 300) failureMessage(res);
	
			// request success
			else successMessage(res);
		
		} catch (Exception e) {
			System.out.println(url + " fatal connection error: "+e.getMessage());	
			System.exit(-1);
		} finally {
			connection.stop();
		}
	}

	/** Outputs a success message to the client console */
	private void successMessage(final HTTPResponse res) {
	
		statisticsManager.incrementSuccessCount();
	
		// retrieve the requested resource path
		try{
			
			// output a 200 OK success message to the console
			System.out.println("request count ["
					+ statisticsManager.getRequestCount() + "] " + "200 OK - ["
					+ res.getEffectiveURI().getPath() + "]");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			flushInputStream(res);
		}
	}

	/** Outputs a failure message to the client */
	private void failureMessage(HTTPResponse res) {

		statisticsManager.incrementFailureCount();
		
		// output the error message to the console
		try {
			
			System.out.println("request count ["
					+ statisticsManager.getRequestCount() + "] "
					+ util.Utilities.getResponseMessage(res.getStatusCode())
					+ " - [" + res.getEffectiveURI().getPath() + "]");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			flushInputStream(res);
		}
	}

	/** Flushes the InputStream associated with an HTTPResponse object */
	private void flushInputStream(HTTPResponse res) {
		try {
			res.getData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}// end class


