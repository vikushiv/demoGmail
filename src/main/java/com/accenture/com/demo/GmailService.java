package com.accenture.com.demo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.io.IOException;
import java.util.Properties;

@Service
public class GmailService {

    private static final String USERNAME = "demoac1308@gmail.com";
    private static final String PASSWORD = "XXXXXXXXXXXXXXX"; // Use app password

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
//        try {
//            // Check if the store is connected
//            if (!store.isConnected()) {
//                connect(); // Reconnect if not connected
//            }
//
//            Folder inbox = store.getFolder("INBOX");
//            inbox.open(Folder.READ_ONLY);
//
//            Message[] messages = inbox.getMessages();
//            for (Message message : messages) {
//                String subject = message.getSubject();
//                System.out.println(message.getContent());
//                // Log the subject if it contains a specific keyword
//                if (subject != null && subject.contains("startProcessInvoice")) {
//                    System.out.println("Found email with subject: " + subject);
//
//                }
//            }
//
//            inbox.close(false);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            // Optionally reconnect on error
//            connect();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try {
            // Check if the store is connected
            if (!store.isConnected()) {
                connect(); // Reconnect if not connected
            }

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();

            for (Message message : messages) {
                try {
                    String subject = message.getSubject();
                    Object content = message.getContent();

                    System.out.println("--------------------------------------------------");
                    System.out.println("Email Subject: " + subject);

                    // Handle different content types
                    if (content instanceof String) {
                        System.out.println("Email Content: " + content);
                    } else if (content instanceof Multipart) {
                        Multipart multipart = (Multipart) content;
                        for (int i = 0; i < multipart.getCount(); i++) {
                            BodyPart part = multipart.getBodyPart(i);
                            System.out.println("Part " + (i + 1) + " Content-Type: " + part.getContentType());
                            System.out.println("Content: " + part.getContent());
                        }
                    } else {
                        System.out.println("Unknown content type: " + content.getClass().getName());
                    }

                    // Check for keyword in subject
                    if (subject != null && subject.contains("startProcessInvoice")) {
                        System.out.println("Found email with subject containing 'startProcessInvoice': " + subject);
                    }

                    System.out.println("--------------------------------------------------");

                } catch (Exception e) {
                    System.out.println("Error while processing individual message: " + e.getMessage());
                    e.printStackTrace();
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
