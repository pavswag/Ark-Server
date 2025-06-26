package io.kyros.sql.dailytracker;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 18/03/2024
 */
public class EmailManager {

    public static void sendEmail(String subject, String body) throws IOException {

        LocalDate currentDate = LocalDate.now();

        // Recipient's email ID needs to be mentioned.
        String[] to = {"ericborawski1@gmail.com", "nullxz@proton.me"};

        // Sender's email ID needs to be mentioned
        String from = "datatracker@realmrsps.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.hostinger.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587"); //TLS Port
        properties.put("mail.smtp.auth", "true"); //enable authentication
        properties.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
        properties.put("mail.smtp.ssl.trust", "smtp.hostinger.com");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("datatracker@realmrsps.com", "*$y7ZbT2N!dn&");

            }

        });

        // Used to debug SMTP issues
        session.setDebug(false);

        new Thread(() -> {
            try {
                for (int i = 0; i < to.length; i++) {
                    // Create a default MimeMessage object.
                    MimeMessage message = new MimeMessage(session);

                    // Set From: header field of the header.
                    message.setFrom(new InternetAddress(from));

                    // Set To: header field of the header.
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));

                    // Set Subject: header field
                    message.setSubject(subject);
                    message.setText(body);

                    System.out.println("Attempting to send email with subject " + subject + "...");

                    // Send message
                    Transport.send(message);
                    System.out.println("Sent email successfully....");
                }

            } catch (javax.mail.MessagingException mex) {
                mex.printStackTrace();
            }
        }).start();
    }

}