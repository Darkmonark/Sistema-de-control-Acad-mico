package com.hospital.sanrafael.service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    private static EmailService instance;
    private final String smtpHost = "smtp.gmail.com";
    private final int smtpPort = 587;
    private String senderEmail;
    private String senderPassword;

    private EmailService() {
        this.senderEmail = "hospital.sanrafael@gmail.com";
        this.senderPassword = "app-password-required";
    }

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    public void sendEmail(String to, String subject, String body) {
        sendEmail(to, subject, body, false);
    }

    public void sendEmail(String to, String subject, String body, boolean isHtml) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            
            if (isHtml) {
                MimeBodyPart contentPart = new MimeBodyPart();
                contentPart.setContent(body, "text/html; charset=utf-8");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(contentPart);
                message.setContent(multipart);
            } else {
                message.setText(body);
            }

            Transport.send(message);
            System.out.println("Email enviado exitosamente a: " + to);
        } catch (MessagingException e) {
            System.err.println("Error al enviar email: " + e.getMessage());
        }
    }

    public void setCredentials(String email, String password) {
        this.senderEmail = email;
        this.senderPassword = password;
    }
}
