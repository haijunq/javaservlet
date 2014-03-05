package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.*;
import javax.servlet.http.*;

import util.StopWatch;

public class AuthServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;						// not used.
	private static final String USERPASSFILE = "./etc/haijunq_users.txt";	// password file
	private static final String NEWUSERSERVLET = "/NewUserServlet";			// NewUserServlet url pattern
	private static final String AUTHSERVLET = "/AuthServlet";				// AuthServlet url pattern
	private static final long BLOCKTIME = 3600000; 			// one hour (used to block the user)
	private static final int MINPASSLENGTH = 8; 			// minimal password length
	private static final int MAXTRYTIMES = 3;				// maximal retry times

	public static int i = 0;		// total counts 
	public static int j = 0;		// login tries
	
	private static HashMap<String, Long> blocked = new HashMap<String, Long>();		// block list
	private static String lastUser = "";											// last login username

	/**
	 * Implement the doGet() for the Servlet.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// print the reply message
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		// print the visit times
		out.println("Run # [" + i++ + "]<br />");
		
		// get the parameters
		String user = request.getParameter("user");
		String pw = request.getParameter("password");
		String servlet = request.getServletPath();
		
		// sanity check the username
		if (user == null || user.equals("")) {
			out.println("Error: no username is given <br />");
		} 
		// sanity check the password
		else if (pw == null) {
			out.println("Error: no password is given <br />");
		} 
		// username cannot contain whitespace
		else if (user.contains(" ")) {
			out.println("Error: username cannot contains whitespace <br />");
		} 
		// if username is good, password is not empty
		else {
			// get the login code
			int loginStatus = login(user, pw);
			
			// if the request comes from NewUserServlet
			if (servlet.equals(NEWUSERSERVLET)) { 	
				
				// if username not exists
				if (loginStatus == 0) {			
					
					// a valid password must be at least 8 letters long
					if (pw.length() < MINPASSLENGTH) {
						out.println("Error: password should be at least " + MINPASSLENGTH + " characters long <br />");
					} 
					
					// a valid password must contains mixed uppercase, lowercase, digit, and special characters  
					else if (!pw.matches(".*[a-z].*") || !pw.matches(".*[A-Z].*") || !pw.matches(".*[0-9].*") || !pw.matches(".*\\W.*")) {
						out.println("Error: password should contain uppercase and lowercase letters, numbers, and special characters <br />");
					} 
					
					// add a new user
					else {
						addNewUser(user, pw);
						out.println("New user is created <br />");
					}
				} 
				
				// if username exists
				else {
					out.println("Failed to create new user: username already exists<br />");
				}
			} 
			
			// if the request comes form AuthServlet
			else if (servlet.equals(AUTHSERVLET)) {	
				
				// if the username is in the block list
				if (blocked.containsKey(user)) {
					
					// if it is within 1 hour, block the username
					if (new Date().getTime() - blocked.get(user) < BLOCKTIME) {
						out.println("Error: this username is blocked, please try after 1 hour <br />");
						out.close();
						return;
					} 
					
					// if it is after 1 hour, remove the username from the block list
					else {
						blocked.remove(user);
					}
				}
				
				// if the login success, reset the lastUser, reset the counter
				if (loginStatus == 1) {		
					lastUser = "";
					j = 0;
					out.println("Logged in.");
				} 
				
				// if login failed
				else {
					
					// if username different from lastUser, reset the counter
					if (!user.equals(lastUser))  {
						lastUser = user;
						j = 0;
					}
					
					// if username is the same with lastUser, increment the counter
					if (loginStatus == -1) {
						
						// if the retry times hit the threshold, put the username into the block list
						if (++j == MAXTRYTIMES) {
							blocked.put(user, new Date().getTime());
							lastUser = "";
							j = 0;
						}
					}
					out.println("Login failed: incorrect username or password <br />");
				}				
			} else {
				out.println("404 not found");
			}
		}
		
		// close the PrintWriter
		out.close();
	}

	/**
	 * Try to login with the username and password.
	 * @param user username
	 * @param pass password
	 * @return 	0 username not exists
	 * 			1 username and password correct
	 * 		   -1 username correct, password wrong
	 * @throws IOException
	 */
	public int login(final String user, final String pass) throws IOException {

		// if the file not exists, add a new user, return 0
		File file = new File(USERPASSFILE);
		if (!file.exists()) {
			return 0;
		}

		// if the userFile does not contain the user
		HashMap<String, ArrayList<String>> userFile = loadUserFile();
		if (!userFile.containsKey(user)) {
			return 0;
		} else {
			String hashed = getHash(userFile.get(user).get(0) + pass);
			// check whether the password is correct
			if (hashed.equals(userFile.get(user).get(1)))
				return 1;
			else
				return -1;
		}
	}

	/** get a hashed or hex string */
	public static String getHash(final String password) {

		MessageDigest md;
		try {
			 md = MessageDigest.getInstance("SHA-256");
//			 md = MessageDigest.getInstance("SHA-1");
//			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

		md.update(password.getBytes());
		byte byteData[] = md.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++)
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
					.substring(1));

		// System.out.println(j++ + " password: " + password + "\n hashed: " +
		// sb.toString());

		return sb.toString();
	}

	/** get a salt for new password */
	public static String getSalt() {
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}

	/** load the userpassfile to a map */
	private HashMap<String, ArrayList<String>> loadUserFile()
			throws IOException {
		// check whether the file exists
		File file = new File(USERPASSFILE);
		if (!file.exists()) {
			return null;
		}

		// define the variables
		HashMap<String, ArrayList<String>> users = new HashMap<>();
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = null;
		String[] segments;

		// read and save
		while (in.ready()) {
			line = in.readLine();
			segments = line.split(" ");
			ArrayList<String> saltpass = new ArrayList<String>();
			saltpass.add(segments[1]);
			saltpass.add(segments[2]);
			users.put(segments[0], saltpass);
		}
		return users;
	}

	/** add a new user/salt/password/ record to the file */
	private void addNewUser(final String user, final String pass)
			throws IOException {
		File file = new File(USERPASSFILE);
		
		// if password file not exist, create a new one.
		if (!file.exists()) {
			file.createNewFile();
		}
		
		// append an entry to the password file.
		PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
		String salt = getSalt();
		pw.write(user + " " + salt + " " + getHash(salt + pass) + "\n");
		pw.flush();
		pw.close();
	}

	/** hash function test, not used by Jetty */
	public static void main(String[] args) throws Exception {

		String uuid = UUID.randomUUID().toString();
		System.out.println("uuid = " + uuid);

		// test for the hash time.
		StopWatch stopwatch = new StopWatch();
		long [] times = new long[1000];

		// 1000 tests
		for (int j = 0; j < 1000; j++) {
			stopwatch.start();
			
			// 10000 times hash
			for (int i = 0; i < 10000; i++) {
				AuthServlet.getHash(uuid + i);
			}
			
			stopwatch.stop();
			times[j] = stopwatch.getElapsedTime();
		}
		
		long ttime = 0;
		for (long t : times) {
			ttime = ttime + t; 
		}
		System.out.println("Average time in ms: " + ttime);
	}
}
