package JIRA_Adapter.CRUD;


/**
 * README: https://docs.atlassian.com/jira/REST/latest/
 * Dependencies: [org.apache.commons] commons-codec-1.6, [com.sun.jersey] jersey-client-1.19, [org.json] json-20090211 and their dependencies.
 */


import org.json.JSONException;

import JIRA_Adapter.model.Issues;

public class JiraRestClient 
{
    /**
     *
     * @param args
     * @throws Exception 
     */
    //public static void main(String... args) throws Exception 
    public void CreateIssueInJIRA(Issues parsedIssue) throws Exception 
    {
        try
        {
            JiraServices jrc = new JiraServices("https://***Web-URL***/jira/", "Username", "Password");
            // String issue = jrc.getIssue("KEY-11");
            //String issue = jrc.createIssue(49305, 7, "testing with jira-REST-client", "worked :) Sudharshan",1,58523);//(ProjectID,IssueType/Story,Summary,description,priority,component/INFRA)
            String issue = jrc.createIssue(parsedIssue.getProjectID(), 7, parsedIssue.getSummary(), parsedIssue.getDescription(), parsedIssue.getPriority(), parsedIssue.getComponent(), parsedIssue.getLabels());
            System.out.println(issue);
        } 
        catch (JSONException ex) 
        {
            System.err.println(ex);
        }
    }
}