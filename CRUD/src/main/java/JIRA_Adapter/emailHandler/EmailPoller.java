package JIRA_Adapter.emailHandler;

/* * Based on Sun Microsystems's JavaMail API * */

import javax.mail.*;
import javax.mail.internet.InternetAddress;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.GregorianCalendar; 
import java.util.Properties; 
import java.util.TimeZone;

import javax.mail.search.FlagTerm;
import JIRA_Adapter.HtmlParser.ListLinks;
import JIRA_Adapter.Utils.ParseHTMLWithTika;

public class EmailPoller 
{
	private static String server = "Serve_Name";
	private static String username = "Username";
	private static String password = "Password";
	private static String folder = "INBOX"; ///ITSM///Log_Analyse_Test

	public static final String FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	

	public static void main(String argv[]) throws Exception
	{
		String lastCheck = "=======";
		for (int i = 0; i < 2; i++) 
		{
			System.out.println(lastCheck);

			CheckMail(argv, lastCheck);

			Thread.currentThread().sleep(3000);
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat(FORMAT_ISO_8601);
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			lastCheck = dateFormatGmt.format(new GregorianCalendar().getTime());
			i=0;
		}
	}

	private static void CheckMail(String argv[], String laskCheck) 
	{
		if (argv == null || argv.length == 0) {
			argv = new String[] { server, username, password, folder };
		}

		System.out.println(Arrays.deepToString(argv));

		if (argv.length != 4) {
			System.out.println("Creds Info: <host> <user> <password> <mbox>");
			// System.exit(1);
		}

		try 
		{
			ParseHTMLWithTika htmlParser = new ParseHTMLWithTika();
			
			Properties props = new Properties();
			props.setProperty("mail.store.protocol", "imaps");
			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			store.connect(argv[0], argv[1], argv[2]);
			Folder inbox = store.getFolder(argv[3]);
			inbox.open(Folder.READ_WRITE);

			Message msg = inbox.getMessage(inbox.getMessageCount());
			System.out.println("Total messages in "+argv[3]+" : "+inbox.getMessageCount());

			/*Address[] in = msg.getFrom();
        for (Address address : in) {
            System.out.println("FROM:" + address.toString());
        }*/

			if (inbox == null || !inbox.exists()) {
				System.out.println("Invalid folder");
				// System.exit(1);
			}

			// search for all "unseen" messages
			Flags seen = new Flags(Flags.Flag.SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
			Message messages[] = inbox.search(unseenFlagTerm);

			if (messages.length == 0)
			{
				System.out.println("No messages found");
			}
			else
			{
				System.out.println("Got " + messages.length + " new messages");
				for (int i = 0; i < messages.length; i++) 
				{
					System.out.println("Message " + (i + 1));
					System.out.println(printMessage(messages[i]));
					
					htmlParser.Html2TextParser(printMessage(messages[i]));
				}
			}
			inbox.close(true);
			store.close();
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}

	public static String printMessage(Message message) 
	{
		String myMail = "";
		try
		{
			// Get the header information
			String from = ((InternetAddress) message.getFrom()[0]).getPersonal();

			if (from == null)
				from = ((InternetAddress) message.getFrom()[0]).getAddress();

			System.out.println("FROM: " + from);
			String subject = message.getSubject();
			System.out.println("SUBJECT: " + subject);

			// -- Get the message part (i.e. the message itself) --
			Part messagePart = message;
			Object content = messagePart.getContent();
			
			// -- or its first body part if it is a multipart message --
			if (content instanceof Multipart) 
			{
				messagePart = ((Multipart) content).getBodyPart(0);
				System.out.println("[ Multipart Message]");
			}
			
			// -- Get the content type --
			String contentType = messagePart.getContentType();
			
			// -- If the content is plain text, we can print it --
			System.out.println("CONTENT:" + contentType);
			if (contentType.startsWith("text/plain")
					|| contentType.startsWith("text/html")) {
				InputStream is = messagePart.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				String thisLine = reader.readLine();
				while (thisLine != null) {
					System.out.println(thisLine);
					myMail = myMail + "\n" + thisLine;
					thisLine = reader.readLine();
				}
				new ListLinks().parseHTML(myMail);
			}
			System.out.println("-----------------------------");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return myMail;

	}


} 


/*// To send an email
	private static void SendMail(String argv[]) {

		System.out.println("\nTesting exJello - Send Mail\n");         
		ExchangeTransport exTran = null;

		try {
			Properties props = System.getProperties();

			// Get a Session object
			Session session = Session.getInstance(props, null);

			exTran = new ExchangeTransport(session, null);

			MimeMessage msg = new MimeMessage(session);
			msg.setSubject("Test Message");

			// Specify Sender
			// InternetAddress fromAddr = new InternetAddress("<email address>");
			// msg.setFrom(fromAddr);

			// Specify Receiver
			InternetAddress[] toAddr = {new
					InternetAddress("<email address>"),
			};

			msg.setRecipients(Message.RecipientType.TO, toAddr);

			// Message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Hello from Java using exJello.");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// Attachment part
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource("d:\\attachment.txt");
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("attachment.txt");
			multipart.addBodyPart(messageBodyPart);

			msg.setContent(multipart);

			// Connect
			exTran.connect(argv[0], argv[1], argv[2]);

			exTran.sendMessage(msg, toAddr);
			System.out.println("\nMail sent\n");

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (exTran != null) {
				try {
					exTran.close();
				} catch (Exception ignore) { }
			}
		}
	}*/
