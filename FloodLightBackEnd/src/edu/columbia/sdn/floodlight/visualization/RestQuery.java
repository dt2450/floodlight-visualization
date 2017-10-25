package edu.columbia.sdn.floodlight.visualization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public class RestQuery {

	/**
	 * 
	 */
	private static final String PROTOCOL =  "http";
	private static String HOST =  "";
	private static final String PORT =  "8080";
	private static ParallelHttpClient client = null;
	private static NetworkModel model1 = new NetworkModel();
	
	public static HttpResponse runGetRestQuery(HttpGet request) throws ClientProtocolException, IOException, AuthenticationException, KeyManagementException, NoSuchAlgorithmException
	{
		//long time = System.currentTimeMillis();
		
		/* Tell the client to process the GET request */
		HttpResponse response = client.request(request);
	    
		//time = System.currentTimeMillis() - time;
		//System.out.println("Time: " + time);
		
		/* return the response of the GET request */
		return response;
	}
	
	public static HttpResponse runPostRestQuery(HttpPost request) throws ClientProtocolException, IOException, AuthenticationException, KeyManagementException, NoSuchAlgorithmException
	{
		//long time = System.currentTimeMillis();
		
		/* Tell the client to process the POST request */
		HttpResponse response = client.request(request);
	    
		//time = System.currentTimeMillis() - time;
		//System.out.println("Time: " + time);
		
		/* return the response of the POST request */
		return response;
	}
	
	public static String processRestQuery(HttpResponse response, String fileName) throws IOException {
		String result = null;
		
	    HttpEntity entity = response.getEntity();
		
	    if(entity != null) {
	    	/* Process the response and convert it to a string */
	    	InputStream is = entity.getContent();
	    	
	    	/* =========================== */
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    
		    String line;
		    StringBuffer sb = new StringBuffer();
		    
		    while((line = reader.readLine()) != null) {   	
		    	sb.append(line);
		    }
		    reader.close();
		    
		    result = sb.toString();
		    
		    /* Also write the response to a file to ease debugging */
		    BufferedWriter writer = null;
	        writer = new BufferedWriter(new FileWriter(fileName));
	        
	        if (writer != null) {
	        	writer.write(result);
	        	writer.close();
	        }
		     
		    //System.out.println(result);
		    
	    }
		
	    /* return the string result */
	    return result;
	}
	
	public static void generateNetworkModel() {
		HttpGet request = null;
		HttpResponse response = null;
		model1 = new NetworkModel();
		String restResult = new String("");
	
		try {
			/* Run and process all the REST APIs to get the network model.
			 * Keep in mind that the network model and the firewall rules
			 * both could change
			 */
			request = new HttpGet("/wm/core/controller/switches/json");
			response =runGetRestQuery(request);
			restResult = processRestQuery(response, "switches.json");
			ReadJson.readSwitchDetails(restResult, model1);
			
			request = new HttpGet("/wm/device/");
			response =runGetRestQuery(request);
			restResult = processRestQuery(response, "devices.json");
			ReadJson.readDeviceDetails(restResult, model1);
			
			request = new HttpGet("/wm/topology/links/json");
			response =runGetRestQuery(request);
			restResult = processRestQuery(response, "links.json");
			ReadJson.readLinkDetails(restResult, model1);
			
			request = new HttpGet("/wm/firewall/rules/json");
			response =runGetRestQuery(request);
			restResult = processRestQuery(response, "rules.json");
			ReadJson.readFirewallRules(restResult, model1);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length == 1) {
			HOST = args[0];
		} else {
			System.out.println("Please provide the IP of the floodlight controller as the first argument");
			return;
		}
		System.out.printf("Connecting to IP: %s\n", HOST);
		
		/* Open a persistent client with floodlight controller and 
		 * run the REST APIs at a regular interval
		 */
		
		client = new ParallelHttpClient(PROTOCOL, HOST, PORT);
		
		do {
			generateNetworkModel();
			try {
				/* dump the network model to file in JSON format */
				WriteJson.createLocalData(model1, "local_data.json");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
			try {
			    Thread.sleep(5000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		} while(true);
	}

}
