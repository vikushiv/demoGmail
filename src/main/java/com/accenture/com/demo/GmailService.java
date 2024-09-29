package com.accenture.com.demo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.util.Properties;

@Service
public class GmailService {

    private static final String USERNAME = "demoac1308@gmail.com";
    private static final String PASSWORD = "esembdoudmroozfq"; // Use app password

    private Session session;
    private Store store;

    public GmailService() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", "imap.gmail.com");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true"); // Ensure SSL is enabled

        // Create a session with the properties
        this.session = Session.getInstance(properties);
        connect(); // Initial connection
    }

    private void connect() {
        try {
            store = session.getStore("imap");
            store.connect(USERNAME, PASSWORD);
            System.out.println("Connected to Gmail: " + store.isConnected());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 60000) // Check every minute
    public void checkEmails() {
        try {
            // Check if the store is connected
            if (!store.isConnected()) {
                connect(); // Reconnect if not connected
            }

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                String subject = message.getSubject();

                // Log the subject if it contains a specific keyword
                if (subject != null && subject.contains("startProcessInvoice")) {
                    System.out.println("Found email with subject: " + subject);

                }
            }

            inbox.close(false);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Optionally reconnect on error
            connect();
        }
    }

    public void close() {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
