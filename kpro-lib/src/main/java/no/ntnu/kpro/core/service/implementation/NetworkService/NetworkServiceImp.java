/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.sun.mail.imap.IMAPMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAP;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPPull;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPPush;
import no.ntnu.kpro.core.service.implementation.NetworkService.SMTP.SMTP;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
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

    public NetworkServiceImp(final String username, final String password, final String mailAdr) {
        this(username, password, mailAdr, new Properties());
    }

    public NetworkServiceImp(final String username, final String password, final String mailAdr, Properties properties) {
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
    }

    public void mailReceivedError(Exception ex) {
    }
}
