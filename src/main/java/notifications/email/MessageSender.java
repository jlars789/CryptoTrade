package notifications.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import notifications.ErrorLogger;

public class MessageSender {
	
	public static void deliverMessage(String subject, String message) {
		final String username = "cryptotradebot789@gmail.com";
        final String password = "DmyB3ky9";

        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        System.out.println("Attempting to send email");
        try {

            Message message1 = new MimeMessage(session);
            message1.setFrom(new InternetAddress("cryptotradebot789@gmail.com"));
            message1.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("jlars789@gmail.com")
            );
            message1.setSubject(subject);
            message1.setText(message);

            Transport.send(message1);

        } catch (MessagingException e) {
        	ErrorLogger.logException(e);
        	System.out.println("Failed to send email");
        }
        
        System.out.println("Email successfully sent!");
	}

}
