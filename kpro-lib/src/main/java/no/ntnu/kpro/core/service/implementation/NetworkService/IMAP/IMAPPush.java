/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import no.ntnu.kpro.core.model.XOMessage;

import no.ntnu.kpro.core.service.implementation.NetworkService.IMAPStrategy;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.NetworkService.Callback;
import no.ntnu.kpro.core.utilities.Converter;

/**
 *
 * @author Nicklas
 */
public class IMAPPush extends IMAPStrategy implements MessageCountListener {

    private Properties props;
    private Authenticator auth;
    private Session session;
    private List<NetworkService.Callback> listeners;
    private IMAPFolder inbox;
    private boolean run = true;
    private Date fetchAfter;
    private long wait = 0;

    public IMAPPush(final Properties props, final Authenticator auth, Date fetchAfter, final List<NetworkService.Callback> listeners, IMAPCache cache) {
        super(cache);
        this.props = props;
        this.auth = auth;
        this.session = Session.getInstance(props, auth);
        this.listeners = listeners;
        this.fetchAfter = fetchAfter;
    }

    public void run() {
        System.out.println("Fetching existing messages");
        IMAPStorage pull = new IMAPStorage(props, auth, listeners, cache);
        pull.getAllMessages(NetworkServiceImp.BoxName.INBOX, new ReceivedDateTerm(ComparisonTerm.GT, fetchAfter));
        System.out.println("Starting IMAPPush");
        Store store = null;
        while (run) {
            try {
                if (wait > 0) {
                    synchronized (this) {
                        this.wait(wait * 10000);
                    }
                }
                if (store == null || !store.isConnected()) {
                    System.out.println("Connection to server");
                    store = session.getStore("imaps");
                    store.connect();
                }
                System.out.println("Opening folder");
                inbox = (IMAPFolder) store.getFolder("Inbox");
                inbox.open(Folder.READ_ONLY);
                inbox.addMessageCountListener(this);

                while (run) {
//                    System.out.println("Waiting for change");
                    if (!inbox.isOpen() && run) {
                        System.out.println("Reopen folder");
                        inbox.open(Folder.READ_ONLY);
                    }
//                    else {
//                        break;
//                    }
                    System.out.println("Waiting");
                    if (run) {
                        inbox.idle();
                    }
                }

            } catch (Exception e) {
                System.out.println("Exception found: " + e.getMessage());
                wait += 2;
                e.printStackTrace();
            }
        }
    }

    @Override
    public void halt() {
        try {
            if (inbox != null) {
                inbox.close(true);
                run = false;
            }
        } catch (MessagingException ex) {
            Logger.getLogger(IMAPPush.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void messagesAdded(MessageCountEvent e) {
//        System.out.println("New message");
        IMAPMessage[] messages = new IMAPMessage[e.getMessages().length];
        for (int i = 0; i < messages.length; i++) {
            messages[i] = (IMAPMessage) e.getMessages()[i];
        }
        try {
            for (IMAPMessage m : messages) {
                IMAPMessage im = (IMAPMessage) m;
                if (cache.contains(im.getMessageID())) {
                    continue;
                }
                XOMessage xo = Converter.getInstance().convertToXO(m);
                for (NetworkService.Callback cb : listeners) {
                    cb.mailReceived(xo);
                    cache.cache(im.getMessageID(), xo);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(IMAPPush.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void messagesRemoved(MessageCountEvent e) {
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

    public void addCallback(Callback callback) {
        this.listeners.add(callback);
    }

    public void removeCallback(Callback callback) {
        this.listeners.remove(callback);
    }
}
