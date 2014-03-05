/*
 * Copyright 2002-2013 Peter Brandt-Erichsen, Brad Zdanivsky, Ardeshir Bagheri, All Rights Reserved.
 */

package util;

/**
 * Simulates a stopwatch for system performance timing
 *
 * Created: 2002.05.13
 * @author Peter Brandt-Erichsen
*/
public class StopWatch {

   // tracks the elapsed time
   private long elapsed = 0;

   // tracks whether this stop watch is running
   private boolean running = false;


   public StopWatch() {}


   /**
    * Starts the stopwatch
    */
   public void start() {
      if(running) return;
      elapsed = System.currentTimeMillis();
      running = true;
   }

   /**
    * Stops the stopwatch
    */
   public void stop() {
      if(!running) return;
      elapsed = System.currentTimeMillis() - elapsed;
      running = false;
   }

   /**
    * Resets the stopwatch to zero
    */
   public void reset() {
      stop();
      elapsed = 0;
   }

   /**
    * Returns the elapsed time
    *
    * If the stop watch is running it will be stopped, prior to returning
    * the elapsed time.
    */
   public long getElapsedTime() {
      stop();
      return elapsed;
   }

}// end class
