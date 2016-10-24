package JIRA_Adapter.JSONHandler;

	import java.io.File;  
	import java.io.FileWriter;  
	import java.io.IOException;
	import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;  
	import org.json.simple.JSONObject;

import JIRA_Adapter.model.Issues;  
	  
	public class JSONWrite 
	{  
	    //public static void main(String[] args)
		public void Write(Issues parsedIssue)
		{  
	    	//Issues parsedIssue = new Issues();
	    	 
	        JSONObject parsedIssueDetails = new JSONObject();  
	        parsedIssueDetails.put("AccountName", parsedIssue.getProjectID());  
	        parsedIssueDetails.put("Summary", parsedIssue.getSummary());
	        parsedIssueDetails.put("Description", parsedIssue.getDescription());
	        parsedIssueDetails.put("Component", parsedIssue.getComponent());
	  
	        try {  
	              
	        // TimeStamping the JSON Name 
	        	String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
	       	// Writing to a file  
	            File file=new File("C:\\TestJSON_2_"+timeStamp+".json");  
	            file.createNewFile();  
	            FileWriter fileWriter = new FileWriter(file);  
	            System.out.print(parsedIssueDetails);  
	  
	            fileWriter.write(parsedIssueDetails.toJSONString());  
	            fileWriter.flush();  
	            fileWriter.close();  
	  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	  
	    }  
	}