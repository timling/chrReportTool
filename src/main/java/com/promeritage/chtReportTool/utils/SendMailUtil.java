package com.promeritage.chtReportTool.utils;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SendMailUtil {

    public static void send(final String username, final String password, final String toEmailAddr,
            final String subject, final String msg) {
        send(username, password, toEmailAddr, subject, msg, null);
    }

    public static void send(final String username, final String password, final String toEmailAddr,
            final String subject, final String msg, final File file) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddr));
            message.setSubject(MimeUtility.encodeText(subject, "MS950", "B"));
            message.setContent(msg, "text/html;charset=UTF-8");
            if (file == null) {
            } else {

                // Part two is attachment
                // DataSource source = new ByteArrayDataSource(new
                // FileInputStream(file).getBytes("UTF-8"), "application/octet-stream");
                DataSource source = new FileDataSource(file.getAbsolutePath());

                // Create the message part
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(MimeUtility.encodeText(source.getName(), "MS950", "B"));

                // Create a multipar message
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                // Send the complete message parts
                message.setContent(multipart);
            }
            Transport.send(message);

            System.out.println("Send Mail Done!");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
