/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package client;

import util.StopWatch;

/**
 * Statistics Manager
 * 
 * Handles the client side, load testing statistics
 * 
 * This class is singleton.
 * 
 * Created: 2002.12.03
 * 
 * @author Peter Brandt-Erichsen
 * @author brad.zdanivsky@gmail.com
 */
public class StatisticsManager {

	// singleton reference to this class
	private static StatisticsManager singleton = null;

	// runtime stats
	private int clients = 0;
	private int successCount = 0;
	private int failureCount = 0;
	private long totalRequestTime = 0;

	// constructor is private to guarantee a singleton instance
	private StatisticsManager() {}

	/**
	 * Returns a reference to this singleton class
	 */
	public static StatisticsManager getReference() {
		if (singleton == null)
			singleton = new StatisticsManager();
		return singleton;
	}

	/**
	 * Returns the number of configured clients
	 */
	public int getClients() {
		return clients;
	}

	/**
	 * Sets the number of configured clients
	 */
	public void setClients(int clients) {
		this.clients = clients;
	}

	/**
	 * Returns the connection request count
	 */
	public int getRequestCount() {
		return successCount + failureCount;
	}

	/**
	 * Returns the successful request count
	 */
	public int getSuccessCount() {
		return successCount;
	}

	/**
	 * Increments the successful request count
	 */
	public synchronized void incrementSuccessCount() {
		successCount++;
	}

	/**
	 * Returns the failed request count
	 */
	public int getFailureCount() {
		return failureCount;
	}

	/**
	 * Increments the failed request count
	 */
	public synchronized void incrementFailureCount() {
		failureCount++;
	}

	/**
	 * Returns the average request time
	 */
	public synchronized double getAverageRequestTime() {
		return (double) totalRequestTime
				/ (double) (successCount + failureCount);
	}

	/**
	 * Returns the total request time
	 */
	public long getTotalRequestTime() {
		return totalRequestTime;
	}

	/**
	 * Increments the total request time
	 */
	public synchronized void incrementTotalRequestTime(StopWatch stopwatch) {
		totalRequestTime += stopwatch.getElapsedTime();
	}

	/**
	 * Displays the statistics to the console
	 */
	public void displayStats() {
		System.out.println();
		System.out.println();
		System.out.println("--- Loadtest Summary ---");
		System.out.println("Number of clients: " + getClients());
		System.out.println("Total request count: " + getRequestCount());
		System.out.println("Successful requests: " + getSuccessCount());
		System.out.println("Failed requests: " + getFailureCount());
		System.out.println("Total request time: " + getTotalRequestTime() + " ms");
		System.out.println("Average request time: " + getAverageRequestTime() + " ms");	
	}
}// end class
