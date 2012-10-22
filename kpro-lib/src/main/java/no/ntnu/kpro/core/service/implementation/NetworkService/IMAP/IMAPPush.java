/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAPStrategy;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.NetworkService.InternalCallback;

/**
 *
 * @author Nicklas
 */
public class IMAPPush extends IMAPStrategy implements MessageCountListener {

    private Properties props;
    private Authenticator auth;
    private Session session;
    private NetworkService.InternalCallback listeners;
    private IMAPFolder inbox;
    private boolean run = true;

    public IMAPPush(final Properties props, final Authenticator auth, final NetworkService.InternalCallback listeners) {
        this.props = props;
        this.auth = auth;
        this.session = Session.getInstance(props, auth);
        this.listeners = listeners;
    }

    public void run() {
        while (run) {
            try {
                Store store = session.getStore("imaps");
                store.connect();
                inbox = (IMAPFolder) store.getFolder("Inbox");
                inbox.open(Folder.READ_ONLY);
                inbox.addMessageCountListener(this);

                while (run) {
                    System.out.println("Waiting for change");
                    if (!inbox.isOpen() && run) {
                        inbox.open(Folder.READ_ONLY);
                    }else {
                        break;
                    }
                    inbox.idle();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void halt() {
        try {
            inbox.close(true);
            run = false;
        } catch (MessagingException ex) {
            Logger.getLogger(IMAPPush.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void messagesAdded(MessageCountEvent e) {
        System.out.println("New message");
        IMAPMessage[] messages = new IMAPMessage[e.getMessages().length];
        for (int i = 0; i < messages.length; i++) {
            messages[i] = (IMAPMessage) e.getMessages()[i];
        }
        for (IMAPMessage m : messages) {
            listeners.mailReceived(m);
        }
    }

    public void messagesRemoved(MessageCountEvent e) {
    }
    void setCallback(InternalCallback callback) {
        this.listeners = callback;
    }
//    public static void main(String[] args) {
//        Properties props = new Properties();
//        props.put("mail.store.protocol", "imaps");
//        props.put("mail.imaps.host", "imap.gmail.com");
//        props.put("mail.imaps.socketFactory.port", "993");
//        props.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.imaps.auth", "true");
//        props.put("mail.imaps.port", "993");
//        IMAPPush p = new IMAPPush(props, new Authenticator() {
//
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("kprothales", "kprothales2012");
//            }
//        }, new LinkedList<NetworkService.Callback>());
//        p.addCallback(new NetworkService.Callback() {
//
//            public void mailSent(XOMessage message, Address[] invalidAddress) {
//                System.out.println("Mailsent: " + message);
//            }
//
//            public void mailSentError(XOMessage message, Exception ex) {
//                System.out.println("MailsentError: " + message);
//            }
//
//            public void mailReceived(XOMessage message) {
//                System.out.println("MailReceived: " + message);
//            }
//
//            public void mailReceivedError(Exception ex) {
//                System.out.println("MailReceivedError");
//            }
//        });
//        new Thread(p).start();
//    }
}
