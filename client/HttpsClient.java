package client;

import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;

public class HttpsClient {

	/**
	 * @param address is a URL like "https://facebook.com"
	 */
	private void testHTTPS(final String address) {

		URL url = null;
		try {

			url = new URL(address);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			printCert(con);
			printContent(con);

		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	private void printCert(HttpsURLConnection con) {

		if (con == null) return;
		System.out.println(con);

//		try {
//			System.out.println("Response Code : " + con.getResponseCode());
//			System.out.println("Cipher Suite : " + con.getCipherSuite());
//			Certificate[] certs = con.getServerCertificates();
//			for (Certificate cert : certs) {
//				System.out.println("Cert Type : " + cert.getType());
//				System.out.println("Cert Hash Code : " + cert.hashCode());
//				System.out.println("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
//				System.out.println("Cert Public Key Format : " + cert.getPublicKey().getFormat());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private void printContent(HttpsURLConnection con) {
		if (con == null) return;
	
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String input = null;
			while ((input = br.readLine()) != null) 
				System.out.println(input);
			
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * you should read in from "args", and create several run configurations 
	 */
	public static void main(String[] args) {
		
		// google for example 
//		new HttpsClient().testHTTPS("https://www.google.com/search");
		
//		System.setProperty("javax.net.ssl.trustStore", "./etc/haijun_key.store");
//		System.setProperty("javax.net.ssl.trustStorePassword", "password");
		
		new HttpsClient().testHTTPS("https://localhost:8443");
	}
}