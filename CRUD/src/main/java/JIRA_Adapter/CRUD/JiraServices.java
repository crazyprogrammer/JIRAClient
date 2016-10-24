package JIRA_Adapter.CRUD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

public class JiraServices 
{
	private final String host;
	private final String authHeader;

	/**
	 *
	 * @param host The server's URL.
	 * @param username User for logging in.
	 * @param password Password for logging in.
	 */
	public JiraServices(String host, String username, String password) 
	{
		this.authHeader = getAuth(username, password);
		if (host.endsWith("/")) {
			this.host = host.substring(0, host.length() - 1);
		} else {
			this.host = host;
		}
	}

	private static String getAuth(String username, String password)
	{
		try {
			String s = username + ":" + password;
			byte[] byteArray = s.getBytes();
			String auth;
			// auth = Base64.encodeBase64String(byteArray);
			auth = new String(DatatypeConverter.printBase64Binary(byteArray)); // Reference :- http://docs.oracle.com/javase/6/docs/api/javax/xml/bind/DatatypeConverter.html#printBase64Binary%28byte%5B%5D%29

			return auth;
		}
		catch (Exception ignore) 
		{
			return "";
		}
	}

	public String getIssue(String issuekey) throws Exception 
	{
		JSONObject result = doREST("GET", "/rest/api/2/issue", issuekey + "?*all,-comment");

		// TODO: format the JSON object to your own favorite object format.
		return result.toString();
	}

	/**
	 *
	 * @param projectid The ID of the project.
	 * @param issuetypeid The ID if the IssueType.
	 * @param summary The Summary of the new issue.
	 * @param description The body of the new issue.
	 * @return JSON serialized message.
	 * @throws Exception 
	 * @throws JiraRestClientException
	 */

	public String createIssue(Integer projectid, Integer issuetypeid, String summary, String description,Integer priority,Integer component,String label) throws Exception 
	{
		JSONObject fields = new JSONObject();
		fields.put("project", new JSONObject().put("id", projectid.toString()));
		fields.put("issuetype", new JSONObject().put("id", issuetypeid.toString()));
		fields.put("summary", summary);
		fields.put("description", description);
		fields.put("priority",new JSONObject().put("id", priority.toString()));
		fields.put("components",new JSONArray().put(new JSONObject().put("id",component.toString())));
		fields.put("labels",new JSONArray().put(label.toString())); //.replaceAll(" ", "_")
		
		JSONObject issue = new JSONObject();
		issue.put("fields", fields);

		JSONObject result = doREST("POST", "/rest/api/2/issue", issue.toString());

		return result.toString();
	}

	/**
	 *
	 * @param method The HTTP method defines if we want to fetch (GET), modify
	 * (PUT), add (POST), or remove (DELETE) entites.
	 * @param context The Resource you want to access.
	 * @param arg The Parameter(s) assembled to simply send it.
	 * @return A JSON object depicting the results, OR an exception detailing
	 * the problem.
	 * @throws Exception 
	 * @throws JiraRestClientException
	 */
	private JSONObject doREST(String method, String context, String arg) throws Exception
	{
		try 
		{
			ClientConfig config = getClientConfig();
			Client client = Client.create(config);

			if (!context.endsWith("/")) {
				context = context.concat("/");
			}

			WebResource webResource;
			if ("GET".equalsIgnoreCase(method)) {
				webResource = client.resource(this.host + context + arg);
			} else {
				webResource = client.resource(this.host + context);
			}

			WebResource.Builder builder = webResource.header("Authorization", "Basic " + this.authHeader).type("application/json").accept("application/json");

			ClientResponse response;

			if ("GET".equalsIgnoreCase(method)) {
				response = builder.get(ClientResponse.class);
			} else if ("POST".equalsIgnoreCase(method)) {
				response = builder.post(ClientResponse.class, arg);
			} else {
				response = builder.method(method, ClientResponse.class);
			}

			if (response.getStatus() == 401) {
				throw new AuthenticationException("HTTP 401 received: Invalid Username or Password.");
			}

			String jsonResponse = response.getEntity(String.class);
			JSONObject responseJson = new JSONObject(jsonResponse);

			return responseJson;
		} catch (JSONException ex) {
			throw new Exception("JSON deserializing failed.", ex);
		} catch (AuthenticationException ex) {
			throw new Exception("Login failed.", ex);
		}
	}

	/*public boolean addAttachmentToIssue(String issueKey, String fullfilename) throws IOException
	{
		String jira_attachment_authentication = new String(DatatypeConverter.printBase64Binary(new byte[] a = ("sacha5:Hello12345").getBytes()));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://tools.publicis.sapient.com/jira/api/latest/issue/"+issueKey+"/attachments");
		httppost.setHeader("X-Atlassian-Token", "nocheck");
		httppost.setHeader("Authorization", "Basic "+jira_attachment_authentication);

		File fileToUpload = new File(fullfilename);
		FileBody fileBody = new FileBody(fileToUpload);

		HttpEntity entity = MultipartEntityBuilder.create()
				.addPart("file", fileBody)
				.build();
		httppost.setEntity(entity);
		String mess = "executing request " + httppost.getRequestLine();
		logger.info(mess);

		CloseableHttpResponse response;

		try {
			response = httpclient.execute(httppost);
		} finally {
			httpclient.close();
		}

		if(response.getStatusLine().getStatusCode() == 200)
			return true;
		else
			return false;
	
	}
	
	public boolean getAttachmentFromIssue(String contentURI, String fullfilename) throws IOException {
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
try {
			HttpGet httpget = new HttpGet(contentURI);
			httpget.setHeader("Authorization", "Basic "+jira_attachment_authentication);
			System.out.println("executing request " + httpget.getURI());
			CloseableHttpResponse response = httpclient.execute(httpget);
			int status = response.getStatusLine().getStatusCode();
			if (status >=200 && status < 300) {
				HttpEntity entity = response.getEntity();
				if (entity.isStreaming()) {
					byte data[] = EntityUtils.toByteArray(entity);
					FileOutputStream fout = new FileOutputStream(new File(fullfilename));
					fout.write(data);
					fout.close();
					            }
				        }
			   } finally {
				httpclient.close();
				}

		return true;
	}
*/
	/**
	 *
	 * @return A clientconfig accepting all hosts and all ssl certificates
	 * unconditionally.
	 */
	private ClientConfig getClientConfig() 
	{
		try {
			TrustManager[] trustAllCerts;
			trustAllCerts = new TrustManager[]
					{
							new X509TrustManager() 
							{
								@Override
								public X509Certificate[] getAcceptedIssuers() 
								{
									return new X509Certificate[0];
								}

								@Override
								public void checkClientTrusted(X509Certificate[] certs, String authType) {}

								@Override
								public void checkServerTrusted(X509Certificate[] certs, String authType) {}
							}};

			// Ignore differences between given hostname and certificate hostname
			HostnameVerifier hv = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);

			HTTPSProperties prop = new HTTPSProperties(hv, sc);

			ClientConfig config = new DefaultClientConfig();
			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, prop);

			return config;
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			System.err.println(e);
		}

		return null;
	}


}
