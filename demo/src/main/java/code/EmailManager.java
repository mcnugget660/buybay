package code;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.Date;
import java.util.Properties;

public class EmailManager {

    //This is a terrible idea
    private static final String username = "officialbuybay@gmail.com";
    private static final String password = "nelw gckk glhw dvqr";

    public void sendNewPassword(String to, String tempPassword) throws MessagingException {
        System.out.println("sent to " + to);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.host", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Email Verification");
        message.setText("You forgot your password." +
                " Here is a one time password : " + tempPassword);

        Transport.send(message);
    }
}
