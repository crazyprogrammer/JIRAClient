package JIRA_Adapter.Utils.test;

import java.io.*;
import java.util.StringTokenizer;

import javax.swing.text.html.*;  
import javax.swing.text.html.parser.*;  
  
public class Html2Text extends HTMLEditorKit.ParserCallback 
{  
 StringBuffer s;
 public Html2Text() {}  
  
 public void parse(Reader in) throws IOException 
 {  
   s = new StringBuffer();  
   ParserDelegator delegator = new ParserDelegator();  
   // the third parameter is TRUE to ignore charset directive  
   delegator.parse(in, this, Boolean.TRUE);  
 }  
  
 public void handleText(char[] text, int pos) 
 {  
   s.append(text);  
 }  
  
 public String getText() 
 {  
   return s.toString();  
 }  
  
 @SuppressWarnings("null")
public static void main (String[] args) 
 {  
   try 
   {  
     // the HTML to convert  
     FileReader in = new FileReader("Resources\\Test_HTML.html");  
     Html2Text parser = new Html2Text();  
     parser.parse(in);  
     in.close();  
     
     //System.out.println(parser.getText());

     String convertedText = parser.getText();
     StringTokenizer tokenizer = new StringTokenizer(convertedText, ":");
     String[] parsedData = null;
     //while (tokenizer.hasMoreElements())
    	for (int i=0;tokenizer.hasMoreElements();i++) 
     {
    	 //System.out.println(tokenizer.nextElement());
    	//parsedData[i] = tokenizer.nextElement();
		i++;
     }
   }  
   catch (Exception e) 
   {  
     e.printStackTrace();  
   }  
 }  
}