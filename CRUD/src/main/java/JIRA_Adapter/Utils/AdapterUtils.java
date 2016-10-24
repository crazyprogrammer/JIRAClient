package JIRA_Adapter.Utils;

import JIRA_Adapter.Utils.PropertyFileReader;

public class AdapterUtils 
{
	PropertyFileReader propertyFileReader = new PropertyFileReader();
	int ProjectID = 0;
	int Priority = 0;
	int Component = 0;
	
	/*public static void main(String[] args) throws Exception
	{
	 System.out.println(new AdapterUtils().getProjectIdFromAccountName("Constellium"));	
	 System.out.println(new AdapterUtils().getPriorityIDFromPriorityNameAndAccountName("Constellium","High"));	
	}*/
	
	public int getProjectIdFromAccountName(String AccountName) throws Exception
	{
		try 
		{
			ProjectID = Integer.parseInt(propertyFileReader.readPropertyFile(AccountName, "Accounts"));
			if (ProjectID==0)
				throw new Exception("Could not find the priority in the Accounts.List");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return ProjectID;
	}
	
	public int getPriorityIDFromPriorityNameAndAccountName(String AccountName , String priority) throws Exception
	{
		try 
		{
			Priority = Integer.parseInt(propertyFileReader.readPropertyFile(AccountName+"."+priority.trim(), "Priorities"));
			if (Priority==0)
				throw new Exception("Could not find the priority in the Priorities.List");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return Priority;
	}
	
	public int getComponentIDFromComponentNameAndAccountName(String AccountName , String component) throws Exception
	{
		try 
		{
			Component = Integer.parseInt(propertyFileReader.readPropertyFile(AccountName+"."+component.trim(), "Components"));
			if (Component==0)
				throw new Exception("Could not find the priority in the Components.List");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return Component;
	}
}
