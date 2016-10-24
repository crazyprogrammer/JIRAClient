package JIRA_Adapter.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PropertyFileReader 
{
	/*public static void main(String[] args) 
	{
		System.out.println(new PropertyFileReader().readPropertyFile("Constellium","Accounts"));// Test Parameters --> "Constellium","Accounts"
	}*/
	public String readPropertyFile(String InputData , String FileName)
	{
		String resultValue = "";
		try {
			File file = new File("C:\\"+FileName+".List");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			Enumeration enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) 
			{
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				if (key.equalsIgnoreCase(InputData.trim()))
					resultValue=value;
				//System.out.println(key + ": " + value);
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultValue;
	}
}