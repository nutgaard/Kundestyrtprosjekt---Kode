/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.sun.mail.imap.IMAPFolder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.NetworkService.Callback;
import no.ntnu.kpro.core.utilities.Converter;

/**
 *
 * @author Nicklas
 */
public class IMAPStorage {

    public enum BoxName {

        INBOX("INBOX", new Box<XOMessage>()),
        SENT("[Gmail]/Sendt e-post", new Box<XOMessage>()),
        OUTBOX("[Gmail]/Utkast", new Box<XOMessage>());
        private String boxName;
        private Box<XOMessage> box;

        private BoxName(String boxName, Box box) {
            this.boxName = boxName;
            this.box = box;
        }

        public String getBoxname() {
            return this.boxName;
        }

        public Box<XOMessage> getBox() {
            return this.box;
        }
    }
    private Properties props;
    private Authenticator auth;
    private List<NetworkService.Callback> listener;

    public IMAPStorage(final Properties props, final Authenticator auth) {
        this.props = props;
        this.auth = auth;
        this.listener = Collections.synchronizedList(new LinkedList<NetworkService.Callback>());
    }

    public void getAllMessages(final BoxName box, final SearchTerm search) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Session session = Session.getInstance(props, auth);
                    Store store = session.getStore("imaps");
                    store.connect();
                    IMAPFolder folder = (IMAPFolder) store.getFolder(box.boxName);
                    folder.open(Folder.READ_ONLY);
                    Message[] messages = folder.search(search);
                    for (Message m : messages) {
                        System.out.println("Message: "+m.getClass().getName());
                        System.out.println("Subject: "+m.getSubject());
                        XOMessage xo = Converter.convertToXO(m);
                        box.getBox().add(xo);
                        for (Callback cb : listener) {
                            cb.mailReceived(xo);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    for (Callback cb : listener) {
                        cb.mailReceivedError(ex);
                    }
                }
            }
        }.start();
    }

    public void addCallback(Callback listener) {
        this.listener.add(listener);
    }

    public void removeCallback(Callback listener) {
        this.listener.remove(listener);
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.socketFactory.port", "465");
        props.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.port", "465");

        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.socketFactory.port", "993");
        props.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.imaps.auth", "true");
        props.put("mail.imaps.port", "993");
        IMAPStorage s = new IMAPStorage(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("kprothales", "kprothales2012");
            }
        });
        s.addCallback(new Listener());
        Flags f = new Flags();
//        f.add(Flags.Flag.RECENT);
        f.add(Flags.Flag.SEEN);

        s.getAllMessages(BoxName.INBOX, new FlagTerm(f, false));
    }
}
class Listener implements NetworkService.Callback{

    public void mailSent(XOMessage message, Address[] invalidAddress) {
        
    }

    public void mailSentError(XOMessage message, Exception ex) {
        
    }

    public void mailReceived(XOMessage message) {
        System.out.println("Message received: "+message.getSubject());
    }

    public void mailReceivedError(Exception ex) {
        System.out.println("Message received error:"+ex.toString());
    }
    
}
