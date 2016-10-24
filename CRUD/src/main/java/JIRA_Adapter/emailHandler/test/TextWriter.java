package JIRA_Adapter.emailHandler.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class  TextWriter
{
	public static void main(String [] args) 
	{
		try
		{
// TimeStamping the file Name 
			String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

// Writing to a file  
			File file=new File("C:\\SampleHelloText_"+timeStamp+".txt");  
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);  

			fileWriter.write("1 --> Test Content :)");  
			fileWriter.flush();  
			fileWriter.close(); 
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}  

	}
}