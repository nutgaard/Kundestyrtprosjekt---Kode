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
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

public class SimpleMail implements NetworkService {

    private final String username;
    private final String password;
    private final String mailAdr;
    private Properties props;
    private Authenticator auth;
    private MessageCountListener callback;
    private boolean run = true;
    private Store store;
    private IMAPFolder inbox;
    //Just for testing purposes
    private int NOF_received = 0;

    public SimpleMail(final String username, final String password, final String mailAdr) {
        this.props = new Properties();
        this.props.put("mail.transport.protocol", "smtps");
        this.props.put("mail.smtps.host", "smtp.gmail.com");
        this.props.put("mail.smtps.socketFactory.port", "465");
        this.props.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        this.props.put("mail.smtps.auth", "true");
        this.props.put("mail.smtps.port", "465");

        this.props.put("mail.store.protocol", "imaps");
        this.props.put("mail.imaps.host", "imap.gmail.com");
        this.props.put("mail.imaps.socketFactory.port", "993");
        this.props.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        this.props.put("mail.imaps.auth", "true");
        this.props.put("mail.imaps.port", "993");

        this.callback = new IMAPListener();

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

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(this.mailAdr));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            
            message.setSubject(subject);
            message.setText(body);
            SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
            t.connect(this.props.getProperty("mail.smtps.host"), this.mailAdr, this.password);
            t.sendMessage(message, message.getAllRecipients());

            t.close();
            return true;
        } catch (MessagingException ex) {
            Logger.getLogger(SimpleMail.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void stopIMAP() {
        if (store != null) {
            try {
                store.close();
                this.run = false;
            } catch (MessagingException ex) {
                Logger.getLogger(SimpleMail.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void startIMAP() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Session session = Session.getDefaultInstance(props, auth);

                    store = session.getStore("imaps");
                    store.connect();
                    System.out.println("Store: store");
                    inbox = (IMAPFolder) store.getFolder("Inbox");
                    inbox.open(Folder.READ_ONLY);
                    inbox.addMessageCountListener(callback);

                    while (run) {
                        inbox.idle();
                    }
                } catch (Exception e) {
                }
            }
        };
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public void setAuth(Authenticator auth) {
        this.auth = auth;
    }

    public void setIMAPListener(MessageCountListener listener) {
        this.callback = listener;
    }

    public class IMAPListener implements MessageCountListener {

        public void messagesAdded(MessageCountEvent mce) {
            try {
//                System.out.println("New message");
                System.out.println("");
                for (Message m : mce.getMessages()) {
                    Multipart content = (Multipart) m.getContent();
                    System.out.println("New Mail");
                    System.out.println("From: " + m.getFrom()[0].toString() + "@" + m.getReceivedDate());
                    System.out.println("Subject: " + m.getSubject());
                    System.out.println("");
                    for (int i = 0; i < content.getCount(); i++) {
                        BodyPart bp = content.getBodyPart(i);
                        if (bp.getContentType().startsWith("TEXT/")) {
                            System.out.println(bp.getContent());
                            break;
                        }
                    }
                    System.out.println("");
                }
                NOF_received++;

            } catch (Exception ex) {
                Logger.getLogger(SimpleMail.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
            }
        }

        public void messagesRemoved(MessageCountEvent mce) {
            System.out.println("Message removed");
            NOF_received--;
        }
    }

    public int getNOFReceived() {
        return this.NOF_received;
    }
}