package JIRA_Adapter.JSONHandler;

	import java.io.FileNotFoundException;  
	import java.io.FileReader;  
	import java.io.IOException;
	
	import java.util.Iterator;  
	import org.json.simple.JSONArray;  
	import org.json.simple.JSONObject;  
	import org.json.simple.parser.JSONParser;  
	import org.json.simple.parser.ParseException;

	import JIRA_Adapter.CRUD.JiraRestClient;
	import JIRA_Adapter.Utils.AdapterUtils;
	import JIRA_Adapter.model.Issues;  

	public class JSONRead
	{  
	 public static void main(String[] args) throws Exception 
	 //public void Reader() 
	 {
	  JSONParser parser = new JSONParser();  
	  
	  Issues parsedIssue = new Issues();
	  
	  try 
	  {  
	   Object obj = parser.parse(new FileReader("C:\\TestJSON_1.json"));  
	  
	   JSONObject jsonObject = (JSONObject) obj;  
	  
	   String AccountName = (String) jsonObject.get("customer_company");  
	   String summary = (String) jsonObject.get("summary");  
	   String description = (String) jsonObject.get("notes"); 
	   String incident_number = (String) jsonObject.get("incident_number");
	   String assigned_group = (String) jsonObject.get("assigned_group");  
	   String priority = (String) jsonObject.get("priority");  
	   
	   parsedIssue.setProjectID(new AdapterUtils().getProjectIdFromAccountName(AccountName));
	   parsedIssue.setDescription("Referencing BMC Remedy Incident ID: "+ incident_number+"\nNotes: "+description+"\n");
	   parsedIssue.setSummary(summary);
	   parsedIssue.setPriority(new AdapterUtils().getPriorityIDFromPriorityNameAndAccountName(AccountName, priority));
	   //parsedIssue.setComponent(new AdapterUtils().get);
	   
	   //new JSONWrite().Write(parsedIssue);
	   new JiraRestClient().CreateIssueInJIRA(parsedIssue);
	   
	  /* System.out.println("Priorities :");  
	   JSONArray Priorities = (JSONArray) jsonObject.get("Priorities");  
	   Iterator<String> iterator = Priorities.iterator();  
	   while (iterator.hasNext()) {  
	    System.out.println(iterator.next());  
	   } */ 
	  
	  } catch (FileNotFoundException e) {  
	   e.printStackTrace();  
	  } catch (IOException e) {  
	   e.printStackTrace();  
	  } catch (ParseException e) {  
	   e.printStackTrace();  
	  }  
	  
	 }  
	}  