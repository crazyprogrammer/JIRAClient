package JIRA_Adapter.emailHandler.test;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;

public class Authtest 
{
	public static void main(String[] args)
	{
		String s = "sacha5:Hello12345";
		byte[] byteArray = s.getBytes();
		String auth;
		//System.out.println(Base64.encodeBase64String(byteArray));
		System.out.println(new String(DatatypeConverter.parseBase64Binary(s)));
	}
}
