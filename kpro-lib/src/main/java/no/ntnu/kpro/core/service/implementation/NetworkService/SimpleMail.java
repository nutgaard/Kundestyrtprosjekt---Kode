package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.smtp.SMTPTransport;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

public class SimpleMail implements NetworkService {

    private final String username;
    private final String password;
    private final String mailAdr;
    private Properties props;
    private Authenticator auth;

    public SimpleMail(final String username, final String password, final String mailAdr) {
        this.props = new Properties();
        this.props.put("mail.smtp.host", "smtp.gmail.com");
        this.props.put("mail.smtp.socketFactory.port", "465");
        this.props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        this.props.put("mail.smtp.auth", "true");
        this.props.put("mail.smtp.port", "465");

        this.props.put("mail.store.protocol", "imaps");
        this.props.put("mail.imaps.host", "imap.gmail.com");
        this.props.put("mail.imaps.socketFactory.port", "993");
        this.props.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        this.props.put("mail.imaps.port", "993");


        this.auth = new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        this.username = username;
        this.password = password;
        this.mailAdr = mailAdr;
    }

    public boolean sendMail(final String recipient, final String subject, final String body) {
        try {
            Session session = Session.getInstance(this.props, this.auth);

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(this.mailAdr));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            t.connect(this.props.getProperty("mail.smtp.host"), this.mailAdr, this.password);
            t.sendMessage(message, message.getAllRecipients());

            t.close();
            return true;
        } catch (MessagingException ex) {
            Logger.getLogger(SimpleMail.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void get() {
        try {
            Session session = Session.getDefaultInstance(props, auth);
            Store store = session.getStore("imaps");
            store.connect();
            System.out.println("Store: store");

            IMAPFolder inbox = (IMAPFolder) store.getFolder("Inbox");

            inbox.addMessageCountListener(new MessageCountListener() {

                public void messagesAdded(MessageCountEvent mce) {
                    System.out.println("New message");
                }

                public void messagesRemoved(MessageCountEvent mce) {
                    System.out.println("Message removed");
                }
            });
            inbox.open(Folder.READ_WRITE);
            inbox.idle();
            System.out.println("Messages: " + inbox.getMessageCount());
//            Message[] messages = inbox.getMessages();
//            for (Message m : messages) {
//                System.out.println(m);
//            }
        } catch (MessagingException ex) {
            Logger.getLogger(SimpleMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public void setAuth(Authenticator auth) {
        this.auth = auth;
    }
}