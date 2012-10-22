/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import android.content.Context;
import com.sun.mail.imap.IMAPMessage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.search.MessageIDTerm;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.factories.PersistenceServiceFactory;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAP;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPPull;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPPush;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPStorage;
import no.ntnu.kpro.core.service.implementation.NetworkService.SMTP.SMTP;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;
import no.ntnu.kpro.core.utilities.Pair;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceImp extends NetworkService implements NetworkService.Callback {

    public enum BoxName {

        INBOX("INBOX", new Box<XOMessage>()),
        SENT("[Gmail]/Sendt e-post", new Box<XOMessage>());
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
    private SMTP smtp;
    private IMAP imap;
    private Map<String, Pair<IMAPMessage, XOMessage>> cache;
    private PersistenceService persistence;

    public NetworkServiceImp(final String username, final String password, final String mailAdr, Context context) {
        this(username, password, mailAdr, new Properties(), context);
    }

    public NetworkServiceImp(final String username, final String password, final String mailAdr, Properties properties, Context context) {
        this.persistence = PersistenceServiceFactory.createMessageStorage(new User(username, password), context);
        try {
            IXOMessage[] savedMessages = this.persistence.castTo(this.persistence.findAll(XOMessage.class), IXOMessage[].class);
            for (IXOMessage message : savedMessages) {
                System.out.println("Found saved message: " + message);
                IMAPStorage store = new IMAPStorage(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }, new LinkedList<NetworkService.Callback>(), cache);
                IMAPMessage[] messages = this.persistence.castTo(store.getAllMessages(BoxName.INBOX, new MessageIDTerm(message.getId())), IMAPMessage[].class);
                if (message == null || messages.length == 0 || messages.length > 1){
                    System.out.println("Something went wrong: "+message);
                }else {
                    cache.put(message.getId(), new Pair<IMAPMessage, XOMessage>(messages[0], message));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Could not load messages from disk");
        }
        cache = new HashMap<String, Pair<IMAPMessage, XOMessage>>();
        this.smtp = new SMTP(username, password, mailAdr, properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        }, listeners);
        IMAPStrategy s = new IMAPPull(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        }, listeners, 10, cache);
        IMAPStrategy ss = new IMAPPush(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        }, listeners, cache);
        this.imap = new IMAP(ss);
        listeners.add(this);
    }

    public void send(XOMessage msg) {
        this.smtp.send(msg);
    }

    @Override
    public Box<XOMessage> getOutbox() {
        return BoxName.SENT.getBox();
    }

    @Override
    public Box<XOMessage> getInbox() {
        return BoxName.INBOX.getBox();
    }

    public void close() {
        smtp.halt();
        imap.halt();
    }

    public void mailSent(XOMessage message, Address[] invalidAddress) {
        getOutbox().add(message);
    }

    public void mailSentError(XOMessage message, Exception ex) {
    }

    public void mailReceived(XOMessage message) {
        getInbox().add(message);
        try {
            System.out.println("Saving GOD DAMNIT");
            this.persistence.save(message);
        } catch (Exception ex) {
            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mailReceivedError(Exception ex) {
    }
}
