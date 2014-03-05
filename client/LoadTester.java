/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package client;

/**
 * Client Load Tester
 *
 * A quick and dirty, threaded, client-based load tester, designed to simulate
 * an arbitrarily large concurrent user base, issuing frequent HTTP requests
 * against an HTTP server.
 *
 * Created: 2002.12.03
 * @author Peter Brandt-Erichsen
 * @author brad.zdanivsky@gmail.com
 */
public class LoadTester {

   // tracks how many clients have been instantiated
   private int clients = 0;

   // tracks how many child threads have finished their work
   private int finished = 0;


   /**
    * Constructs the load tester
    *
    * @param url is the address of the server
    * @param port specifies the http ser's port (80 or 8080 for proxy)
    * @param clients specifies how many concurrent clients (threads) to create
    * @param requests specifies how many requests per client to send to the server
    */
   public LoadTester(String url, int port, int clients, int requests) {

      // force the HTTPClient to use HTTP/1.0
      System.getProperties().put("HTTPClient.forceHTTP_1.0", "true");
//      System.getProperties().put("java.protocol.handler.pkgs", "HTTPClient");
      

      // store the number of specified clients
      this.clients = clients;
      StatisticsManager.getReference().setClients(clients);

      // instantiate the load testing threads
      for(int i = 0; i < clients; i++) 
         new LoadTestThread(this, url, port, requests).start();
   }


   /**
    * A callback method, used by the child testing threads to notify
    * that they are finished.
    */
   /**
 * 
 */
protected synchronized void threadFinished() {

      // increment the count of finished threads
      finished++;

      // are all the threads finished?
      if(finished >= clients) {

         // display statistics
         StatisticsManager.getReference().displayStats();

         // terminate execution
         terminate();
      }
   }


   // Terminates execution of the client load tester
   private void terminate() {
	   System.exit(0);
   }

   // driver
   public static void main(String args[]) {

      // get the parameters from the command line
      String url = args[0];
      int port = Integer.parseInt(args[1]);
      int clients = Integer.parseInt(args[2]);
      int requests = Integer.parseInt(args[3]);

      // error check
      if(url==null) url = "localhost";
      if(url.equals("")) url = "localhost";
      
      new LoadTester(url, port, clients, requests);
   }
}// end class
