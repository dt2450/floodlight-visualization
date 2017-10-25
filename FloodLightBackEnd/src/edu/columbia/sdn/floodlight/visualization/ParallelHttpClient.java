package edu.columbia.sdn.floodlight.visualization;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ParallelHttpClient extends HttpClientBuilder {
	/* HTTP client that invokes the REST API requests with the controller server
	 * This can be multi-threaded, hence the name
	 */
	HttpHost host;
	private CloseableHttpClient httpclient;
	
	public ParallelHttpClient(String proto, String host, String port) {
		/* set the server parameters in the constructor */
		setConnectionManager(new PoolingHttpClientConnectionManager());
		this.host = new HttpHost(host, Integer.valueOf(port), proto);
		this.setMaxConnPerRoute(10);
		this.setMaxConnTotal(50);
	}
	
	HttpResponse request(HttpRequestBase request) throws ClientProtocolException, IOException {
		httpclient = HttpClients.custom().build();
		/* Execute a HTTP request to the configured server */
		return httpclient.execute(host, request);
	}
	
	void closeClient() throws IOException {
		/* Close the HTTP client */
		httpclient.close();
	}
	
}