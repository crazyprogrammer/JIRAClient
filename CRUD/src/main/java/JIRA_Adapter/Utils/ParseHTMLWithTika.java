package JIRA_Adapter.Utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.sis.internal.jdk7.StandardCharsets;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import JIRA_Adapter.CRUD.JiraRestClient;
import JIRA_Adapter.model.Issues;

public class ParseHTMLWithTika 
{

	public static Map<String, String> splitToMap(String source, String entriesSeparator, String keyValueSeparator) 
	{
		Map<String, String> map = new HashMap<String, String>();
		String[] entries = source.split(entriesSeparator);
		for (String entry : entries) 
		{
			if (!entry.isEmpty() && entry.contains(keyValueSeparator)) 
			{
				String[] keyValue = entry.split(keyValueSeparator);
				map.put(keyValue[0], (keyValue.length > 1 ? keyValue[1] : ""));
			}
		}
		// Constellium specific:
		if (map.get("Customer Company").equalsIgnoreCase(" Constellium"))
		{
			for (String entry : entries) 
			{
				if (!entry.isEmpty() && entry.contains("Notes:"))
				{
					Matcher matcher = Pattern.compile(
							Pattern.quote("Notes:")
							+ "(.*?)"
							+ Pattern.quote("Status: Assigned")
							).matcher(source.replaceAll("\n|\r", " "));
					while(matcher.find())
					{
						String match = matcher.group(1);
						System.out.println("Notes Value : "+match);
						map.put("Notes", match);
					}
				}
			}
			Matcher matcher = Pattern.compile(
					Pattern.quote("Incident")
					+ "(.*?)"
					+ Pattern.quote("is assigned to you")
					).matcher(source.replaceAll("\n|\r", " "));
			while(matcher.find())
			{
				String match = matcher.group(1);
				System.out.println("Incident Number : "+match);
				map.putIfAbsent("Incident_Key", match.trim());
			}
		}			
		return map;
	}

	//public static void main(String args[]) throws Exception
	public void Html2TextParser(String htmlContent) throws IOException 
	{
		Issues issue = new Issues();
		AdapterUtils Utils = new AdapterUtils();
		JiraRestClient callJiraRestClient = new JiraRestClient();
		//InputStream is = null;
		InputStream stream = new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8));
		int i = 0;
		try 
		{
			//is = new FileInputStream("Resources/Test_HTML.html");

			ContentHandler contenthandler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			Parser parser = new AutoDetectParser();
			parser.parse(stream, contenthandler, metadata, new ParseContext());
			Map<String, String> extractedData = new HashMap<>();

			/** Testing */
			//System.out.println(splitToMap(contenthandler.toString(), "\n", ":"));
			//System.out.println(splitToMap(contenthandler.toString(), "\\|", "="));

			/**
			 * --> Constillium Work Area 
			 * */

			extractedData = splitToMap(contenthandler.toString(), "\n", ":");

			for(Map.Entry text : extractedData.entrySet())
			{
				System.out.println(i + " - " + text.getKey() + " = " + text.getValue());
				i++;
			}
			issue.setSummary(extractedData.get("Summary").trim());
			issue.setDescription(extractedData.get("Notes"));
			issue.setPriority(Utils.getPriorityIDFromPriorityNameAndAccountName(extractedData.get("Customer Company").trim(), extractedData.get("Priority").trim()));
			issue.setLabels(extractedData.get("Incident_Key").trim());
			issue.setProjectID(Utils.getProjectIdFromAccountName(extractedData.get("Customer Company").trim()));

			callJiraRestClient.CreateIssueInJIRA(issue);



			/** 
			 * -->DE Log Analyser Work Area 
			 * */ 

			/**--> Sample
			 *  Summary="Summary123"|Description="Desc123"|Issue Type=Bug|Component=LogEvent|Priority=2 */

			/*extractedData = splitToMap(contenthandler.toString(), "\\|", "="); 
         for(Map.Entry text : extractedData.entrySet())
         {
        	 System.out.println(i + " - " + text.getKey() + " = " + text.getValue());
        	 i++;
         }
         issue.setSummary(extractedData.get("\nSummary"));
         issue.setDescription(extractedData.get("Description"));

         //System.out.println("DE."+extractedData.get("Priority"));
         issue.setPriority(Utils.getPriorityIDFromPriorityNameAndAccountName("DE", extractedData.get("Priority").replaceAll("\\n|\\r", " ").trim()));
         issue.setComponent(Utils.getComponentIDFromComponentNameAndAccountName("DE", extractedData.get("Component").trim()));
         issue.setProjectID(Utils.getProjectIdFromAccountName("Constellium"));
         issue.setLabels(extractedData.get("labels").trim());
         callJiraRestClient.CreateIssueInJIRA(issue);*/

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally {
			if (stream != null) stream.close();
		}
	}
}