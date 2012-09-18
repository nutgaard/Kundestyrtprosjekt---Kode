package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.smtp.SMTPTransport;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

@Deprecated
public class SimpleMail extends NetworkService {

    private List<XOMessage> outboxM;
    private List<XOMessage> inboxM;
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
        this.inboxM = new LinkedList<XOMessage>();
        this.outboxM = new LinkedList<XOMessage>();
        getAllMessages();
        startIMAP();
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
            this.outboxM.add(new XOMessage(mailAdr, recipient, subject, body));
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
        new Thread() {
            @Override
            public void run() {
                try {
                    Session session = Session.getDefaultInstance(props, auth);

                    store = session.getStore("imaps");
                    store.connect();
//                    System.out.println("Store: store");
                    inbox = (IMAPFolder) store.getFolder("Inbox");
                    inbox.open(Folder.READ_ONLY);
                    inbox.addMessageCountListener(callback);

                    while (run) {
//                        System.out.println("Idle");
                        inbox.idle();
                    }
                } catch (Exception e) {
                }
            }
        }.start();
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

    public void send(XOMessage message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void startIMAPIdle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void stopIMAPIdle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getMessages(FlagTerm flagterm, int no) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getAllMessages() {
        try {
            Session session = Session.getDefaultInstance(props, auth);

            Store lstore = session.getStore("imaps");
            lstore.connect();
            IMAPFolder linbox = (IMAPFolder) lstore.getFolder("Inbox");
            linbox.open(Folder.READ_ONLY);
            Message[] messages = linbox.getMessages();

            for (Message m : messages) {
                String from, to, subject, body = "--";
                if (m.getContentType().startsWith("multipart")) {
                    Multipart content = (Multipart) m.getContent();

                    from = m.getFrom()[0].toString();
                    to = mailAdr;
                    subject = m.getSubject();
                    for (int i = 0; i < content.getCount(); i++) {
                        BodyPart bp = content.getBodyPart(i);
                        if (bp.getContentType().startsWith("TEXT/")) {
                            body = bp.getContent().toString();
                            break;
                        }
                    }
//                    System.out.println("");
                } else {
                    from = m.getHeader("From")[0];
                    to = m.getHeader("To")[0];
                    subject = m.getHeader("Subject")[0];
                    body = m.getContent().toString();
                }
                inboxM.add(new XOMessage(from, to, subject, body));
            }
        } catch (Exception ex) {
            Logger.getLogger(SimpleMail.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    public List<XOMessage> getInbox() {
        return inboxM;
    }

    public List<XOMessage> getOutbox() {
        return outboxM;
    }

    @Override
    public void getMessages(SearchTerm searchterm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    public static void main(String[] args) throws InterruptedException {
//        SimpleMail sm = new SimpleMail("kprothales", "kprothales2012", "kprothales@gmail.com");
//        sm.getAllMessages();
//        List<XOMessage> o = sm.getInbox();
//        System.out.println("InboxSize: " + o.size());
//        for (XOMessage m : o){
//            System.out.println(m);
//        }
////        sm.startIMAP();
////        System.out.println("Sending");
////        sm.sendMail("kprothales@gmail.com", "JAOSFIJAOSFJ", "THIS IS THA BODY");
//    }

    public class IMAPListener implements MessageCountListener {

        public void messagesAdded(MessageCountEvent mce) {
            try {
//                System.out.println("New message");
//                System.out.println("");
//                System.out.println("Length: "+mce.getMessages().length);
                for (Message m : mce.getMessages()) {
                    String from, to, subject, body = "--";
                    if (m.getContentType().startsWith("multipart")) {
                        Multipart content = (Multipart) m.getContent();

                        from = m.getFrom()[0].toString();
                        to = mailAdr;
                        subject = m.getSubject();
                        for (int i = 0; i < content.getCount(); i++) {
                            BodyPart bp = content.getBodyPart(i);
                            if (bp.getContentType().startsWith("TEXT/")) {
                                body = bp.getContent().toString();
                                break;
                            }
                        }
//                        System.out.println("");
                    } else {
                        from = m.getHeader("From")[0];
                        to = m.getHeader("To")[0];
                        subject = m.getHeader("Subject")[0];
                        body = m.getContent().toString();
                    }
//                    System.out.println("--------");
//                    System.out.println("New Mail");
//                    System.out.println("From: " + from);
//                    System.out.println("Subject: " + subject);
//                    System.out.println("");
//                    System.out.println(body);
//                    System.out.println("--------");
                    XOMessage newMessage = new XOMessage(from, to, subject, body);
                    inboxM.add(newMessage);
                }
                NOF_received++;

            } catch (Exception ex) {
                Logger.getLogger(SimpleMail.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
            }
        }

        public void messagesRemoved(MessageCountEvent mce) {
//            System.out.println("Message removed");
            NOF_received--;
        }
    }

    public int getNOFReceived() {
        return this.NOF_received;
    }
}