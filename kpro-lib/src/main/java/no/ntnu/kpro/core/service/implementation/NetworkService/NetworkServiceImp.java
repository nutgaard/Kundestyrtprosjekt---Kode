/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import android.content.Context;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.factories.PersistenceServiceFactory;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAP;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPCache;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPPull;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPPush;
import no.ntnu.kpro.core.service.implementation.NetworkService.SMTP.SMTP;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceImp extends NetworkService implements NetworkService.Callback {

    public enum BoxName {

        INBOX("INBOX", new Box<IXOMessage>()),
        SENT("[Gmail]/Sendt e-post", new Box<IXOMessage>());
        private String boxName;
        private Box<IXOMessage> box;

        private BoxName(String boxName, Box box) {
            this.boxName = boxName;
            this.box = box;
        }

        public String getBoxname() {
            return this.boxName;
        }

        public Box<IXOMessage> getBox() {
            return this.box;
        }
    }
    private SMTP smtp;
    private IMAP imap;
    private IMAPCache cache;
    private PersistenceService persistence;

    public NetworkServiceImp(final String username, final String password, final String mailAdr, Context context) {
        this(username, password, mailAdr, new Properties(), context);
    }

    public NetworkServiceImp(final String username, final String password, final String mailAdr, Properties properties, Context context) {
        cache = new IMAPCache(properties, username, password);
        this.persistence = PersistenceServiceFactory.createMessageStorage(new User(username, password), context);
        Date lastSeen = new Date(0);
        try {
            IXOMessage[] savedMessages = PersistenceService.castTo(this.persistence.findAll(XOMessage.class), IXOMessage[].class);
            for (IXOMessage message : savedMessages) {
                System.out.println("Found saved message: " + message);
                message.getBoxAffiliation().getBox().add(message);
                cache.cache(message.getId(), message);
                lastSeen = message.getDate();
            }
        } catch (Exception ex) {
//            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Could not load messages from disk");
        }
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
        }, lastSeen, listeners, 10, cache);
        IMAPStrategy ss = new IMAPPush(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        }, lastSeen, listeners, cache);
        this.imap = new IMAP(ss);
        listeners.add(this);
    }

    public void send(XOMessage msg) {
        this.smtp.send(msg);
    }

    @Override
    public Box<IXOMessage> getOutbox() {
        return BoxName.SENT.getBox();
    }

    @Override
    public Box<IXOMessage> getInbox() {
        return BoxName.INBOX.getBox();
    }

    public void close() {
        smtp.halt();
        imap.halt();
    }

    public void mailSent(XOMessage message, Address[] invalidAddress) {
        getOutbox().add(message);
        message.setBoxAffiliation(BoxName.SENT);
    }

    public void mailSentError(XOMessage message, Exception ex) {
    }

    public void mailReceived(XOMessage message) {
        getInbox().add(message);
        try {
            System.out.println("Saving GOD DAMNIT");
            message.setBoxAffiliation(BoxName.INBOX);
            this.persistence.save(message);
        } catch (Exception ex) {
            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mailReceivedError(Exception ex) {
    }
}
