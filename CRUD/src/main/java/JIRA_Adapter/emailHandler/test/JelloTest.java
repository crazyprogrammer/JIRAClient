package JIRA_Adapter.emailHandler.test;

import java.util.Properties;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

public class JelloTest {

    public static void main(String[] args) throws Exception {
        Session session = Session.getInstance(new Properties());
        Store store = session.getStore("pop3");
        String storeClass = store.getClass().getName();
        System.out.println("POP3 Store provider class is \"" +
                storeClass + "\".");
        System.out.println("The exJello POP3 provider " +
                ("org.exjello.mail.ExchangeStore".equals(storeClass) ?
                        "IS" : "is NOT") + " installed.");
        System.out.println();
        Transport transport = session.getTransport("smtp");
        String transportClass = transport.getClass().getName();
        System.out.println("SMTP Transport provider class is \"" +
                transportClass + "\".");
        System.out.println("The exJello SMTP provider " +
                ("org.exjello.mail.ExchangeTransport".equals(transportClass) ?
                        "IS" : "is NOT") + " installed.");
    }

}